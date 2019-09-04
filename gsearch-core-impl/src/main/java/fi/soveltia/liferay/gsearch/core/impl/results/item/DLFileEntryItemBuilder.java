
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.impl.util.GSearchUtil;

/**
 * DLFileEntry item type result builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	service = ResultItemBuilder.class
)
public class DLFileEntryItemBuilder extends BaseResultItemBuilder
	implements ResultItemBuilder {

	@Override
	public boolean canBuild(Document document) {

		return NAME.equals(document.get(Field.ENTRY_CLASS_NAME));
	}

	@Override
	public String getThumbnail(QueryContext queryContext, Document document)
		throws Exception {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest) queryContext.getParameter(
				ParameterNames.HTTP_SERVLET_REQUEST);

		PortletRequest portletRequest =
			GSearchUtil.getPortletRequest(httpServletRequest);

		if (portletRequest == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(getDirectLink(httpServletRequest, document));
			sb.append("?imageThumbnail=1");
			return sb.toString();
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		long entryClassPK = Long.valueOf(document.get(Field.ENTRY_CLASS_PK));

		FileEntry fileEntry = _dLAppService.getFileEntry(entryClassPK);

		return DLUtil.getThumbnailSrc(fileEntry, themeDisplay);
	}

	@Override
	public String getLink(QueryContext queryContext, Document document)
		throws Exception {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest) queryContext.getParameter(
				ParameterNames.HTTP_SERVLET_REQUEST);

		PortletRequest portletRequest =
			GSearchUtil.getPortletRequest(httpServletRequest);

		boolean viewResultsInContext = isViewInContext(queryContext);

		String assetPublisherPageURL = getAssetPublisherPageURL(queryContext);

	    return getDirectLink(httpServletRequest, document);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@Override
	public Map<String, String> getMetadata(
		QueryContext queryContext, Document document)
		throws Exception {

		Map<String, String> metaData = new HashMap<String, String>();

		String mimeType = document.get("mimeType");

		// Format

		metaData.put("format", translateMimetype(mimeType));

		// Size

		metaData.put("size", getSize(document));

		// Image metadata

		if (mimeType.startsWith("image_")) {
			setImageMetadata(queryContext, document, metaData);
		}

		return metaData;
	}

	protected String getDirectLink(
		HttpServletRequest httpServletRequest, Document document) {

		StringBundler sb = new StringBundler();

		sb.append(PortalUtil.getPortalURL(httpServletRequest));
		sb.append("/documents/");
		sb.append(document.get(Field.SCOPE_GROUP_ID));
		sb.append("/");
		sb.append(document.get(Field.FOLDER_ID));
		sb.append("/");
		sb.append(document.get("path"));

		return sb.toString();
	}

	/**
	 * Beautify file size
	 * 
	 * @param size
	 * @param locale
	 * @return
	 */
	protected String getSize(Document document) {

		long size = Long.valueOf(document.get("size"));

		StringBundler sb = new StringBundler();

		if (size >= MBYTES) {
			sb.append(Math.round(size / (float) MBYTES)).append(" MB");

		}
		else if (size >= KBYTES) {
			sb.append(Math.round(size / (float) KBYTES)).append(" KB");
		}
		else {
			sb.append(1).append(" KB");
		}
		return sb.toString();
	}

	/**
	 * Get index translated field name for a Tikaraw metadata field.
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	protected String getTikaRawMetadataField(
		QueryContext queryContext, String key)
		throws Exception {

		Locale locale =
			(Locale) queryContext.getParameter(ParameterNames.LOCALE);

		StringBundler sb = new StringBundler();

		sb.append("ddm__text__");
		sb.append(String.valueOf(getTikaRawStructureId(queryContext)));
		sb.append("__TIFF_IMAGE_");
		sb.append(key);
		sb.append("_");
		sb.append(locale.toString());

		return sb.toString();
	}

	/**
	 * Get the id for structure holding image metadata ("TIKARAWMETADATA") Using
	 * static map here to reduce DB queries.
	 * 
	 * @param queryContext
	 * @return
	 * @throws Exception
	 */
	protected long getTikaRawStructureId(QueryContext queryContext)
		throws Exception {

		long companyId =
			(long) queryContext.getParameter(ParameterNames.COMPANY_ID);

		if (TIKARAW_STRUCTURE_ID_MAP == null ||
			TIKARAW_STRUCTURE_ID_MAP.get(companyId) == null) {

			DynamicQuery structureQuery =
				_ddmStructureLocalService.dynamicQuery();
			structureQuery.add(
				RestrictionsFactoryUtil.eq("structureKey", "TIKARAWMETADATA"));
			structureQuery.add(
				RestrictionsFactoryUtil.eq("companyId", companyId));

			List<DDMStructure> structures =
				DDMStructureLocalServiceUtil.dynamicQuery(structureQuery);

			DDMStructure structure = structures.get(0);

			TIKARAW_STRUCTURE_ID_MAP = new HashMap<Long, Long>();

			TIKARAW_STRUCTURE_ID_MAP.put(companyId, structure.getStructureId());

		}

		return TIKARAW_STRUCTURE_ID_MAP.get(companyId);
	}

	/**
	 * Set image metadata.
	 * 
	 * @param metaData
	 * @throws Exception
	 */
	protected void setImageMetadata(
		QueryContext queryContext, Document document,
		Map<String, String> metaData)
		throws Exception {

		// Dimensions

		StringBundler sb = new StringBundler();
		sb.append(document.get(getTikaRawMetadataField(queryContext, "WIDTH")));
		sb.append(" x ");
		sb.append(
			document.get(getTikaRawMetadataField(queryContext, "LENGTH")));
		sb.append(" px");

		metaData.put("dimensions", sb.toString());
	}

	/**
	 * Translate mimetype for UI
	 * 
	 * @param mimeType
	 * @return
	 */
	protected String translateMimetype(String mimeType) {

		if (mimeTypes.containsKey(mimeType)) {
			return mimeTypes.get(mimeType);
		}
		else if (mimeType.startsWith("application_")) {
			return mimeType.split("application_")[1];
		}
		else if (mimeType.startsWith("image_")) {
			return mimeType.split("image_")[1];
		}
		else if (mimeType.startsWith("text_")) {
			return mimeType.split("text_")[1];
		}
		else if (mimeType.startsWith("video_")) {
			return mimeType.split("video_")[1];
		}

		return mimeType;
	}

	protected static Map<String, String> mimeTypes;

	static {
		mimeTypes = new HashMap<String, String>();

		mimeTypes.put(
			"application_vnd.openxmlformats-officedocument.wordprocessingml.document",
			"docx");
		mimeTypes.put(
			"application_vnd.openxmlformats-officedocument.presentationml.presentation",
			"pptx");
		mimeTypes.put(
			"application_vnd.openxmlformats-officedocument.spreadsheetml.sheet",
			"xlsx");

		mimeTypes.put("application_vnd.ms-excel", "xls");
		mimeTypes.put("application_vnd.ms-powerpoint", "ppt");
		mimeTypes.put("application_vnd.ms-word", "doc");

		mimeTypes.put("application_vnd.oasis.opendocument.presentation", "odp");
		mimeTypes.put("application_vnd.oasis.opendocument.spreadsheet", "ods");
		mimeTypes.put("application_vnd.oasis.opendocument.text", "odt");
	}

	protected static final long KBYTES = 1024;
	protected static final long MBYTES = 1024 * 1024;

	protected Map<Long, Long> TIKARAW_STRUCTURE_ID_MAP = null;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private DLAppService _dLAppService;

	private static final String NAME = DLFileEntry.class.getName();
}
