
package fi.soveltia.liferay.gsearch.core.impl.facet;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessor;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Filters DDM structures by key.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	service = FacetProcessor.class
)
public class FilterDDMStructuresByKeyFacetProcessor extends BaseFacetProcessor
	implements FacetProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {

		return NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject processFacetResults(
		QueryContext queryContext, Collection<Facet> facets,
		JSONObject facetConfiguration)
		throws Exception {

		JSONObject processorParams =
			facetConfiguration.getJSONObject("processor_params");

		String fieldName = processorParams.getString("field_name");

		FacetCollector facetCollector = getFacetCollector(facets, fieldName);

		Locale locale =
			(Locale) queryContext.getParameter(ParameterNames.LOCALE);

		JSONArray ddmStructureKeys =
			processorParams.getJSONArray("ddm_structure_key");

		JSONArray termArray = JSONFactoryUtil.createJSONArray();

		if (ddmStructureKeys != null && ddmStructureKeys.length() > 0) {

			try {
				List<TermCollector> termCollectors =
					facetCollector.getTermCollectors();

				for (TermCollector tc : termCollectors) {

					for (int i = 0; i < ddmStructureKeys.length(); i++) {

						if (tc.getTerm().equals(
							ddmStructureKeys.getString(i))) {

							JSONObject item =
								JSONFactoryUtil.createJSONObject();

							item.put("frequency", tc.getFrequency());
							item.put(
								"name",
								getDDMStructureName(tc.getTerm(), locale));
							item.put("term", tc.getTerm());

							termArray.put(item);
						}
					}
				}

			}
			catch (PortalException e) {
				_log.error(e.getMessage(), e);
			}

		}
		return createResultObject(termArray, fieldName, facetConfiguration);
	}

	private String getDDMStructureName(String ddmStructureKey, Locale locale)
		throws Exception {

		DynamicQuery structureQuery = _ddmStructureLocalService.dynamicQuery();
		structureQuery.add(
			RestrictionsFactoryUtil.eq("structureKey", ddmStructureKey));

		List<DDMStructure> structures =
			DDMStructureLocalServiceUtil.dynamicQuery(structureQuery);

		DDMStructure structure = structures.get(0);

		return structure.getName(locale, true);

	}

	private static final Logger _log =
		LoggerFactory.getLogger(FilterDDMStructuresByKeyFacetProcessor.class);

	private static final String NAME = "filter_ddm_structures_by_key";

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;
}
