package springapp.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import springapp.domain.Asset;

/**
 * @author oliver
 * An Interface to fetch assets and calculate all the relevant parameters for
 * the corresponding assets.
 */
public interface AssetManager extends Serializable{

	public List<Asset> getAssets(String symbol);
	public Map<String, List<?>> calculateAssetParameters(List<Asset> assets);
}