package springapp.service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import springapp.domain.Asset;
import springapp.domain.AssetParameters;
import springapp.domain.Portfolio;
import springapp.repository.AssetDao;
import springapp.repository.PortfolioOptimizer;

/**
 * @author oliver
 * This is an implementation of a Asset Manager
 */
public class SimpleAssetManager implements AssetManager {

	private static final long serialVersionUID = 1L;
	private AssetDao assetDao;
	private PortfolioOptimizer portfolioOptimizer;

	public List<Asset> getAssets(String symbol) {
		Calendar fromDate = new GregorianCalendar(2000,0,1);
		Calendar toDate = Calendar.getInstance();
		toDate.add(Calendar.YEAR, -5);
		return assetDao.getAssets(symbol,fromDate,toDate);
	}

	public AssetDao getAssetDao() {
		return assetDao;
	}

	public void setAssetDao(AssetDao assetDao) {
		this.assetDao = assetDao;
	}

	public PortfolioOptimizer getPortfolioOptimizer() {
		return portfolioOptimizer;
	}

	public void setPortfolioOptimizer(PortfolioOptimizer portfolioOptimizer) {
		this.portfolioOptimizer = portfolioOptimizer;
	}

	public Map<String, List<?>> calculateAssetParameters(List<Asset> assets) {
		Map<String, List<?>> assetsMap = assetDao.calculateAssetParameters(assets);
		@SuppressWarnings("unchecked")
		List<AssetParameters> assetParametersList = (List<AssetParameters>) assetsMap.get("Assets");
		Portfolio p =  (Portfolio) assetsMap.get("Portfolios").get(0);
		List<Portfolio> portfolios = this.portfolioOptimizer.optimizePortfolio(p,assetParametersList);
		Map<String, List<?>> returnMap = new HashMap<String, List<?>>();
		returnMap.put("Assets", assetParametersList);
		returnMap.put("Portfolios", portfolios);
		return returnMap;
	}
}