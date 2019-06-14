
package fi.soveltia.liferay.gsearch.morelikethis.util;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.portal.kernel.exception.PortalException;

import javax.portlet.ResourceRequest;

/**
 * Asset helper.
 * 
 * @author Petteri Karttunen
 *
 */
public interface AssetHelper {

	/**
	 * Try to find an Asset Entry information in the request parameters.
	 * 
	 * @param resourceRequest
	 * @return
	 * @throws NumberFormatException
	 * @throws PortalException
	 */
	public AssetEntry findAssetEntry(ResourceRequest resourceRequest)
		throws NumberFormatException, PortalException;
}
