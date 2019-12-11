
package fi.soveltia.liferay.gsearch.core.impl.query.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.util.PortalUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContextBuilder;

/**
 * Query params builder implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = QueryContextBuilder.class
)
public class QueryContextBuilderImpl implements QueryContextBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryContext buildQueryContext(
		HttpServletRequest httpServletRequest, long companyId, Locale locale,
		String keywords)
		throws Exception {

		QueryContext queryContext = new QueryContext();
		
		queryContext.setParameter(ParameterNames.HTTP_SERVLET_REQUEST, httpServletRequest);
		queryContext.setParameter(ParameterNames.COMPANY_ID, companyId);
		queryContext.setParameter(ParameterNames.LOCALE, locale);
		queryContext.setParameter(ParameterNames.KEYWORDS, keywords);
		queryContext.setPortalUrl(PortalUtil.getPortalURL(httpServletRequest));
		
		return queryContext;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryContext buildQueryContext(HttpServletRequest httpServletRequest,
		String[] filterConfiguration,
		String[] clauseConfiguration, String[] facetConfiguration,
		String[] sortConfiguration, String[] suggesterConfiguration, Locale locale)
		throws Exception {
		 
		QueryContext queryContext = new QueryContext();
		
		queryContext.setParameter(ParameterNames.HTTP_SERVLET_REQUEST, httpServletRequest);

		queryContext.setLocale(locale);
		queryContext.setPortalUrl(PortalUtil.getPortalURL(httpServletRequest));

		if (filterConfiguration == null) {
			filterConfiguration = _configurationHelper.getFilterConfiguration();
		}
		queryContext.setConfiguration(ConfigurationKeys.FILTER, filterConfiguration);

		if (clauseConfiguration == null) {
			clauseConfiguration = _configurationHelper.getClauseConfiguration();
		}
		queryContext.setConfiguration(ConfigurationKeys.CLAUSE, clauseConfiguration);

		if (facetConfiguration == null) {
			facetConfiguration = _configurationHelper.getFacetConfiguration();
		}
		queryContext.setConfiguration(ConfigurationKeys.FACET, facetConfiguration);

		if (sortConfiguration == null) {
			sortConfiguration = _configurationHelper.getSortConfiguration();
		}
		queryContext.setConfiguration(ConfigurationKeys.SORT, sortConfiguration);
		
		if (suggesterConfiguration == null) {
			suggesterConfiguration = _configurationHelper.getKeywordSuggesterConfiguration();
		}
		queryContext.setConfiguration(ConfigurationKeys.SUGGESTER, suggesterConfiguration);
		
		return queryContext;
	}

	@Override
	public QueryContext buildSuggesterQueryContext(
		HttpServletRequest httpServletRequest, String[] suggesterConfiguration, 
		long companyId, long groupId, Locale locale, String keywords)
		throws Exception {

		QueryContext queryContext = new QueryContext();
		
		queryContext.setParameter(ParameterNames.HTTP_SERVLET_REQUEST, httpServletRequest);

		queryContext.setLocale(httpServletRequest.getLocale());
		queryContext.setPortalUrl(PortalUtil.getPortalURL(httpServletRequest));

		if (suggesterConfiguration == null) {
			queryContext.setConfiguration(ConfigurationKeys.SUGGESTER,
				_configurationHelper.getKeywordSuggesterConfiguration());
		}
		
		queryContext.setParameter(ParameterNames.COMPANY_ID, companyId);
		queryContext.setParameter(ParameterNames.GROUP_ID, groupId);
		queryContext.setParameter(ParameterNames.LOCALE, locale);
		queryContext.setKeywords(keywords);
		
		return queryContext;
	}
	
	@Override
	public void parseParameters(QueryContext queryContext, Map<String, Object> parameterMap)
		throws Exception {

		// Run parameter builders

		if (_parameterBuilders == null) {
			throw new RuntimeException("No parameter builders.");
		}

		if (parameterMap == null) {
			return;
		}

		for (ParameterBuilder parameterBuilder : _parameterBuilders) {

			if (parameterBuilder.validate(queryContext, parameterMap)) {
				parameterBuilder.addParameter(queryContext, parameterMap);
			}
		}
	}	

	@Override
	public void parseParameters(QueryContext queryContext)
		throws Exception {

		// Run parameter builders

		if (_parameterBuilders == null) {
			throw new RuntimeException("No parameter builders.");
		}

		for (ParameterBuilder parameterBuilder : _parameterBuilders) {

			if (parameterBuilder.validate(queryContext)) {
				parameterBuilder.addParameter(queryContext);
			}
		}
	}	
	
	/**
	 * Add parameter builder.
	 * 
	 */
	protected void addParameterBuilder(ParameterBuilder parameterBuilder) {

		if (_parameterBuilders == null) {
			_parameterBuilders = new ArrayList<ParameterBuilder>();
		}
		_parameterBuilders.add(parameterBuilder);
	}

	/**
	 * Remove parameter builder.
	 * 
	 * @param parameterBuilder
	 */
	protected void removeParameterBuilder(ParameterBuilder parameterBuilder) {

		_parameterBuilders.remove(parameterBuilder);
	}

	@Reference(
		bind = "addParameterBuilder", 
		cardinality = ReferenceCardinality.MULTIPLE, 
		policy = ReferencePolicy.DYNAMIC, 
		service = ParameterBuilder.class, 
		unbind = "removeParameterBuilder"
	)
	private volatile List<ParameterBuilder> _parameterBuilders = null;

	@Reference
	private ConfigurationHelper _configurationHelper;
}
