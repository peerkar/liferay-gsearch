package fi.soveltia.liferay.gsearch.core.api.configuration;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.Locale;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Core Configuration helper.
 * 
 * @author Petteri Karttunen
 */
public interface CoreConfigurationHelper {

	/**
	 * Gets clause configuration.
	 * 
	 * @return
	 */
	public JSONArray getClauses();

	/**
	 * Gets result item description max length.
	 * 
	 * @return 
	 */
	public int getDescriptionMaxLength();

	/**
	 * Gets facets configuration.
	 * 
	 * @return 
	 */
	public JSONArray getFacets();
	
	/**
	 * Gets filter configuration.
	 * 
	 * @return 
	 */
	public JSONArray getFilters();

	/**
	 * Gets an array of highlight field names.
	 * 
	 * @return
	 */
	public String[] getHightlightFields(Locale locale);
	
	/**
	 * Gets hightlight fragment size.
	 * 
	 * @return
	 */
	public int getHighlightFragmentSize();

	/**
	 * Gets highlight snippet size.
	 * 
	 * @return
	 */
	public int getHighlightSnippetSize();
	
	/**
	 * Gets keyword suggester configuration.
	 * 
	 * @return
	 */
	public JSONArray getKeywordSuggesters();

	/**
	 * Gets search index names.
	 * 
	 * @param companyId
	 * @return
	 */
	public String getLiferayIndexName(long companyId);
	
	/**
	 * Gets max facet terms to collect.
	 * 
	 * @return
	 */
	public int getMaxFacetTerms();

	/**
	 * Gets rescore clauses configuration.
	 * 
	 * @return
	 */
	public JSONArray getRescoreClauses();

	/**
	 * Gets search index names.
	 * 
	 * @param companyId
	 * @return
	 */
	public String[] getSearchIndexNames(long companyId);

	/**
	 * Gets sort configuration.
	 * 
	 * @return
	 */
	public JSONArray getSorts();

	/**
	 * Gets suggester index name.
	 * 
	 * @param companyId
	 * @return
	 */
	public String getSuggesterIndexName(long companyId);
	
	/**
	 * Is hightlight enabled.
	 * 
	 * @return
	 */
	public boolean isHighlightEnabled();

	/**
	 * Converts String[] to JSONArray
	 * 
	 * @return
	 */
	public JSONArray stringArrayToJSONArray(String[] stringArray);
	
	/**
	 * Parses known variables in configuration key or value.
	 *  
	 * @param queryContext
	 * @param configurationItem
	 * @return
	 * @throws Exception
	 */
	public JSONObject parseConfigurationVariables(
			QueryContext queryContext, JSONObject configurationItem) 
					throws Exception;
}
