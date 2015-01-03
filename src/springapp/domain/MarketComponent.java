package springapp.domain;

import java.io.Serializable;

/**
 * @author oliver
 * JAVA Bean for MarketComponent object.
 */
public class MarketComponent implements Serializable{
	private static final long serialVersionUID = 1L;
	private int id;
	private String indexName;
	private String indexTickerSymbol;
	private String componentName;
	private String componentTickerSymbol;
	private String currency;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public String getIndexTickerSymbol() {
		return indexTickerSymbol;
	}

	public void setIndexTickerSymbol(String indexTickerSymbol) {
		this.indexTickerSymbol = indexTickerSymbol;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getComponentTickerSymbol() {
		return componentTickerSymbol;
	}

	public void setComponentTickerSymbol(String componentTickerSymbol) {
		this.componentTickerSymbol = componentTickerSymbol;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return "MarketComponent [id=" + id + ", indexName=" + indexName
				+ ", indexTickerSymbol=" + indexTickerSymbol
				+ ", componentName=" + componentName
				+ ", componentTickerSymbol=" + componentTickerSymbol
				+ ", currency=" + currency + "]";
	}
}
