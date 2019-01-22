
package fi.soveltia.liferay.gsearch.core.api.params;

import java.util.List;

/**
 * Facet parameter pojo.
 * 
 * @author Petteri Karttunen
 */
public class FacetParameter {

	public FacetParameter(
		String fieldName, List<String> values, boolean allowMultipleValues,
		String multiValueOperator, String filterMode) {

		_fieldName = fieldName;
		_values = values; 
		_allowMultipleValues = allowMultipleValues;
		_multiValueOperator = multiValueOperator;
		_filterMode = filterMode;
	}

	public boolean isAllowMultipleValues() {

		return _allowMultipleValues;
	}

	public void setAllowMultipleValues(boolean allowMultipleValues) {

		_allowMultipleValues = allowMultipleValues;
	}

	public String getFieldName() {

		return _fieldName;
	}

	public void setFieldName(String fieldName) {

		_fieldName = fieldName;
	}

	public String getFilterMode() {

		return _filterMode;
	}

	public void setFilterMode(String filterMode) {

		_filterMode = filterMode;
	}

	public String getMultiValueOperator() {

		return _multiValueOperator;
	}

	public void setMultiValueOperator(String multiValueOperator) {

		_multiValueOperator = multiValueOperator;
	}

	public List<String>getValues() {

		return _values;
	}

	public void setValues(List<String> values) {

		_values = values;
	}

	boolean _allowMultipleValues;

	String _fieldName;

	String _filterMode;

	String _multiValueOperator;

	List<String> _values;
}
