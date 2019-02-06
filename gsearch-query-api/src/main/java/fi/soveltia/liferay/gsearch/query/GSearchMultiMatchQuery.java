
package fi.soveltia.liferay.gsearch.query;

import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.generic.MultiMatchQuery;

import java.util.HashMap;
import java.util.Map;

/**
 * This extension of MultiMatchQuery add the missing field level boost setter.
 * 
 * @author Petteri Karttunen
 */
public class GSearchMultiMatchQuery extends MultiMatchQuery implements Query {

	private static final long serialVersionUID = 1L;

	public GSearchMultiMatchQuery(String value) {

		super(value);
	}

	public void addField(String field, float boost) {

		super.addField(field);
		_fieldsBoosts.put(field, boost);
	}
	
	public Map<String, Float> getFieldsBoosts() {
		return _fieldsBoosts;
	}

	public boolean isFieldBoostsEmpty() {
		return _fieldsBoosts.isEmpty();
	}
	
	private final Map<String, Float> _fieldsBoosts = new HashMap<>();
}
