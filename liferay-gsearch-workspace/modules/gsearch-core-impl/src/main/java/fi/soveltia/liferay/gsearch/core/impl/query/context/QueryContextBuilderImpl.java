
package fi.soveltia.liferay.gsearch.core.impl.query.context;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.http.HttpAuthManagerUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationNames;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContextBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.contributor.QueryContextContributor;
import fi.soveltia.liferay.gsearch.core.impl.util.GSearchUtil;

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
			HttpServletRequest httpServletRequest,
			Locale locale, String keywords)
		throws Exception {

		QueryContext queryContext = new QueryContext();
		
		setBasicParameters(httpServletRequest, queryContext, locale, keywords);

		return queryContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryContext buildQueryContext(
			HttpServletRequest httpServletRequest, Locale locale, JSONArray filterConfiguration,
			JSONArray clauseConfiguration, JSONArray facetConfiguration,
			JSONArray sortConfiguration, JSONArray suggesterConfiguration,
			JSONArray rescorerConfiguration, String keywords)
		throws Exception {

		QueryContext queryContext = new QueryContext();

		setBasicParameters(httpServletRequest, queryContext, locale, keywords);

		if (filterConfiguration == null) {
			filterConfiguration = _coreConfigurationHelper.getFilters();
		}

		queryContext.setConfiguration(
			ConfigurationNames.FILTER, filterConfiguration);

		if (clauseConfiguration == null) {
			clauseConfiguration = _coreConfigurationHelper.getClauses();
		}

		queryContext.setConfiguration(
			ConfigurationNames.CLAUSE, clauseConfiguration);

		if (facetConfiguration == null) {
			facetConfiguration = _coreConfigurationHelper.getFacets();
		}

		queryContext.setConfiguration(
			ConfigurationNames.FACET, facetConfiguration);

		if (sortConfiguration == null) {
			sortConfiguration = _coreConfigurationHelper.getSorts();
		}

		queryContext.setConfiguration(
				ConfigurationNames.SORT, sortConfiguration);

		if (rescorerConfiguration == null) {
			rescorerConfiguration = _coreConfigurationHelper.getRescoreClauses();
		}

		queryContext.setConfiguration(
				ConfigurationNames.RESCORE, rescorerConfiguration);

		if (suggesterConfiguration == null) {
			suggesterConfiguration =
				_coreConfigurationHelper.getKeywordSuggesters();
		}

		queryContext.setConfiguration(
			ConfigurationNames.SUGGESTER, suggesterConfiguration);

		return queryContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryContext buildSuggesterQueryContext(
			HttpServletRequest httpServletRequest,
			JSONArray suggesterConfiguration, long groupId,
			Locale locale, String keywords)
		throws Exception {

		QueryContext queryContext = new QueryContext();

		setBasicParameters(httpServletRequest, queryContext, locale, keywords);

		if (suggesterConfiguration == null) {
			queryContext.setConfiguration(
				ConfigurationNames.SUGGESTER,
				_coreConfigurationHelper.getKeywordSuggesters());
		}

		queryContext.setParameter(ParameterNames.GROUP_ID, groupId);

		return queryContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parseParameters(QueryContext queryContext) throws Exception {

		// Run parameter builders

		if (_parameterBuilders == null) {
			throw new RuntimeException("No parameter builders available.");
		}

		for (ParameterBuilder parameterBuilder : _parameterBuilders) {
			if (parameterBuilder.validate(queryContext)) {
				parameterBuilder.addParameter(queryContext);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parseParametersHeadless(
			QueryContext queryContext, Map<String, Object> parameterMap)
		throws Exception {

		// Run parameter builders

		if (_parameterBuilders == null) {
			throw new RuntimeException("No parameter builders available.");
		}

		if (parameterMap == null) {
			return;
		}

		for (ParameterBuilder parameterBuilder : _parameterBuilders) {
			if (parameterBuilder.validateHeadless(queryContext, parameterMap)) {
				parameterBuilder.addParameterHeadless(queryContext, parameterMap);
			}
		}
	}
	
	@Override
	public void processQueryContextContributor(QueryContext queryContext, String name)
			throws Exception {
		
		for (QueryContextContributor queryContextContributor : _queryContextContributors) {
			if (queryContextContributor.getName().equals(name)) {
				queryContextContributor.contribute(queryContext);
			}
		}
	}

	@Override
	public void processQueryContextContributors(QueryContext queryContext)
			throws Exception {

		for (QueryContextContributor queryContextContributor : _queryContextContributors) {
			queryContextContributor.contribute(queryContext);
		}
		
		if (_log.isDebugEnabled()) {

			_log.debug("Configuration variables in the query context:");
			
			if (queryContext.getConfigurationVariables() != null) {
				for (Entry<String, String>entry : queryContext.getConfigurationVariables().entrySet()) {
					_log.debug(entry.getKey() + ":" + entry.getValue());
				}
			} else {
				_log.debug("No variables exist.");
			}
		}
	}
	
	/**
	 * Adds keywords to query context.
	 *
	 * @param queryContext
	 * @param keywords
	 */
	protected void addKeywords(QueryContext queryContext, String keywords) {

		// We can also have empty keywords (recommender)
		
		if (Validator.isBlank(keywords)) {
			queryContext.setKeywords(keywords);
			return;
		}
		
		// Try to check if we have a Lucene style search eg. userName:foo
		// title:bar and don't lowercase the field names.
		// Also, don't lowercase BOOLEAN uppercase operators.

		StringBundler sb = new StringBundler();

		// Remove quotes id there's an uneven count of them.
		
		int quoteCount = StringUtils.countMatches(keywords, "\"");
		
		if (quoteCount % 2 != 0) {
			keywords = keywords.replaceAll("\"", "");
		}
		
		// Encode.
		
		String encodedKeywords = GSearchUtil.encodeKeywords(keywords);
				
		String[] keywordsArray = encodedKeywords.split(" ");

		for (String s : keywordsArray) {
			String[] fields = s.split(":");

			if (fields.length == 2) {
				sb.append(fields[0]);
				sb.append(":");
				sb.append(fields[1].toLowerCase());
			}
			else if (s.equals("AND") || s.equals("OR") || s.equals("NOT")) {
				sb.append(s);
			}
			else {
				sb.append(s.toLowerCase());
			}

			sb.append(" ");
		}

		queryContext.setKeywords(
			sb.toString(
			).trim());

		// Also store raw keywords as we might need them to store keyword suggestions.
		
		queryContext.setParameter(ParameterNames.RAW_KEYWORDS, keywords);

	}
	
	/**
	 * Adds parameter builder.
	 *
	 * @param clauseBuilder
	 */
	protected void addParameterBuilder(ParameterBuilder parameterBuilder) {
		if (_parameterBuilders == null) {
			_parameterBuilders = new ArrayList<>();
		}

		_parameterBuilders.add(parameterBuilder);
	}

	/**
	 * Adds query context contributor
	 * 
	 * @param queryContextContributor
	 */
	protected void addQueryContextContributor(QueryContextContributor queryContextContributor) {
		if (_queryContextContributors == null) {
			_queryContextContributors = new ArrayList<>();
		}

		_queryContextContributors.add(queryContextContributor);
	}
	
	/**
	 * Removes parameter builder.
	 *
	 * @param parameterBuilder
	 */
	protected void removeParameterBuilder(ParameterBuilder parameterBuilder) {
		_parameterBuilders.remove(parameterBuilder);
	}
	
	/**
	 * Removes query context contributor.
	 * 
	 * @param queryContextContributor
	 */
	protected void removeQueryContextContributor(
			QueryContextContributor queryContextContributor) {
		_queryContextContributors.remove(queryContextContributor);
	}
	
	/**
	 * Sets basic QueryContext parameters.
	 * 
	 * @param httpServletRequest
	 * @param queryContext
	 * @param locale
	 * @param keywords
	 */
	protected void setBasicParameters(HttpServletRequest httpServletRequest, 
			QueryContext queryContext, Locale locale, String keywords) {
		
		PortletRequest portletRequest =
				GSearchUtil.getPortletRequest(httpServletRequest);
		
		if (portletRequest != null) {
			
			ThemeDisplay themeDisplay = 
					(ThemeDisplay)portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
			queryContext.setParameter(ParameterNames.USER, themeDisplay.getUser());
			queryContext.setParameter(ParameterNames.COMPANY_ID, themeDisplay.getCompanyId());
		
		} else {
			
			User user = (User)httpServletRequest.getAttribute("USER");		
			long companyId = (long)httpServletRequest.getAttribute("COMPANY_ID");

			if (user == null) {
				user = _getUserFromBasicAuth(httpServletRequest);
				companyId = user.getCompanyId();
			}
			
			queryContext.setParameter(ParameterNames.USER, user);
			queryContext.setParameter(ParameterNames.COMPANY_ID, companyId);

		}
		
		addKeywords(queryContext, keywords);

		queryContext.setParameter(ParameterNames.LOCALE, locale);

		queryContext.setParameter(
				ParameterNames.HTTP_SERVLET_REQUEST, httpServletRequest);
		
		queryContext.setConfiguration(
				ConfigurationNames.RESULT_DESCRIPTION_MAX_LENGTH, 
				_coreConfigurationHelper.getDescriptionMaxLength());
	}

	/**
	 * Tries to retrieve used from Basic Auth headers.
	 * 
	 * @return
	 */
	private User _getUserFromBasicAuth(HttpServletRequest httpServletRequest) {
		
		String authHeader =  httpServletRequest.getHeader("authorization");

		try {
		if (authHeader.startsWith("Basic "));

			long userId = HttpAuthManagerUtil.getBasicUserId(httpServletRequest);
			return _userLocalService.getUser(userId);
			
		} catch (Exception e) {
			_log.error("Error in resolving user from authorization header (" + 
					authHeader + ")", e);
		}
		
		return null;
	}

	private static final Logger _log = LoggerFactory.getLogger(
			QueryContextBuilderImpl.class);

	@Reference
	private CoreConfigurationHelper _coreConfigurationHelper;

	@Reference(
		bind = "addParameterBuilder",
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC, service = ParameterBuilder.class,
		unbind = "removeParameterBuilder"
	)
	private volatile List<ParameterBuilder> _parameterBuilders = null;
	
	@Reference(
		bind = "addQueryContextContributor", 
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC, 
		service = QueryContextContributor.class,
		unbind = "removeQueryContextContributor"
	)
	private volatile List<QueryContextContributor> _queryContextContributors = null;
	
	@Reference
	UserLocalService _userLocalService;

}