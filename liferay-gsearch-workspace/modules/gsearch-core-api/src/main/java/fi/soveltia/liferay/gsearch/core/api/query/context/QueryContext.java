
package fi.soveltia.liferay.gsearch.core.api.query.context;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.kernel.util.StringBundler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.soveltia.liferay.gsearch.core.api.params.FacetParameter;
import fi.soveltia.liferay.gsearch.core.api.params.FilterParameter;

/**
 * Query context pojo.
 *
 * @author Petteri Karttunen
 */
public class QueryContext {

	public void addConfigurationVariable(String variable, String value) {
		if (_configurationVariables == null) {
			_configurationVariables = new HashMap<String, String>();
		}

		_configurationVariables.put(variable, value);
	}
	
	public void addFacetParameters(FacetParameter facetParameter) {
		if (_facetParameters == null) {
			_facetParameters = new ArrayList<>();
		}

		_facetParameters.add(facetParameter);
	}

	public void addFilterParameter(
		String key, FilterParameter filterParameter) {

		if (_filterParameters == null) {
			_filterParameters = new HashMap<>();
		}

		_filterParameters.put(key, filterParameter);
	}
	
	public Object getConfiguration(String key) {
		if (_configurations != null) {
			return _configurations.get(key);
		}

		return null;
	}
	
	public Object getConfigurationVariable(String key) {

		if (_configurationVariables != null) {
			return _configurationVariables.get(key);
		}

		return null;
	}

	public Map<String, String> getConfigurationVariables() {
		return _configurationVariables;
	}

	public List<FacetParameter> getFacetParameters() {
		return _facetParameters;
	}

	public FilterParameter getFilterParameter(String key) {
		if (_filterParameters != null) {
			return _filterParameters.get(key);
		}

		return null;
	}

	public String getKeywords() {
		return _keywords;
	}

	public String getOriginalKeywords() {
		return _originalKeywords;
	}

	public Integer getPageSize() {
		return _pageSize;
	}

	public Object getParameter(String key) {
		if (_parameters != null) {
			return _parameters.get(key);
		}

		return null;
	}

	public Sort[] getSorts() {
		return _sorts;
	}

	public Integer getStart() {
		return _start;
	}

	public boolean isQueryContributorsEnabled() {
		return _queryContributorsEnabled;
	}

	public boolean isQueryPostProcessorsEnabled() {
		return _queryPostProcessorsEnabled;
	}

	public void setConfiguration(String key, Object value) {
		if (_configurations == null) {
			_configurations = new HashMap<>();
		}

		_configurations.put(key, value);
	}

	public void setFacetParameters(List<FacetParameter> facetParameters) {
		_facetParameters = facetParameters;
	}

	public void setKeywords(String keywords) {
		_keywords = keywords;
	}

	public void setOriginalKeywords(String originalKeywords) {
		_originalKeywords = originalKeywords;
	}

	public void setPageSize(int pageSize) {
		_pageSize = pageSize;
	}

	public void setParameter(String key, Object value) {
		if (_parameters == null) {
			_parameters = new HashMap<>();
		}

		_parameters.put(key, value);
	}

	public void setQueryContributorsEnabled(boolean queryContributorsEnabled) {
		_queryContributorsEnabled = queryContributorsEnabled;
	}

	public void setQueryPostProcessorsEnabled(
		boolean queryPostProcessorsEnabled) {

		_queryPostProcessorsEnabled = queryPostProcessorsEnabled;
	}

	public void setSorts(Sort[] sorts) {
		_sorts = sorts;
	}

	public void setStart(int start) {
		_start = start;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("Keywords: ");
		sb.append(_keywords);
		sb.append("Original keywords: ");
		sb.append(_originalKeywords);
		sb.append("Start: ");
		sb.append(_start);
		sb.append("Page size: ");
		sb.append(_pageSize);

		if (!_parameters.isEmpty()) {
			sb.append("Parameters:");
			sb.append("===========");

			for (Map.Entry<String, Object> entry : _parameters.entrySet()) {
				sb.append(entry.getKey());
				sb.append(":");
				sb.append(entry.getValue());
			}
		}

		return sb.toString();
	}

	private Map<String, String> _configurationVariables;
	private Map<String, Object> _configurations;
	private List<FacetParameter> _facetParameters;
	private Map<String, FilterParameter> _filterParameters;
	private String _keywords;
	private String _originalKeywords;
	private Integer _pageSize;
	private Map<String, Object> _parameters;
	private boolean _queryContributorsEnabled = true;
	
	// Paging and sorting

	private boolean _queryPostProcessorsEnabled = true;
	private Sort[] _sorts;
	private Integer _start;

}