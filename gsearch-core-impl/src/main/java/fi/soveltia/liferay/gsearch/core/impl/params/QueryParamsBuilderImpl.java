
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.core.api.facet.translator.FacetTranslator;
import fi.soveltia.liferay.gsearch.core.api.facet.translator.FacetTranslatorFactory;
import fi.soveltia.liferay.gsearch.core.api.params.FacetParam;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParamsBuilder;
import fi.soveltia.liferay.gsearch.core.api.params.RequestParamValidator;
import fi.soveltia.liferay.gsearch.core.impl.exception.KeywordsException;

/**
 * Query params builder implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = QueryParamsBuilder.class
)
public class QueryParamsBuilderImpl implements QueryParamsBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryParams buildQueryParams(
		PortletRequest portletRequest, int pageSize)
		throws Exception {

		return buildQueryParams(portletRequest, null, null, null, null, pageSize);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryParams buildQueryParams(
		PortletRequest portletRequest, String[] assetTypeConfiguration,
		String[] clauseConfiguration, String[] facetConfiguration,
		String[] sortConfiguration, int pageSize)
		throws Exception {

		QueryParams queryParams = new QueryParams();

		queryParams.setPageSize(pageSize);

		setConfigurations(queryParams, 
			assetTypeConfiguration, clauseConfiguration, facetConfiguration,
			sortConfiguration);
		
		setCompanyParam(portletRequest, queryParams);
		setGroupsParam(portletRequest, queryParams);
		setLocaleParam(portletRequest, queryParams);
		setUserParam(portletRequest, queryParams);

		setKeywordsParam(portletRequest, queryParams);
		setTimeParam(portletRequest, queryParams);
		setAssetTypeParam(portletRequest, queryParams);
		setFacetParams(portletRequest, queryParams);
		setStatusParam(portletRequest, queryParams);

		setStartEndParams(portletRequest, queryParams);

		setSortParam(portletRequest, queryParams);

		return queryParams;
	}

	/**
	 * Parse entry classname corresponding the key.
	 * 
	 * @param key
	 * @param configuration
	 * @return
	 * @throws JSONException
	 * @throws ClassNotFoundException
	 */
	protected String parseEntryClass(String key, String[] configuration)
		throws JSONException, ClassNotFoundException {

		String className = null;

		for (int i = 0; i < configuration.length; i++) {

			JSONObject item =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			if (key.equals(item.getString("key"))) {

				className = item.getString("entry_class_name");
				break;
			}
		}

		return className;
	}

	/**
	 * Parse default set of asset class names to search for.
	 * 
	 * @param configuration
	 * @return
	 * @throws ClassNotFoundException
	 * @throws JSONException
	 */
	protected List<String> parseDefaultEntryClasses(String[] configuration)
		throws ClassNotFoundException, JSONException {

		List<String> classNames = new ArrayList<String>();

		for (int i = 0; i < configuration.length; i++) {

			JSONObject item =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			classNames.add(item.getString("entry_class_name"));
		}

		return classNames;
	}

	/**
	 * Set asset types (entryClassName).
	 * 
	 * @param portletRequest
	 * @param queryParams
	 * @param assetTypeConfiguration
	 * @throws PatternSyntaxException
	 * @throws ClassNotFoundException
	 * @throws JSONException
	 */
	protected void setAssetTypeParam(
		PortletRequest portletRequest, QueryParams queryParams)
		throws PatternSyntaxException, ClassNotFoundException, JSONException {

		String typeFilter =
			ParamUtil.getString(portletRequest, GSearchWebKeys.FILTER_TYPE);

		List<String> entryClassNames = new ArrayList<String>();

		String className = parseEntryClass(typeFilter, queryParams.getAssetTypeConfiguration());

		if (className != null) {
			entryClassNames.add(className);
		}
		else {
			entryClassNames.addAll(
				parseDefaultEntryClasses(queryParams.getAssetTypeConfiguration()));
		}

		queryParams.setEntryClassNames(entryClassNames);
	}

	/**
	 * Set company parameter.
	 * 
	 * @param portletRequest
	 * @param queryParams
	 */
	protected void setCompanyParam(
		PortletRequest portletRequest, QueryParams queryParams) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		queryParams.setCompanyId(themeDisplay.getCompanyId());
	}

	/**
	 * Set configurations.
	 * 
	 * @param assetTypeConfiguration
	 * @param clauseConfiguration
	 * @param facetConfiguration
	 * @param sortConfiguration
	 */
	protected void setConfigurations(QueryParams queryParams, 
		String[] assetTypeConfiguration, String[] clauseConfiguration,
		String[] facetConfiguration, String[] sortConfiguration) {

		if (assetTypeConfiguration == null) {
			queryParams.setAssetTypeConfiguration(
				_configurationHelper.getAssetTypeConfiguration());
		}

		if (clauseConfiguration == null) {
			queryParams.setClauseConfiguration(
				_configurationHelper.getClauseConfiguration());
		}

		if (facetConfiguration == null) {
			queryParams.setFacetConfiguration(
				_configurationHelper.getFacetConfiguration());
		}

		if (sortConfiguration == null) {
			queryParams.setSortConfiguration(
				_configurationHelper.getSortConfiguration());
		}
	}

	/**
	 * Set facet parameters.
	 * 
	 * @throws JSONException
	 */
	protected void setFacetParams(
		PortletRequest portletRequest, QueryParams queryParams)
		throws JSONException {

		Map<FacetParam, BooleanClauseOccur> facetParams =
			new HashMap<FacetParam, BooleanClauseOccur>();

		String fieldParam;
		String fieldName;
		String[] fieldValues;
		boolean isMultiValued;
		String multiValueOperator;
		BooleanClauseOccur multiValueOccur = null;
		
		String[] configuration = queryParams.getFacetConfiguration();

		for (int i = 0; i < configuration.length; i++) {

			JSONObject facetConfiguration =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			isMultiValued = GetterUtil.getBoolean(
				facetConfiguration.getString("is_multivalued"), false);

			fieldParam = facetConfiguration.getString("param_name");

			fieldName = facetConfiguration.getString("field_name");

			multiValueOperator =
				facetConfiguration.getString("multivalue_operator");

			if (Validator.isNotNull(multiValueOperator)) {

				if ("or".equalsIgnoreCase(multiValueOperator)) {
					multiValueOccur = BooleanClauseOccur.SHOULD;
				}
				else {
					multiValueOccur = BooleanClauseOccur.MUST;
				}
			}
			else {
				multiValueOccur = BooleanClauseOccur.SHOULD;
			}

			fieldValues = ParamUtil.getStringValues(portletRequest, fieldParam);

			if (Validator.isNotNull(fieldValues) && fieldValues.length > 0) {

				FacetTranslator translator =
					_facetTranslatorFactory.getTranslator(fieldName);

				if (translator != null) {

					if (isMultiValued) {
						for (String fieldValue : fieldValues) {

							String[] values = translator.translateParams(
								fieldValue, facetConfiguration);

							FacetParam facetParam = new FacetParam(
								fieldName, values, multiValueOccur);
							facetParams.put(
								facetParam, BooleanClauseOccur.MUST);
						}
					}
					else {
						String[] values = translator.translateParams(
							fieldValues[0], facetConfiguration);

						FacetParam facetParam = new FacetParam(
							fieldName, values, BooleanClauseOccur.SHOULD);
						facetParams.put(facetParam, BooleanClauseOccur.MUST);
					}
				}
				else {

					if (isMultiValued) {

						FacetParam facetParam = new FacetParam(
							fieldName, fieldValues, multiValueOccur);
						facetParams.put(facetParam, BooleanClauseOccur.SHOULD);

					}
					else {

						FacetParam facetParam = new FacetParam(
							fieldName, new String[] {
								fieldValues[0]
							}, BooleanClauseOccur.MUST);
						facetParams.put(facetParam, BooleanClauseOccur.MUST);
					}
				}
			}
		}
		queryParams.setFacetParams(facetParams);
	}

	/**
	 * Set groups parameter.
	 * 
	 * @param portletRequest
	 * @param queryParams
	 */
	protected void setGroupsParam(
		PortletRequest portletRequest, QueryParams queryParams) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		String scopeFilter =
			ParamUtil.getString(portletRequest, GSearchWebKeys.FILTER_SCOPE);

		long[] groupIds;

		if ("this-site".equals(scopeFilter)) {
			groupIds = new long[] {
				themeDisplay.getScopeGroupId()
			};
		}
		else {
			groupIds = new long[] {};
		}
		queryParams.setGroupIds(groupIds);
	}

	/**
	 * Set keywords parameter.
	 * 
	 * @param portletRequest
	 * @param queryParams
	 * @throws KeywordsException
	 */
	protected void setKeywordsParam(
		PortletRequest portletRequest, QueryParams queryParams)
		throws KeywordsException {

		String keywords =
			ParamUtil.getString(portletRequest, GSearchWebKeys.KEYWORDS);

		// Validate keywords.

		if (!_requestParamValidator.validateKeywords(keywords)) {
			throw new KeywordsException();
		}
		
		// Lowercase
		
		keywords = keywords.toLowerCase();
		
		queryParams.setKeywords(keywords);
	}

	/**
	 * Set locale parameter.
	 * 
	 * @param portletRequest
	 * @param queryParams
	 */
	protected void setLocaleParam(
		PortletRequest portletRequest, QueryParams queryParams) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		queryParams.setLocale(themeDisplay.getLocale());
	}

	/**
	 * Set sort. Default sort field equals to score.
	 * 
	 * @throws JSONException
	 */
	protected void setSortParam(
		PortletRequest portletRequest, QueryParams queryParams)
		throws Exception {

		String sortField =
			ParamUtil.getString(portletRequest, GSearchWebKeys.SORT_FIELD);

		String sortDirection =
			ParamUtil.getString(portletRequest, GSearchWebKeys.SORT_DIRECTION);

		Sort sort1 = null;
		Sort sort2 = null;

		boolean reverse;

		if ("desc".equals(sortDirection)) {
			reverse = true;
		}
		else {
			reverse = false;
		}

		String defaultFieldName = null;
		Integer defaultFieldType = 0;

		String fieldName = null;
		Integer fieldType = null;

		String[] configuration = queryParams.getSortConfiguration();
		
		for (int i = 0; i < configuration.length; i++) {

			JSONObject item =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			if (item.getString("key").equals(sortField)) {

				fieldName = _configurationHelper.parseConfigurationVariables(
					portletRequest, queryParams, item.getString("field_name"));

				fieldType = Integer.valueOf(item.getString("field_type"));

				break;

			}
			else if (item.getBoolean("default")) {

				defaultFieldName =
					_configurationHelper.parseConfigurationVariables(
						portletRequest, queryParams,
						item.getString("field_name"));

				defaultFieldType =
					Integer.valueOf(item.getString("field_type"));
			}
		}

		if (fieldName == null || fieldType == null) {

			fieldName = defaultFieldName;
			fieldType = defaultFieldType;
		}

		sort1 = new Sort(fieldName, fieldType, reverse);

		// If primary sort is score, use modified as secondary
		// Use score as secondary for other primary sorts

		if (Validator.isNull(fieldName) || "_score".equals(fieldName)) {

			sort2 = new Sort(MODIFIED_SORT_FIELD, Sort.LONG_TYPE, reverse);

		}
		else {

			sort2 = new Sort(null, Sort.SCORE_TYPE, reverse);
		}

		queryParams.setSorts(
			new Sort[] {
				sort1, sort2
			});
	}

	/**
	 * Set start and end parameter.
	 */
	protected void setStartEndParams(
		PortletRequest portletRequest, QueryParams queryParams) {

		int start =
			ParamUtil.getInteger(portletRequest, GSearchWebKeys.START, 0);
		queryParams.setStart(start);

		queryParams.setEnd(start + queryParams.getPageSize());
	}

	/**
	 * Set start and end parameter.
	 */
	protected void setStatusParam(
		PortletRequest portletRequest, QueryParams queryParams) {

		int status =
			ParamUtil.getInteger(portletRequest, GSearchWebKeys.STATUS, 0);
		queryParams.setStatus(status);
	}

	/**
	 * Set time parameter (modification date between).
	 */
	protected void setTimeParam(
		PortletRequest portletRequest, QueryParams queryParams) {

		String timeFilter =
			ParamUtil.getString(portletRequest, GSearchWebKeys.FILTER_TIME);

		Date timeFrom = null;

		if ("last-day".equals(timeFilter)) {

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			timeFrom = calendar.getTime();

		}
		else if ("last-hour".equals(timeFilter)) {

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.HOUR_OF_DAY, -1);
			timeFrom = calendar.getTime();

		}
		else if ("last-month".equals(timeFilter)) {

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -1);
			timeFrom = calendar.getTime();

		}
		else if ("last-week".equals(timeFilter)) {

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.WEEK_OF_MONTH, -1);
			timeFrom = calendar.getTime();

		}
		else if ("last-year".equals(timeFilter)) {

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, -1);
			timeFrom = calendar.getTime();
		}

		if (timeFrom != null) {
			queryParams.setTimeFrom(timeFrom);
		}
	}

	/**
	 * Set user parameter.
	 */
	protected void setUserParam(
		PortletRequest portletRequest, QueryParams queryParams) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		queryParams.setUserId(themeDisplay.getUserId());
	}

	// Modification date field name in the index.

	private static final String MODIFIED_SORT_FIELD = "modified_sortable";

	@Reference
	private ConfigurationHelper _configurationHelper;

	@Reference
	private FacetTranslatorFactory _facetTranslatorFactory;

	@Reference
	private RequestParamValidator _requestParamValidator;
}
