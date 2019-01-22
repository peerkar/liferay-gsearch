package fi.soveltia.liferay.gsearch.core.impl.results.facet.processor;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.Facet;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.facet.processor.FacetProcessor;


public class AssetVocabularyFacetProcessor implements FacetProcessor {

	@Override
	public boolean canProcess(String facetName) {
		return NAME.equals(facetName);
	}

	@Override
	public JSONArray process(
		QueryContext queryContext, JSONObject configuration, Facet facet)
		throws Exception {

		// TODO Auto-generated method stub
		return null;
	}

	
	private static final String NAME = "filter_by_asset_vocabulary";
}
