package springapp.repository;

import java.util.List;

import springapp.domain.MarketComponent;

/**
 * @author oliver
 * This is an interface to fetch the components of an Index
 */
public interface ComponentDao {
	public List<MarketComponent> getIndexComponent(String index);
}
