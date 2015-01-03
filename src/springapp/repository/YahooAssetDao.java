package springapp.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import springapp.domain.Asset;
import springapp.domain.AssetParameters;
import springapp.domain.Portfolio;

/**
 * @author oliver
 * This is an implementation of the AssetDao interface.
 * This implementation fetches the data using the YAHOO! finance API. 
 */
public class YahooAssetDao implements AssetDao {

	/** Logger for this class and subclasses */
	protected final Log logger = LogFactory.getLog(getClass());
	private CurrencyConverter currencyConverter;

	public CurrencyConverter getCurrencyConverter() {
		return currencyConverter;
	}

	public void setCurrencyConverter(CurrencyConverter currencyConverter) {
		this.currencyConverter = currencyConverter;
	}

	/**
	 * @param symbol
	 * @param fromDate
	 * @param toDate
	 * @return
	 * Function to fetch and parse data from YAHOO! Finance.
	 */
	public List<Asset> getAssets(String symbol,Calendar fromDate,Calendar toDate) {
		logger.info("Getting Assets from Yahoo Finance!");
		List<Asset> assets = new ArrayList<Asset>();
		try {
			String delim = ",";
			StringTokenizer toke = new StringTokenizer(symbol, "-");
			String assetSym=null;
			String currency=null;
			while (toke.hasMoreTokens()) {
				if(assetSym==null)
					assetSym = toke.nextToken();
				else
					currency = toke.nextToken();
			}
			symbol=assetSym;
			logger.info("From "+fromDate.get(Calendar.DATE) +"/" +
					(fromDate.get(Calendar.MONTH)+1) + "/" + fromDate.get(Calendar.YEAR));
			logger.info("To "+toDate.get(Calendar.DATE) +"/" +
					(toDate.get(Calendar.MONTH)+1) + "/" + toDate.get(Calendar.YEAR));
			String fromMonth = String.valueOf(fromDate.get(Calendar.MONTH));
			String fromDay = String.valueOf(fromDate.get(Calendar.DATE));
			String fromYear = String.valueOf(fromDate.get(Calendar.YEAR));
			String toMonth = String.valueOf(toDate.get(Calendar.MONTH));
			String toDay = String.valueOf(toDate.get(Calendar.DATE));
			String toYear = String.valueOf(toDate.get(Calendar.YEAR));
			//URL myURL = new URL("http://ichart.finance.yahoo.com/table.csv?s="+symbol+
			URL myURL = new URL("http://real-chart.finance.yahoo.com/table.csv?s="+symbol+
					"&a="+fromMonth+"&b="+fromDay+"&c="+fromYear+
					"&d="+toMonth+"&e="+toDay+"&f="+toYear+"&g=m&ignore=.csv");
			logger.info(myURL);
			URLConnection myURLConnection = myURL.openConnection();
			myURLConnection.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					myURLConnection.getInputStream()));
			String inputLine;
			in.readLine();
			while ((inputLine = in.readLine()) != null){
				StringTokenizer tok = new StringTokenizer(inputLine, delim);
				int tokenIndex=1;
				Asset asset = new Asset();
				asset.setSymbol(symbol);
				asset.setCurrency(currency);
				while (tok.hasMoreTokens()) {
					String token = tok.nextToken();
					if(tokenIndex==1){
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						Calendar cal  = Calendar.getInstance();
						try {
							cal.setTime(df.parse(token));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						asset.setDate(cal);
					}
					if(tokenIndex==2){
						float open = Float.parseFloat(token);
						asset.setOpen(open);
					}
					if(tokenIndex==3){
						float high = Float.parseFloat(token);
						asset.setHigh(high);
					}
					if(tokenIndex==4){
						float low = Float.parseFloat(token);
						asset.setLow(low);
					}
					if(tokenIndex==5){
						float close = Float.parseFloat(token);
						asset.setClose(close);
					}
					if(tokenIndex==6){
						float volume = Float.parseFloat(token);
						asset.setVolume(volume);
					}
					if(tokenIndex==7){
						float adjClose = Float.parseFloat(token);
						asset.setAdjustedClose(adjClose);
					}
					tokenIndex++;
				}
				assets.add(asset);
			}
			in.close();
			logger.info("Done for "+symbol);
		} 
		catch (MalformedURLException e1) { 
			logger.info("Cannot create the URL");
		} 
		catch (IOException e) {   
			logger.info("Cannot connect to the URL");
		}
		calculateReturns(assets);
		return assets;
	}

	/**
	 * @param assets
	 * @return
	 * Function to calculate returns of the assets.
	 */
	public List<Asset> calculateReturns(List<Asset> assets){
		String currentSymbol=null;
		float currentClose=0;
		float nextClose=0;
		String nextSymbol=null;
		if(assets!=null){
			for(int i=0;i<assets.size();i++){
				currentSymbol = assets.get(i).getSymbol();
				currentClose = assets.get(i).getAdjustedClose();
				if(i!=assets.size()-1){
					nextSymbol = assets.get(i+1).getSymbol();
					nextClose = assets.get(i+1).getAdjustedClose();
				}
				if(currentSymbol.equals(nextSymbol)){
					assets.get(i).setReturns((currentClose-nextClose)/nextClose);
				}
			}
		}
		return assets;
	}

	/**
	 * @param assets
	 * @return
	 * Function to calculate the all the individual asset parameters.
	 */
	public Map<String, List<?>> calculateAssetParameters(List<Asset> assets){
		Map<String,List<Asset>> assetMap = new HashMap<String,List<Asset>>();
		Calendar fromDate = Calendar.getInstance();
		Calendar toDate = Calendar.getInstance();
		List<AssetParameters> assetParametersList = new ArrayList<AssetParameters>();
		List<String> symbols = new ArrayList<String>();
		List<String> symbolsAndCurrencies = new ArrayList<String>();
		if(assets!=null){
			for(int i=0;i<assets.size();i++){
				List<Asset> ast = assetMap.get((assets.get(i).getSymbol()));
				if(ast==null||ast.isEmpty()){
					ast=new ArrayList<Asset>();
				}
				ast.add(assets.get(i));
				assetMap.put(assets.get(i).getSymbol(), ast);
			}
		}
		assetMap=purgeReturns(assetMap);
		if(!this.currencyConverter.checkAssetsCurrency(assetMap) || assetMap.keySet().size()==1){
			assetMap=this.currencyConverter.convertToUniformCurrency(assetMap);
		}
		Iterator<Entry<String, List<Asset>>> entries = assetMap.entrySet().iterator();
		Map<String,List<Asset>> newAssetMap = new HashMap<String,List<Asset>>();
		while (entries.hasNext()) {
			Entry<String, List<Asset>> thisEntry = (Entry<String, List<Asset>>) entries.next();
			String assetKey = (String)thisEntry.getKey();
			List<Asset> assetValues = (List<Asset>)thisEntry.getValue();
			fromDate = assetValues.get(assetValues.size()-1).getDate();
			toDate = assetValues.get(0).getDate();
			List<Asset> newAssetValues = calculateReturns(assetValues);
			newAssetMap.put(assetKey, newAssetValues);
		}
		assetMap=newAssetMap;
		double[][] data = new double[assetMap.size()][];
		int index = 0;
		for (Iterator<String> iterator = assetMap.keySet().iterator(); iterator.hasNext(); index++) {
			AssetParameters assetParameters = new AssetParameters();
			String key = iterator.next().toString();
			String symCurr=null;
			symbols.add(key);
			List<Asset> assetList = assetMap.get(key);
			double returnsArray[] = null;
			for(int i=0;i<assetList.size();i++){
				if(returnsArray==null){
					returnsArray = new double[assetList.size()];
				}
				returnsArray[i]=assetList.get(i).getReturns();
				symCurr=key+"-"+assetList.get(0).getCurrency();
			}
			symbolsAndCurrencies.add(symCurr);
			float actRet = (assetList.get(0).getAdjustedClose()-assetList.get(assetList.size()-1).getAdjustedClose())
					/assetList.get(assetList.size()-1).getAdjustedClose();
			double mean = StatUtils.mean(returnsArray);
			double variance = StatUtils.variance(returnsArray);
			data[index]=returnsArray;
			logger.info("Mean of"+key+"= "+mean+" Variance = "+variance);
			assetParameters.setSymbol(key);
			assetParameters.setExpectedAssetReturn((float) mean);
			assetParameters.setAssetVariance((float) variance);
			assetParameters.setAssetStandardDev((float) Math.sqrt(variance));
			assetParameters.setFromDate(fromDate);
			assetParameters.setToDate(toDate);
			assetParameters.setActualAssetReturn(actRet);
			assetParametersList.add(assetParameters);
		}
		logger.info("transpose");
		for (int t=0;t<data.length;t++){
			logger.info(data[t].length +",");
		}
		data = transposeMatrix(data);
		Covariance c = new Covariance(data);
		PearsonsCorrelation pc = new PearsonsCorrelation(c);
		Portfolio p = new Portfolio();
		p.setCorrelationMatrix(pc.getCorrelationMatrix());
		p.setCovarianceMatrix(c.getCovarianceMatrix());
		p.setSymbols(symbols);
		p.setSymbolsAndCurrencies(symbolsAndCurrencies);
		List<Portfolio> portfolios = new ArrayList<Portfolio>();
		portfolios.add(p);
		Map<String, List<?>> returnMap = new HashMap<String, List<?>>();
		returnMap.put("Assets", assetParametersList);
		returnMap.put("Portfolios", portfolios);
		return returnMap;
	}
	
	
	/**
	 * @param m
	 * @return
	 * Function to transpose a matrix.
	 */
	public double[][] transposeMatrix(double [][] m){
		double[][] temp = new double[m[0].length][m.length];
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[i].length; j++)
				temp[j][i] = m[i][j];
		return temp;
	}
	
	/**
	 * @param assetMap
	 * @return
	 * Function to purge returns list so that each asset will have all the 
	 * returns only for the common duration.
	 */
	public Map<String,List<Asset>> purgeReturns(Map<String,List<Asset>> assetMap){
		Map<String,List<Asset>> returnMap = new HashMap<String,List<Asset>>();
		Iterator<Entry<String, List<Asset>>> entries = assetMap.entrySet().iterator();
		int minValue=0;
		while (entries.hasNext()) {
			Entry<String, List<Asset>> thisEntry = (Entry<String, List<Asset>>) entries.next();
			List<Asset> value = (List<Asset>)thisEntry.getValue();
			if(minValue==0)
				minValue=value.size();
			else if(minValue>value.size())
				minValue=value.size();
		}
		entries = assetMap.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<String, List<Asset>> thisEntry = (Entry<String, List<Asset>>) entries.next();
			String key = (String)thisEntry.getKey();
			List<Asset> value = (List<Asset>)thisEntry.getValue();
			List<Asset> newValues = value;
			if(value.size()>minValue)
				newValues = new ArrayList<Asset>(value.subList(0, minValue));
			returnMap.put(key, newValues);
		}
		return returnMap;
	}
}