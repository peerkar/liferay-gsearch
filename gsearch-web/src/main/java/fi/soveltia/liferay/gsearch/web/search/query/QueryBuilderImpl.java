
package fi.soveltia.liferay.gsearch.web.search.query;

import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * QueryBuilder implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(immediate = true, service = QueryBuilder.class)
public class QueryBuilderImpl implements QueryBuilder {

	@Override
	public BooleanQuery buildQuery(
		PortletRequest portletRequest, QueryParams queryParams)
		throws Exception {

		_query = new BooleanQueryImpl();
		_queryParams = queryParams;
		_portletRequest = portletRequest;

		buildClassesCondition();
		buildGroupsCondition();
		buildModificationTimeCondition();
		buildStatusCondition();
		buildViewPermissionCondition();

		return _query;
	}

	/**
	 * Add classes condition
	 * 
	 * @throws ParseException
	 */
	@SuppressWarnings("rawtypes")
	protected void buildClassesCondition()
		throws ParseException {

		List<Class> clazzes = _queryParams.getClazzes();

		BooleanQuery query = new BooleanQueryImpl();

		for (Class<?> c : clazzes) {

			if (c.getName().equals(JournalArticle.class.getName())) {

				BooleanQuery journalArticleQuery = new BooleanQueryImpl();

				// Add version limitation to JournalArticle

				BooleanQuery journalArticleVersionQuery =
					getJournalArticleVersionCondition();
				journalArticleQuery.add(
					journalArticleVersionQuery, BooleanClauseOccur.MUST);

				// Add display date limitation to JournalArticle

				BooleanQuery journalArticleDisplayDateQuery =
					getJournalArticleDisplayDateCondition();
				journalArticleQuery.add(
					journalArticleDisplayDateQuery, BooleanClauseOccur.MUST);

				journalArticleQuery.addTerm(
					Field.ENTRY_CLASS_NAME, c.getName(), false,
					BooleanClauseOccur.MUST);

				query.add(journalArticleQuery, BooleanClauseOccur.SHOULD);

			}
			else {
				query.addTerm(
					Field.ENTRY_CLASS_NAME, c.getName(), false,
					BooleanClauseOccur.SHOULD);
			}
		}
		_query.add(query, BooleanClauseOccur.MUST);
	}

	/**
	 * Add groups condition
	 * 
	 * @throws ParseException
	 */
	protected void buildGroupsCondition()
		throws ParseException {

		long[] groupIds = _queryParams.getGroupIds();

		BooleanQueryImpl query = new BooleanQueryImpl();

		for (long l : groupIds) {
			query.addTerm(
				Field.SCOPE_GROUP_ID, String.valueOf(l), false,
				BooleanClauseOccur.SHOULD);
		}

		_query.add(query, BooleanClauseOccur.MUST);
	}

	/**
	 * Add status condition
	 */
	protected void buildStatusCondition() {

		// Set to approved only

		int status = WorkflowConstants.STATUS_APPROVED;

		_query.addRequiredTerm(Field.STATUS, status);
	}

	/**
	 * Add modification date condition
	 * 
	 * @throws ParseException
	 */
	protected void buildModificationTimeCondition()
		throws ParseException {

		// Set modified from limit

		Date from = _queryParams.getTimeFrom();

		if (from != null) {
			BooleanQuery query = new BooleanQueryImpl();
			query.addRangeTerm(
				"modified_sortable", from.getTime(), Long.MAX_VALUE);
			_query.add(query, BooleanClauseOccur.MUST);
		}

		// Set modified to limit

		Date to = _queryParams.getTimeTo();

		if (to != null) {
			BooleanQuery query = new BooleanQueryImpl();
			query.addRangeTerm(
				"modified_sortable", to.getTime(), Long.MAX_VALUE);
			_query.add(query, BooleanClauseOccur.MUST);
		}
	}

	/**
	 * Add view permissions condition
	 * 
	 * @throws Exception
	 */
	protected void buildViewPermissionCondition()
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) _portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		User user = themeDisplay.getUser();
		long userId = user.getUserId();

		// Don't add conditions to company admin.

		if (_portal.isCompanyAdmin(user)) {
			return;
		}

		Role guestRole = RoleLocalServiceUtil.getRole(
			_queryParams.getCompanyId(), RoleConstants.GUEST);
		Role siteMemberRole = RoleLocalServiceUtil.getRole(
			_queryParams.getCompanyId(), RoleConstants.SITE_MEMBER);

		BooleanQueryImpl query = new BooleanQueryImpl();

		// Show guest content for logged in users.

		if (themeDisplay.isSignedIn()) {
			query.addTerm(
				Field.ROLE_ID, String.valueOf(guestRole.getRoleId()), false,
				BooleanClauseOccur.SHOULD);
		}

		// Regular roles

		for (Role r : RoleLocalServiceUtil.getUserRoles(userId)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Regular role " + r.getName() + "(" + r.getRoleId() + ")");
			}
			query.addTerm(
				Field.ROLE_ID, String.valueOf(r.getRoleId()), false,
				BooleanClauseOccur.SHOULD);
		}

		// Group roles

		// Notice that user.getGroupIds() won't give you groups joined by
		// usergroup (Site Member Role)
		//
		// Group returned by getSiteGroups() means implicity being in a "Site
		// Member" role.

		for (Group g : user.getSiteGroups()) {

			long l = g.getGroupId();

			query.addTerm(
				Field.GROUP_ROLE_ID, l + "-" + siteMemberRole.getRoleId(),
				false, BooleanClauseOccur.SHOULD);

			for (Role r : RoleLocalServiceUtil.getUserGroupRoles(userId, l)) {

				query.addTerm(
					Field.GROUP_ROLE_ID, l + "-" + r.getRoleId(), false,
					BooleanClauseOccur.SHOULD);

				_log.info(
					"Group " + g.getName(_queryParams.getLocale()) + ": Role " +
						r.getName() + "(" + r.getRoleId() + ")");
			}
		}

		_query.add(query, BooleanClauseOccur.MUST);
	}

	/**
	 * Get a condition to search only for the latest article version.
	 * 
	 * @param searchContext
	 * @param status
	 * @return
	 */
	protected BooleanQuery getJournalArticleVersionCondition() {

		BooleanQuery query = new BooleanQueryImpl();

		query.addRequiredTerm(
			Field.ENTRY_CLASS_NAME, JournalArticle.class.getName());
		query.addRequiredTerm("head", Boolean.TRUE.toString());

		return query;
	}

	/**
	 * Get a condition to limit displaydate only to those visible now.
	 * 
	 * @return BooleanQuery
	 * @throws ParseException
	 */
	protected BooleanQuery getJournalArticleDisplayDateCondition()
		throws ParseException {

		BooleanQuery query = new BooleanQueryImpl();

		Date now = new Date();

		BooleanQuery from = new BooleanQueryImpl();
		from.addRangeTerm(
			"displayDate_sortable", Long.MIN_VALUE, now.getTime());
		query.add(from, BooleanClauseOccur.MUST);

		BooleanQuery to = new BooleanQueryImpl();
		to.addRangeTerm(
			"expirationDate_sortable", now.getTime(), Long.MAX_VALUE);
		query.add(to, BooleanClauseOccur.MUST);

		return query;
	}

	
	public static final DateFormat INDEX_DATE_FORMAT =
		new SimpleDateFormat("yyyyMMddHHmmss");
	private BooleanQuery _query;
	private QueryParams _queryParams;

	@Reference
	private Portal _portal;

	private PortletRequest _portletRequest;

	private static final Log _log =
		LogFactoryUtil.getLog(QueryBuilderImpl.class);
}
