package fi.soveltia.liferay.gsearch.core.api.params;

import java.util.HashMap;
import java.util.Map;

/**
 * Filter parameter pojo.
 * 
 * @author Petteri Karttunen
 *
 */
public class FilterParameter {

	public FilterParameter(String fieldName) {

		_fieldName = fieldName;
	}

	public Object getAttribute(String key) {

		if (_attributes == null) {
			return null;
		}
		return _attributes.get(key);
	}
	
	public void setAttribute(String key, Object value) {

		if (_attributes == null) {
			_attributes = new HashMap<String, Object>();
		}
		_attributes.put(key, value);
	}
	
	public String getFieldName() {
		return _fieldName;
	}

	public void setFieldName(String fieldName) {
		_fieldName = fieldName;
	}
	
	Map<String, Object> _attributes;
	
	String _fieldName;
}