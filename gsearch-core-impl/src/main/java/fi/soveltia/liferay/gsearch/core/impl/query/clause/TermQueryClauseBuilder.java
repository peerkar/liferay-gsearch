
package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

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

		String fieldName = configuration.getString("field_name");

		if (fieldName == null) {
			return null;
		}

		String keywords = null;

		if (Validator.isNotNull(configuration.get("query"))) {

			keywords = configuration.getString("query");

			keywords = _configurationHelper.parseConfigurationVariables(
				queryContext, keywords);
		}

		if (Validator.isNull(keywords)) {
			keywords = queryContext.getKeywords();
		}
		
		TermQuery termQuery = new TermQueryImpl(fieldName, keywords);

		// Boost
		
		if (Validator.isNotNull(configuration.get("boost"))) {
			termQuery.setBoost(
				GetterUtil.getFloat(configuration.get("boost")));
		}

		return termQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBuild(String querytype) {

		return (querytype.equals(QUERY_TYPE));
	}

	private static final String QUERY_TYPE = "term";
	
	@Reference
	private ConfigurationHelper _configurationHelper;	
}
