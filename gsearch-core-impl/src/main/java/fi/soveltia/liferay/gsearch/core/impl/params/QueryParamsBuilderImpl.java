
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONArray;
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

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.core.api.facet.translator.FacetTranslator;
import fi.soveltia.liferay.gsearch.core.api.facet.translator.FacetTranslatorFactory;
import fi.soveltia.liferay.gsearch.core.api.params.FacetParam;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParamsBuilder;
import fi.soveltia.liferay.gsearch.core.api.params.RequestParamValidator;
import fi.soveltia.liferay.gsearch.core.api.results.layout.ResultLayoutService;
import fi.soveltia.liferay.gsearch.core.impl.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.core.impl.exception.KeywordsException;

/**
 * Query params builder implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.impl.configuration.ModuleConfiguration", 
	immediate = true, 
	service = QueryParamsBuilder.class
)
public class QueryParamsBuilderImpl implements QueryParamsBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryParams buildQueryParams(PortletRequest portletRequest)
		throws Exception {

		QueryParams queryParams = new QueryParams();

		setCompanyParam(portletRequest, queryParams);
		setGroupsParam(portletRequest, queryParams);
		setLocaleParam(portletRequest, queryParams);
		setUserParam(portletRequest, queryParams);

		setKeywordsParam(portletRequest, queryParams);
		setTimeParam(portletRequest, queryParams);
		setTypeParam(portletRequest, queryParams);
		setStatusParam(portletRequest, queryParams);

		setFacetParams(portletRequest, queryParams);

		setResultsLayoutParam(portletRequest, queryParams);

		setStartEndParams(portletRequest, queryParams);
		setPageSizeParam(portletRequest, queryParams);

		setSortParam(portletRequest, queryParams);

		return queryParams;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}
	
	/**
	 * Parse asset class name corresponding the key.
	 * 
	 * @param key
	 *            search key
	 * @return corresponding class name.
	 * @throws JSONException
	 */
	protected String parseAssetClass(String key)
		throws JSONException, ClassNotFoundException {

		JSONArray configurationArray = JSONFactoryUtil.createJSONArray(
			_moduleConfiguration.typeConfiguration());

		String className = null;

		for (int i = 0; i < configurationArray.length(); i++) {

			JSONObject item = configurationArray.getJSONObject(i);

			if (key.equals(item.getString("key"))) {

				className = item.getString("entryClassName");
				break;
			}
		}

		return className;
	}

	/**
	 * Parse default set of asset class names to search for.
	 * 
	 * @return list of class names
	 * @throws JSONException
	 */
	protected List<String> parseDefaultAssetClasses()
		throws ClassNotFoundException, JSONException {

		JSONArray configurationArray = JSONFactoryUtil.createJSONArray(
			_moduleConfiguration.typeConfiguration());

		List<String> classNames = new ArrayList<String>();

		for (int i = 0; i < configurationArray.length(); i++) {

			JSONObject item = configurationArray.getJSONObject(i);

			classNames.add(item.getString("entryClassName"));
		}

		return classNames;
	}

	/**
	 * Set company parameter.
	 */
	protected void setCompanyParam(PortletRequest portletRequest, QueryParams queryParams) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		queryParams.setCompanyId(themeDisplay.getCompanyId());
	}

	/**
	 * Set facet parameters.
	 * 
	 * @throws JSONException
	 */
	protected void setFacetParams(PortletRequest portletRequest, QueryParams queryParams)
		throws JSONException {

		JSONArray configurationArray = JSONFactoryUtil.createJSONArray(
			_moduleConfiguration.facetConfiguration());

		Map<FacetParam, BooleanClauseOccur> facetParams = new HashMap<FacetParam, BooleanClauseOccur>();

		String fieldParam;
		String fieldName;
		String[] fieldValues;
		boolean isMultiValued;
		String multiValueOperator;
		BooleanClauseOccur multiValueOccur = null;

		for (int i = 0; i < configurationArray.length(); i++) {

			JSONObject facetConfiguration = configurationArray.getJSONObject(i);

			isMultiValued = GetterUtil.getBoolean(
				facetConfiguration.getString("isMultiValued"), false);

			fieldParam = facetConfiguration.getString("paramName");

			fieldName = facetConfiguration.getString("fieldName");

			multiValueOperator = facetConfiguration.getString("multiValueOperator");
			
			if (Validator.isNotNull(multiValueOperator)) {
				
				if ("or".equalsIgnoreCase(multiValueOperator)) {
					multiValueOccur = BooleanClauseOccur.SHOULD;
				} else {
					multiValueOccur = BooleanClauseOccur.MUST;
				}
			} else {
				multiValueOccur = BooleanClauseOccur.SHOULD;
			}

			fieldValues =
				ParamUtil.getStringValues(portletRequest, fieldParam);

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
							facetParams.put(facetParam, BooleanClauseOccur.MUST);
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

						FacetParam facetParam =
							new FacetParam(fieldName, new String[] {
								fieldValues[0]
							}, BooleanClauseOccur.MUST);
						facetParams.put(facetParam, BooleanClauseOccur.MUST);
					}
				}
			}
		}
		queryParams.setFacetsParams(facetParams);
	}

	@Reference(unbind = "-")
	protected void setFacetTranslatorFactory(
		FacetTranslatorFactory facetTranslatorFactory) {

		_facetTranslatorFactory = facetTranslatorFactory;
	}

	/**
	 * Set groups parameter.
	 */
	protected void setGroupsParam(PortletRequest portletRequest, QueryParams queryParams) {

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
	 * @throws KeywordsException
	 */
	protected void setKeywordsParam(PortletRequest portletRequest, QueryParams queryParams)
		throws KeywordsException {

		String keywords =
			ParamUtil.getString(portletRequest, GSearchWebKeys.KEYWORDS);

		// Validate keywords.

		if (!_requestParamValidator.validateKeywords(keywords)) {
			throw new KeywordsException();
		}
		queryParams.setKeywords(keywords);
	}

	/**
	 * Set locale parameter.
	 */
	protected void setLocaleParam(PortletRequest portletRequest, QueryParams queryParams) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		queryParams.setLocale(themeDisplay.getLocale());
	}

	/**
	 * Set page size parameter.
	 */
	protected void setPageSizeParam(PortletRequest portletRequest, QueryParams queryParams) {

		queryParams.setPageSize(_moduleConfiguration.pageSize());
	}

	@Reference(unbind = "-")
	protected void setRequestParamValidator(
		RequestParamValidator requestParamValidator) {

		_requestParamValidator = requestParamValidator;
	}

	/**
	 * Set search type (normal / image search). Search type is determined from
	 * typefilter value
	 */
	protected void setResultsLayoutParam(PortletRequest portletRequest, QueryParams queryParams) {

		String resultsLayoutParam =
			ParamUtil.getString(portletRequest, GSearchWebKeys.RESULTS_LAYOUT);
		
		if (_resultLayoutService.isValidResultLayoutKey(portletRequest, resultsLayoutParam)) {
			queryParams.setResultsLayout(resultsLayoutParam);
		} else {
			queryParams.setResultsLayout(_resultLayoutService.getDefaultResultLayoutKey());
		}
	}

	/**
	 * Set sort. Default sort field equals to score.
	 * 
	 * @throws JSONException
	 */
	protected void setSortParam(PortletRequest portletRequest, QueryParams queryParams)
		throws JSONException {

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

		JSONArray configurationArray = JSONFactoryUtil.createJSONArray(
			_moduleConfiguration.sortFieldConfiguration());

		for (int i = 0; i < configurationArray.length(); i++) {

			JSONObject item = configurationArray.getJSONObject(i);

			if (item.getString("key").equals(sortField)) {

				fieldName = _configurationHelper.parseConfigurationKey(
					portletRequest, item.getString("fieldName"));

				fieldType = Integer.valueOf(item.getString("fieldType"));

				break;

			}
			else if (item.getBoolean("default")) {

				defaultFieldName = _configurationHelper.parseConfigurationKey(
					portletRequest, item.getString("fieldName"));

				defaultFieldType = Integer.valueOf(item.getString("fieldType"));
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

		queryParams.setSorts(new Sort[] {
			sort1, sort2
		});
	}

	/**
	 * Set start and end parameter.
	 */
	protected void setStartEndParams(PortletRequest portletRequest, QueryParams queryParams) {

		int start =
			ParamUtil.getInteger(portletRequest, GSearchWebKeys.START, 0);
		queryParams.setStart(start);
		queryParams.setEnd(start + _moduleConfiguration.pageSize());
	}

	/**
	 * Set start and end parameter.
	 */
	protected void setStatusParam(PortletRequest portletRequest, QueryParams queryParams) {

		int status =
			ParamUtil.getInteger(portletRequest, GSearchWebKeys.STATUS, 0);
		queryParams.setStatus(status);
	}
	
	/**
	 * Set time parameter (modification date between).
	 */
	protected void setTimeParam(PortletRequest portletRequest, QueryParams queryParams) {

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
	 * Set types (asset types to search for).
	 * 
	 * @throws ClassNotFoundException
	 * @throws PatternSyntaxException
	 * @throws JSONException
	 */
	protected void setTypeParam(PortletRequest portletRequest, QueryParams queryParams)
		throws PatternSyntaxException, ClassNotFoundException, JSONException {

		String typeFilter =
			ParamUtil.getString(portletRequest, GSearchWebKeys.FILTER_TYPE);

		List<String> classNames = new ArrayList<String>();

		String className = parseAssetClass(typeFilter);
		
		if (className != null) {
			classNames.add(className);
		}
		else {
			classNames.addAll(parseDefaultAssetClasses());
		}

		queryParams.setClassNames(classNames);
	}

	/**
	 * Set user parameter.
	 */
	protected void setUserParam(PortletRequest portletRequest, QueryParams queryParams) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		queryParams.setUserId(themeDisplay.getUserId());
	}
	
	@Reference
	private ConfigurationHelper _configurationHelper;
	
	@Reference
	private ResultLayoutService _resultLayoutService;

	// Modification date field name in the index.

	private static final String MODIFIED_SORT_FIELD = "modified_sortable";

	private FacetTranslatorFactory _facetTranslatorFactory;

	private volatile ModuleConfiguration _moduleConfiguration;

	private RequestParamValidator _requestParamValidator;
}
