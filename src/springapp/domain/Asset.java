package springapp.domain;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @author oliver
 * JAVA Bean to for Asset object.
 */
public class Asset implements Serializable {

	private static final long serialVersionUID = 1L;
	private String symbol;
	private Calendar date;
	private float open;
	private float high;
	private float low;
	private float close;
	private float volume;
	private float adjustedClose;
	private float returns;
	private String currency;
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Calendar getDate() {
		return date;
	}
	public void setDate(Calendar date) {
		this.date = date;
	}
	public float getOpen() {
		return open;
	}
	public void setOpen(float open) {
		this.open = open;
	}
	public float getHigh() {
		return high;
	}
	public void setHigh(float high) {
		this.high = high;
	}
	public float getLow() {
		return low;
	}
	public void setLow(float low) {
		this.low = low;
	}
	public float getClose() {
		return close;
	}
	public void setClose(float close) {
		this.close = close;
	}
	public float getVolume() {
		return volume;
	}
	public void setVolume(float volume) {
		this.volume = volume;
	}
	public float getAdjustedClose() {
		return adjustedClose;
	}
	public void setAdjustedClose(float adjustedClose) {
		this.adjustedClose = adjustedClose;
	}
	
	public float getReturns() {
		return returns;
	}
	public void setReturns(float returns) {
		this.returns = returns;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	@Override
	public String toString() {
		return "Asset [symbol=" + symbol + ", date=" + date.getTime() + ", open=" + open
				+ ", high=" + high + ", low=" + low + ", close=" + close
				+ ", volume=" + volume + ", adjustedClose=" + adjustedClose
				+ ", returns=" + returns + ", currency=" + currency + "]";
	}
	
}