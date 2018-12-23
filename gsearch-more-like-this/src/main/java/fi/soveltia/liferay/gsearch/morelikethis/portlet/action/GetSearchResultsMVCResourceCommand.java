
package fi.soveltia.liferay.gsearch.morelikethis.portlet.action;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.blogs.kernel.service.BlogsEntryLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiPageLocalService;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.GSearch;
import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.morelikethis.constants.GSearchMoreLikeThisPortletKeys;

/**
 * Resource command for getting the search results.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	property = {
		"javax.portlet.name=" + GSearchMoreLikeThisPortletKeys.MORE_LIKE_THIS_PORTLET,
		"mvc.command.name=get_search_results"
	}, 
	service = MVCResourceCommand.class
)
public class GetSearchResultsMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("GetSearchResultsMVCResourceCommand.doServeResource()");
		}

		AssetEntry assetEntry =
			getAssetEntry(resourceRequest, resourceResponse);

		System.out.println(assetEntry);

		if (assetEntry == null) {
			return;
		}

		try {
			getMoreLikeThis(resourceRequest, resourceResponse, assetEntry);
		}
		catch (Exception e) {
			_log.error(e, e);
		}
	}

	/**
	 * Try to find the assetentry for the contents being shown on asset
	 * publisher.
	 * 
	 * @param resourceRequest
	 * @param resourceResponse
	 * @return
	 * @throws PortalException
	 * @throws NumberFormatException
	 */
	private AssetEntry getAssetEntry(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws NumberFormatException, PortalException {

		AssetEntry assetEntry = null;

		HttpServletRequest r =
			PortalUtil.getHttpServletRequest(resourceRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay) r.getAttribute(WebKeys.THEME_DISPLAY);

		String layoutURL =
			themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout());

		String currentFriendlyURL = resourceRequest.getParameter("currentURL");

		String assetUrlOrId = null;
		
		if (currentFriendlyURL.indexOf(layoutURL) < 0) {

			int pos = currentFriendlyURL.indexOf("/-/");

			if (currentFriendlyURL.indexOf("/-/") > 0) {
				assetUrlOrId = currentFriendlyURL.substring((pos + 3));

				JournalArticle journalArticle = getJournalArticle(assetUrlOrId);
				assetEntry = _assetEntryLocalService.getEntry(JournalArticle.class.getName(), journalArticle.getResourcePrimKey());
			}
		}
		else if (currentFriendlyURL.indexOf("/-/asset_publisher") > 0) {

			if (currentFriendlyURL.indexOf(JOURNAL_KEY) > 0) {
				assetUrlOrId = getAssetUrlOrId(currentFriendlyURL, JOURNAL_KEY);
				
				JournalArticle journalArticle = getJournalArticle(assetUrlOrId);
				assetEntry = _assetEntryLocalService.getEntry(JournalArticle.class.getName(), journalArticle.getResourcePrimKey());
				
			} else if (currentFriendlyURL.indexOf(DL_KEY) > 0) {
				assetUrlOrId = getAssetUrlOrId(currentFriendlyURL, DL_KEY);

				assetEntry = _assetEntryLocalService.getAssetEntry(Long.valueOf(assetUrlOrId));

			} else if (currentFriendlyURL.indexOf(WIKI_KEY) > 0) {
				assetUrlOrId = getAssetUrlOrId(currentFriendlyURL, WIKI_KEY);

				assetEntry = _assetEntryLocalService.getAssetEntry(Long.valueOf(assetUrlOrId));

			} else if (currentFriendlyURL.indexOf(BLOG_KEY) > 0) {
				assetUrlOrId = getAssetUrlOrId(currentFriendlyURL, BLOG_KEY);

				BlogsEntry blogsEntry = getBlogsEntry(assetUrlOrId);
				assetEntry = _assetEntryLocalService.getEntry(BlogsEntry.class.getName(), blogsEntry.getPrimaryKey());
			}
		} else {
			
			// Try to get wiki page resourcePrimKey.
	
			String[] urlParts = currentFriendlyURL.split("&");
			
			for (String s : urlParts) {
				
				if (s.startsWith("_com_liferay_wiki_web_portlet_WikiPortlet_pageResourcePrimKey")) {
					
					String[] valueParts = s.split("=");
					assetEntry = _assetEntryLocalService.getEntry(WikiPage.class.getName(), Long.valueOf(valueParts[1]));

					break;
				}
				
			}
		}
		return assetEntry;

	}

	/**
	 * Try to parse a an asset id or friendly url from url.
	 * 
	 * @param currentFriendlyURL
	 * @param key
	 * @return
	 */
	private String getAssetUrlOrId(String currentFriendlyURL, String key) {

		int start = currentFriendlyURL.indexOf(key) + key.length();
		int stop = currentFriendlyURL.indexOf("?");

		if (start > 0 && stop > 0 && start < stop) {
			return currentFriendlyURL.substring(start, stop);
		}

		return null;
	}

	/**
	 * Find blogs entry by urlTitle.
	 * 
	 * @param urlTitle
	 * @return
	 */
	private BlogsEntry getBlogsEntry(String urlTitle) {
		
        DynamicQuery dynamicQuery = _blogsEntryLocalService.dynamicQuery()
            .add(RestrictionsFactoryUtil.eq("urlTitle", urlTitle)).add(RestrictionsFactoryUtil.eq("status", 0));

        List<BlogsEntry> entries = _blogsEntryLocalService.dynamicQuery(dynamicQuery);
        
        if (entries != null && entries.size() > 0) {
        	return entries.get(0);
        }
        return null;
	}

	/**
	 * Try to find the Elasticsearch document uid value for a single contents
	 * being shown.
	 * 
	 * @param resourceRequest
	 * @param resourceResponse
	 * @return
	 * @throws Exception
	 */
	private String getDocUID(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		AssetEntry assetEntry)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);

		PortletPreferences preferences = resourceRequest.getPreferences();
		
		// Build query parameters object.

		QueryParams queryParams = new QueryParams();

		queryParams.setAssetTypeConfiguration(new String[0]);
		queryParams.setClauseConfiguration(getResolveUIDClauseConfiguration(preferences));
		queryParams.setFacetConfiguration(new String[0]);
		queryParams.setSortConfiguration(new String[0]);
		
		queryParams.setCompanyId(themeDisplay.getCompanyId());
		queryParams.setGroupIds(new long[] {assetEntry.getGroupId()});
		queryParams.setLocale(themeDisplay.getLocale());
	
		queryParams.setStart(0);
		queryParams.setEnd(1);
		queryParams.setPageSize(1);

		List<String> entryClassNames = new ArrayList<String>();
		entryClassNames.add(assetEntry.getClassName());
		queryParams.setEntryClassNames(entryClassNames);
		
		queryParams.setAssetPublisherPageURL(preferences.getValue("assetPublisherPage", ""));

		queryParams.setKeywords("entryClassPK: " + assetEntry.getClassPK());

		// This extra param adds docUID to the response.
		// We won't share the UID publicly.
		
		queryParams.addExtraParam("includeDocUID", true);

		// Try to get search results.

		String docUID = null;

		JSONObject responseObject = _gSearch.getSearchResults(
			resourceRequest, resourceResponse, queryParams);

		if (responseObject != null) {
			JSONObject item =
				responseObject.getJSONArray("items").getJSONObject(0);
			docUID = item.getString("uid");
		}

		System.out.println(docUID);
		
		return docUID;
	}

	/**
	 * Find journal article by urlTitle.
	 * 
	 * @param urlTitle
	 * @return
	 */
	private JournalArticle getJournalArticle(String urlTitle) {
		
        DynamicQuery dynamicQuery = _journalArticleLocalService.dynamicQuery()
            .add(RestrictionsFactoryUtil.eq("urlTitle", urlTitle)).add(RestrictionsFactoryUtil.eq("status", 0));

        List<JournalArticle> entries = _journalArticleLocalService.dynamicQuery(dynamicQuery);
        
        if (entries != null && entries.size() > 0) {
        	return entries.get(0);
        }
        return null;
	}	
	
	/**
	 * Get localization.
	 * 
	 * @param key
	 * @param locale
	 * @param objects
	 * @return
	 */
	private String getLocalization(
		String key, Locale locale, Object... objects) {

		if (_resourceBundle == null) {
			_resourceBundle = _resourceBundleLoader.loadResourceBundle(locale);
		}

		String value =
			ResourceBundleUtil.getString(_resourceBundle, key, objects);

		return value == null ? _language.format(locale, key, objects) : value;
	}
	
	/**
	 * Get more like this list.
	 * 
	 * @param resourceRequest
	 * @param resourceResponse
	 * @param entryClassPK
	 * @throws Exception
	 */
	private void getMoreLikeThis(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		AssetEntry assetEntry)
		throws Exception {

		String docUID =
			getDocUID(resourceRequest, resourceResponse, assetEntry);

		if (docUID == null) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);

		PortletPreferences preferences = resourceRequest.getPreferences();

		// Build query parameters object.

		QueryParams queryParams = new QueryParams();
		
		// Set configurations.
		
		queryParams.setClauseConfiguration(getMoreLikeThisClauseConfiguration(preferences));
		queryParams.setFacetConfiguration(new String[0]);
		
		queryParams.setCompanyId(themeDisplay.getCompanyId());
		queryParams.setGroupIds(new long[0]);
		queryParams.setLocale(themeDisplay.getLocale());
		queryParams.setStart(0);
		
		queryParams.setStatus(WorkflowConstants.STATUS_APPROVED);

		int itemsToShow = GetterUtil.getInteger(preferences.getValue("itemsToShow", "5"));
		queryParams.setEnd(itemsToShow - 1);
		queryParams.setPageSize(itemsToShow);

		queryParams.setViewResultsInContext(true);
		queryParams.setAssetPublisherPageURL(preferences.getValue("assetPublisherPage", ""));

		JSONArray classNameArray = JSONFactoryUtil.createJSONArray(preferences.getValue("classNames", ""));

		List<String> entryClassNames = new ArrayList<String>();

		for (Object item : classNameArray) {
			entryClassNames.add((String) item);
		}
		queryParams.setEntryClassNames(entryClassNames);
		
		// This extra param adds docUID to the response.
		// We won't share the UID publicly.

		queryParams.addExtraParam("docUID", docUID);

		String resultLayout = preferences.getValue("resultLayout", "list");
		
		// Include thumbnail?
		
		if (resultLayout.equals("thumbnailList") ||
			resultLayout.equals("image")) {

			queryParams.addExtraParam("includeThumbnail", true);	
		}

		// Include user initials?
		
		if (resultLayout.equals("userImageList") ||
			resultLayout.equals("image")) {

			queryParams.addExtraParam("includeUserPortrait", true);	
		}
		
		// Try to get search results.

		JSONObject responseObject = null;

		try {
			responseObject = _gSearch.getSearchResults(resourceRequest, resourceResponse, queryParams);

			setResultTypeLocalizations(resourceRequest, responseObject);
		}
		catch (Exception e) {

			_log.error(e, e);

			return;
		}
		
		// Write response to output stream.

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, responseObject);
	}
	
	/**
	 * Get query clause configuration for MLT query.
	 * 
	 * @param preferences
	 * @return
	 * @throws JSONException
	 */
	private String[] getMoreLikeThisClauseConfiguration(PortletPreferences preferences) throws JSONException {
		
		JSONArray queryConfiguration = JSONFactoryUtil.createJSONArray(
			preferences.getValue("moreLikeThisClauses", null));

		if (queryConfiguration == null || queryConfiguration.length() == 0) {
			return new String[0];
		}

		String[] configuration = new String[queryConfiguration.length()];
		
		for (int i = 0; i < queryConfiguration.length(); i++) {
			configuration[i] = queryConfiguration.getString(i);
		}
		
		return configuration;
	}

	/**
	 * Get query clause configuration for resolving document UID.
	 * 
	 * @param preferences
	 * @return
	 * @throws JSONException
	 */
	private String[] getResolveUIDClauseConfiguration(PortletPreferences preferences) throws JSONException {
		
		JSONArray queryConfiguration = JSONFactoryUtil.createJSONArray(
			preferences.getValue("resolveUIDClauses", null));

		if (queryConfiguration == null || queryConfiguration.length() == 0) {
			return new String[0];
		}

		String[] configuration = new String[queryConfiguration.length()];
		
		for (int i = 0; i < queryConfiguration.length(); i++) {
			configuration[i] = queryConfiguration.getString(i);
		}
		
		return configuration;
	}	
	
	/**
	 * Localize result types.
	 * 
	 * @param portletRequest
	 * @param responseObject
	 * @throws JSONException 
	 */
	private void setResultTypeLocalizations(PortletRequest portletRequest, JSONObject responseObject) throws JSONException {
		
		String[]configuration = _configurationHelper.getAssetTypeConfiguration();
		
		Locale locale = portletRequest.getLocale();
		
		JSONArray items = responseObject.getJSONArray("items");
		
		if (items == null || items.length() == 0) {
			return;
		}
		
		for (int i = 0; i < items.length(); i++) {
			
			JSONObject resultItem = items.getJSONObject(i);

			for (int j = 0; j < configuration.length; j++) {
				
				JSONObject configurationItem = JSONFactoryUtil.createJSONObject(configuration[i]);
				
				if (configurationItem.getString("entry_class_name").equalsIgnoreCase(resultItem.getString("type"))) {
					resultItem.put("key", configurationItem.getString("key"));
					break;
				}
			}
			
			resultItem.put(
				"type", getLocalization(
					resultItem.getString("type").toLowerCase(), locale));
		}
	}		
		
	
	private static final Log _log =
					LogFactoryUtil.getLog(GetSearchResultsMVCResourceCommand.class);
	
	private static final String JOURNAL_KEY = "/content/";
	private static final String DL_KEY = "/document/id/";
	private static final String BLOG_KEY = "/blog/";
	private static final String WIKI_KEY = "/wiki/id/";

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	BlogsEntryLocalService _blogsEntryLocalService;

	@Reference
	private ConfigurationHelper _configurationHelper;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private GSearch _gSearch;

	@Reference
	private Language _language;
	
	private ResourceBundle _resourceBundle;

	@Reference(
		target = "(bundle.symbolic.name=fi.soveltia.liferay.gsearch.mini.web)", 
		unbind = "-"
	)
	private ResourceBundleLoader _resourceBundleLoader;
	
	@Reference
	private WikiPageLocalService _wikiPageLocalService;
}

