package springapp.service;

import java.io.Serializable;
import java.util.List;

import springapp.domain.MarketComponent;


/**
 * @author oliver
 * This is an interface used to get components for a given index.
 */
public interface ComponentManager extends Serializable{

	public List<MarketComponent> getComponents(String index);
	
}