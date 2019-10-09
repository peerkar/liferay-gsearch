package fi.soveltia.liferay.gsearch.core.api.aggregation;

import com.liferay.portal.search.aggregation.Aggregation;

import java.util.List;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Aggregations builder service.
 *
 * @author Petteri Karttunen
 */
public interface AggregationsBuilder {

	/**
	 * Builds a collection of aggregations.
	 *
	 * @param queryContext
	 * @return
	 * @throws Exception
	 */
	public List<Aggregation> buildAggregation(QueryContext queryContext) 
			throws Exception;

}