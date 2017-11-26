package fi.soveltia.liferay.gsearch.core.impl.query.ct;

import com.liferay.content.targeting.service.UserSegmentLocalService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.util.Map;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.query.ct.CTQueryBuilder;
import fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration;

/**
 * CT (Audience Targeting aka Content Targeting aka CT) query builder implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration", 
	immediate = true, 
	service = CTQueryBuilder.class
)
public class CTQueryBuilderImpl implements CTQueryBuilder {

	@Override
	public String buildCTQuery(
		PortletRequest portletRequest)
		throws Exception {

		if (portletRequest.getAttribute("userSegmentIds") == null) {
			return null;
		}
		
		long[] userSegmentIds = (long[])portletRequest.getAttribute("userSegmentIds");

		if (_log.isDebugEnabled()) {
			_log.debug("Found " + userSegmentIds.length + " user segments.");
		}

		StringBundler sb = new StringBundler();
		
		sb.append("AND (");

		for (int i = 0; i < userSegmentIds.length; i++) {

			long ctCategoryId = _userSegmentLocalService.getUserSegment(userSegmentIds[i]).getAssetCategoryId();
						
			if (i > 0) {
				sb.append(" OR ");
			}
			sb.append("assetCategoryIds:");
			sb.append(String.valueOf(ctCategoryId));
			sb.append("^");
			sb.append(_gSearchConfiguration.audienceTargetingBoost());
		}
		sb.append(" OR *)");
				
		return sb.toString();
	}
	

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_gSearchConfiguration = ConfigurableUtil.createConfigurable(
			GSearchConfiguration.class, properties);
	}	
	
	@Reference(unbind = "-")
	protected void setUserSegmentLocalService(UserSegmentLocalService userSegmentLocalService) {

		_userSegmentLocalService = userSegmentLocalService;
	}
	
	protected volatile GSearchConfiguration _gSearchConfiguration;
	
	@Reference
	UserSegmentLocalService _userSegmentLocalService;


	private static final Log _log =
					LogFactoryUtil.getLog(CTQueryBuilderImpl.class);
}


