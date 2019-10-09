
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;

/**
 * Result item builder reference.
 *
 * Class for keeping ResultItemBuilder and service ranking together and making
 * ResultItemBuilder comparable by ranking.
 */
public class ResultItemBuilderReference
	implements Comparable<ResultItemBuilderReference> {

	public ResultItemBuilderReference(
		ResultItemBuilder resultItemBuilder, int serviceRanking) {

		_resultItemBuilder = resultItemBuilder;
		_serviceRanking = serviceRanking;
	}

	@Override
	public int compareTo(ResultItemBuilderReference r) {

		// Return descending order

		return Integer.compare(r._serviceRanking, _serviceRanking);
	}

	public ResultItemBuilder getResultItemBuilder() {
		return _resultItemBuilder;
	}

	private ResultItemBuilder _resultItemBuilder;
	private int _serviceRanking;

}