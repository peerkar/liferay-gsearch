package fi.soveltia.liferay.gsearch.query;

import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.StringQuery;

import java.util.List;
import java.util.Map;

import fi.soveltia.liferay.gsearch.query.core.StringQueryImpl;

/**
 * This is an extension of standard Liferay MultiMatchQuery.
 * 
 * We are implementing the StringQuery to
 * avoid extending the QueryVisitor interface and generally, to minimize
 * customizations to search adapter and search API.
 *  
 * The query translator for StringQuery is overridden in the adapter, dispatching the 
 * custom query to its custom translator. 
 * 
 * @author Petteri Karttunen
 */
public class GSearchLTRQuery extends StringQueryImpl
  implements Query, StringQuery {

	private static final long serialVersionUID = 1L;
	
	public GSearchLTRQuery(String query, 
			Map<String, Object>params, String model, List<String>activeFeatures) {
		super(null);
		
		_activeFeatures = activeFeatures;
		_model = model;
		_params = params;
		_query = query;
	}
	
	public List<String> getActiveFeatures() {
		return _activeFeatures;
	}

	public void setActiveFeatures(List<String> activeFeatures) {
		_activeFeatures = activeFeatures;
	}

	public String getModel() {
		return _model;
	}

	public void setModel(String model) {
		_model = model;
	}

	public Map<String, Object> getParams() {
		return _params;
	}

	public void setParams(Map<String, Object> params) {
		_params = params;
	}
	
	public String getQuery() {
		return _query;
	}

	public void setQuery(String query) {
		_query = query;
	}

	private List<String> _activeFeatures;
	private String _model;
	private Map<String, Object> _params;
	private String _query;
}