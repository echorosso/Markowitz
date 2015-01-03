package springapp.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.linear.RealMatrix;
import org.ojalgo.access.Access2D.Builder;
import org.ojalgo.finance.portfolio.MarketEquilibrium;
import org.ojalgo.finance.portfolio.MarkowitzModel;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;

import springapp.domain.Asset;
import springapp.domain.AssetParameters;
import springapp.domain.Portfolio;

/**
 * @author oliver
 * This class calculates the various portfolios on the frontier.
 */
public class PortfolioOptimizer {
	private AssetDao assetDao;
	protected final Log logger = LogFactory.getLog(getClass());

	public AssetDao getAssetDao() {
		return assetDao;
	}

	public void setAssetDao(AssetDao assetDao) {
		this.assetDao = assetDao;
	}

	/**
	 * @param portfolio
	 * @param assets
	 * @return
	 * Function that does the job of creating the portfolios on the frontier.
	 * The markowitz model minimizes (risk aversion factor/2) [w]T[C][w] - [w]T[r] 
	 * subject to |[w]| = 1. The w is the weight of the assets corresponding to 
	 * specific risk aversion factor.
	 */
	public List<Portfolio> optimizePortfolio(Portfolio portfolio,
			List<AssetParameters> assets) {

		List<Portfolio> portfolios = new ArrayList<Portfolio>();
		BasicMatrix<?> covariances = getCovariances(portfolio);
		Builder<PrimitiveMatrix> expectedExcessReturns1 = getAssetReturns(assets);
		MarketEquilibrium tmpME = new MarketEquilibrium(covariances);
		MarkowitzModel tmpMarkowitz = new MarkowitzModel(tmpME,expectedExcessReturns1.build());

		for (int i = 1; i < 100; i++) { // Some different risk aversion factors
			tmpMarkowitz.setRiskAversion(new BigDecimal(i));
			List<BigDecimal> tmpWeights = tmpMarkowitz.getWeights();
			Portfolio port = new Portfolio();
			Map<String, BigDecimal> weightMap = new HashMap<String, BigDecimal>();
			for (int j = 0; j < portfolio.getSymbols().size(); j++) {
				weightMap.put(portfolio.getSymbols().get(j), tmpWeights.get(j));
			}
			port.setWeights(weightMap);
			port.setPortfolioExpectedReturn(tmpMarkowitz.calculatePortfolioReturn(tmpMarkowitz));
			port.setPortfolioVariance(tmpMarkowitz.calculatePortfolioVariance(tmpMarkowitz));
			port.setPortfolioStandardDev(Math.sqrt(port.getPortfolioVariance()));
			port.setCorrelationMatrix(portfolio.getCorrelationMatrix());
			port.setCovarianceMatrix(portfolio.getCovarianceMatrix());
			port.setSymbols(portfolio.getSymbols());
			port.setSymbolsAndCurrencies(portfolio.getSymbolsAndCurrencies());
			portfolios.add(port);
		}
		portfolios = removeDuplicatePortfolios(portfolios);
		portfolios = calculatePortfolioReturns(portfolios);
		return portfolios;
	}

	/**
	 * @param portfolios
	 * @return
	 * This function is used to remove the duplicate portfolios from the frontier.
	 */
	public List<Portfolio> removeDuplicatePortfolios(List<Portfolio> portfolios) {
		List<Portfolio> refineList = portfolios;
		List<Integer> removeList = new ArrayList<Integer>();
		for (int i = 0; i < portfolios.size(); i++) {
			Map<String, BigDecimal> wt = portfolios.get(i).getWeights();
			Set<String> keys = wt.keySet();
			for (int j = i + 1; j < portfolios.size(); j++) {
				Map<String, BigDecimal> compareWt = portfolios.get(j)
						.getWeights();
				boolean status = true;
				for (String key : keys) {
					if (!wt.get(key).equals(compareWt.get(key))) {
						status = false;
						break;
					}
				}
				if (status) {
					removeList.add(j);
					break;
				}
			}
		}
		int i = 0;
		for (Iterator<Portfolio> iter = refineList.listIterator(); iter
				.hasNext(); i++) {
			iter.next();
			if (removeList.contains(i)) {
				iter.remove();
			}
		}
		return refineList;
	}

