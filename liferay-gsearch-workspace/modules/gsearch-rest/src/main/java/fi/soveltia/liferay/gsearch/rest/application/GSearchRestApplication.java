
package fi.soveltia.liferay.gsearch.rest.application;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

import fi.soveltia.liferay.gsearch.core.api.GSearch;
import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContextBuilder;
import fi.soveltia.liferay.gsearch.core.api.suggest.GSearchKeywordSuggester;
import fi.soveltia.liferay.gsearch.localization.api.LocalizationHelper;
import fi.soveltia.liferay.gsearch.recommender.api.RecommenderService;

/**
 * Liferay GSearch REST API.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	property = {
	    "auth.verifier.auth.verifier.BasicAuthHeaderAuthVerifier.urls.includes=/*",
	    "auth.verifier.auth.verifier.PortalSessionAuthVerifier.urls.includes=/*",
		JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/gsearch-rest",
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=Gsearch.Rest"
	},
	service = Application.class
)
public class GSearchRestApplication extends Application {

	@GET
	@Path("/recommendations/{languageId}")
	@Produces({MediaType.APPLICATION_JSON})
	public String getRecommendations(
		@Context HttpServletRequest httpServletRequest,
		@PathParam("languageId") String languageId,
		@QueryParam("assetEntryId") Long[] assetEntryId,
		@QueryParam("text") String[] text,
		@QueryParam("count") Integer count,
		@QueryParam("includeAssetTags") Boolean includeAssetTags,
		@QueryParam("includeAssetCategories") Boolean includeAssetCategories,
		@QueryParam("includeThumbnail") Boolean includeThumbnail,
		@QueryParam("includeUserPortrait") Boolean includeUserPortrait) {

		JSONObject results = JSONFactoryUtil.createJSONObject();

		if (Validator.isNull(languageId)) {
			return results.toString();
		}

		Locale locale = LocaleUtil.fromLanguageId(languageId);

		try {
			QueryContext queryContext = _queryContextBuilder.buildQueryContext(
				httpServletRequest, locale, null);

			queryContext.setPageSize(GetterUtil.getInteger(count, 5));
			queryContext.setStart(0);

			queryContext.setParameter(ParameterNames.PATH_IMAGE, "/image");

			queryContext.setParameter(
				ParameterNames.INCLUDE_THUMBNAIL,
				GetterUtil.getBoolean(includeThumbnail));

			queryContext.setParameter(
				ParameterNames.INCLUDE_USER_PORTRAIT,
				GetterUtil.getBoolean(includeUserPortrait));

			Map<String, Class<?>> additionalResultFields = new HashMap<>();

			if (GetterUtil.get(includeAssetCategories, false)) {
				additionalResultFields.put(
					"assetCategoryTitles_en_US", String[].class);
			}

			if (GetterUtil.get(includeAssetTags, false)) {
				additionalResultFields.put(Field.ASSET_TAG_NAMES, String[].class);
			}

			additionalResultFields.put(Field.ENTRY_CLASS_NAME, String.class);
			additionalResultFields.put(Field.ENTRY_CLASS_PK, String.class);

			queryContext.setParameter(
				ParameterNames.ADDITIONAL_RESULT_FIELDS,
				additionalResultFields);
			
			// Optional recommendation texts for MLT query.
			
			queryContext.setParameter(
					ParameterNames.TEXTS, text);
			
			results = _recommenderService.getRecommendationsByAssetEntryIds(
				assetEntryId, queryContext);
			
			if (results == null) {
				return JSONFactoryUtil.createJSONObject().toString();
			}

			_localizationHelper.setResultTypeLocalizations(locale, results);
			
			_processResultFacets(locale, results);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return results.toString();
	}

	@GET
	@Path("/search/{languageId}/{keywords}")
	@Produces({MediaType.APPLICATION_JSON})
	public String getSearchResults(
		@Context HttpServletRequest httpServletRequest,
		@PathParam("languageId") String languageId,
		@PathParam("keywords") String keywords,
		@QueryParam("start") Integer start,
		@QueryParam("pageSize") Integer pageSize,
		@QueryParam("sortField") String sortField,
		@QueryParam("sortDirection") String sortDirection,
		@QueryParam("groupId") Long groupId,
		@QueryParam("includeAssetTags") Boolean includeAssetTags,
		@QueryParam("includeAssetCategories") Boolean includeAssetCategories,
		@QueryParam("includeThumbnail") Boolean includeThumbnail,
		@QueryParam("includeUserPortrait") Boolean includeUserPortrait,
		@QueryParam("time") String time,
		@QueryParam("dateFormat") String dateFormat,
		@QueryParam("timeFrom") String timeFrom,
		@QueryParam("timeTo") String timeTo) {

		JSONObject results = JSONFactoryUtil.createJSONObject();

		if (Validator.isNull(languageId)) {
			return results.toString();
		}

		Locale locale = LocaleUtil.fromLanguageId(languageId);
		
		Map<String, Object> parameters = new HashMap<>();
		
		parameters.put(ParameterNames.LOCALE, locale);
		parameters.put(ParameterNames.KEYWORDS, keywords);

		if (groupId != null) {
			parameters.put(ParameterNames.GROUP_ID, groupId);
		}

		parameters.put(ParameterNames.START, GetterUtil.get(start, 0));
		parameters.put(ParameterNames.SORT_FIELD, sortField);
		parameters.put(ParameterNames.SORT_DIRECTION, sortDirection);

		if (Validator.isNotNull(time)) {
			parameters.put(ParameterNames.TIME, time);
			parameters.put(ParameterNames.TIME_FROM, timeFrom);
			parameters.put(ParameterNames.TIME_TO, timeTo);
			parameters.put(ParameterNames.DATE_FORMAT, dateFormat);
		}

		try {
			QueryContext queryContext = _queryContextBuilder.buildQueryContext(
				httpServletRequest, locale, null, null, null, null, null, null, keywords);

			queryContext.setPageSize(GetterUtil.get(pageSize, 10));
			queryContext.setParameter(ParameterNames.PATH_IMAGE, "/image");

			_queryContextBuilder.parseParametersHeadless(queryContext, parameters);

			// Process query context contributors.
			
			_queryContextBuilder.processQueryContextContributors(queryContext);
			
			queryContext.setParameter(
				ParameterNames.INCLUDE_THUMBNAIL,
				GetterUtil.getBoolean(includeThumbnail));

			queryContext.setParameter(
				ParameterNames.INCLUDE_USER_PORTRAIT,
				GetterUtil.getBoolean(includeUserPortrait));

			Map<String, Class<?>> additionalResultFields = new HashMap<>();

			if (GetterUtil.get(includeAssetCategories, false)) {
				additionalResultFields.put(
					Field.ASSET_CATEGORY_TITLES, String[].class);
			}

			if (GetterUtil.get(includeAssetTags, false)) {
				additionalResultFields.put(Field.ASSET_TAG_NAMES, String[].class);
			}
			
			additionalResultFields.put(Field.ENTRY_CLASS_NAME, String.class);
			additionalResultFields.put(Field.ENTRY_CLASS_PK, String.class);

			queryContext.setParameter(
				ParameterNames.ADDITIONAL_RESULT_FIELDS,
				additionalResultFields);

			results = _gSearch.getSearchResults(queryContext);

			_localizationHelper.setResultTypeLocalizations(locale, results);
			_processResultFacets(locale, results);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return results.toString();
	}

	public Set<Object> getSingletons() {
		return Collections.<Object>singleton(this);
	}

	@GET
	@Path("/suggestions/{companyId}/{groupId}/{languageId}/{keywords}")
	@Produces({MediaType.APPLICATION_JSON})
	public String getSuggestions(
		@Context HttpServletRequest httpServletRequest,
		@PathParam("companyId") Integer companyId,
		@PathParam("groupId") Integer groupId,
		@PathParam("languageId") String languageId,
		@PathParam("keywords") String keywords) {

		JSONArray results = JSONFactoryUtil.createJSONArray();

		if (Validator.isNull(companyId) || Validator.isNull(groupId) ||
			Validator.isNull(languageId) || Validator.isNull(keywords)) {

			return results.toString();
		}

		try {		
			Locale locale = LocaleUtil.fromLanguageId(languageId);
			
			QueryContext queryContext =
				_queryContextBuilder.buildSuggesterQueryContext(
					httpServletRequest, null, groupId, locale, keywords);

			results = _gSearchKeywordSuggester.getSuggestions(queryContext);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return results.toString();
	}

	/**
	 * Format facets for displaying.
	 *
	 * @param locale
	 * @param responseObject
	 */
	private void _processResultFacets(Locale locale, JSONObject responseObject) {
		JSONArray facets = responseObject.getJSONArray("facets");

		if ((facets == null) || (facets.length() == 0)) {
			return;
		}

		for (int i = 0; i < facets.length(); i++) {
			JSONObject resultItem = facets.getJSONObject(i);

			JSONArray values = resultItem.getJSONArray("values");

			for (int j = 0; j < values.length(); j++) {
				JSONObject value = values.getJSONObject(j);

				value.put(
					"text",
					_localizationHelper.getLocalization(
						locale,
						value.getString(
							"name"
						).toLowerCase()) + " (" + value.getString("frequency") +
							")");
			}
		}
	}

	@Reference
	private CoreConfigurationHelper _coreConfigurationHelper;

	@Reference
	private GSearch _gSearch;

	@Reference
	private GSearchKeywordSuggester _gSearchKeywordSuggester;

	@Reference
	private LocalizationHelper _localizationHelper;

	@Reference
	private QueryContextBuilder _queryContextBuilder;

	@Reference
	private RecommenderService _recommenderService;

}