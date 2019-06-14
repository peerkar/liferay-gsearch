
package fi.soveltia.liferay.gsearch.recommender.api;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.portal.kernel.json.JSONObject;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Recommender service.
 * 
 * @author Petteri Karttunen
 */
public interface RecommenderService {

	/**
	 * Get recommendations based on index document UID. Meant to be used in
	 * with MLT query.
	 * 
	 * @param queryContext
	 * @param docUID
	 * @return
	 * @throws Exception
	 */
	public JSONObject getRecommendationsByDocUID(
		QueryContext queryContext, String[] docUID)
		throws Exception;

	/**
	 * Resolves index document UID.
	 * 
	 * @param queryContext
	 * @param assetEntry
	 * @return
	 * @throws Exception
	 */
	public String resolveDocUIDByAssetEntry(
		QueryContext queryContext, AssetEntry assetEntry)
		throws Exception;
	

	/**
	 * Resolves index document UID.
	 * 
	 * @param httpServletRequest
	 * @param queryContext
	 * @param assetEntryId
	 * @return
	 * @throws Exception
	 */
	public String resolveDocUIDByAssetEntryId(
		QueryContext queryContext, long assetEntryId)
		throws Exception;
	
}
