
package fi.soveltia.liferay.gsearch.web.search.internal.results.item;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.web.constants.GSearchWebKeys;

/**
 * DLFileEntry item type result builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true
)
public class DLFileEntryItemBuilder extends BaseResultItemBuilder {

	/**
	 * {@inheritDoc}
	 * @throws Exception 
	 */
	@Override
	public String getImageSrc() throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)_portletRequest.getAttribute(GSearchWebKeys.THEME_DISPLAY);
		
		FileEntry fileEntry = _dLAppService.getFileEntry(_entryClassPK);
		
		return DLUtil.getThumbnailSrc(fileEntry, themeDisplay);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLink() {

		StringBundler sb = new StringBundler();
		sb.append(PortalUtil.getPortalURL(_portletRequest));
		sb.append("/documents/");
		sb.append(_document.get(Field.SCOPE_GROUP_ID));
		sb.append("/");
		sb.append(_document.get(Field.FOLDER_ID));
		sb.append("/");
		sb.append(_document.get("path"));

		return sb.toString();
	}
	
	@Reference(unbind = "-")
	protected void setDLAppService(
		DLAppService dLAppService) {

		_dLAppService = dLAppService;
	}

	private static DLAppService _dLAppService;
}
