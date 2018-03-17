
package fi.soveltia.liferay.gsearch.core.impl.facet.translator;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.service.GroupLocalService;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.facet.translator.FacetTranslator;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;

/**
 * Facet translator for web content structures.
 * 
 * {@see FacetTranslator}
 *  
 * @author Petteri Karttunen
 */
@Component(
	immediate = true
)
public class WebContentStructureFacetTranslator implements FacetTranslator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFacetName(String facetName) {

		_facetName = facetName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray translateValues(
		QueryParams queryParams, FacetCollector facetCollector,
		JSONObject configuration)
		throws Exception {

		JSONArray facetArray = JSONFactoryUtil.createJSONArray();

		List<TermCollector> termCollectors = facetCollector.getTermCollectors();

		for (TermCollector tc : termCollectors) {

			JSONObject item = parseStructureData(tc, queryParams.getLocale());
			facetArray.put(item);
		}

		return facetArray;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] translateParams(String value, JSONObject configuration) {

		return new String[]{value};
	}
	
	/**
	 * Set structure data.
	 * 
	 * There's no method for fetching
	 * structure by key so we are using DynamicQuery here.
	 * 
	 * @param structureKey
	 * @return
	 * @throws PortalException 
	 */
	protected JSONObject parseStructureData(TermCollector tc, Locale locale) throws PortalException {

		DynamicQuery structureQuery = _ddmStructureLocalService.dynamicQuery();
		structureQuery.add(
			RestrictionsFactoryUtil.eq("structureKey", tc.getTerm()));

		List<DDMStructure> structures =
			DDMStructureLocalServiceUtil.dynamicQuery(structureQuery);
		
		DDMStructure structure =  structures.get(0);

		JSONObject item = JSONFactoryUtil.createJSONObject();

		item.put("frequency", tc.getFrequency());
		item.put("groupName", _groupLocalService.getGroup(structure.getGroupId()).getName(
					locale, true));
		item.put("name", structure.getName(locale, true));
		item.put("term", tc.getTerm());
		
		return item;
	}

	@Reference(unbind = "-")
	protected void setDDMStructureLocalService(
		DDMStructureLocalService ddmStructureLocalService) {

		_ddmStructureLocalService = ddmStructureLocalService;
	}

	@Reference(unbind = "-")
	protected void setGroupLocalService(GroupLocalService groupLocalService) {

		_groupLocalService = groupLocalService;
	}

	protected String _facetName;

	private static DDMStructureLocalService _ddmStructureLocalService;

	private static GroupLocalService _groupLocalService;	
}
