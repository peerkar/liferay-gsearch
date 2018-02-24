package fi.soveltia.liferay.gsearch.ct.query;

import com.liferay.content.targeting.service.UserSegmentLocalService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;

import java.util.Map;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.query.ct.CTQueryBuilder;
import fi.soveltia.liferay.gsearch.ct.configuration.GSearchCTConfiguration;

/**
 * Audience Targeting aka Content Targeting aka CT query builder implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.ct.GSearchCTConfiguration", 
	immediate = true, 
	service = CTQueryBuilder.class
)
public class CTQueryBuilderImpl implements CTQueryBuilder {

	@Override
	public BooleanQuery buildCTQuery(
		PortletRequest portletRequest)
		throws Exception {

		if (portletRequest.getAttribute(USER_SEGMENT_ID_PARAM) == null) {
			return null;
		}
		
		long[] userSegmentIds = (long[])portletRequest.getAttribute(USER_SEGMENT_ID_PARAM);

		if (_log.isDebugEnabled()) {
			_log.debug("Found " + userSegmentIds.length + " user segments.");
		}

		BooleanQuery query = new BooleanQueryImpl();
		
		for (int i = 0; i < userSegmentIds.length; i++) {

			long ctCategoryId = _userSegmentLocalService.getUserSegment(userSegmentIds[i]).getAssetCategoryId();

			TermQuery condition = new TermQueryImpl(Field.ASSET_CATEGORY_IDS, String.valueOf(ctCategoryId)); 
			query.add(condition, BooleanClauseOccur.SHOULD);
		}
				
		query.setBoost(_gSearchCTConfiguration.audienceTargetingBoost());

		return query;
	}

	@Override
	public boolean isEnabled() {

		return _gSearchCTConfiguration.enableAudienceTargeting();
	}
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_gSearchCTConfiguration = ConfigurableUtil.createConfigurable(
			GSearchCTConfiguration.class, properties);
	}	
	
	@Reference(unbind = "-")
	protected void setUserSegmentLocalService(UserSegmentLocalService userSegmentLocalService) {

		_userSegmentLocalService = userSegmentLocalService;
	}
	
	protected static final String USER_SEGMENT_ID_PARAM = "userSegmentIds";
	
	protected volatile GSearchCTConfiguration _gSearchCTConfiguration;
	
	private UserSegmentLocalService _userSegmentLocalService;

	private static final Log _log =
					LogFactoryUtil.getLog(CTQueryBuilderImpl.class);

}