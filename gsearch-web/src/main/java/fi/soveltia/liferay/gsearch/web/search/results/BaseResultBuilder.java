
package fi.soveltia.liferay.gsearch.web.search.results;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.WindowState;

import fi.soveltia.liferay.gsearch.web.portlet.GsearchWebPortlet;
import fi.soveltia.liferay.gsearch.web.search.query.QueryBuilderImpl;

/**
 * Base result builder.
 * 
 * @author Petteri Karttunen
 */
public abstract class BaseResultBuilder implements ResultBuilder {

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

	@Override
	public String getDescription()
		throws SearchException {

		return HtmlUtil.stripHtml(getSummary().getContent());
	}

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

	@Override
	public String getTitle()
		throws NumberFormatException, PortalException {

		String title = getSummary().getTitle();

		if (Validator.isNull(title)) {
			title = getAssetRenderer().getTitle(_locale);
		}
		return HtmlUtil.stripHtml(title);
	}

	@Override
	public String getType() {

		return LanguageUtil.get(_resourceBundle, _entryClassName);
	}

	@Override
	public void setProperties(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, String assetPublisherPageFriendlyURL) {

		_assetPublisherPageFriendlyURL = assetPublisherPageFriendlyURL;
		_portletRequest = portletRequest;
		_portletResponse = portletResponse;
		_document = document;
		_locale = _portletRequest.getLocale();
		_resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", _locale, GsearchWebPortlet.class);
		_entryClassName = _document.get(Field.ENTRY_CLASS_NAME);
		_entryClassPK = Long.valueOf(_document.get(Field.ENTRY_CLASS_PK));
	}

	/**s
	 * Get AssetRenderer
	 * 
	 * @return
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
	 * Get indexer for class
	 * 
	 * @param className
	 * @return
	 */
	protected Indexer<Object> getIndexer(String className) {

		if (_indexerRegistry != null) {
			return _indexerRegistry.getIndexer(className);
		}

		return IndexerRegistryUtil.getIndexer(className);
	}

	/**
	 * Get document summary
	 * 
	 * @return Summary
	 * @throws SearchException
	 */
	protected Summary getSummary()
		throws SearchException {

		if (_summary == null) {

			Indexer<?> indexer =
				getIndexer(_document.get(Field.ENTRY_CLASS_NAME));

			if (indexer != null) {
				String snippet = _document.get(Field.SNIPPET);

				Summary summary = indexer.getSummary(
					_document, snippet, _portletRequest, _portletResponse);

				summary.setHighlight(true);

				return summary;
			}
		}

		return _summary;
	}


	protected String _assetPublisherPageFriendlyURL;
	protected Document _document;
	protected String _entryClassName;
	protected long _entryClassPK;
	protected IndexerRegistry _indexerRegistry;
	protected Locale _locale;
	protected PortletRequest _portletRequest;
	protected PortletResponse _portletResponse;
	protected ResourceBundle _resourceBundle;

	private AssetRenderer<?> _assetRenderer;
	private Summary _summary = null;

	private static final Log _log =
		LogFactoryUtil.getLog(BaseResultBuilder.class);
}
