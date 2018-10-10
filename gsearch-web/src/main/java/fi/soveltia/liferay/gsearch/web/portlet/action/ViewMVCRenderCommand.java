
package fi.soveltia.liferay.gsearch.web.portlet.action;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.HttpServletRequest;

import fi.soveltia.liferay.gsearch.web.portlet.CategoryDTO;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.web.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GSearchPortletKeys;
import fi.soveltia.liferay.gsearch.web.constants.GSearchResourceKeys;

/**
 * View render command. Primary/default view.
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.mini.web.configuration.GSearchPortlet",
	immediate = true,
	property = {
		"javax.portlet.name=" + GSearchPortletKeys.GSEARCH_PORTLET,
		"mvc.command.name=/"
	},
	service = MVCRenderCommand.class
)
public class ViewMVCRenderCommand implements MVCRenderCommand{

	private static final String FLAMMA_GROUP_FRIENDLY_URL = "/flamma";
	private static final String UNIT_VOCABULARY_NAME = "Yksikkö";

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		if (_log.isDebugEnabled()) {
			_log.debug("ViewMVCRenderCommand.render()");
		}

		Template template =
			(Template) renderRequest.getAttribute(WebKeys.TEMPLATE);

		// Set namespace (a convenience alias for $id).

		String portletNamespace = renderResponse.getNamespace();
		template.put(GSearchWebKeys.PORTLET_NAMESPACE, portletNamespace);

		// Autocomplete on/off.

		template.put(
			GSearchWebKeys.AUTO_COMPLETE_ENABLED,
			_moduleConfiguration.enableAutoComplete());

		// Autocomplete request delay.

		template.put(
			GSearchWebKeys.AUTO_COMPLETE_REQUEST_DELAY,
			_moduleConfiguration.autoCompleteRequestDelay());

		// Set help text resource url.

		template.put(
			GSearchWebKeys.HELP_TEXT_URL,
			createResourceURL(renderResponse, GSearchResourceKeys.GET_HELP_TEXT));

		// Set search results resource url.

		template.put(
			GSearchWebKeys.SEARCH_RESULTS_URL,
			createResourceURL(renderResponse, GSearchResourceKeys.GET_SEARCH_RESULTS));

		// Set autocomplete/suggestions resource url.

		template.put(
			GSearchWebKeys.SUGGESTIONS_URL,
			createResourceURL(renderResponse, GSearchResourceKeys.GET_SUGGESTIONS));

		try {

			// Set supported asset type options.

			template.put(
				GSearchWebKeys.ASSET_TYPE_OPTIONS,
				_configurationHelperService.getAssetTypeOptions(renderRequest.getLocale()));

			template.put(
				GSearchWebKeys.SORT_OPTIONS,
				_configurationHelperService.getSortOptions(renderRequest.getLocale()));

		} catch (Exception e) {
			_log.error(e, e);
		}

		// Set request timeout.

		template.put(
			GSearchWebKeys.REQUEST_TIMEOUT,
			_moduleConfiguration.requestTimeout());

		// Set query min length.

		template.put(
			GSearchWebKeys.QUERY_MIN_LENGTH,
			_moduleConfiguration.queryMinLength());

		// Enable / disable JS console logging messages.

		template.put(
			GSearchWebKeys.JS_DEBUG_ENABLED,
			_moduleConfiguration.jsDebuggingEnabled());

		// Get/set parameters from url

		setInitialParameters(renderRequest, template);

		// Set facet fields

		template.put(GSearchWebKeys.FACET_FIELDS, _facetFields);

		// Tags param name

		template.put(GSearchWebKeys.ASSET_TAG_PARAM, "assetTagNames");

		// Show tags

		template.put(GSearchWebKeys.SHOW_ASSET_TAGS, _moduleConfiguration.showTags());

		template.put(GSearchWebKeys.UNIT_FILTERS, getUnitCategories(PortalUtil.getCompanyId(renderRequest), renderRequest.getLocale()));

		return "View";
	}

	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {
		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);

		try {
			setFacetFields();
		}
		catch (JSONException e) {
			_log.error(e, e);
		}
	}

	private List<CategoryDTO> getUnitCategories(long companyId, Locale locale) {
		List<CategoryDTO> categories = new ArrayList<>();

		categories.add(CategoryDTO.newBuilder().name("Koko yliopisto").build()); // TODO add localization

		try {
			Optional<AssetVocabulary> unitVocabulary = getUnitVocabulary(companyId, locale);

			if (unitVocabulary.isPresent()) {
				categories = getCategoriesFromVocabulary(unitVocabulary.get(), locale);
			} else {
				_log.error("Cannot get unit categories: unit vocabulary not found!");
			}
			unitVocabulary.ifPresent(AssetVocabulary::getCategories);
		} catch (PortalException e) {
			_log.error(String.format("Cannot get Flamma group with friendly URL %s", FLAMMA_GROUP_FRIENDLY_URL), e);
		}

		return categories;

	}

	private List<CategoryDTO> getCategoriesFromVocabulary(AssetVocabulary assetVocabulary, Locale locale) {
		List<AssetCategory> rootCategories =
			assetCategoryLocalService.getVocabularyRootCategories(assetVocabulary.getVocabularyId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		return getCategoryDtos(locale, rootCategories);
	}

	private List<CategoryDTO> getCategoryDtos(Locale locale, List<AssetCategory> assetCategories) {
		return assetCategories
			.stream()
			.map(cat ->
				CategoryDTO.newBuilder()
					.name(cat.getTitle(locale))
					.categoryId(cat.getCategoryId())
					.children(getChildCategoryDtos(cat, locale))
					.build())
			.collect(Collectors.toList());
	}

	private List<CategoryDTO> getChildCategoryDtos(AssetCategory assetCategory, Locale locale) {
		List<AssetCategory> childCategories = assetCategoryLocalService.getChildCategories(assetCategory.getCategoryId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		return getCategoryDtos(locale, childCategories);
	}

	private Optional<AssetVocabulary> getUnitVocabulary(long companyId, Locale locale) throws PortalException {
		Group flammaGroup = groupLocalService.getFriendlyURLGroup(companyId, FLAMMA_GROUP_FRIENDLY_URL);
		List<AssetVocabulary> vocabularies = assetVocabularyLocalService.getGroupVocabularies(flammaGroup.getGroupId());
		return vocabularies
			.stream()
			.filter(v ->
				v.getTitle(locale).equalsIgnoreCase(UNIT_VOCABULARY_NAME))
			.findFirst();
	}

	/**
	 * Create resource URL for a resourceId
	 *
	 * @param renderResponse
	 * @param resourceId
	 *
	 * @return url string
	 */
	protected String createResourceURL(RenderResponse renderResponse, String resourceId) {

		ResourceURL portletURL = renderResponse.createResourceURL();

		portletURL.setResourceID(resourceId);

		return portletURL.toString();
	}

	/**
	 * Get / set facet field configuration
	 *
	 * @throws JSONException
	 */
	protected void setFacetFields() throws JSONException {

		JSONArray configuration = _configurationHelperService.getFacetConfiguration();

		if (configuration.length() > 0) {

			_facetFields = new String[configuration.length()];

			for (int i = 0; i < configuration.length(); i++) {

				JSONObject item = configuration.getJSONObject(i);

				_facetFields[i] = item.getString("paramName");
			}
		}
	}

	/**
	 * Set initial parameters for the templates.
	 *
	 * This is used to make search bookmarkable and linkable.
	 *
	 * @param renderRequest
	 * @param template
	 */
	protected void setInitialParameters(RenderRequest renderRequest, Template template) {

		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpServletRequest request = PortalUtil.getOriginalServletRequest(httpServletRequest);

		// Basic params

		String keywords = ParamUtil.getString(request, GSearchWebKeys.KEYWORDS);

		String scopeFilter = ParamUtil.getString(request, GSearchWebKeys.FILTER_SCOPE);
		String timeFilter = ParamUtil.getString(request, GSearchWebKeys.FILTER_TIME);
		String typeFilter = ParamUtil.getString(request, GSearchWebKeys.FILTER_TYPE);

		String sortField = ParamUtil.getString(request, GSearchWebKeys.SORT_FIELD);
		String sortDirection = ParamUtil.getString(request, GSearchWebKeys.SORT_DIRECTION);
		String start = ParamUtil.getString(request, GSearchWebKeys.START);

		String resultsLayout = ParamUtil.getString(request, GSearchWebKeys.RESULTS_LAYOUT);

		Map<String, String[]>initialParameters = new HashMap<String, String[]>();

		if (Validator.isNotNull(keywords)) {
			initialParameters.put(GSearchWebKeys.KEYWORDS, new String[]{keywords});
		}

		if (Validator.isNotNull(scopeFilter)) {
			initialParameters.put(GSearchWebKeys.FILTER_SCOPE, new String[]{scopeFilter});
		}

		if (Validator.isNotNull(timeFilter)) {
			initialParameters.put(GSearchWebKeys.FILTER_TIME, new String[]{timeFilter});
		}

		if (Validator.isNotNull(typeFilter)) {
			initialParameters.put(GSearchWebKeys.FILTER_TYPE, new String[]{typeFilter});
		}

		if (Validator.isNotNull(sortDirection)) {
			initialParameters.put(GSearchWebKeys.SORT_DIRECTION, new String[]{sortDirection});
		}

		if (Validator.isNotNull(sortField)) {
			initialParameters.put(GSearchWebKeys.SORT_FIELD, new String[]{sortField});
		}

		if (Validator.isNotNull(start)) {
			initialParameters.put(GSearchWebKeys.START, new String[]{start});
		}

		if (Validator.isNotNull(resultsLayout)) {
			initialParameters.put(GSearchWebKeys.RESULTS_LAYOUT, new String[]{resultsLayout});
		}

		// Facets.

		if (_facetFields != null) {

			for (String facetKey : _facetFields) {

				String[] facetValue = ParamUtil.getStringValues(request, facetKey);

				if (Validator.isNotNull(facetValue)) {
					initialParameters.put(facetKey, facetValue);
				}
			}
		}

		template.put(GSearchWebKeys.INITIAL_QUERY_PARAMETERS, initialParameters);
	}

	@Reference(unbind = "-")
	protected void setJSONConfigurationHelperService(ConfigurationHelper configurationHelperService) {

		_configurationHelperService = configurationHelperService;
	}

	@Reference
	protected ConfigurationHelper _configurationHelperService;

	@Reference
	private AssetCategoryLocalService assetCategoryLocalService;

	@Reference
	private AssetVocabularyLocalService assetVocabularyLocalService;

	@Reference
	private GroupLocalService groupLocalService;

	private volatile ModuleConfiguration _moduleConfiguration;

	private static String[] _facetFields = null;

	private static final Log _log = LogFactoryUtil.getLog(
		ViewMVCRenderCommand.class);
}
