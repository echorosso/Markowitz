package springapp.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import springapp.chart.ScatterPlotDataProducer;
import springapp.domain.Asset;
import springapp.domain.MarketComponent;
import springapp.service.AssetManager;
import springapp.service.AssetSelector;
import springapp.service.AssetSelectorValidator;
import springapp.service.ComponentManager;
import springapp.service.IndexSelector;

/**
 * @author oliver
 * An Auto-wired Controller for the Spring Framework
 */
@Controller
public class FetchAssetFormController {

	/** Logger for this class and subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	private AssetManager assetManager;
	private ComponentManager componentManager;
	AssetSelectorValidator assetSelectorValidator;
	
	@Autowired
	public FetchAssetFormController(AssetSelectorValidator assetSelectorValidator, 
			AssetManager assetManager, ComponentManager componentManager){
		this.assetSelectorValidator = assetSelectorValidator;
		this.componentManager = componentManager;
		this.assetManager = assetManager;
	}
	
	/**
	 * @param model
	 * @return
	 * This is used to initialize the Form, in future make this a function to
	 * query database to fetch all the existing indexes. 
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String initForm(ModelMap model){
 
		AssetSelector assets = new AssetSelector();
		assets.setSymbol("ADS.DE-EUR");
		model.addAttribute("assets", assets);
		
		IndexSelector indexSelector = new IndexSelector();
		Map<String,String> indexes = new LinkedHashMap<String,String>();
		indexes.put("^IXIC", "NASDAQ Composite");
		indexes.put("^GDAXI", "DAX");
		indexSelector.setMarketIndexes(indexes);
		model.addAttribute("indexes", indexSelector);
		return "fetchAssetForm";
	}
	
	/**
	 * @param indexes
	 * @param outputStream
	 * @throws IOException
	 * This function is used to  query the database to fetch all the components 
	 * of a selected index.
	 */
	@RequestMapping(method = RequestMethod.POST, value="/fetchIndex.htm")
	public void processFetchIndexes(@ModelAttribute IndexSelector indexes, OutputStream outputStream) throws IOException {
		String selectedIndex = indexes.getIndex();
		List<MarketComponent> ind = this.componentManager.getComponents(selectedIndex);
		Map<String, String> compMap = new LinkedHashMap<String, String>();
		for(MarketComponent comp:ind){
			compMap.put(comp.getComponentTickerSymbol()+"-"+comp.getCurrency(),comp.getComponentName());
		}
		outputStream.write(compMap.toString().getBytes());
		outputStream.flush();
	}
	
	/**
	 * @param assets
	 * @param request
	 * @return
	 * This function is used to calculate the EF and creates a response for the 
	 * request with the graph and the portfolio's with their 1,3 and 5 year 
	 * returns.
	 */
	@RequestMapping(method = RequestMethod.POST, value="/fetchAsset.htm")
	public ModelAndView calculateMarkowtitzEF(@ModelAttribute AssetSelector assets, HttpServletRequest request){
		String delim = ",";
		String input = assets.getSymbol();
		StringTokenizer tok = new StringTokenizer(input, delim);
		List<Asset> assetsList = new ArrayList<Asset>();
		while (tok.hasMoreTokens()) {
			String symbol = tok.nextToken();
			logger.info("Fetching data for " + symbol);
			assetsList.addAll(this.assetManager.getAssets(symbol));
		}
		Map<String, List<?>> assetsAndPortfolios = this.assetManager.calculateAssetParameters(assetsList);
		ScatterPlotDataProducer graph = new ScatterPlotDataProducer(assetsAndPortfolios);
		Map<String, Object> myModel = new HashMap<String, Object>();
		request.setAttribute("graph", graph);
		myModel.put("assets", assetsAndPortfolios);
		return new ModelAndView("graphs","assets",assetsAndPortfolios);
	}
}