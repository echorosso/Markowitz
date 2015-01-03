package springapp.chart;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import springapp.domain.AssetParameters;
import springapp.domain.Portfolio;
import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;

/**
 * @author oliver
 * This class is used to produce the graph using jfreechart API.
 */
public class ScatterPlotDataProducer implements DatasetProducer, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected final Log logger = LogFactory.getLog(getClass());
	Map<String, List<?>> model;

	public ScatterPlotDataProducer(Map<String, List<?>> model) {
		this.model = model;
	}

	/**
	 * @param params
	 * @return
	 * This function is used to produce a scatter plot for both the portfolios 
	 * and assets.
	 */
	public Object produceDataset(Map<String, Object> params)
			throws DatasetProduceException {
		logger.info("producing data.");
		logger.info(this.model.keySet());
		XYSeriesCollection collection = new XYSeriesCollection();
		Iterator<Entry<String, List<?>>> entries = this.model.entrySet()
				.iterator();
		while (entries.hasNext()) {
			Entry<String, List<?>> thisEntry = (Entry<String, List<?>>) entries
					.next();
			String key = thisEntry.getKey().toString();
			if (key.equalsIgnoreCase("Assets")) {
				@SuppressWarnings("unchecked")
				List<AssetParameters> assets = (List<AssetParameters>) thisEntry
						.getValue();
				for (AssetParameters asset : assets) {
					XYSeries xys = new XYSeries(asset.getSymbol());
					double x = asset.getAssetStandardDev() * 100;
					double y = asset.getExpectedAssetReturn() * 100;
					BigDecimal xp = new BigDecimal(x).setScale(2,
							BigDecimal.ROUND_HALF_UP);
					x = xp.doubleValue();
					BigDecimal yp = new BigDecimal(y).setScale(2,
							BigDecimal.ROUND_HALF_UP);
					y = yp.doubleValue();
					xys.add(x, y);
					collection.addSeries(xys);
				}
			} else {
				@SuppressWarnings("unchecked")
				List<Portfolio> portfolios = (List<Portfolio>) thisEntry
						.getValue();
				XYSeries xys = new XYSeries("Portfolios");
				for (Portfolio portfolio : portfolios) {
					double x = portfolio.getPortfolioStandardDev() * 100;
					double y = portfolio.getPortfolioExpectedReturn() * 100;
					BigDecimal xp = new BigDecimal(x).setScale(2,
							BigDecimal.ROUND_HALF_UP);
					x = xp.doubleValue();
					BigDecimal yp = new BigDecimal(y).setScale(2,
							BigDecimal.ROUND_HALF_UP);
					y = yp.doubleValue();
					xys.add(x, y);
				}
				collection.addSeries(xys);
			}
		}
		return collection;
	}

	/**
	 * This producer's data is invalidated after 5 seconds. By this method the
	 * we can influence Cewolf's caching behaviour the way it wants to.
	 */
	@SuppressWarnings("rawtypes")
	public boolean hasExpired(Map params, Date since) {
		logger.debug(getClass().getName() + "hasExpired()");
		return (System.currentTimeMillis() - since.getTime()) > 10000;
	}

	/**
	 * Returns a unique ID for this DatasetProducer
	 */
	public String getProducerId() {
		return "ScatterPlot DatasetProducer";
	}

	/**
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		super.finalize();
		logger.debug(this + " finalized.");
	}
}
