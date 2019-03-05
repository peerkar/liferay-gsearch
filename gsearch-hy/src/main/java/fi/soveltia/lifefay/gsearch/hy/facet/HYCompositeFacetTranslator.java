package fi.soveltia.lifefay.gsearch.hy.facet;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessor;
import fi.soveltia.liferay.gsearch.core.api.params.FacetParameter;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * HY composite facet translator.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	service = FacetProcessor.class
)
public class HYCompositeFacetTranslator implements FacetProcessor {

	@Override
	public String getName() {

		return NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	public JSONObject processFacetResults(
		QueryContext queryContext, Collection<Facet> facets,
		JSONObject facetConfiguration) {

		JSONObject processorParams =
			facetConfiguration.getJSONObject("processor_params");

		JSONObject resultItem = JSONFactoryUtil.createJSONObject();

		JSONArray termArray = JSONFactoryUtil.createJSONArray();

		resultItem.put("field_name", "hf");
		resultItem.put("param_name", facetConfiguration.get("param_name"));
		resultItem.put("icon", facetConfiguration.get("icon"));
		resultItem.put("hide", facetConfiguration.getBoolean("hide", false));
		resultItem.put("values", termArray);

		addNewsResults(facets, processorParams, termArray);
		addContentsResults(facets, processorParams, termArray);
		addDocumentResults(facets, processorParams, termArray);
		addFeedResults(facets, processorParams, termArray);
		addExpertSearchContactResults(facets, processorParams, termArray);
		addToolResults(facets, processorParams, termArray);

		return resultItem;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processFacetParameters(
		List<FacetParameter> facetParameters, String[] parameterValues,
		JSONObject facetConfiguration) {

		JSONObject processorParams =
			facetConfiguration.getJSONObject("processor_params");

		String multiValueOperator = processorParams.getString("multi_value_operator");

		String documents = processorParams.getString("documents_param_value");

		String feeds = processorParams.getString("feeds_param_value");

		String expertSearchContacts = processorParams.getString("expert_search_contacts_param_value");

		String tools = processorParams.getString("tools_param_value");

		String news = processorParams.getString("news_param_value");

		String contents = processorParams.getString("contents_param_value");

		List<String> entryClassNames = new ArrayList<>();

		List<String> ddmStructureKeys = new ArrayList<>();

		for (String parameterValue : parameterValues) {

			// Documents.

			if (parameterValue.equals(documents)) {

				String value =
					processorParams.getString("documents_entry_class_name");

				if (value != null) {
					entryClassNames.add(value);
				}
			}

			// Feeds.

			if (parameterValue.equals(feeds)) {

				String value =
					processorParams.getString("feeds_entry_class_name");

				if (value != null) {
					entryClassNames.add(value);
				}
			}

			if (parameterValue.equals(expertSearchContacts)) {

				String value =
					processorParams.getString("expert_search_contacts_entry_class_name");

				if (value != null) {
					entryClassNames.add(value);
				}
			}

			if (parameterValue.equals(tools)) {

				String value =
					processorParams.getString("tools_entry_class_name");

				if (value != null) {
					entryClassNames.add(value);
				}
			}

			// Contents.

			if (parameterValue.equals(contents)) {

				ddmStructureKeys.addAll(
					getDDMStructureKeyValues(
						"contents_ddm_structure_keys", processorParams));

			}

			// News.

			if (parameterValue.equals(news)) {

				ddmStructureKeys.addAll(
					getDDMStructureKeyValues(
						"news_ddm_structure_keys", processorParams));

			}
		}

		if (entryClassNames.size() > 0) {

			facetParameters.add(
				new FacetParameter(
					"entryClassName", entryClassNames, true, multiValueOperator, "post"));
		}

		if (ddmStructureKeys.size() > 0) {

			facetParameters.add(
				new FacetParameter(
					"ddmStructureKey", ddmStructureKeys, true, multiValueOperator, "post"));
		}
	}

	private void addDocumentResults(Collection<Facet> facets, JSONObject processorParams, JSONArray termArray) {

		String termKey =
			processorParams.getString("documents_entry_class_name");

		String paramValue = processorParams.getString("documents_param_value");

		FacetCollector facetCollector =
			getFacetCollector(facets, "entryClassName");

		if (facetCollector == null) {
			return;
		}

		List<TermCollector> termCollectors = facetCollector.getTermCollectors();

        addTermCollectorItems(termArray, termKey, paramValue, termCollectors);
    }

    private void addTermCollectorItems(JSONArray termArray, String termKey, String paramValue, List<TermCollector> termCollectors) {
        for (TermCollector tc : termCollectors) {

            if (termKey.equals(tc.getTerm())) {
                addTermItem(termArray, paramValue, tc.getFrequency());
                break;
            }
        }
    }

    private void addContentsResults(Collection<Facet> facets, JSONObject processorParams, JSONArray termArray) {

		List<String> ddmStructureKeys = getDDMStructureKeyValues(
			"contents_ddm_structure_keys", processorParams);

		String paramValue = processorParams.getString("contents_param_value");

		FacetCollector facetCollector =
			getFacetCollector(facets, "ddmStructureKey");

		if (facetCollector == null) {
			return;
		}

		List<TermCollector> termCollectors = facetCollector.getTermCollectors();

		int count = 0;

		for (TermCollector tc : termCollectors) {

			if (ddmStructureKeys.contains(tc.getTerm())) {
				count += tc.getFrequency();
			}
		}

		if (count > 0) {
            addTermItem(termArray, paramValue, count);
        }
	}

    private void addTermItem(JSONArray termArray, String paramValue, int count) {
        JSONObject item = JSONFactoryUtil.createJSONObject();

        item.put("frequency", count);
        item.put("name", paramValue);
        item.put("term", paramValue);

        termArray.put(item);
    }

    private void addFeedResults(Collection<Facet> facets, JSONObject processorParams, JSONArray termArray) {

		String termkey = processorParams.getString("feeds_entry_class_name");

		String paramValue = processorParams.getString("feeds_param_value");

		FacetCollector facetCollector =
			getFacetCollector(facets, "entryClassName");

		if (facetCollector == null) {
			return;
		}

		List<TermCollector> termCollectors = facetCollector.getTermCollectors();

        addTermCollectorItems(termArray, termkey, paramValue, termCollectors);
    }

	private void addExpertSearchContactResults(Collection<Facet> facets, JSONObject processorParams, JSONArray termArray) {

		String termkey = processorParams.getString("expert_search_contacts_entry_class_name");

		String paramValue = processorParams.getString("expert_search_contacts_param_value");

		FacetCollector facetCollector =
			getFacetCollector(facets, "entryClassName");

		if (facetCollector == null) {
			return;
		}

		List<TermCollector> termCollectors = facetCollector.getTermCollectors();

        addTermCollectorItems(termArray, termkey, paramValue, termCollectors);
    }

	private void addToolResults(Collection<Facet> facets, JSONObject processorParams, JSONArray termArray) {

		String termkey = processorParams.getString("tools_entry_class_name");

		String paramValue = processorParams.getString("tools_param_value");

		FacetCollector facetCollector =
			getFacetCollector(facets, "entryClassName");

		if (facetCollector == null) {
			return;
		}

		List<TermCollector> termCollectors = facetCollector.getTermCollectors();

		addTermCollectorItems(termArray, termkey, paramValue, termCollectors);
	}

	private void addNewsResults(Collection<Facet> facets, JSONObject processorParams, JSONArray termArray) {

		List<String> ddmStructureKeys = getDDMStructureKeyValues(
			"news_ddm_structure_keys", processorParams);

		String paramValue = processorParams.getString("news_param_value");

		FacetCollector facetCollector =
			getFacetCollector(facets, "ddmStructureKey");

		if (facetCollector == null) {
			return;
		}

		List<TermCollector> termCollectors = facetCollector.getTermCollectors();

		int count = 0;

		for (TermCollector tc : termCollectors) {

			if (ddmStructureKeys.contains(tc.getTerm())) {
				count += tc.getFrequency();
			}
		}

		if (count > 0) {

            addTermItem(termArray, paramValue, count);
        }
	}

	private List<String> getDDMStructureKeyValues(
		String key, JSONObject configuration) {

		JSONArray keys = configuration.getJSONArray(key);

		List<String> values = new ArrayList<>();

		for (int i = 0; i < keys.length(); i++) {

			values.add(keys.getString(i));
		}

		return values;
	}

	private FacetCollector getFacetCollector(Collection<Facet> facets, String fieldName) {

		for (Facet facet : facets) {

			if (facet.isStatic()) {
				continue;
			}

			if (facet.getFieldName().equals(fieldName)) {
				return facet.getFacetCollector();
			}
		}
		return null;
	}

	private static final String NAME = "hy_composite_facet";

}
