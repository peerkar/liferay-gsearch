
package fi.soveltia.liferay.gsearch.web.search.internal.queryparams;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GSearchSearchTypes;
import fi.soveltia.liferay.gsearch.web.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.web.search.internal.exception.KeywordsException;
import fi.soveltia.liferay.gsearch.web.search.queryparams.QueryParamsBuilder;

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
		PortletRequest portletRequest,
		GSearchDisplayConfiguration configuration)
		throws Exception {

		_queryParamValidator = new RequestParamValidator();
		_portletRequest = portletRequest;
		_queryParams = new QueryParams();
		_gSearchDisplayConfiguration = configuration;

		setCompanyParam();
		setGroupsParam();
		setLocaleParam();
		setUserParam();

		setDocumentExtensionParam();
		setDocumentTypeParam();
		setKeywordsParam();
		setSearchTypeParams();
		setTimeParam();
		setTypeParam();
		setWebContentStructureParam();

		setStartEndParams();
		setPageSizeParam();
		setSortParam();

		return _queryParams;
	}

	/**
	 * Parse asset class corresponding the filter key.
	 * 
	 * @param key search key
	 * @return corresponding class.
	 * @throws ClassNotFoundException, PatternSyntaxException 
	 */
	protected Class<?> parseAssetClass(String key) throws ClassNotFoundException, PatternSyntaxException {

		String[] configurationOptions =
						_gSearchDisplayConfiguration.assetTypeOptions();

		Class<?> clazz = null;

		// Syntax: filter_key;asset_class_name
		
		for (String option : configurationOptions) {
			
			String[] parts = option.split(";");

			if (parts.length == 2) {
				
				if (parts[0].equals(key)){
					clazz = Class.forName(parts[1]);	
					break;
				}
			}
		}

		return clazz;
	}	
	
	/**
	 * Parse default set of asset classes to search for.
	 * 
	 * @return list of classes
	 * @throws ClassNotFoundException, PatternSyntaxException 
	 */
	protected List<Class<?>> parseDefaultAssetClasses() throws ClassNotFoundException, PatternSyntaxException {

		String[] configurationOptions =
						_gSearchDisplayConfiguration.assetTypeOptions();

		List<Class<?>> classes = new ArrayList<Class<?>>();
		
		// Syntax: filter_key;asset_class_name

		for (String option : configurationOptions) {
			
			String[] parts = option.split(";");

			if (parts.length == 2) {
				classes.add(Class.forName(parts[1]));
			}
		}

		return classes;
	}		
	
	/** Parse document extensions corresponding the filter key.
	 *  
	 * @param key defined in configuration options
	 * @return corresponding array of file extensions
	 * @throws PatternSyntaxException
	 */
	protected String[] parseDocumentExtensions(String key) throws PatternSyntaxException {
		String[] configurationOptions =
						_gSearchDisplayConfiguration.documentExtensionOptions();

		//Syntax: filter_key;translation_key_for_ui;comma_separated_extensions_list

		String[]extensions = null;
		
		try {
		
			for (String extension : configurationOptions) {

				String[] parts = extension.split(";");
	
				if (parts.length == 3) {
					
					if (parts[0].equals(key)){
						extensions = parts[2].split("_");
						break;
					}
				}
			}
		} catch (PatternSyntaxException e) {
			_log.error(e, e);
		}
		return extensions;
	}

	/**
	 * Set company parameter.
	 */
	protected void setCompanyParam() {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) _portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		_queryParams.setCompanyId(themeDisplay.getCompanyId());
	}

	/**
	 * Set document extensions (formats)
	 */
	protected void setDocumentExtensionParam() {

		String documentExtensionFilter = ParamUtil.getString(
			_portletRequest, GSearchWebKeys.DOCUMENT_EXTENSION_FILTER);

		if (Validator.isNotNull(documentExtensionFilter)) {
			String[] extensions = parseDocumentExtensions(documentExtensionFilter);
			
			if (extensions != null) {
				_queryParams.setDocumentExtensions(extensions);
			}
		}
	}

	/**
	 * Set document type parameter.
	 */
	protected void setDocumentTypeParam() {

		long documentTypeFilter = ParamUtil.getLong(
			_portletRequest, GSearchWebKeys.DOCUMENT_TYPE_FILTER, -1);

		if (documentTypeFilter > 0) {
			_queryParams.setDocumentTypeId(documentTypeFilter);
		}
	}	
	
	/**
	 * Set groups parameter.
	 */
	protected void setGroupsParam() {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) _portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		String scopeFilter =
			ParamUtil.getString(_portletRequest, GSearchWebKeys.SCOPE_FILTER);

		long[] groupIds;

		if (!"all".equals(scopeFilter)) {
			groupIds = new long[] {
				themeDisplay.getScopeGroupId()
			};
		}
		else {
			groupIds = new long[] {};
		}
		_queryParams.setGroupIds(groupIds);
	}

	/**
	 * Set keywords parameter.
	 * 
	 * @throws KeywordsException
	 */
	protected void setKeywordsParam()
		throws KeywordsException {

		String keywords =
			ParamUtil.getString(_portletRequest, GSearchWebKeys.KEYWORDS);

		// Validate keywords.

		if (!_queryParamValidator.validateKeywords(keywords)) {
			throw new KeywordsException();
		}
		_queryParams.setKeywords(keywords);
	}

	/**
	 * Set locale parameter.
	 */
	protected void setLocaleParam() {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) _portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		_queryParams.setLocale(themeDisplay.getLocale());
	}

	/**
	 * Set page size parameter.
	 */
	protected void setPageSizeParam() {

		_queryParams.setPageSize(_gSearchDisplayConfiguration.pageSize());
	}
	

	/**
	 * Set search type (normal / image search). 
	 * 
	 * Search type is determined from typefilter value 
	 * 
	 */
	protected void setSearchTypeParams() {

		String typeFilter =
						ParamUtil.getString(_portletRequest, GSearchWebKeys.TYPE_FILTER);
		String documentExtensionFilter =
						ParamUtil.getString(_portletRequest, GSearchWebKeys.DOCUMENT_EXTENSION_FILTER);

		if ("file".equals(typeFilter) && "image".equals(documentExtensionFilter)) {
			_queryParams.setSearchType(GSearchSearchTypes.IMAGES);
		} else {
			_queryParams.setSearchType(GSearchSearchTypes.ALL);
		}
	}	

	/**
	 * Set sort. Default sort field equals to score.
	 */
	protected void setSortParam() {

		String sortField =
			ParamUtil.getString(_portletRequest, GSearchWebKeys.SORT_FIELD);

		String sortDirection =
			ParamUtil.getString(_portletRequest, GSearchWebKeys.SORT_DIRECTION);

		// Always define primary and secondary sort in case of equal primary
		// values.

		Sort sort1;
		Sort sort2;

		boolean reverse;

		if ("desc".equals(sortDirection)) {
			reverse = true;
		}
		else {
			reverse = false;
		}

		String field;

		if ("title".equals(sortField)) {
			field = "localized_title_" + _queryParams.getLocale().toString() +
				"_sortable";

			sort1 = new Sort(field, reverse);
			sort2 = new Sort(MODIFIED_SORT_FIELD, Sort.LONG_TYPE, reverse);

		}
		else if ("modified".equals(sortField)) {

			sort1 = new Sort(MODIFIED_SORT_FIELD, Sort.LONG_TYPE, reverse);
			sort2 = new Sort(null, Sort.SCORE_TYPE, reverse);
		}
		else {
			sort1 = new Sort(null, Sort.SCORE_TYPE, reverse);
			sort2 = new Sort(MODIFIED_SORT_FIELD, reverse);
		}

		_queryParams.setSorts(new Sort[] {
			sort1, sort2
		});
	}

	/**
	 * Set start and end parameter.
	 */
	protected void setStartEndParams() {

		int start =
			ParamUtil.getInteger(_portletRequest, GSearchWebKeys.START, 0);
		_queryParams.setStart(start);
		_queryParams.setEnd(start + _gSearchDisplayConfiguration.pageSize());
	}

	/**
	 * Set time parameter (modification date between).
	 */
	protected void setTimeParam() {

		String timeFilter =
			ParamUtil.getString(_portletRequest, GSearchWebKeys.TIME_FILTER);

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
			_queryParams.setTimeFrom(timeFrom);
		}
	}

	/**
	 * Set types (asset types to search for).
	 * 
	 * @throws ClassNotFoundException 
	 * @throws PatternSyntaxException 
	 */
	protected void setTypeParam() throws PatternSyntaxException, ClassNotFoundException {

		String typeFilter =
			ParamUtil.getString(_portletRequest, GSearchWebKeys.TYPE_FILTER);

		List<Class<?>> clazzes = new ArrayList<Class<?>>();
		
		Class<?>clazz = parseAssetClass(typeFilter);

		if (clazz != null) {
			clazzes.add(clazz);
		}
		else {
			clazzes.addAll(parseDefaultAssetClasses());
		}

		_queryParams.setClazzes(clazzes);
	}

	/**
	 * Set user parameter.
	 */
	protected void setUserParam() {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) _portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		_queryParams.setUserId(themeDisplay.getUserId());
	}
	
	/**
	 * Set web content structure parameter.
	 */
	protected void setWebContentStructureParam() {

		String wcsFilter = ParamUtil.getString(
			_portletRequest, GSearchWebKeys.WEB_CONTENT_STRUCTURE_FILTER);

		if (Validator.isNotNull(wcsFilter)) {
			_queryParams.setWebContentStructureKey(wcsFilter);
		}
	}	

	// Modification date field name in the index.

	private static final String MODIFIED_SORT_FIELD = "modified_sortable";

	private volatile GSearchDisplayConfiguration _gSearchDisplayConfiguration;
	private PortletRequest _portletRequest;
	private QueryParams _queryParams;
	private RequestParamValidator _queryParamValidator;
	
	private static final Log _log =
					LogFactoryUtil.getLog(QueryParamsBuilderImpl.class);

}
