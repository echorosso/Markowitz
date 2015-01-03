package springapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.RealMatrix;

/**
 * @author oliver
 * JAVA Bean for Portfolio object.
 */
public class Portfolio implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<String> symbols;
	private Map<String,BigDecimal> weights;
	private double portfolioVariance;
	private double portfolioStandardDev;
	private double portfolioExpectedReturn;
	private RealMatrix covarianceMatrix;
	private RealMatrix correlationMatrix;
	private float portfolioOneYrReturn;
	private float portfolioThreeYrReturn;
	private float portfolioFiveYrReturn;
	private List<String> symbolsAndCurrencies;

	public List<String> getSymbols() {
		return symbols;
	}
	public void setSymbols(List<String> symbols) {
		this.symbols = symbols;
	}
	public Map<String, BigDecimal> getWeights() {
		return weights;
	}
	public void setWeights(Map<String, BigDecimal> weights) {
		this.weights = weights;
	}
	public double getPortfolioVariance() {
		return portfolioVariance;
	}
	public void setPortfolioVariance(double portfolioVariance) {
		this.portfolioVariance = portfolioVariance;
	}
	public double getPortfolioStandardDev() {
		return portfolioStandardDev;
	}
	public void setPortfolioStandardDev(double portfolioStandardDev) {
		this.portfolioStandardDev = portfolioStandardDev;
	}
	public double getPortfolioExpectedReturn() {
		return portfolioExpectedReturn;
	}
	public void setPortfolioExpectedReturn(double portfolioExpectedReturn) {
		this.portfolioExpectedReturn = portfolioExpectedReturn;
	}
	public RealMatrix getCovarianceMatrix() {
		return covarianceMatrix;
	}
	public void setCovarianceMatrix(RealMatrix covarianceMatrix) {
		this.covarianceMatrix = covarianceMatrix;
	}
	public RealMatrix getCorrelationMatrix() {
		return correlationMatrix;
	}
	public void setCorrelationMatrix(RealMatrix correlationMatrix) {
		this.correlationMatrix = correlationMatrix;
	}
	public float getPortfolioOneYrReturn() {
		return portfolioOneYrReturn;
	}
	public void setPortfolioOneYrReturn(float portfolioOneYrReturn) {
		this.portfolioOneYrReturn = portfolioOneYrReturn;
	}
	public float getPortfolioThreeYrReturn() {
		return portfolioThreeYrReturn;
	}
	public void setPortfolioThreeYrReturn(float portfolioThreeYrReturn) {
		this.portfolioThreeYrReturn = portfolioThreeYrReturn;
	}
	public float getPortfolioFiveYrReturn() {
		return portfolioFiveYrReturn;
	}
	public void setPortfolioFiveYrReturn(float portfolioFiveYrReturn) {
		this.portfolioFiveYrReturn = portfolioFiveYrReturn;
	}
	
	public List<String> getSymbolsAndCurrencies() {
		return symbolsAndCurrencies;
	}
	public void setSymbolsAndCurrencies(List<String> symbolsAndCurrencies) {
		this.symbolsAndCurrencies = symbolsAndCurrencies;
	}
	@Override
	public String toString() {
		return "Portfolio [symbols=" + symbols + ", weights=" + weights
				+ ", portfolioVariance=" + portfolioVariance
				+ ", portfolioStandardDev=" + portfolioStandardDev
				+ ", portfolioExpectedReturn=" + portfolioExpectedReturn
				+ ", portfolioOneYrReturn=" + portfolioOneYrReturn
				+ ", portfolioThreeYrReturn=" + portfolioThreeYrReturn
				+ ", portfolioFiveYrReturn=" + portfolioFiveYrReturn
				+ ", symbolsAndCurrencies=" + symbolsAndCurrencies + "]";
	}
}
