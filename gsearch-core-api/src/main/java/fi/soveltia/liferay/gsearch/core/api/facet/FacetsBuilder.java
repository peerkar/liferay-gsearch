package fi.soveltia.liferay.gsearch.core.api.facet;

import com.liferay.portal.kernel.search.SearchContext;

/**
 * Facets builder. 
 * 
 * Implementations of this interface take care of injecting the
 * facet aggregations, according to configuration, to the search context.
 * 
 * @author Petteri Karttunen
 */
public interface FacetsBuilder {

	/**
	 * Set facets to searchcontext.
	 * 
	 * @param searchContext
	 */
	public void setFacets(SearchContext searchContext) throws Exception;

}