	/**
	 * @param portfolios
	 * @return
	 * This function is used to calculate the 1,3 and 5 year return of the 
	 * portfolios on the frontier.
	 */
	public List<Portfolio> calculatePortfolioReturns(List<Portfolio> portfolios) {
		List<String> uniqueAssetSymbols = new ArrayList<String>();
		for (Portfolio portfolio : portfolios) {
			List<String> symbols = portfolio.getSymbolsAndCurrencies();
			for (String symbol : symbols)
				if (!uniqueAssetSymbols.contains(symbol)) {
					uniqueAssetSymbols.add(symbol);
				}
		}
		portfolios = calculateOneYearReturns(portfolios, uniqueAssetSymbols);
		portfolios = calculateThreeYearReturns(portfolios, uniqueAssetSymbols);
		portfolios = calculateFiveYearReturns(portfolios, uniqueAssetSymbols);
		logger.info(portfolios);
		return portfolios;
	}

	/**
	 * @param portfolios
	 * @param assetsSymbols
	 * @return
	 * This function is used to calculate the 1 year returns of the portfolios 
	 * on the frontier.
	 */
	public List<Portfolio> calculateOneYearReturns(List<Portfolio> portfolios,
			List<String> assetsSymbols) {
		List<Asset> assetsList = new ArrayList<Asset>();
		for (String symbol : assetsSymbols) {
			Calendar fromDate = Calendar.getInstance();
			fromDate.add(Calendar.YEAR, -5);
			Calendar toDate = Calendar.getInstance();
			toDate.add(Calendar.YEAR, -4);
			List<Asset> assets = this.assetDao.getAssets(symbol, fromDate,
					toDate);
			assetsList.addAll(assets);
		}
		Map<String, List<?>> assetsMap = this.assetDao
				.calculateAssetParameters(assetsList);
		@SuppressWarnings("unchecked")
		List<AssetParameters> assetParametersList = (List<AssetParameters>) assetsMap
				.get("Assets");
		for (Portfolio portfolio : portfolios) {
			float ret = 0;
			for (int i = 0; i < portfolio.getSymbols().size(); i++) {
				float assetReturn=0;
				for(int j=0;j<assetParametersList.size();j++){
					if(portfolio.getSymbols().get(i).equals(assetParametersList.get(j).getSymbol()))
						assetReturn=assetParametersList.get(j).getActualAssetReturn();
				}
				ret = ret
						+ portfolio.getWeights()
								.get(portfolio.getSymbols().get(i))
								.floatValue()
						*assetReturn;// assetParametersList.get(i).getActualAssetReturn();
			}
			portfolio.setPortfolioOneYrReturn(ret);
		}
		return portfolios;
	}

	/**
	 * @param portfolios
	 * @param assetsSymbols
	 * @return
	 * This function is used to calculate the 3 year return of the portfolios 
	 * on the frontier.
	 */
	public List<Portfolio> calculateThreeYearReturns(
			List<Portfolio> portfolios, List<String> assetsSymbols) {
		List<Asset> assetsList = new ArrayList<Asset>();
		for (String symbol : assetsSymbols) {
			Calendar fromDate = Calendar.getInstance();
			fromDate.add(Calendar.YEAR, -5);
			Calendar toDate = Calendar.getInstance();
			toDate.add(Calendar.YEAR, -2);
			List<Asset> assets = this.assetDao.getAssets(symbol, fromDate,
					toDate);
			assetsList.addAll(assets);
		}
		Map<String, List<?>> assetsMap = this.assetDao
				.calculateAssetParameters(assetsList);
		@SuppressWarnings("unchecked")
		List<AssetParameters> assetParametersList = (List<AssetParameters>) assetsMap
				.get("Assets");
		for (Portfolio portfolio : portfolios) {
			float ret = 0;
			for (int i = 0; i < portfolio.getSymbols().size(); i++) {
				float assetReturn=0;
				for(int j=0;j<assetParametersList.size();j++){
					if(portfolio.getSymbols().get(i).equals(assetParametersList.get(j).getSymbol()))
						assetReturn=assetParametersList.get(j).getActualAssetReturn();
				}
				ret = ret
						+ portfolio.getWeights()
								.get(portfolio.getSymbols().get(i))
								.floatValue()
						* assetReturn;//assetParametersList.get(i).getActualAssetReturn();
			}
			portfolio.setPortfolioThreeYrReturn(ret);
		}
		return portfolios;
	}

