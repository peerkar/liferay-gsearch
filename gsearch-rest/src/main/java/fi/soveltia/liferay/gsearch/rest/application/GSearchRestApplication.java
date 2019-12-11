
package fi.soveltia.liferay.gsearch.rest.application;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.GSearch;
import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContextBuilder;
import fi.soveltia.liferay.gsearch.core.api.suggest.GSearchKeywordSuggester;
import fi.soveltia.liferay.gsearch.localization.LocalizationHelper;
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

	public Set<Object> getSingletons() {

		return Collections.<Object> singleton(this);
	}

	@GET
	@Path("/recommendations/{languageId}")
	@Produces({
		MediaType.APPLICATION_JSON
	})
	public String getRecommendations(
		@Context HttpServletRequest httpServletRequest,
		@PathParam("languageId") String languageId,
		@QueryParam("assetEntryId") Long[] assetEntryId,
		@QueryParam("count") Integer count,
		@QueryParam("includeAssetTags") Boolean includeAssetTags,
		@QueryParam("includeAssetCategories") Boolean includeAssetCategories,
		@QueryParam("includeThumbnail") Boolean includeThumbnail,
		@QueryParam("includeUserPortrait") Boolean includeUserPortrait) {

		JSONObject results = JSONFactoryUtil.createJSONObject();

		if (Validator.isNull(languageId)) {
			return results.toString();
		}

		long companyId = (long) httpServletRequest.getAttribute("COMPANY_ID");
		User user = (User) httpServletRequest.getAttribute("USER");
		Locale locale = LocaleUtil.fromLanguageId(languageId);

		try {

			QueryContext queryContext = _queryContextBuilder.buildQueryContext(
				httpServletRequest, companyId, locale, null);
			
			queryContext.setParameter(ParameterNames.USER_ID, user.getUserId());

			String[]docUIDs = null;

			if (assetEntryId != null) {
				
				List<String> ids = new ArrayList<String>();
				
				String docUID = null;
				
				for (int i = 0; i < assetEntryId.length; i++) {

					docUID = _recommenderService.resolveDocUIDByAssetEntryId(
						queryContext, assetEntryId[i]);
					
					if (docUID != null) {
						ids.add(docUID);
					}
				}
				
				docUIDs = ids.stream().toArray(String[]::new);
			}
						
			if (docUIDs == null) {
				return results.toString();
			}

			queryContext = _queryContextBuilder.buildQueryContext(
				httpServletRequest, companyId, locale, null);

			queryContext.setParameter(ParameterNames.PATH_IMAGE, "/image");

			queryContext.setParameter(ParameterNames.USER_ID, user.getUserId());
			queryContext.setPageSize(GetterUtil.getInteger(count, 5));
			queryContext.setStart(0);
			
			queryContext.setParameter(
				ParameterNames.INCLUDE_THUMBNAIL,
				GetterUtil.getBoolean(includeThumbnail, false));

			queryContext.setParameter(
				ParameterNames.INCLUDE_USER_PORTRAIT,
				GetterUtil.getBoolean(includeUserPortrait, false));

			Map<String, Class<?>> additionalResultFields =
				new HashMap<String, Class<?>>();

			if (GetterUtil.get(includeAssetCategories, false)) {
				additionalResultFields.put(
					"assetCategoryTitles_en_US", String[].class);
			}
			if (GetterUtil.get(includeAssetTags, false)) {
				additionalResultFields.put("assetTagNames", String[].class);
			}
			additionalResultFields.put("entryClassName", String.class);
			additionalResultFields.put("entryClassPK", String.class);
			
			queryContext.setParameter(
				ParameterNames.ADDITIONAL_RESULT_FIELDS,
				additionalResultFields);

			results = _recommenderService.getRecommendationsByDocUID(
				queryContext, docUIDs);

			_localizationHelper.setResultTypeLocalizations(locale, results);
			_localizationHelper.setFacetLocalizations(locale, results);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return results.toString();
	}

	@GET
	@Path("/search/{languageId}/{keywords}")
	@Produces({
		MediaType.APPLICATION_JSON
	})
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

		long companyId = (long) httpServletRequest.getAttribute("COMPANY_ID");
		User user = (User) httpServletRequest.getAttribute("USER");
		Locale locale = LocaleUtil.fromLanguageId(languageId);

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(ParameterNames.COMPANY_ID, companyId);
		parameters.put(ParameterNames.USER_ID, user.getUserId());
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

		JSONObject responseObject = JSONFactoryUtil.createJSONObject();

		QueryContext queryContext;
		try {
			queryContext = _queryContextBuilder.buildQueryContext(
				httpServletRequest, null, null, null, null, null, locale);

			queryContext.setPageSize(GetterUtil.get(pageSize, 10));
			queryContext.setParameter(ParameterNames.PATH_IMAGE, "/image");

			_queryContextBuilder.parseParameters(queryContext, parameters);

			queryContext.setParameter(
				ParameterNames.INCLUDE_THUMBNAIL,
				GetterUtil.getBoolean(includeThumbnail, false));

			queryContext.setParameter(
				ParameterNames.INCLUDE_USER_PORTRAIT,
				GetterUtil.getBoolean(includeUserPortrait, false));

			Map<String, Class<?>> additionalResultFields =
				new HashMap<String, Class<?>>();

			if (GetterUtil.get(includeAssetCategories, false)) {
				additionalResultFields.put(
					"assetCategoryTitles", String[].class);
			}
			if (GetterUtil.get(includeAssetTags, false)) {
				additionalResultFields.put("assetTagNames", String[].class);
			} 
			additionalResultFields.put("entryClassName", String.class);
			additionalResultFields.put("entryClassPK", String.class);

			queryContext.setParameter(
				ParameterNames.ADDITIONAL_RESULT_FIELDS,
				additionalResultFields);
			
			responseObject = _gsearch.getSearchResults(queryContext);
			
			_localizationHelper.setResultTypeLocalizations(locale, responseObject);
			_localizationHelper.setFacetLocalizations(locale, responseObject);
		}
		catch (Exception e) {
			e.printStackTrace();

		}
		return responseObject.toString();
	}

	@GET
	@Path("/suggestions/{companyId}/{groupId}/{languageId}/{keywords}")
	@Produces({
		MediaType.APPLICATION_JSON
	})
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
			QueryContext queryContext =
				_queryContextBuilder.buildSuggesterQueryContext(
					httpServletRequest, null, companyId, groupId,
					LocaleUtil.fromLanguageId(languageId), keywords);
			results = _gSearchKeywordSuggester.getSuggestions(queryContext);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return results.toString();
	}
	
	@Reference
	private ConfigurationHelper _configurationHelper;

	@Reference
	private GSearch _gsearch;

	@Reference
	private GSearchKeywordSuggester _gSearchKeywordSuggester;

	@Reference
	private LocalizationHelper _localizationHelper;
	
	@Reference
	private QueryContextBuilder _queryContextBuilder;

	@Reference
	private RecommenderService _recommenderService;

	private static final Logger _log = 
					LoggerFactory.getLogger(GSearchRestApplication.class);
}