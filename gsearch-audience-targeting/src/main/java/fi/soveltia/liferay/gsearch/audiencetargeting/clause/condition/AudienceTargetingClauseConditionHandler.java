
package fi.soveltia.liferay.gsearch.audiencetargeting.clause.condition;

import com.liferay.content.targeting.service.UserSegmentLocalService;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.audiencetargeting.constants.GSearchAudienceTargetingConstants;
import fi.soveltia.liferay.gsearch.audiencetargeting.query.contributor.AudienceTargetingQueryContributor;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseConditionHandler;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Processes "audience_targeting_user_segments" clause condition.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseConditionHandler.class
)
public class AudienceTargetingClauseConditionHandler
	implements ClauseConditionHandler {

	@Override
	public boolean canProcess(String handlerName) {

		return (handlerName.equals(HANDLER_NAME));
	}

	@Override
	public boolean isTrue(
		PortletRequest portletRequest, QueryContext queryParams,
		JSONObject configuration)
		throws Exception {

		long[] userSegmentIds = (long[]) portletRequest.getAttribute(
			GSearchAudienceTargetingConstants.USER_SEGMENT_ID_PARAM);

		if (_log.isDebugEnabled()) {

			if (userSegmentIds != null) {
				_log.debug("Current user has following user segments:");

				for (long l : userSegmentIds) {
					_log.debug(String.valueOf(l));
				}
			}
			else {
				_log.debug("Current user doesn't have any user segments.");
			}
		}

		if (userSegmentIds == null) {
			return false;
		}

		Long[] matchUserSegmentIds = getMatchUserSegmentIds(configuration);

		for (long id : userSegmentIds) {

			for (long matchId : matchUserSegmentIds) {

				if (id == matchId) {

					if (_log.isDebugEnabled()) {
						_log.debug("Found match:" + id);
					}

					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Get matching user segment ids as Long array.
	 * 
	 * @param configuration
	 * @return
	 */
	public Long[] getMatchUserSegmentIds(JSONObject configuration) {

		JSONArray matchArray = configuration.getJSONArray("user_segment_ids");

		if (matchArray == null || matchArray.length() == 0) {
			return new Long[0];
		}

		Long[] userSegmentIds = new Long[matchArray.length()];

		for (int i = 0; i < matchArray.length(); i++) {
			userSegmentIds[i] = matchArray.getLong(i);
		}

		return userSegmentIds;
	}

	private static final Logger _log =
		LoggerFactory.getLogger(AudienceTargetingQueryContributor.class);

	private static final String HANDLER_NAME =
		"audience_targeting_user_segments";

	@Reference
	private UserSegmentLocalService _userSegmentLocalService;
}
