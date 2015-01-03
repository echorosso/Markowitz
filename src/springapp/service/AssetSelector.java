package springapp.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author oliver
 * This class instantiates objects that will hold all the assets ticker symbols selected by the user.
 */
public class AssetSelector {

	/** Logger for this class and subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	private String symbol;
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
		logger.info("Sybmol selected " +symbol);
	}

	public String getSymbol() {
		return symbol;
	}

}