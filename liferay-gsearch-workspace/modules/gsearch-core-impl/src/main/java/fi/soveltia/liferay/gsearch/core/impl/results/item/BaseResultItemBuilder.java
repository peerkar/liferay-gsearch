
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.document.Document;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.WindowState;

import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationNames;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.impl.query.QueryBuilderImpl;
import fi.soveltia.liferay.gsearch.core.impl.util.GSearchUtil;

/**
 * Abstract base result builder class.
 *
 * @author Petteri Karttunen
 */
public abstract class BaseResultItemBuilder implements ResultItemBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDate(QueryContext queryContext, Document document)
		throws Exception {

		Locale locale = (Locale)queryContext.getParameter(
			ParameterNames.LOCALE);

		String dateString = "";

		try {
			String modified = document.getDate(Field.MODIFIED_DATE);
			
			if (!Validator.isBlank(modified)) {
				Date lastModified = QueryBuilderImpl.INDEX_DATE_FORMAT.parse(
					modified);

				DateFormat dateFormat = DateFormat.getDateInstance(
					DateFormat.SHORT, locale);

				dateString = dateFormat.format(lastModified);
			}
		}
		catch (Exception e) {
			_log.error("Error in getting date for:" + 
					document.getString(buildLocalizedFieldName(Field.TITLE, locale)), e);
		}

		return dateString;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription(QueryContext queryContext, Document document)
		throws Exception {

		int descriptionMaxLength = GetterUtil.getInteger(
				(Integer)queryContext.getConfiguration(
						ConfigurationNames.RESULT_DESCRIPTION_MAX_LENGTH), 700);
		
		Locale locale = (Locale)queryContext.getParameter(
			ParameterNames.LOCALE);

		String description = 
				getStringFieldContent(
						document, Field.CONTENT, locale);

		return GSearchUtil.stripHTML(description, descriptionMaxLength);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLink(QueryContext queryContext, Document document)
		throws Exception {

		// Don't even try to create links if we do not have a portletrequest.

		PortletRequest portletRequest =
			GSearchUtil.getPortletRequestFromContext(queryContext);

		if (portletRequest == null) {
			return null;
		}

		PortletResponse portletResponse =
			GSearchUtil.getPortletResponseFromContext(queryContext);

		boolean viewResultsInContext = isViewInContext(queryContext);

		StringBundler sb = new StringBundler();

		if (viewResultsInContext) {
			sb.append(
				getAssetRenderer(
					document
				).getURLViewInContext(
					(LiferayPortletRequest)portletRequest,
					(LiferayPortletResponse)portletResponse, ""
				));
		}
		
		// If no view-in-context or URL failed 
		// (for example no Wiki portlet available)
		
		if (sb.length() == 0) {
			sb.append(
				getAssetRenderer(
					document
				).getURLView(
					(LiferayPortletResponse)portletResponse,
					WindowState.MAXIMIZED
				));
		}

		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getMetadata(
			QueryContext queryContext, Document document)
		throws Exception {

		return null;
	}

	/**
	 * Get a view url for an article which is not bound to a layout or has a
	 * default view page.
	 *
	 * @return url string
	 * @throws PortalException
	 */
	public String getNotLayoutBoundJournalArticleUrl(
			PortletRequest portletRequest, JournalArticle journalArticle,
			String assetPublisherPageFriendlyURL)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = GSearchUtil.getLayoutByFriendlyURL(
			portletRequest, assetPublisherPageFriendlyURL);

		String assetPublisherInstanceId =
			GSearchUtil.findDefaultAssetPublisherInstanceId(layout);

		StringBundler sb = new StringBundler();

		sb.append(PortalUtil.getLayoutFriendlyURL(layout, themeDisplay));
		sb.append("/-/asset_publisher/");
		sb.append(assetPublisherInstanceId);
		sb.append("/content/");
		sb.append(journalArticle.getUrlTitle());
		sb.append("?_");
		sb.append(AssetPublisherPortletKeys.ASSET_PUBLISHER);
		sb.append("_INSTANCE_");
		sb.append(assetPublisherInstanceId);
		sb.append("_groupId=");
		sb.append(journalArticle.getGroupId());

		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws Exception
	 */
	@Override
	public String getThumbnail(QueryContext queryContext, Document document)
		throws Exception {

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle(
			QueryContext queryContext, Document document)
		throws Exception {

		Locale locale = (Locale)queryContext.getParameter(
			ParameterNames.LOCALE);

		String title = getStringFieldContent(
				document, Field.TITLE, locale);

		return GSearchUtil.stripHTML(title, -1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getType(Document document) {
		return document.getString(Field.ENTRY_CLASS_NAME);
	}

	/**
	 * Gets AP page URL.
	 * 
	 * @param queryContext
	 * @return
	 */
	protected String getAssetPublisherPageURL(QueryContext queryContext) {
		return (String)queryContext.getParameter(
			ParameterNames.ASSET_PUBLISHER_URL);
	}

	/**
	 * Gets asset renderer.
	 *
	 * @return asset renderer object specific for the item type
	 * @throws PortalException
	 * @throws NumberFormatException
	 */
	protected AssetRenderer<?> getAssetRenderer(Document document)
		throws NumberFormatException, PortalException {

		String entryClassName = document.getString(
				Field.ENTRY_CLASS_NAME);
		long entryClassPK = Long.valueOf(document.getString(
				Field.ENTRY_CLASS_PK));

		return getAssetRenderer(entryClassName, entryClassPK);
	}

	/**
	 * Gets asset renderer for a class.
	 *
	 * @param entryClassName
	 * @param entryClassPK
	 * @return
	 * @throws NumberFormatException
	 * @throws PortalException
	 */
	protected AssetRenderer<?> getAssetRenderer(
			String entryClassName, long entryClassPK)
		throws PortalException {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				entryClassName);

		return assetRendererFactory.getAssetRenderer(entryClassPK);
	}

	/**
	 * Get string field content 
	 * trying all the localized variants.
	 * 
	 * @param document
	 * @param field
	 * @param locale
	 * @param highlight
	 * @return
	 */
	protected String getStringFieldContent(
			Document document, String field, Locale locale) {
	
		String fieldName = null;	
		String value = null;	
			
		if (Validator.isNull(value)) {
			
			fieldName = 
					buildLocalizedFieldName(field, locale);
			value = document.getString(fieldName);
		}
	
		if (Validator.isNull(value)) {
			
			fieldName = 
					buildNewLocalizedFieldName(field, locale);
			value = document.getString(fieldName);
	
		}
	
		// Try once more on non localized legacy field as 
		// some assets like Wiki still might have it.
		
		if (Validator.isNull(value)) {
			value = document.getString(value);
		}
		
		return value;
	}
	
	/**
	 * Gets indexer for the item class name.
	 *
	 * @param name
	 *            of the item class
	 * @return indexer object
	 */
	protected Indexer<Object> getIndexer(String className) {
		return IndexerRegistryUtil.getIndexer(className);
	}
	
	/**
	 * Builds a localized field name.
	 * 
	 * @param field
	 * @param locale
	 * @return
	 */
	protected String buildLocalizedFieldName(String field, Locale locale) {

		StringBundler sb = new StringBundler();
		sb.append(field);
		sb.append(StringPool.UNDERLINE);
		sb.append(locale.toString());
		
		return sb.toString();
	}
	
	/**
	 * Builds a localized field name.
	 * 
	 * Means fields starting with "localized".
	 * 
	 * @param field
	 * @param locale
	 * @return
	 */
	protected String buildNewLocalizedFieldName(String field, Locale locale) {

		StringBundler sb = new StringBundler();
		sb.append("localized");
		sb.append(StringPool.UNDERLINE);
		sb.append(field);
		sb.append(StringPool.UNDERLINE);
		sb.append(locale.toString());
		
		return sb.toString();
	}

	/**
	 * Builds a localized snippet field name.
	 * 
	 * @param field
	 * @param locale
	 * @return
	 */
	protected String buildLocalizedSnippetFieldName(String field, Locale locale) {

		StringBundler sb = new StringBundler();
		sb.append(Field.SNIPPET);
		sb.append(StringPool.UNDERLINE);
		sb.append(field);
		sb.append(StringPool.UNDERLINE);
		sb.append(locale.toString());
		
		return sb.toString();
	}
	
	/**
	 * Checks if view in context is set.
	 * 
	 * @param queryContext
	 * @return
	 */
	protected boolean isViewInContext(QueryContext queryContext) {
		return GetterUtil.getBoolean(
			queryContext.getParameter(ParameterNames.VIEW_RESULTS_IN_CONTEXT),
			true);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseResultItemBuilder.class);

}