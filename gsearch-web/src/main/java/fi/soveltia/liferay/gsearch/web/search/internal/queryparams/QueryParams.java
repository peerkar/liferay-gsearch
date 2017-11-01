
package fi.soveltia.liferay.gsearch.web.search.internal.queryparams;

import com.liferay.portal.kernel.search.Sort;

import java.util.Date;
import java.util.List;
import java.util.Locale;

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

	private List<Class<?>> clazzes;
	private String[] documentExtensions;
	private Long documentTypeId = null;
	private String keywords;
	private String originalKeywords;
	private String searchType;
	private Date timeFrom = null;
	private Date timeTo = null;
	private String webContentStructureKey;

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

	public List<Class<?>> getClazzes() {

		return clazzes;
	}

	public void setClazzes(List<Class<?>> clazzes) {

		this.clazzes = clazzes;
	}

	public String[] getDocumentExtensions() {

		return documentExtensions;
	}

	public void setDocumentExtensions(String[] documentExtensions) {

		this.documentExtensions = documentExtensions;
	}

	public Long getDocumentTypeId() {

		return documentTypeId;
	}

	public void setDocumentTypeId(Long documentTypeId) {

		this.documentTypeId = documentTypeId;
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

	
	public String getSearchType() {
	
		return searchType;
	}

	
	public void setSearchType(String searchType) {
	
		this.searchType = searchType;
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

	public String getWebContentStructureKey() {

		return webContentStructureKey;
	}

	public void setWebContentStructureKey(String webContentStructureKey) {

		this.webContentStructureKey = webContentStructureKey;
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
