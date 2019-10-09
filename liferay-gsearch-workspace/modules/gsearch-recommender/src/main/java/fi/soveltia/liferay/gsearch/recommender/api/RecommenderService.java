
package fi.soveltia.liferay.gsearch.recommender.api;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.List;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Recommender service.
 * 
 * @author Petteri Karttunen
 */
public interface RecommenderService {

	/**
	 * Gets recommendations.
	 * 
	 * Uses the recommender clause configuration
	 * if no clause configuration found in the context.
	 * 
	 * @param queryContext
	 * @return
	 * @throws Exception
	 */
	public JSONObject getRecommendations(
		QueryContext queryContext)
		throws Exception;

	/**
	 * Gets recommendations based on the give asset entries.
	 * 
	 * Uses the recommender clause configuration
	 * if no clause configuration found in the context.
	 * 
	 * Notice that the MLT query has to be properly configured (by default)
	 * to make any use of the provided entry ids.
	 * 
	 * @param assetEntryList
	 * @param queryContext
	 * @return
	 * @throws Exception
	 */
	public JSONObject getRecommendationsByAssetEntries(
		List<AssetEntry> assetEntryList, QueryContext queryContext)
		throws Exception;

	/**
	 * Gets recommendations based on the asset ids.
	 * 
	 * Uses the recommender clause configuration
	 * if no clause configuration found in the context.
	 * 
	 * Notice that the MLT query has to be properly configured (by default)
	 * to make any use of the provided entry ids.
	 * 
	 * @param assetEntryIds
	 * @param queryContext
	 * @return
	 * @throws Exception
	 */
	public JSONObject getRecommendationsByAssetEntryIds(
		Long[]assetEntryIds, QueryContext queryContext)
		throws Exception;
	
}
