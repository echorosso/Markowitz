package springapp.service;

import java.util.List;

import springapp.domain.MarketComponent;
import springapp.repository.ComponentDao;

/**
 * @author oliver
 * This is an implementation of ComponentManager
 */
public class YahooComponentManager implements ComponentManager{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ComponentDao componentDao;
	
	public ComponentDao getComponentDao() {
		return componentDao;
	}

	public void setComponentDao(ComponentDao componentDao) {
		this.componentDao = componentDao;
	}

	public List<MarketComponent> getComponents(String index) {
		return componentDao.getIndexComponent(index);
	}


}
