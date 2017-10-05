
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
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.WindowState;

import fi.soveltia.liferay.gsearch.web.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.web.portlet.GsearchWebPortlet;
import fi.soveltia.liferay.gsearch.web.search.query.QueryBuilder;
import fi.soveltia.liferay.gsearch.web.search.util.GSearchUtil;

/**
 * Base (Default) Result Builder.
 * 
 * @author Petteri Karttunen
 */
public class BaseResultBuilder implements ResultBuilder {

	public BaseResultBuilder(
		ResourceRequest resourceRequest,
		ResourceResponse resourceResponse, Document document) {

		_resourceRequest = resourceRequest;
		_resourceResponse = resourceResponse;
		_document = document;
		_locale = _resourceRequest.getLocale();
		_resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", _locale, GsearchWebPortlet.class);
		_entryClassName = _document.get(Field.ENTRY_CLASS_NAME);
		_entryClassPK = Long.valueOf(_document.get(Field.ENTRY_CLASS_PK));
	}

	@Override
	public String getDate()
		throws ParseException {

		String dateString = "";

		try {
			if (Validator.isNotNull(_document.get(Field.MODIFIED_DATE))) {

				Date lastModified = QueryBuilder.INDEX_DATE_FORMAT.parse(
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
				(LiferayPortletResponse) _resourceResponse,
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

	/**
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

				com.liferay.portal.kernel.search.Summary summary =
					indexer.getSummary(
						_document, snippet, _resourceRequest,
						_resourceResponse);

				summary.setHighlight(true);

				return summary;
			}
		}

		return _summary;
	}

	/**
	 * Get redirect url.
	 * 
	 * @return String
	 * @throws PortalException
	 */
	protected String getRedirectURL()
		throws PortalException {

		StringBundler sb = new StringBundler();

		sb.append(GSearchUtil.getCurrentLayoutURL(_resourceRequest));
		sb.append("?");
		sb.append(GSearchWebKeys.KEYWORDS).append("=").append(
			ParamUtil.getString(_resourceRequest, GSearchWebKeys.KEYWORDS));
		sb.append("&").append(GSearchWebKeys.SCOPE_FILTER).append("=").append(
			ParamUtil.getString(_resourceRequest, GSearchWebKeys.SCOPE_FILTER));
		sb.append("&").append(GSearchWebKeys.TIME_FILTER).append("=").append(
			ParamUtil.getString(_resourceRequest, GSearchWebKeys.TIME_FILTER));
		sb.append("&").append(GSearchWebKeys.TYPE_FILTER).append("=").append(
			ParamUtil.getString(_resourceRequest, GSearchWebKeys.TYPE_FILTER));
		sb.append("&").append(GSearchWebKeys.SORT_FIELD).append("=").append(
			ParamUtil.getString(_resourceRequest, GSearchWebKeys.SORT_FIELD));
		sb.append("&").append(GSearchWebKeys.SORT_DIRECTION).append("=").append(
			ParamUtil.getString(
				_resourceRequest, GSearchWebKeys.SORT_DIRECTION));
		sb.append("&").append(GSearchWebKeys.START).append("=").append(
			ParamUtil.getString(_resourceRequest, GSearchWebKeys.START));

		return HtmlUtil.escapeURL(sb.toString());
	}

	protected Document _document;
	protected Locale _locale;
	protected String _entryClassName;
	protected long _entryClassPK;

	protected ResourceRequest _resourceRequest;
	protected ResourceResponse _resourceResponse;

	protected IndexerRegistry _indexerRegistry;
	protected ResourceBundle _resourceBundle;

	private AssetRenderer<?> _assetRenderer;
	private Summary _summary = null;

	private static final Log _log =
		LogFactoryUtil.getLog(BaseResultBuilder.class);
}
