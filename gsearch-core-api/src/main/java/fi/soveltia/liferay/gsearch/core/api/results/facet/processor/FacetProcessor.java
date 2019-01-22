
package fi.soveltia.liferay.gsearch.core.api.results.facet.processor;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.Facet;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Post processes facets like for example filters only categories belonging to
 * certain vocabulary.
 * 
 * @author Petteri Karttunen
 */
public interface FacetProcessor {

	/**
	 * Can process the facet by name. 
	 * 
	 * @return
	 */
	public boolean canProcess(String facetName);
		
	/**
	 * Process facet.
	 * 
	 * @param queryContext
	 * @param configuration
	 * @param facet
	 * @return
	 * @throws Exception
	 */
	public JSONArray process(
		QueryContext queryContext, JSONObject configuration, 
		Facet facet)
		throws Exception;
}