	/**
	 * @param portfolios
	 * @param assetsSymbols
	 * @return
	 * This function is used to calculate the 5 year return of the portfolios 
	 * on the frontier.
	 */
	public List<Portfolio> calculateFiveYearReturns(List<Portfolio> portfolios,
			List<String> assetsSymbols) {
		List<Asset> assetsList = new ArrayList<Asset>();
		for (String symbol : assetsSymbols) {
			Calendar fromDate = Calendar.getInstance();
			fromDate.add(Calendar.YEAR, -5);
			Calendar toDate = Calendar.getInstance();
			List<Asset> assets = this.assetDao.getAssets(symbol, fromDate,
					toDate);
			assetsList.addAll(assets);
		}
		Map<String, List<?>> assetsMap = this.assetDao
				.calculateAssetParameters(assetsList);
		@SuppressWarnings("unchecked")
		List<AssetParameters> assetParametersList = (List<AssetParameters>) assetsMap
				.get("Assets");
		for (Portfolio portfolio : portfolios) {
			float ret = 0;
			for (int i = 0; i < portfolio.getSymbols().size(); i++) {
				float assetReturn=0;
				for(int j=0;j<assetParametersList.size();j++){
					if(portfolio.getSymbols().get(i).equals(assetParametersList.get(j).getSymbol()))
						assetReturn=assetParametersList.get(j).getActualAssetReturn();
				}
				ret = ret
						+ portfolio.getWeights()
								.get(portfolio.getSymbols().get(i))
								.floatValue()
						* assetReturn;//assetParametersList.get(i).getActualAssetReturn();
			}
			portfolio.setPortfolioFiveYrReturn(ret);
		}
		return portfolios;
	}

	/**
	 * @param assets
	 * @return
	 * This function is used to convert the list of assets to a primitive matrix.
	 */
	public Builder<PrimitiveMatrix> getAssetReturns(List<AssetParameters> assets) {
		Builder<PrimitiveMatrix> expectedReturns = PrimitiveMatrix.getBuilder(
				assets.size(), 1);
		for (int i = 0; i < assets.size(); i++) {
			expectedReturns = expectedReturns.set(i, 0, assets.get(i)
					.getExpectedAssetReturn());
		}
		return expectedReturns;
	}

	/**
	 * @param portfolio
	 * @return
	 * This function is used to calculate the covariance between the assets. 
	 */
	public BasicMatrix<?> getCovariances(Portfolio portfolio) {
		RealMatrix rmat = portfolio.getCovarianceMatrix();
		final int row = rmat.getRowDimension();
		final int col = rmat.getColumnDimension();
		Builder<PrimitiveMatrix> covariances = PrimitiveMatrix.getBuilder(row,col);
		for (int i = 1; i <= row; i++) {
			for (int j = i; j <= col; j++) {
				covariances = covariances.set(i - 1, j - 1,
						rmat.getEntry(i - 1, j - 1));
				covariances = covariances.set(j - 1, i - 1,
						rmat.getEntry(j - 1, i - 1));
			}

		}
		return covariances.build();
	}

}
