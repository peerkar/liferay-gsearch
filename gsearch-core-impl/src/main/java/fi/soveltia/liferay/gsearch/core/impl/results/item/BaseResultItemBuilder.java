
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
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.WindowState;

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
		throws ParseException {

		Locale locale =
			(Locale) queryContext.getParameter(ParameterNames.LOCALE);

		String dateString = "";

		try {
			if (Validator.isNotNull(document.get(Field.MODIFIED_DATE))) {

				Date lastModified = QueryBuilderImpl.INDEX_DATE_FORMAT.parse(
					document.get(Field.MODIFIED_DATE));

				DateFormat dateFormat =
					DateFormat.getDateInstance(DateFormat.SHORT, locale);
				dateString = dateFormat.format(lastModified);
			}
		}
		catch (ParseException e) {
			_log.error(e, e);
		}

		return dateString;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription(QueryContext queryContext, Document document)
		throws SearchException {

		Locale locale =
			(Locale) queryContext.getParameter(ParameterNames.LOCALE);

		String description = null;

		description = document.get(
			locale, Field.SNIPPET + StringPool.UNDERLINE + Field.CONTENT);

		if (Validator.isNull(description)) {

			PortletRequest portletRequest = GSearchUtil.getPortletRequestFromContext(queryContext);
			
			if (portletRequest != null) {
			
				description = getSummary(
					queryContext, document, true,
					DESCRIPTION_MAX_LENGTH).getContent();
			} else {
				description = document.get(Field.CONTENT);
			}
		}

		return stripHTML(description, DESCRIPTION_MAX_LENGTH);
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
				getAssetRenderer(document).getURLViewInContext(
					(LiferayPortletRequest) portletRequest,
					(LiferayPortletResponse) portletResponse, ""));
		}
		else {
			sb.append(
				getAssetRenderer(document).getURLView(
					(LiferayPortletResponse) portletResponse,
					WindowState.MAXIMIZED));
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

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

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
		QueryContext queryContext, Document document, boolean isHighlight)
		throws NumberFormatException, PortalException {

		Locale locale =
			(Locale) queryContext.getParameter(ParameterNames.LOCALE);

		String title = null;

		if (isHighlight) {

			title = document.get(
				locale, Field.SNIPPET + StringPool.UNDERLINE + Field.TITLE,
				Field.TITLE);
		}

		if (Validator.isNull(title)) {

			title = document.get(locale, Field.TITLE);
		}

		if (Validator.isNull(title)) {

			title = document.get(
				locale, "localized" + StringPool.UNDERLINE + Field.TITLE);
		}

		return stripHTML(title, -1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getType(Document document) {

		return document.get(Field.ENTRY_CLASS_NAME);
	}

	protected String getAssetPublisherPageURL(QueryContext queryContext) {

		return (String) queryContext.getParameter(
			ParameterNames.ASSET_PUBLISHER_URL);
	}

	/**
	 * Get asset renderer.
	 * 
	 * @return asset renderer object specific for the item type
	 * @throws PortalException
	 * @throws NumberFormatException
	 */
	protected AssetRenderer<?> getAssetRenderer(Document document)
		throws NumberFormatException, PortalException {

		String entryClassName = document.get(Field.ENTRY_CLASS_NAME);
		long entryClassPK = Long.valueOf(document.get(Field.ENTRY_CLASS_PK));

		return getAssetRenderer(entryClassName, entryClassPK);
	}

	/**
	 * Get asset renderer for a class.
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
	 * Get indexer for the item class name.
	 * 
	 * @param name
	 *            of the item class
	 * @return indexer object
	 */
	protected Indexer<Object> getIndexer(String className) {

		return IndexerRegistryUtil.getIndexer(className);
	}

	/**
	 * Get document summary.
	 * 
	 * @param queryContext
	 * @param document
	 * @param isHighlight
	 * @return
	 * @throws SearchException
	 */
	protected Summary getSummary(
		QueryContext queryContext, Document document, boolean isHighlight,
		int maxContentLength)
		throws SearchException {

		PortletRequest portletRequest =
			GSearchUtil.getPortletRequestFromContext(queryContext);

		PortletResponse portletResponse =
			GSearchUtil.getPortletResponseFromContext(queryContext);
		
		Indexer<?> indexer = getIndexer(document.get(Field.ENTRY_CLASS_NAME));

		if (indexer != null) {

			String snippet = document.get(Field.CONTENT);

			Summary summary = indexer.getSummary(
				document, snippet, portletRequest, portletResponse);

			summary.setMaxContentLength(maxContentLength);
			summary.setHighlight(isHighlight);

			return summary;
		}

		return null;
	}

	protected boolean isViewInContext(QueryContext queryContext) {

		return GetterUtil.getBoolean(
			queryContext.getParameter(ParameterNames.VIEW_RESULTS_IN_CONTEXT),
			true);
	}

	/**
	 * Replace other than highlight HTML.
	 * 
	 * (Should be done in the adapter)
	 * 
	 * @param html
	 * @param length
	 * @return
	 */
	protected String stripHTML(String string, int length) {

		// Replace other than highlight tags.
		
		string = string.replaceAll("<liferay-hl>", "---LR-HL-START---");
		string = string.replaceAll("</liferay-hl>", "---LR-HL-STOP---");
		string = HtmlUtil.stripHtml(string);
		string = string.replaceAll("---LR-HL-START---", "<liferay-hl>");
		string = string.replaceAll("---LR-HL-STOP---", "</liferay-hl>");
					
		if (length > -1 && string.length() > length) {
			
			String temp = string.substring(0, length);

			// Check that we are not breaking the HTML.
			
			if (temp.lastIndexOf("<") > temp.lastIndexOf(">")) {
			
				temp = string.substring(0, 1 + string.indexOf('>', temp.lastIndexOf('<')));
			}
				
			string = temp.concat("...");
		}
		string = string.concat("..aaaa.");
		
		return string;
	}
	
	private static final Log _log =
		LogFactoryUtil.getLog(BaseResultItemBuilder.class);

	private static final int DESCRIPTION_MAX_LENGTH = 400;

}
