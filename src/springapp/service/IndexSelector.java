package springapp.service;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author oliver
 * This class instantiates object that hold indexes and corresponding components
 */
public class IndexSelector {
	protected final Log logger = LogFactory.getLog(getClass());
	
	private Map<String,String> marketIndexes;
	private String index;
	private Map<String,String> componenets;
	
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public Map<String, String> getMarketIndexes() {
		return marketIndexes;
	}
	public void setMarketIndexes(Map<String, String> marketIndexes) {
		this.marketIndexes = marketIndexes;
	}
	public Map<String, String> getComponenets() {
		return componenets;
	}
	public void setComponenets(Map<String, String> componenets) {
		this.componenets = componenets;
	}
	
}
