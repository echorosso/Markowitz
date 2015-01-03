package springapp.repository;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import springapp.domain.Asset;

/**
 * @author oliver
 * This is a interface to get the assets and calculate the asset parameters.
 * In future we can extend this to fetch assets from more than one repositories.
 */
public interface AssetDao {

	public List<Asset> getAssets(String symbol,Calendar fromDate,Calendar toDate);
	public Map<String, List<?>> calculateAssetParameters(List<Asset> assets);
}