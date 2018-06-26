package fi.soveltia.liferay.gsearch.core.impl.results.item;

import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;

/**
 * Class for keeping ResultItemBuilder and service ranking together and making
 * ResultItemBuilder comparable by ranking.
 */
public class ResultItemBuilderReference implements Comparable<ResultItemBuilderReference> {

    public ResultItemBuilderReference(ResultItemBuilder resultItemBuilder, int serviceRanking) {

        _resultItemBuilder = resultItemBuilder;
        _serviceRanking = serviceRanking;
    }

    public ResultItemBuilder getResultItemBuilder() {

        return _resultItemBuilder;
    }

    @Override
    public int compareTo(ResultItemBuilderReference r) {

        // Return descending order
        return Integer.compare(r._serviceRanking, this._serviceRanking);
    }

    private ResultItemBuilder _resultItemBuilder;
    private int _serviceRanking;
}
