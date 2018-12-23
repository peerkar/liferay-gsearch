
package fi.soveltia.liferay.gsearch.core.api.params;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Sort;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Query parameters pojo.
 * 
 * @author Petteri Karttunen
 */
public class QueryParams {

	// Configurations

	private String[] clauseConfiguration;
	private String[] facetConfiguration;
	private String[] assetTypeConfiguration;
	private String[] sortConfiguration;

	// Basic filter params

	private long companyId;
	private long[] groupIds;
	private Locale locale;
	private long userId;

	// Query params

	private List<String> entryClassNames;
	private Map<FacetParam, BooleanClauseOccur> facetParams;
	private String keywords;
	private String originalKeywords;
	private Integer status;
	private Date timeFrom = null;
	private Date timeTo = null;

	// Paging and sorting

	private int start;
	private int end;
	private int pageSize;
	private Sort[] sorts;

	// Other parameters

	private String assetPublisherPageURL = null;
	private boolean viewResultsInContext = false;

	// Additional index fields to be included in the
	// result items.

	private Map<String, Class<?>> additionalResultFields =
		new HashMap<String, Class<?>>();

	// Any extra parameters.

	private Map<String, Object> extraParams = new HashMap<String, Object>();

	public void addAdditionalResultField(String key, Class<?> value) {

		additionalResultFields.put(key, value);
	}

	public Map<String, Class<?>> getAdditionalResultFields() {

		return additionalResultFields;
	}

	/**
	 * These fields are added (as such) to the result items. Map value can be
	 * used for typing the field.
	 * 
	 * @param additionalResultFields
	 */
	public void setAdditionalResultFields(
		Map<String, Class<?>> additionalResultFields) {

		this.additionalResultFields = additionalResultFields;
	}

	public String getAssetPublisherPageURL() {

		return assetPublisherPageURL;
	}

	/**
	 * Set Asset Publisher page URL. A friendly URL to a page having an asset
	 * publisher for showing assets (Web Contents) which are not bound to any
	 * layout.
	 * 
	 * @param assetPublisherPageURL
	 */
	public void setAssetPublisherPageURL(String assetPublisherPageURL) {

		this.assetPublisherPageURL = assetPublisherPageURL;
	}

	public String[] getAssetTypeConfiguration() {

		return assetTypeConfiguration;
	}

	public void setAssetTypeConfiguration(String[] assetTypeConfiguration) {

		this.assetTypeConfiguration = assetTypeConfiguration;
	}

	public String[] getClauseConfiguration() {

		return clauseConfiguration;
	}

	public void setClauseConfiguration(String[] clauseConfiguration) {

		this.clauseConfiguration = clauseConfiguration;
	}

	public long getCompanyId() {

		return companyId;
	}

	public void setCompanyId(long companyId) {

		this.companyId = companyId;
	}

	public int getEnd() {

		return end;
	}

	public void setEnd(int end) {

		this.end = end;
	}

	public List<String> getEntryClassNames() {

		return entryClassNames;
	}

	public void setEntryClassNames(List<String> entryClassNames) {

		this.entryClassNames = entryClassNames;
	}

	public void addExtraParam(String key, Object value) {

		extraParams.put(key, value);
	}

	public Map<String, Object> getExtraParams() {

		return extraParams;
	}

	/**
	 * Set extra parameters. These parameters can include any instructions from
	 * the calling client to the backend. By default these are used to indicate
	 * whether thumbnail or user portrait should be included in the results. As
	 * they are not indexed fields we cannot use additionalResultFields for the
	 * purpose.
	 * 
	 * @param extraParams
	 */
	public void setExtraParams(Map<String, Object> extraParams) {

		this.extraParams = extraParams;
	}

	public String[] getFacetConfiguration() {

		return facetConfiguration;
	}

	public void setFacetConfiguration(String[] facetConfiguration) {

		this.facetConfiguration = facetConfiguration;
	}

	public Map<FacetParam, BooleanClauseOccur> getFacetParams() {

		return facetParams;
	}

	public void setFacetParams(
		Map<FacetParam, BooleanClauseOccur> facetParams) {

		this.facetParams = facetParams;
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

	public int getPageSize() {

		return pageSize;
	}

	public void setPageSize(int pageSize) {

		this.pageSize = pageSize;
	}

	public String[] getSortConfiguration() {

		return sortConfiguration;
	}

	public void setSortConfiguration(String[] sortConfiguration) {

		this.sortConfiguration = sortConfiguration;
	}

	public Sort[] getSorts() {

		return sorts;
	}

	public void setSorts(Sort[] sorts) {

		this.sorts = sorts;
	}

	public Integer getStatus() {

		return status;
	}

	public void setStatus(Integer status) {

		this.status = status;
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

	public boolean isViewResultsInContext() {

		return viewResultsInContext;
	}

	/**
	 * Should we show the results in context?
	 * 
	 * @param viewResultsInContext
	 */
	public void setViewResultsInContext(boolean viewResultsInContext) {

		this.viewResultsInContext = viewResultsInContext;
	}

}
