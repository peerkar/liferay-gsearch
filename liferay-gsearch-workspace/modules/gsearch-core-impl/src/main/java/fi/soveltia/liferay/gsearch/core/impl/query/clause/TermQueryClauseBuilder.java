
package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.TermQuery;

import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationValues;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Term query builder.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseBuilder.class
)
public class TermQueryClauseBuilder implements ClauseBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query buildClause(
			QueryContext queryContext, JSONObject configuration)
		throws Exception {

		String fieldName = configuration.getString(ClauseConfigurationKeys.FIELD_NAME);

		if (fieldName == null) {
			return null;
		}

		String keywords = (String)configuration.get(ClauseConfigurationKeys.QUERY);
		
		if (Validator.isBlank(keywords)) {
			keywords = queryContext.getKeywords();
		}
		
		TermQuery termQuery = _queries.term(fieldName, keywords);

		// Boost

		if (Validator.isNotNull(configuration.get(ClauseConfigurationKeys.BOOST))) {
			termQuery.setBoost(GetterUtil.getFloat(configuration.get(ClauseConfigurationKeys.BOOST)));
		}

		return termQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBuild(String querytype) {
		return querytype.equals(_QUERY_TYPE);
	}

	public static final String _QUERY_TYPE =
			ClauseConfigurationValues.QUERY_TYPE_TERM;

	@Reference
	private CoreConfigurationHelper _coreConfigurationHelper;

	@Reference
	private Queries _queries;

}