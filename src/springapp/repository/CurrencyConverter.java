package springapp.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import springapp.domain.Asset;
import springapp.domain.CurrencyExchange;

/**
 * @author oliver
 * This class is used to convert to a required currency.
 */
public class CurrencyConverter extends SimpleJdbcDaoSupport{
	public static String EURcurrency= "EUR";
	
	/**
	 * @param assetMap
	 * @return
	 * Given a set of assets, this function will verify if all the assets have a 
	 * common currency.
	 */
	public boolean checkAssetsCurrency(Map<String,List<Asset>> assetMap){
		Iterator<Entry<String, List<Asset>>> entries = assetMap.entrySet().iterator();
		String currency=null;
		while (entries.hasNext()) {
			Entry<String, List<Asset>> thisEntry = (Entry<String, List<Asset>>) entries.next();
			List<Asset> value = (List<Asset>)thisEntry.getValue();
			if(currency==null)
				currency=value.get(0).getCurrency();
			else if(!value.get(0).getCurrency().equals(currency))
				return false;
		}
		return true;
	}
	
	/**
	 * @param assetMap
	 * @return
	 * This function converts a given set of assets to uniform EUR currency.
	 */
	@SuppressWarnings("deprecation")
	public Map<String,List<Asset>> convertToUniformCurrency(Map<String,List<Asset>> assetMap){
		Map<String,List<Asset>> newAssetMap = new HashMap<String,List<Asset>>();
		Iterator<Entry<String, List<Asset>>> entries = assetMap.entrySet().iterator();
		while (entries.hasNext()) {
			Entry<String, List<Asset>> thisEntry = (Entry<String, List<Asset>>) entries.next();
			String key = (String)thisEntry.getKey();
			List<Asset> assets = (List<Asset>)thisEntry.getValue();
			List<CurrencyExchange> components  = new ArrayList<CurrencyExchange>();
			java.sql.Date toDate=new java.sql.Date(assets.get(0).getDate().getTimeInMillis());
			java.sql.Date fromDate=new java.sql.Date(assets.get(assets.size()-1).getDate().getTimeInMillis());
			fromDate.setMonth(fromDate.getMonth()-1);
			if(!assets.get(0).getCurrency().equals(EURcurrency)){
				try{
					components = getSimpleJdbcTemplate().query(
							"select * from currency_exchange_rates where from_currency = :fromcurr "
							+ " and to_currency = :tocurr and exc_date between :fromdate and :todate order by exc_date desc", 
							new CurrencyExchMapper(),
							new MapSqlParameterSource().addValue("fromcurr", "USD")
							.addValue("tocurr", "EUR")
							.addValue("fromdate", fromDate/*"1999-05-31"*/)
							.addValue("todate", toDate/*"2009-05-31"*/));
				}catch (Exception e){
					e.printStackTrace();
				}
				if(assets.size()!=components.size()){
					components=fetchCurrencyFromYahoo(fromDate,toDate);
				}
				for(int i=0;i<components.size();i++){
					assets.get(i).setAdjustedClose(assets.get(i).getAdjustedClose()*Float.parseFloat(components.get(i).getExchangeRate()));
					//assets.get(i).setCurrency("EUR");
				}
				newAssetMap.put(key, assets);
			}else{
				newAssetMap.put(key, assets);
			}
		}
		return newAssetMap;
	}
	
	/**
	 * @param fromDate
	 * @param toDate
	 * @return
	 * If the exchange rates are not available in the database, we fetch them 
	 * from YAHOO! Finance API
	 */
	private List<CurrencyExchange> fetchCurrencyFromYahoo(java.sql.Date fromDate,
			java.sql.Date toDate) {
		// TODO Auto-generated method stub
		Calendar fromCal=Calendar.getInstance(); 
		fromCal.setTime(fromDate);
		fromCal.add(Calendar.MONTH, 1);
		Calendar toCal=Calendar.getInstance(); 
		toCal.setTime(toDate);
		List<CurrencyExchange> components = new ArrayList<CurrencyExchange>();
		try {
			String delim = ",";
			String fromMonth = String.valueOf(fromCal.get(Calendar.MONTH));
			String fromDay = String.valueOf(fromCal.get(Calendar.DATE));
			String fromYear = String.valueOf(fromCal.get(Calendar.YEAR));
			String toMonth = String.valueOf(toCal.get(Calendar.MONTH));
			String toDay = String.valueOf(toCal.get(Calendar.DATE));
			String toYear = String.valueOf(toCal.get(Calendar.YEAR));
			URL myURL = new URL("http://ichart.yahoo.com/table.csv?s=EUR=X"+
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
				CurrencyExchange currencyExchange = new CurrencyExchange();
				while (tok.hasMoreTokens()) {
					String token = tok.nextToken();
					if(tokenIndex==1){
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						Calendar cal  = Calendar.getInstance();
						try {
							cal.setTime(df.parse(token));
							logger.info(cal.getTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						currencyExchange.setExcDate(cal.getTime());
					}
					if(tokenIndex==5){
						currencyExchange.setExchangeRate(token);
					}
					tokenIndex++;
				}
				components.add(currencyExchange);
			}
			in.close();
		} 
		catch (MalformedURLException e1) { 
			logger.info("Cannot create the URL");
		} 
		catch (IOException e) {   
			logger.info("Cannot connect to the URL");
		}
		return components;
	}


	/**
	 * @author oliver
	 * Class that maps the record from for the database query to an object of 
	 * type CurrencyExchange
	 */
	private class CurrencyExchMapper implements ParameterizedRowMapper<CurrencyExchange> {

		@Override
		public CurrencyExchange mapRow(ResultSet rs, int rowNum) throws SQLException {
			CurrencyExchange currencyExchange = new CurrencyExchange();
			currencyExchange.setId(rs.getInt("id"));
			currencyExchange.setFromCurrency(rs.getString("from_currency"));
			currencyExchange.setToCurrency(rs.getString("to_currency"));
			currencyExchange.setExchangeRate(rs.getString("exchange_rate"));
			currencyExchange.setExcDate(rs.getDate("exc_date"));
			return currencyExchange;
		}
	}
}
