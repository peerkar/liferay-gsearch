
package fi.soveltia.liferay.gsearch.core.api.facet;

import com.liferay.portal.kernel.search.SearchContext;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;

/**
 * Facets builder interface. 
 * 
 * Implementations of this interface take care of
 * injecting the facet aggregations, according to configuration, to the search
 * context.
 * 
 * @author Petteri Karttunen
 */
public interface FacetsBuilder {

	/**
	 * Set facets to searchcontext.
	 * 
	 * @param searchContext
	 * @param queryParams
	 * @throws Exception
	 */
	public void setFacets(
		SearchContext searchContext, QueryParams queryParams)
		throws Exception;

}
