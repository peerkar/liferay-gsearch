package fi.soveltia.liferay.gsearch.segments.query.context.contributor;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.segments.context.Context;
import com.liferay.segments.provider.SegmentsEntryProvider;
import com.liferay.segments.simulator.SegmentsEntrySimulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.contributor.QueryContextContributor;
import fi.soveltia.liferay.gsearch.segments.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.segments.constants.SegmentsConfigurationVariables;
import fi.soveltia.liferay.gsearch.segments.constants.SegmentsParameterNames;

/**
 * Adds segments data to querycontext.
 * 
 *  In 7.2, the user segment ids are injected into request
 *	only at full lifecycle render and are not available for
 *  example in resource requests automatically any more 
 *  (like they were in previous versions).
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.weather.configuration.ModuleConfiguration",
	immediate = true, 
	service = QueryContextContributor.class
)
public class SegmentsQueryContextContributor
	implements QueryContextContributor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void contribute(QueryContext queryContext) throws Exception {

		if (!_moduleConfiguration.isEnabled()) {
			return;
		}

		long[] userGroupIds = _getUserAccessibleSiteGroupIds(queryContext);

		long[] segmentsEntryIds = _getSegmentEntryIds(
			queryContext, userGroupIds);
		
		if (userGroupIds.length > 0) {

			_addConfigurationVariables(queryContext, segmentsEntryIds);
			
			_addContextParameters(queryContext, segmentsEntryIds);
		}
	}
	
	@Override
	public String getName() {
		return _NAME;
	}
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}	
	
	/**
	 * Adds configuration variables. 
	 * 	
	 * @param queryContext
	 * @param ids
	 */
	private void _addConfigurationVariables(QueryContext queryContext, long[] segmentsEntryIds) {

		String ids = Arrays.stream(
			segmentsEntryIds
		).mapToObj(
			String::valueOf
		).collect(
			Collectors.joining(" ")
		);

		queryContext.addConfigurationVariable(
				SegmentsConfigurationVariables.USER_SEGMENT_IDS, ids);
	}
	
	/**
	 * Add parameters to query context.
	 * 
	 * @param queryContext
	 * @param locationData
	 */
	private void _addContextParameters(QueryContext queryContext, long[] segmentsEntryIds) {
		queryContext.setParameter(
				SegmentsParameterNames.USER_SEGMENT_IDS, segmentsEntryIds);

	}
	
	/**
	 * Gets company group id.
	 *
	 * @param companyId
	 * @return the group id of the company.
	 * @throws PortalException
	 */
	private long _getCompanyGroupId(long companyId) throws PortalException {
		Company company = _companyLocalService.getCompany(companyId);

		return company.getGroupId();
	}

	/**
	 * Gets segment entry ids for a user. Supports simulation.
	 *
	 * @param queryContext
	 * @param groupIds
	 * @return an array of user's SegmentEntry ids
	 * @throws Exception
	 */
	private long[] _getSegmentEntryIds(
			QueryContext queryContext, long[] groupIds)
		throws Exception {

		User user = (User)queryContext.getParameter(ParameterNames.USER);

		if ((_segmentsEntrySimulator != null) &&
			_segmentsEntrySimulator.isSimulationActive(user.getUserId())) {

			return _segmentsEntrySimulator.getSimulatedSegmentsEntryIds(
				user.getUserId());
		}

		try {
			List<Long> allSegmentEntryIds = new ArrayList<>();

            Context context = new Context();
			
			for (long groupId : groupIds) {
				long[] segmentEntryIds =
					_segmentsEntryProvider.getSegmentsEntryIds(
						groupId, User.class.getName(), user.getUserId(), context);

				Arrays.stream(
					segmentEntryIds
				).forEach(
					allSegmentEntryIds::add
				);
			}

			return allSegmentEntryIds.stream(
			).mapToLong(
				value -> value
			).toArray();
		}
		catch (PortalException pe) {
			if (_log.isWarnEnabled()) {
				_log.warn(pe.getMessage());
			}
		}

		return new long[0];
	}

	/**
	 * Gets ids of the groups accessible for the current user.
	 *
	 * @param queryContext
	 * @return array of groupIds accessible for the current user
	 * @throws PortalException
	 */
	private long[] _getUserAccessibleSiteGroupIds(QueryContext queryContext)
		throws Exception {

		List<Long> groupIds = new ArrayList<>();

		long companyId = (long)queryContext.getParameter(
			ParameterNames.COMPANY_ID);

		// Add global group.

		long companyGroupId = _getCompanyGroupId(companyId);
		groupIds.add(companyGroupId);

		// For a guest user list all public sites.

		for (Group group : _groupLocalService.getGroups(companyId, 0, true)) {
			if (group.isActive() && !group.isStagingGroup() &&
				group.hasPublicLayouts()) {

				groupIds.add(group.getGroupId());
			}
		}

		// For a logged in user additionally list all sites she's a member of.

		User user = (User)queryContext.getParameter(ParameterNames.USER);

		for (Group group : user.getSiteGroups()) {
			if (!groupIds.contains(group.getGroupId()) && group.isActive() &&
				!group.isStagingGroup()) {

				groupIds.add(group.getGroupId());
			}
		}

		return groupIds.stream(
		).mapToLong(
			l -> l
		).toArray();
	}

	private static final Logger _log = LoggerFactory.getLogger(
		SegmentsQueryContextContributor.class);
	
	private static final String _NAME = "segments";

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	private volatile ModuleConfiguration _moduleConfiguration;

	@Reference
	private SegmentsEntryProvider _segmentsEntryProvider;

	@Reference(
		cardinality = ReferenceCardinality.OPTIONAL,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.portal.kernel.model.User)"
	)
	private volatile SegmentsEntrySimulator _segmentsEntrySimulator;

}