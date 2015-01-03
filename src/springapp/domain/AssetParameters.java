package springapp.domain;

import java.util.Calendar;


/**
 * @author oliver
 * JAVA Bean for AssetParameter object.
 */
public class AssetParameters {
	private String symbol;
	private float expectedAssetReturn;
	private float assetVariance;
	private float assetStandardDev;
	private Calendar fromDate;
	private Calendar toDate;
	private float actualAssetReturn;
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public float getExpectedAssetReturn() {
		return expectedAssetReturn;
	}
	public void setExpectedAssetReturn(float expectedAssetReturn) {
		this.expectedAssetReturn = expectedAssetReturn;
	}
	public float getAssetVariance() {
		return assetVariance;
	}
	public void setAssetVariance(float assetVariance) {
		this.assetVariance = assetVariance;
	}
	public float getAssetStandardDev() {
		return assetStandardDev;
	}
	public void setAssetStandardDev(float assetStandardDev) {
		this.assetStandardDev = assetStandardDev;
	}
	public Calendar getFromDate() {
		return fromDate;
	}
	public void setFromDate(Calendar fromDate) {
		this.fromDate = fromDate;
	}
	public Calendar getToDate() {
		return toDate;
	}
	public void setToDate(Calendar toDate) {
		this.toDate = toDate;
	}
	public float getActualAssetReturn() {
		return actualAssetReturn;
	}
	public void setActualAssetReturn(float actualAssetReturn) {
		this.actualAssetReturn = actualAssetReturn;
	}
	@Override
	public String toString() {
		return "AssetParameters [symbol=" + symbol + ", expectedAssetReturn="
				+ expectedAssetReturn + ", assetVariance=" + assetVariance
				+ ", assetStandardDev=" + assetStandardDev + ", fromDate="
				+ fromDate.getTime() + ", toDate=" + toDate.getTime() + ", actualAssetReturn="
				+ actualAssetReturn + "]";
	}
}
