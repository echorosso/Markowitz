package springapp.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import springapp.domain.MarketComponent;

/**
 * @author oliver
 * This is an implementation of ComponentDao interface that fetches the 
 * components for an index from database.
 */
public class YahooComponentDao extends SimpleJdbcDaoSupport implements ComponentDao {
	protected final Log logger = LogFactory.getLog(getClass());
	
	public List<MarketComponent> getIndexComponent(String index){
		List<MarketComponent> components  = new ArrayList<MarketComponent>();
		try{
			components = getSimpleJdbcTemplate().query(
					"select * from market_indexes where index_ticker_symbol = :sym", 
					new MarketComponentMapper(),
					new MapSqlParameterSource().addValue("sym", index));
		}catch (Exception e){
			e.printStackTrace();
		}
		return components;
	}
	
	/**
	 * @author oliver
	 * Class that maps the record from for the database query to an object of 
	 * type MarketComponent
	 */
	private class MarketComponentMapper implements ParameterizedRowMapper<MarketComponent> {
		@Override
		public MarketComponent mapRow(ResultSet rs, int rowNum) throws SQLException {
			MarketComponent comp = new MarketComponent();
			comp.setId(rs.getInt("id"));
			comp.setIndexName(rs.getString("index_name"));
			comp.setIndexTickerSymbol(rs.getString("index_ticker_symbol"));
			comp.setComponentName(rs.getString("component_name"));
			comp.setComponentTickerSymbol(rs.getString("component_ticker_symbol"));
			comp.setCurrency(rs.getString("currency"));
			return comp;
		}
	}
}

