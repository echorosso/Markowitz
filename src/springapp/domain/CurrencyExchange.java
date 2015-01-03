package springapp.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author oliver
 * JAVA Bean for CurrencyExchange object.
 */
public class CurrencyExchange implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int id;
	private String fromCurrency;
	private String toCurrency;
	private String exchangeRate;
	private Date excDate;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFromCurrency() {
		return fromCurrency;
	}
	public void setFromCurrency(String fromCurrency) {
		this.fromCurrency = fromCurrency;
	}
	public String getToCurrency() {
		return toCurrency;
	}
	public void setToCurrency(String toCurrency) {
		this.toCurrency = toCurrency;
	}
	public String getExchangeRate() {
		return exchangeRate;
	}
	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	public Date getExcDate() {
		return excDate;
	}
	public void setExcDate(Date excDate) {
		this.excDate = excDate;
	}
	@Override
	public String toString() {
		return "CurrencyExchange [id=" + id + ", fromCurrency=" + fromCurrency
				+ ", toCurrency=" + toCurrency + ", exchangeRate="
				+ exchangeRate + ", excDate=" + excDate + "]";
	}
}
