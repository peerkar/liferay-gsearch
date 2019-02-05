
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
import com.liferay.portal.kernel.service.GroupLocalService;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessor;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Facet processor for web content structures.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = FacetProcessor.class
)
public class DDMStructureFacetProcessor extends BaseFacetProcessor
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

		if (facetCollector == null) {
			return null;
		}

		Locale locale =
			(Locale) queryContext.getParameter(ParameterNames.LOCALE);

		JSONArray termArray = JSONFactoryUtil.createJSONArray();

		List<TermCollector> termCollectors = facetCollector.getTermCollectors();

		for (TermCollector tc : termCollectors) {

			JSONObject item = parseStructureData(tc, locale);

			termArray.put(item);
		}

		return createResultObject(termArray, fieldName, facetConfiguration);
	}

	/**
	 * Set structure data. There's no method for fetching structure by key so we
	 * are using DynamicQuery here.
	 * 
	 * @param structureKey
	 * @return
	 * @throws PortalException
	 */
	protected JSONObject parseStructureData(TermCollector tc, Locale locale)
		throws PortalException {

		DynamicQuery structureQuery = _ddmStructureLocalService.dynamicQuery();
		structureQuery.add(
			RestrictionsFactoryUtil.eq("structureKey", tc.getTerm()));

		List<DDMStructure> structures =
			DDMStructureLocalServiceUtil.dynamicQuery(structureQuery);

		DDMStructure structure = structures.get(0);

		JSONObject item = JSONFactoryUtil.createJSONObject();

		item.put("frequency", tc.getFrequency());
		item.put(
			"group_name",
			_groupLocalService.getGroup(structure.getGroupId()).getName(
				locale, true));
		item.put("name", structure.getName(locale, true));
		item.put("term", tc.getTerm());

		return item;
	}

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	private static final String NAME = "ddm_structure";
}
