
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

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
	public String getDate(Document document, Locale locale)
		throws ParseException {

		String dateString = "";

		try {
			if (Validator.isNotNull(document.get(Field.MODIFIED_DATE))) {

				DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
				Date lastModified = df.parse(
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
	public String getDescription(PortletRequest portletRequest, PortletResponse portletResponse, Document document, Locale locale)
		throws SearchException {

		return HtmlUtil.stripHtml(getSummary(portletRequest, portletResponse, document).getContent());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws Exception
	 */
	@Override
	public String getImageSrc(PortletRequest portletRequest, long entryClassPK)
		throws Exception {

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract String getLink(PortletRequest portletRequest, PortletResponse portletResponse, Document document, String assetPublisherPageFriendlyURL, long entryClassPK)
		throws Exception;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getMetadata(Document document, Locale locale, long companyId)
		throws Exception {

		return null;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getTags(Document document) {
		return document.getValues(Field.ASSET_TAG_NAMES);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract String getTitle(PortletRequest portletRequest, PortletResponse portletResponse, Document document, Locale locale, long entryClassPK)
		throws NumberFormatException, PortalException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract String getType();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getBreadcrumbs(PortletRequest portletRequest, PortletResponse portletResponse, Document document, String assetPublisherPageFriendlyURL, long entryClassPK) throws Exception {
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getCategories(Document document, Locale locale) throws Exception {
		return new String[] {};
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
	 * @param className name of the item class
	 * @return indexer object
	 */
	Indexer<Object> getIndexer(String className) {

		return IndexerRegistryUtil.getIndexer(className);
	}

	/**
	 * Get document summary.
	 *
	 * @return document summary object
	 * @throws SearchException
	 */
	protected Summary getSummary(PortletRequest portletRequest, PortletResponse portletResponse, Document document)
		throws SearchException {

			Indexer<?> indexer =
				getIndexer(document.get(Field.ENTRY_CLASS_NAME));

			Summary summary = null;

			if (indexer != null) {
				String snippet = document.get(Field.SNIPPET);

				try {
					summary = indexer.getSummary(
						document, snippet, portletRequest, portletResponse);
					summary.setHighlight(true);
					summary.setMaxContentLength(300);
				} catch (Exception e) {
					_log.error(String.format("Cannot get summary for '%s'", document.get(Field.ENTRY_CLASS_PK)));
				}
			}

		return summary;
	}

	private AssetRenderer _assetRenderer;

	private static final Log _log =
		LogFactoryUtil.getLog(BaseResultItemBuilder.class);
}
