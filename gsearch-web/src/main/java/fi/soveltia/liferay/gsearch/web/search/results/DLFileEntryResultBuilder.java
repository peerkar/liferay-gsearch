
package fi.soveltia.liferay.gsearch.web.search.results;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * DLFileEntry Result Builder
 * 
 * @author Petteri Karttunen
 */
public class DLFileEntryResultBuilder extends BaseResultBuilder {

	public DLFileEntryResultBuilder(
		ResourceRequest resourceRequest,
		ResourceResponse resourceResponse, Document document) {
		super(resourceRequest, resourceResponse, document);
	}

	@Override
	public String getLink() {

		StringBundler sb = new StringBundler();
		sb.append(PortalUtil.getPortalURL(_resourceRequest));
		sb.append("/documents/");
		sb.append(_document.get(Field.SCOPE_GROUP_ID));
		sb.append("/");
		sb.append(_document.get(Field.FOLDER_ID));
		sb.append("/");
		sb.append(_document.get("path"));

		return sb.toString();
	}
}
