
package fi.soveltia.liferay.gsearch.web.search.results;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;

import org.osgi.service.component.annotations.Component;

/**
 * DLFileEntry Result Builder
 * 
 * @author Petteri Karttunen
 */
@Component(immediate = true)
public class DLFileEntryResultBuilder extends BaseResultBuilder {

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
}
