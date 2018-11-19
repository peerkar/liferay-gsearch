
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.core.api.facet.translator.FacetTranslatorFactory;
import fi.soveltia.liferay.gsearch.core.api.params.FacetParam;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParamsBuilder;
import fi.soveltia.liferay.gsearch.core.api.params.RequestParamValidator;
import fi.soveltia.liferay.gsearch.core.impl.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.core.impl.exception.KeywordsException;

/**
 * Query params builder implementation.
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.configuration.GSearchCore",
	immediate = true,
	service = QueryParamsBuilder.class
)
public class QueryParamsBuilderImpl implements QueryParamsBuilder {

	private static final DateTimeFormatter rangeDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryParams buildQueryParams(PortletRequest portletRequest)
		throws Exception {

		String[] typeFilter =
			ParamUtil.getStringValues(portletRequest, GSearchWebKeys.FILTER_TYPE, new String[]{});

		return buildQueryParamsWithType(portletRequest, typeFilter);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryParams buildUnfilteredQueryParams(PortletRequest portletRequest) throws Exception {
		return buildQueryParamsWithType(portletRequest, new String[] {"everything"});
	}

	private QueryParams buildQueryParamsWithType(PortletRequest portletRequest, String[] types)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		QueryParams queryParams = new QueryParams();
		queryParams.setCompanyId(themeDisplay.getCompanyId());
		queryParams.setLocale(themeDisplay.getLocale());

		setGroupsParam(portletRequest, queryParams);
		setUserParam(themeDisplay, queryParams);

		setKeywordsParam(portletRequest, queryParams);
		setTimeParam(portletRequest, queryParams);
		setTypeParam(queryParams, types);

		setUnitParam(portletRequest, queryParams);

		setStartEndParams(portletRequest, queryParams);
		setPageSizeParam(portletRequest, queryParams);

		return queryParams;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}

	/**
	 * Parse asset class name and other type related params corresponding the key.
	 *
	 * @param keys
	 *            search key
	 * @return corresponding query type object.
	 * @throws JSONException
	 */
	protected List<QueryType> parseQueryTypes(String[] keys)
		throws JSONException, ClassNotFoundException {

		JSONArray configurationArray = JSONFactoryUtil.createJSONArray(
			_moduleConfiguration.typeConfiguration());

		List<QueryType> queryTypes = new ArrayList<>();
		Arrays.asList(keys).forEach(key -> {
			for (int i = 0; i < configurationArray.length(); i++) {
				JSONObject item = configurationArray.getJSONObject(i);
				if (key.equals(item.getString("key"))) {
					queryTypes.add(getQueryType(item));
					break;
				}
			}
		});

		return queryTypes;
	}

	private QueryType getQueryType(JSONObject item) {
		String entryClassName = item.getString("entryClassName");
		List<String> ddmStructureKeys = getCSVStringAsList(item.getString("ddmStructureKey", null));
		return QueryType.newBuilder().entryClassName(entryClassName).ddmStructureKeys(ddmStructureKeys).build();
	}

	private List<String> getCSVStringAsList(String input) {
		if (input == null) {
			return null;
		}
		String[] values = input.split("\\s*,\\s*");
		return Arrays.asList(values);
	}

	/**
	 * Parse default set of asset class names to search for.
	 *
	 * @return list of class names
	 * @throws JSONException
	 */
	protected List<QueryType> parseDefaultQueryTypes()
		throws ClassNotFoundException, JSONException {

		JSONArray configurationArray = JSONFactoryUtil.createJSONArray(
			_moduleConfiguration.typeConfiguration());

		List<QueryType> queryTypes = new ArrayList<>();

		for (int i = 0; i < configurationArray.length(); i++) {
			JSONObject item = configurationArray.getJSONObject(i);
			queryTypes.add(getQueryType(item));
		}

		return queryTypes;
	}

	protected void setUnitParam(PortletRequest portletRequest, QueryParams queryParams) {
		String[] units = ParamUtil.getStringValues(portletRequest, GSearchWebKeys.UNIT_PARAM);
		if ((units != null) && (units.length > 0)) {
			List<String> unitList = Arrays.asList(units);
			try {
				queryParams.setCategories(unitList
					.stream()
					.filter(unit -> !unit.equals("0")) // ignore 0 as it is used for 'search all categories'
					.map(Long::valueOf)
					.collect(Collectors.toList())
				);
			} catch (NumberFormatException e) {
				log.warn(String.format("Cannot parse %s as long", StringUtil.merge(units, ", ")));
			}
		}
	}

	/**
	 * Set groups parameter.
	 */
	protected void setGroupsParam(PortletRequest portletRequest, QueryParams queryParams) {

		ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		String scopeFilter = ParamUtil.getString(portletRequest, GSearchWebKeys.FILTER_SCOPE, "");

		long[] groupIds;

		groupIds = new long[] {};
		if ("this-site".equals(scopeFilter)) {
			groupIds = new long[]{themeDisplay.getScopeGroupId()};
		} else if(!scopeFilter.isEmpty()) {
			String[] groupIdStrings = scopeFilter.split(",");
			groupIds = Arrays
				.stream(groupIdStrings)
				.mapToLong(Long::valueOf)
				.toArray();
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
			ParamUtil.getString(portletRequest, GSearchWebKeys.KEYWORDS, "");

		boolean suggestions = Boolean.valueOf(ParamUtil.getString(portletRequest, GSearchWebKeys.SUGGESTIONS_MODE, "false"));

		// to make autocomplete search find results starting with given string
		if (suggestions &&
			!keywords.isEmpty() &&
			!keywords.endsWith("*") &&
			!keywords.matches("\\W$")) {
			keywords += "*";
		}

		// Validate keywords.

		if (!_requestParamValidator.validateKeywords(keywords)) {
			throw new KeywordsException();
		}
		queryParams.setKeywords(keywords);
	}

	/**
	 * Set page size parameter.
	 */
	protected void setPageSizeParam(PortletRequest portletRequest, QueryParams queryParams) {
		int pageSize = ParamUtil.getInteger(portletRequest, GSearchWebKeys.PAGE_SIZE, _moduleConfiguration.pageSize());
		queryParams.setPageSize(pageSize);
	}

	@Reference(unbind = "-")
	protected void setRequestParamValidator(
		RequestParamValidator requestParamValidator) {

		_requestParamValidator = requestParamValidator;
	}

	/**
	 * Set start and end parameter.
	 */
	protected void setStartEndParams(PortletRequest portletRequest, QueryParams queryParams) {

		int start =
			ParamUtil.getInteger(portletRequest, GSearchWebKeys.START, 0);
		queryParams.setStart(start);
		int pageSize = ParamUtil.getInteger(portletRequest, GSearchWebKeys.PAGE_SIZE, _moduleConfiguration.pageSize());
		queryParams.setEnd(start + pageSize);
	}

	/**
	 * Set time parameter (modification date between).
	 */
	protected void setTimeParam(PortletRequest portletRequest, QueryParams queryParams) {

		String timeFilter =
			ParamUtil.getString(portletRequest, GSearchWebKeys.FILTER_TIME);

		Date timeFrom = null;
		Date timeTo = null;

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
		else if ("range".equals(timeFilter)) {
			String timeStartParameter =
				ParamUtil.getString(portletRequest, GSearchWebKeys.FILTER_TIME_START, "");
			String timeEndParameter =
				ParamUtil.getString(portletRequest, GSearchWebKeys.FILTER_TIME_END, "");
			timeFrom = getDateFromString(timeStartParameter, false);
			timeTo = getDateFromString(timeEndParameter, true);
		}

		if (timeFrom != null) {
			queryParams.setTimeFrom(timeFrom);
		}
		if (timeTo != null) {
			queryParams.setTimeTo(timeTo);
		}
	}

	private Date getDateFromString(String dateString, boolean isRangeEnd) {
		if (!dateString.isEmpty()) {
			try {
				LocalDate localDate = LocalDate.parse(dateString, rangeDateFormatter);
				if (isRangeEnd) { // use the beginning of next day as the range end date
					localDate = localDate.plusDays(1);
				}
				return GregorianCalendar.from(localDate.atStartOfDay(ZoneId.systemDefault())).getTime();
			} catch (NullPointerException | IllegalArgumentException | DateTimeParseException e) {
				log.warn(String.format("Cannot get date from '%s'", dateString));
			}
		}
		return null;
	}

	/**
	 * Set types (asset types to search for).
	 *
	 * @throws ClassNotFoundException
	 * @throws PatternSyntaxException
	 * @throws JSONException
	 */
	protected void setTypeParam(QueryParams queryParams, String[] types)
		throws PatternSyntaxException, ClassNotFoundException, JSONException {

		List<String> classNames = new ArrayList<>();
		List<String> ddmStructureKeys = new ArrayList<>();

		List<QueryType> queryTypes = parseQueryTypes(types);

		if (queryTypes.size() > 0) {
			queryTypes.forEach(queryType -> {
				if (queryType.getEntryClassName() != null) {
					classNames.add(queryType.getEntryClassName());
					if (queryType.getDDMStructureKeys() != null) {
						ddmStructureKeys.addAll(queryType.getDDMStructureKeys());
					}
				}
			});
		} else {
			List<QueryType> defaultQueryTypes = parseDefaultQueryTypes();
			classNames.addAll(
				defaultQueryTypes
					.stream()
					.map(QueryType::getEntryClassName)
					.collect(Collectors.toList())
			);
		}

		queryParams.setClassNames(classNames);

		if (ddmStructureKeys.size() > 0) {
			queryParams.setDdmStructureKeys(ddmStructureKeys);
		}

	}

	/**
	 * Set user parameter.
	 */
	protected void setUserParam(ThemeDisplay themeDisplay, QueryParams queryParams) {

		queryParams.setUserId(themeDisplay.getUserId());
	}

	private static final Log log = LogFactoryUtil.getLog(QueryParamsBuilderImpl.class);

	// Modification date field name in the index.

	@Reference
	protected ConfigurationHelper _configurationHelper;

	private static final String MODIFIED_SORT_FIELD = "modified_sortable";

	private FacetTranslatorFactory _facetTranslatorFactory;

	private volatile ModuleConfiguration _moduleConfiguration;

	private RequestParamValidator _requestParamValidator;
}
