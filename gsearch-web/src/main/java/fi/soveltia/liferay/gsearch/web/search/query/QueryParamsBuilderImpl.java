
package fi.soveltia.liferay.gsearch.web.search.query;

import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.message.boards.kernel.model.MBMessage;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.web.search.exception.KeywordsException;

/**
 * QueryParamsBuilder implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(immediate = true, service = QueryParamsBuilder.class)
public class QueryParamsBuilderImpl implements QueryParamsBuilder {

	public QueryParams buildQueryParams(
		PortletRequest portletRequest,
		GSearchDisplayConfiguration configuration)
		throws PortalException {

		_queryParamValidator = new RequestParamValidator();
		_portletRequest = portletRequest;
		_queryParams = new QueryParams();
		_gSearchDisplayConfiguration = configuration;

		setKeywordsParam();
		setCompanyParam();
		setGroupsParam();
		setLocaleParam();
		setPageSizeParam();
		setSortParam();
		setStartEndParams();
		setTimeParam();
		setTypeParam();
		setUserParam();

		return _queryParams;
	}

	/**
	 * Set company
	 */
	protected void setCompanyParam() {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) _portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		_queryParams.setCompanyId(themeDisplay.getCompanyId());
	}

	/**
	 * Set groups
	 */
	protected void setGroupsParam() {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) _portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		String scopeFilter =
			ParamUtil.getString(_portletRequest, GSearchWebKeys.SCOPE_FILTER);

		long[] groupIds;

		if (!"all".equals(scopeFilter)) {
			groupIds = new long[] {
				themeDisplay.getScopeGroupId()
			};
		}
		else {
			groupIds = new long[] {};
		}
		_queryParams.setGroupIds(groupIds);
	}

	/**
	 * Set keywords
	 * 
	 * @throws KeywordsException
	 */
	protected void setKeywordsParam()
		throws KeywordsException {

		String keywords =
			ParamUtil.getString(_portletRequest, GSearchWebKeys.KEYWORDS);

		// Validate keywords

		if (!_queryParamValidator.validateKeywords(keywords)) {
			throw new KeywordsException();
		}
		_queryParams.setKeywords(keywords);
	}

	/**
	 * Set locale
	 */
	protected void setLocaleParam() {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) _portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		_queryParams.setLocale(themeDisplay.getLocale());
	}

	/**
	 * Set page size param
	 */
	protected void setPageSizeParam() {

		_queryParams.setPageSize(_gSearchDisplayConfiguration.pageSize());
	}

	/**
	 * Set sort. Default sort field equals to "last modified".
	 */
	protected void setSortParam() {

		String sortField =
			ParamUtil.getString(_portletRequest, GSearchWebKeys.SORT_FIELD);

		String sortDirection =
			ParamUtil.getString(_portletRequest, GSearchWebKeys.SORT_DIRECTION);

		String field;

		if ("title".equals(sortField)) {
			field = "localized_title_" + _queryParams.getLocale().toString() +
				"_sortable";
		}
		else {
			field = "modified_sortable";
		}

		boolean reverse;

		if ("desc".equals(sortDirection)) {
			reverse = true;
		}
		else {
			reverse = false;
		}

		Sort sort = new Sort(field, reverse);

		_queryParams.setSorts(new Sort[] {
			sort
		});
	}

	/**
	 * Set start and end.
	 */
	protected void setStartEndParams() {

		int start =
			ParamUtil.getInteger(_portletRequest, GSearchWebKeys.START, 0);
		_queryParams.setStart(start);
		_queryParams.setEnd(start + _gSearchDisplayConfiguration.pageSize());
	}

	/**
	 * Set time parameter (modification date between).
	 */
	protected void setTimeParam() {

		String timeFilter =
			ParamUtil.getString(_portletRequest, GSearchWebKeys.TIME_FILTER);

		Date timeFrom = null;

		if ("last-day".equals(timeFilter)) {

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			timeFrom = calendar.getTime();

		}
		else if ("last-hour".equals(timeFilter)) {

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.HOUR_OF_DAY, -1);
			timeFrom = calendar.getTime();

		}
		else if ("last-month".equals(timeFilter)) {

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -1);
			timeFrom = calendar.getTime();

		}
		else if ("last-week".equals(timeFilter)) {

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.WEEK_OF_MONTH, -1);
			timeFrom = calendar.getTime();

		}
		else if ("last-year".equals(timeFilter)) {

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, -1);
			timeFrom = calendar.getTime();

		}

		if (timeFrom != null) {
			_queryParams.setTimeFrom(timeFrom);
		}
	}

	/**
	 * Set types (asset types to search for). Modify or override this to pass
	 * for you needs.
	 */
	@SuppressWarnings("rawtypes")
	protected void setTypeParam() {

		String typeFilter =
			ParamUtil.getString(_portletRequest, GSearchWebKeys.TYPE_FILTER);

		List<Class> clazzes = new ArrayList<Class>();

		if ("blog".equals(typeFilter)) {
			clazzes.add(BlogsEntry.class);
		}
		else if ("file".equals(typeFilter)) {
			clazzes.add(DLFileEntry.class);
		}
		else if ("web-content".equals(typeFilter)) {
			clazzes.add(JournalArticle.class);
		}
		else if ("discussion".equals(typeFilter)) {
			clazzes.add(MBMessage.class);
		}
		else {
			clazzes.add(BlogsEntry.class);
			clazzes.add(DLFileEntry.class);
			clazzes.add(JournalArticle.class);
			clazzes.add(MBMessage.class);
		}

		_queryParams.setClazzes(clazzes);
	}

	/**
	 * Set user
	 */
	protected void setUserParam() {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) _portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		_queryParams.setUserId(themeDisplay.getUserId());
	}

	private volatile GSearchDisplayConfiguration _gSearchDisplayConfiguration;
	private PortletRequest _portletRequest;
	private QueryParams _queryParams;
	private RequestParamValidator _queryParamValidator;
}
