
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.publisher.web.constants.AssetPublisherPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.theme.ThemeDisplay;
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
	public String getDate()
		throws ParseException {

		String dateString = "";

		try {
			if (Validator.isNotNull(_document.get(Field.MODIFIED_DATE))) {

				Date lastModified = QueryBuilderImpl.INDEX_DATE_FORMAT.parse(
					_document.get(Field.MODIFIED_DATE));

				DateFormat dateFormat =
					DateFormat.getDateInstance(DateFormat.SHORT, _locale);
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
	public String getDescription()
		throws SearchException {

		return HtmlUtil.stripHtml(getSummary().getContent());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws Exception
	 */
	@Override
	public String getImageSrc()
		throws Exception {

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLink()
		throws Exception {

		StringBundler sb = new StringBundler();
		sb.append(
			getAssetRenderer().getURLView(
				(LiferayPortletResponse) _portletResponse,
				WindowState.MAXIMIZED));

		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getMetadata()
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
		JournalArticle journalArticle)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) _portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		Layout layout = GSearchUtil.getLayoutByFriendlyURL(
			_portletRequest, _assetPublisherPageFriendlyURL);

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
	 */
	@Override
	public String[] getTags() {

		String[] tags = _document.getValues(Field.ASSET_TAG_NAMES);

		return tags;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle()
		throws NumberFormatException, PortalException {

		String title = getSummary().getTitle();

		if (Validator.isNull(title)) {
			title = getAssetRenderer().getTitle(_locale);
		}
		return HtmlUtil.stripHtml(title);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getType() {

		return _entryClassName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getBreadcrumbs() throws Exception {
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperties(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, String assetPublisherPageFriendlyURL) {

		_assetPublisherPageFriendlyURL = assetPublisherPageFriendlyURL;
		_portletRequest = portletRequest;
		_portletResponse = portletResponse;
		_document = document;
		_locale = _portletRequest.getLocale();
		_entryClassName = _document.get(Field.ENTRY_CLASS_NAME);
		_entryClassPK = Long.valueOf(_document.get(Field.ENTRY_CLASS_PK));
		_assetRenderer = null;
		_summary = null;
	}

	/**
	 * s Get AssetRenderer
	 *
	 * @return asset renderer object specific for the item type
	 * @throws PortalException
	 * @throws NumberFormatException
	 */
	protected AssetRenderer<?> getAssetRenderer()
		throws NumberFormatException, PortalException {

		if (_assetRenderer == null) {

			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
					_entryClassName);
			_assetRenderer =
				assetRendererFactory.getAssetRenderer(_entryClassPK);
		}

		return _assetRenderer;
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
	 * @return document summary object
	 * @throws SearchException
	 */
	protected Summary getSummary()
		throws SearchException {

		if (_summary == null) {

			Indexer<?> indexer =
				getIndexer(_document.get(Field.ENTRY_CLASS_NAME));

			if (indexer != null) {
				String snippet = _document.get(Field.SNIPPET);

				_summary = indexer.getSummary(
					_document, snippet, _portletRequest, _portletResponse);

				_summary.setHighlight(true);

				return _summary;
			}
		}

		return _summary;
	}

	protected String _assetPublisherPageFriendlyURL;
	protected Document _document;
	protected String _entryClassName;
	protected long _entryClassPK;
	protected Locale _locale;
	protected PortletRequest _portletRequest;
	protected PortletResponse _portletResponse;

	private AssetRenderer<?> _assetRenderer;
	private Summary _summary = null;

	private static final Log _log =
		LogFactoryUtil.getLog(BaseResultItemBuilder.class);
}
