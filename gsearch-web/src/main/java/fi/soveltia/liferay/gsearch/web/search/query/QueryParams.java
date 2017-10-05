
package fi.soveltia.liferay.gsearch.web.search.query;

import com.liferay.portal.kernel.search.Sort;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Query params
 * 
 * @author Petteri Karttunen
 */
public class QueryParams {

	@SuppressWarnings("rawtypes")
	private List<Class> clazzes;
	private long companyId;
	private Date timeFrom = null;
	private Date timeTo = null;
	private long[] groupIds;
	private String keywords;
	long userId;
	private Locale locale;
	private Sort[] sorts;
	private int start;
	private int end;
	private int pageSize;

	@SuppressWarnings("rawtypes")
	public List<Class> getClazzes() {

		return clazzes;
	}

	@SuppressWarnings("rawtypes")
	public void setClazzes(List<Class> clazzes) {

		this.clazzes = clazzes;
	}

	public long getCompanyId() {

		return companyId;
	}

	public void setCompanyId(long companyId) {

		this.companyId = companyId;
	}

	public Date getTimeFrom() {

		return timeFrom;
	}

	public void setTimeFrom(Date timeFrom) {

		this.timeFrom = timeFrom;
	}

	public Date getTimeTo() {

		return timeTo;
	}

	public void setTimeTo(Date timeTo) {

		this.timeTo = timeTo;
	}

	public int getEnd() {

		return end;
	}

	public void setEnd(int end) {

		this.end = end;
	}

	public long[] getGroupIds() {

		return groupIds;
	}

	public void setGroupIds(long[] groupIds) {

		this.groupIds = groupIds;
	}

	public String getKeywords() {

		return keywords;
	}

	public void setKeywords(String keywords) {

		this.keywords = keywords;
	}

	public Locale getLocale() {

		return locale;
	}

	public void setLocale(Locale locale) {

		this.locale = locale;
	}

	public int getPageSize() {

		return pageSize;
	}

	public void setPageSize(int pageSize) {

		this.pageSize = pageSize;
	}

	public Sort[] getSorts() {

		return sorts;
	}

	public void setSorts(Sort[] sort) {

		this.sorts = sort;
	}

	public int getStart() {

		return start;
	}

	public void setStart(int start) {

		this.start = start;
	}

	public long getUserId() {

		return userId;
	}

	public void setUserId(long userId) {

		this.userId = userId;
	}
}
