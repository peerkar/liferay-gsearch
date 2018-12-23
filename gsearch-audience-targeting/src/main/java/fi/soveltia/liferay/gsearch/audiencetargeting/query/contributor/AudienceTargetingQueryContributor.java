
package fi.soveltia.liferay.gsearch.audiencetargeting.query.contributor;

import com.liferay.content.targeting.service.UserSegmentLocalService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.audiencetargeting.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.audiencetargeting.constants.GSearchAudienceTargetingConstants;
import fi.soveltia.liferay.gsearch.core.api.query.contributor.QueryContributor;

/**
 * Audience Targeting query contributor.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.audiencetargeting.configuration.ModuleConfiguration", 
	immediate = true, 
	service = QueryContributor.class
)
public class AudienceTargetingQueryContributor implements QueryContributor {

	@Override
	public BooleanQuery buildQuery(PortletRequest portletRequest)
		throws Exception {

		if (!isEnabled()) {
			return null;
		}

		long[] userSegmentIds = (long[]) portletRequest.getAttribute(
			GSearchAudienceTargetingConstants.USER_SEGMENT_ID_PARAM);

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Found " + (userSegmentIds != null && userSegmentIds.length > 0
					? userSegmentIds.length : 0) + " user segments.");
		}

		if (userSegmentIds == null) {
			return null;
		}

		BooleanQuery query = new BooleanQueryImpl();

		for (int i = 0; i < userSegmentIds.length; i++) {

			long atCategoryId = _userSegmentLocalService.getUserSegment(
				userSegmentIds[i]).getAssetCategoryId();

			TermQuery condition = new TermQueryImpl(
				Field.ASSET_CATEGORY_IDS, String.valueOf(atCategoryId));
			query.add(condition, BooleanClauseOccur.SHOULD);
		}

		query.setBoost(_moduleConfiguration.queryContributorBoost());

		return query;
	}

	@Override
	public BooleanClauseOccur getOccur() {

		return BooleanClauseOccur.SHOULD;
	}

	@Override
	public boolean isEnabled() {

		return _moduleConfiguration.enableQueryContributor();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}

	private static final Logger _log =
		LoggerFactory.getLogger(AudienceTargetingQueryContributor.class);

	protected volatile ModuleConfiguration _moduleConfiguration;

	@Reference
	private UserSegmentLocalService _userSegmentLocalService;
}
