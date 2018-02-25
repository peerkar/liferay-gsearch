package fi.soveltia.liferay.gsearch.core.impl.query.builder;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.query.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.query.builder.TermQueryBuilder;

/**
 * MatchQuery builder service implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = TermQueryBuilder.class
)
public class TermQueryBuilderImpl implements TermQueryBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TermQuery buildQuery(
		JSONObject configurationObject, QueryParams queryParams)
		throws Exception {

		String fieldName = configurationObject.getString("fieldName");

		if (fieldName == null) {
			return null;
		}

		// If there's a predefined value in the configuration, use that

		String value = configurationObject.getString("value");

		if (Validator.isNull(value)) {
			value = queryParams.getKeywords();
		}
		
		TermQuery termQuery =
			new TermQueryImpl(fieldName, value);
		
		float boost =
			GetterUtil.getFloat(configurationObject.get("boost"), 1.0f);
		termQuery.setBoost(boost);
		
		return termQuery;
	}
}