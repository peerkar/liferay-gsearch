
package fi.soveltia.liferay.gsearch.core.impl.query.filter;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Locale;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.filter.PermissionFilterQueryBuilder;

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
	public Query buildPermissionQuery(
		PortletRequest portletRequest, QueryContext queryContext)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		long companyId = (long)queryContext.getParameter(ParameterNames.COMPANY_ID);
				
		Locale locale = (Locale)queryContext.getParameter(ParameterNames.LOCALE);

		User user = themeDisplay.getUser();
		long userId = user.getUserId();

		// Don't add conditions to company admin. Return.

		if (_portal.isCompanyAdmin(user)) {
			return null;
		}

		Role guestRole = _roleLocalService.getRole(
			companyId, RoleConstants.GUEST);
		Role siteMemberRole = _roleLocalService.getRole(
			companyId, RoleConstants.SITE_MEMBER);

		BooleanQueryImpl query = new BooleanQueryImpl();

		// Show guest content for logged in users.

		if (themeDisplay.isSignedIn()) {
			TermQuery termQuery = new TermQueryImpl(
				Field.ROLE_ID, String.valueOf(guestRole.getRoleId()));
			query.add(termQuery, BooleanClauseOccur.SHOULD);
		}

		// Add user's regular roles.

		for (Role r : _roleLocalService.getUserRoles(userId)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Regular role " + r.getName() + "(" + r.getRoleId() + ")");
			}

			TermQuery termQuery =
				new TermQueryImpl(Field.ROLE_ID, String.valueOf(r.getRoleId()));
			query.add(termQuery, BooleanClauseOccur.SHOULD);
		}

		// Group roles.

		// Notice that user.getGroupIds() won't give you groups joined by
		// usergroup (Site Member Role)
		//
		// Group returned by getSiteGroups() means implicity being in a "Site
		// Member" role.

		for (Group g : user.getSiteGroups()) {

			long l = g.getGroupId();

			TermQuery termQuery = new TermQueryImpl(
				Field.GROUP_ROLE_ID, l + "-" + siteMemberRole.getRoleId());
			query.add(termQuery, BooleanClauseOccur.SHOULD);

			for (Role r : _roleLocalService.getUserGroupRoles(userId, l)) {

				TermQuery groupTermQuery = new TermQueryImpl(
					Field.GROUP_ROLE_ID, l + "-" + r.getRoleId());
				query.add(groupTermQuery, BooleanClauseOccur.SHOULD);

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Group " + g.getName(locale) +
							": Role " + r.getName() + "(" + r.getRoleId() +
							")");
				}
			}
		}

		// Add owner condition

		TermQuery groupTermQuery =
			new TermQueryImpl(Field.USER_ID, String.valueOf(userId));
		query.add(groupTermQuery, BooleanClauseOccur.SHOULD);

		return query;

	}

	private static final Logger _log =
		LoggerFactory.getLogger(PermissionFilterQueryBuilderImpl.class);

	@Reference
	private Portal _portal;

	@Reference
	private RoleLocalService _roleLocalService;

}
