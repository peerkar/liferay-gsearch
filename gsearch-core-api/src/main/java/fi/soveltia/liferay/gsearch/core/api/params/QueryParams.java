
package fi.soveltia.liferay.gsearch.core.api.params;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Sort;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Query parameters pojo.
 * 
 * @author Petteri Karttunen
 */
public class QueryParams {

	private long companyId;
	private long[] groupIds;
	private Locale locale;
	long userId;

	private List<String> classNames;
	private Map<FacetParam, BooleanClauseOccur> facetParams;
	private String keywords;
	private String originalKeywords;
	private String resultsLayout;
	private Date timeFrom = null;
	private Date timeTo = null;

	private int start;
	private int end;
	private int pageSize;
	private Sort[] sorts;

	public long getCompanyId() {

		return companyId;
	}

	public void setCompanyId(long companyId) {

		this.companyId = companyId;
	}

	public long[] getGroupIds() {

		return groupIds;
	}

	public void setGroupIds(long[] groupIds) {

		this.groupIds = groupIds;
	}

	public Locale getLocale() {

		return locale;
	}

	public void setLocale(Locale locale) {

		this.locale = locale;
	}

	public long getUserId() {

		return userId;
	}

	public void setUserId(long userId) {

		this.userId = userId;
	}

	public List<String> getClassNames() {

		return classNames;
	}

	public void setClassNames(List<String> classNames) {

		this.classNames = classNames;
	}
	
	public Map<FacetParam, BooleanClauseOccur> getFacetParams() {
	
		return facetParams;
	}
	
	public void setFacetsParams(Map<FacetParam, BooleanClauseOccur> facetParams) {
	
		this.facetParams = facetParams;
	}

	public String getKeywords() {

		return keywords;
	}

	public void setKeywords(String keywords) {

		this.keywords = keywords;
	}

	public String getOriginalKeywords() {

		return originalKeywords;
	}

	public void setOriginalKeywords(String originalKeywords) {

		this.originalKeywords = originalKeywords;
	}

	public String getResultsLayout() {

		return resultsLayout;
	}

	public void setResultsLayout(String resultsLayout) {

		this.resultsLayout = resultsLayout;
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

	public int getStart() {

		return start;
	}

	public void setStart(int start) {

		this.start = start;
	}

	public int getEnd() {

		return end;
	}

	public void setEnd(int end) {

		this.end = end;
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
}
