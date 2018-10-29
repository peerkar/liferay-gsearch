
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.WindowState;

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

	/**
	 * {@inheritDoc}
	 *
	 * @throws Exception
	 */
	@Override
	public String getImageSrc(PortletRequest portletRequest, long entryClassPK) {
		return "icon-file-text";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLink(PortletRequest portletRequest, PortletResponse portletResponse, Document document, String assetPublisherPageFriendlyURL, long entryClassPK) {

		StringBundler sb = new StringBundler();
		sb.append(PortalUtil.getPortalURL(portletRequest));
		sb.append("/documents/");
		sb.append(document.get(Field.SCOPE_GROUP_ID));
		sb.append("/");
		sb.append(document.get(Field.FOLDER_ID));
		sb.append("/");
		sb.append(document.get("path"));

		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws Exception
	 */
	@Override
	public Map<String, String> getMetadata(Document document, Locale locale, long companyId)
		throws Exception {

		Map<String, String> metaData = new HashMap<String, String>();

		String mimeType = document.get("mimeType");

		// Format

		metaData.put("format", translateMimetype(mimeType));

		// Size

		metaData.put("size", getSize(document));

		// Image metadata

		if (mimeType.startsWith("image_")) {
			setImageMetadata(metaData, document, locale, companyId);
		}

		return metaData;
	}

	@Override
	public String getTitle(PortletRequest portletRequest, PortletResponse portletResponse, Document document, Locale locale, long entryClassPK) throws NumberFormatException, PortalException {

			String title = getSummary(portletRequest, portletResponse, document).getTitle();

			if (Validator.isNull(title)) {
				title = getAssetRenderer(DLFileEntry.class.getName(), entryClassPK).getTitle(locale);
			}
			return HtmlUtil.stripHtml(title);
	}

	@Override
	public String getType() {
		return "file";
	}

	@Override
	public String getBreadcrumbs(PortletRequest portletRequest, PortletResponse portletResponse, Document document, String assetPublisherPageFriendlyURL, long entryClassPK) throws Exception {
		return "";
	}

	/**
	 * Set image metadata.
	 *
	 * @param metaData
	 * @throws Exception
	 */
	private void setImageMetadata(Map<String, String> metaData, Document document, Locale locale, long companyId)
		throws Exception {

		// Dimensions

		StringBundler sb = new StringBundler();
		sb.append(document.get(getTikaRawMetadataField("WIDTH", locale, companyId)));
		sb.append(" x ");
		sb.append(document.get(getTikaRawMetadataField("LENGTH", locale, companyId)));
		sb.append(" px");

		metaData.put("dimensions", sb.toString());
	}

	/**
	 * Translate mimetype for UI
	 *
	 * @param mimeType
	 * @return
	 */
	private String translateMimetype(String mimeType) {

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

	/**
	 * Beautify file size
	 *
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
	protected String getTikaRawMetadataField(String key, Locale locale, long companyId)
		throws Exception {

		StringBundler sb = new StringBundler();

		sb.append("ddm__text__");
		sb.append(String.valueOf(getTikaRawStructureId(companyId)));
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
	 * @return
	 * @throws Exception
	 */
	protected long getTikaRawStructureId(long companyId)
		throws Exception {

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

	@Reference(unbind = "-")
	protected void setDDMStructureLocalService(
		DDMStructureLocalService ddmStructureLocalService) {

		_ddmStructureLocalService = ddmStructureLocalService;
	}

	private static Map<String, String> mimeTypes;

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

	private static final long KBYTES = 1024;
	private static final long MBYTES = 1024 * 1024;

	private Map<Long, Long> TIKARAW_STRUCTURE_ID_MAP = null;

	private static DDMStructureLocalService _ddmStructureLocalService;

	private static final String NAME = DLFileEntry.class.getName();
}
