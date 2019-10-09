
package fi.soveltia.liferay.gsearch.query;

import com.liferay.portal.search.query.MultiMatchQuery;
import com.liferay.portal.search.query.Query;

import java.util.HashMap;
import java.util.Map;

import fi.soveltia.liferay.gsearch.query.core.MultiMatchQueryImpl;

/**
 * This is an extension of standard Liferay MultiMatchQuery.
 * 
 * Adds the missing field level boost setter on the API level.
 * (Getter already exists in the standard adapter).
 *
 * @author Petteri Karttunen
 */
public class GSearchMultiMatchQuery 
	extends MultiMatchQueryImpl implements Query, MultiMatchQuery {

	private static final long serialVersionUID = 1L;
	
	public GSearchMultiMatchQuery(
			Object value, Map<String, Float> fieldsWithboosts) {
		
		super(value, fieldsWithboosts.keySet());
		_fieldBoosts = fieldsWithboosts;
	}
		
	@Override
	public Map<String, Float> getFieldsBoosts() {
		return _fieldBoosts;
	}

	@Override
	public boolean isFieldBoostsEmpty() {
		return _fieldBoosts.isEmpty();
	}

	public void setFieldBoosts (Map<String, Float> fieldBoosts) {
		_fieldBoosts = fieldBoosts;
	}
	
	private Map<String, Float> _fieldBoosts = new HashMap<String, Float>();

}