
package fi.soveltia.liferay.gsearch.core.api.query.context;

import com.liferay.portal.kernel.search.Sort;

import java.util.HashMap;
import java.util.Map;

/**
 * Query context.
 * 
 * @author Petteri Karttunen
 */
public class QueryContext {

	public String[] getConfiguration(String key) {
		
		if (_configurations != null) {
			return _configurations.get(key);
		}
		return null;
	}
	
	public void setConfiguration(String key, String[] value) {
	
		if (_configurations == null) {
			_configurations = new HashMap<String, String[]>();
		}
		_configurations.put(key, value);
	}

	public int getEnd() {

		return _end;
	}

	public void setEnd(int end) {

		_end = end;
	}

	public String getKeywords() {

		return _keywords;
	}

	public void setKeywords(String keywords) {

		_keywords = keywords;
	}

	public String getOriginalKeywords() {

		return _originalKeywords;
	}

	public void setOriginalKeywords(String originalKeywords) {

		_originalKeywords = originalKeywords;
	}
	
	public Object getParameter(String key) {

		if (_parameters != null) {
			return _parameters.get(key);
		}
		return null;
	}

	public void setParameter(String key, Object value) {

		if (_parameters == null) {
			_parameters = new HashMap<String, Object>();
		}
		_parameters.put(key, value);
	}

	public int getPageSize() {

		return _pageSize;
	}

	public void setPageSize(int pageSize) {

		_pageSize = pageSize;
	}

	public boolean isQueryContributorsEnabled() {

		return _queryContributorsEnabled;
	}

	public void setQueryContributorsEnabled(boolean queryContributorsEnabled) {

		_queryContributorsEnabled = queryContributorsEnabled;
	}

	public boolean isQueryPostProcessorsEnabled() {

		return _queryPostProcessorsEnabled;
	}

	public void setQueryPostProcessorsEnabled(
		boolean queryPostProcessorsEnabled) {

		_queryPostProcessorsEnabled = queryPostProcessorsEnabled;
	}

	public Sort[] getSorts() {

		return _sorts;
	}

	public void setSorts(Sort[] sorts) {

		_sorts = sorts;
	}

	public int getStart() {

		return _start;
	}

	public void setStart(int start) {

		_start = start;
	}

	private String _keywords;
	private String _originalKeywords;
	
	private Map<String, String[]> _configurations;
	private Map<String, Object> _parameters;
	
	private boolean _queryContributorsEnabled = true;
	private boolean _queryPostProcessorsEnabled = true;

	// Paging and sorting

	private int _start;
	private int _end;
	private int _pageSize;
	private Sort[] _sorts;
	
}
