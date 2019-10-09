
package fi.soveltia.liferay.gsearch.core.impl.query.filter;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.TermQuery;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.filter.PermissionFilterQueryBuilder;
import fi.soveltia.liferay.gsearch.core.impl.util.GSearchUtil;

/**
 * Permission query filter builder implementation.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = PermissionFilterQueryBuilder.class
)
public class PermissionFilterQueryBuilderImpl
	implements PermissionFilterQueryBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query buildPermissionQuery(QueryContext queryContext)
		throws Exception {

		long companyId = (long)queryContext.getParameter(
			ParameterNames.COMPANY_ID);

		PortletRequest portletRequest =
			GSearchUtil.getPortletRequestFromContext(queryContext);

		User user;

		if (portletRequest != null) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			user = themeDisplay.getUser();
		}
		else {
			user = (User)queryContext.getParameter(ParameterNames.USER);
		}
		
		long userId = user.getUserId();

		// Don't add conditions to company admin. Return.

		if (_portal.isCompanyAdmin(user)) {
			return null;
		}

		Role guestRole = _roleLocalService.getRole(
			companyId, RoleConstants.GUEST);
		Role siteMemberRole = _roleLocalService.getRole(
			companyId, RoleConstants.SITE_MEMBER);

		BooleanQuery query = _queries.booleanQuery();

		// Show guest content for logged in users.

		if (userId != 0) {
			
			TermQuery termQuery = _queries.term(
				Field.ROLE_ID, guestRole.getRoleId());

			query.addShouldQueryClauses(termQuery);
		}

		// Add user's regular roles.

		for (Role r : _roleLocalService.getUserRoles(userId)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Regular role " + r.getName() + "(" + r.getRoleId() + ")");
			}

			TermQuery termQuery = _queries.term(
				Field.ROLE_ID, r.getRoleId());

			query.addShouldQueryClauses(termQuery);
		}

		// Group roles.

		// Notice that user.getGroupIds() won't give you groups joined by
		// usergroup (Site Member Role)


		// Group returned by getSiteGroups() means implicity being in a "Site
		// Member" role.

		for (Group g : user.getSiteGroups()) {
			long l = g.getGroupId();

			TermQuery termQuery = _queries.term(
				Field.GROUP_ROLE_ID, l + "-" + siteMemberRole.getRoleId());

			query.addShouldQueryClauses(termQuery);

			for (Role r : _roleLocalService.getUserGroupRoles(userId, l)) {
				TermQuery groupTermQuery = _queries.term(
					Field.GROUP_ROLE_ID, l + "-" + r.getRoleId());

				query.addShouldQueryClauses(groupTermQuery);

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Group " + g.getName() + ": Role " + r.getName() + "(" +
							r.getRoleId() + ")");
				}
			}
		}

		// Add owner condition

		TermQuery groupTermQuery = _queries.term(
			Field.USER_ID, userId);

		query.addShouldQueryClauses(groupTermQuery);

		return query;
	}

	private static final Logger _log = LoggerFactory.getLogger(
		PermissionFilterQueryBuilderImpl.class);

	@Reference
	private Portal _portal;
	
	@Reference
	private Queries _queries;
	

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}