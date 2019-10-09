
package fi.soveltia.liferay.gsearch.core.impl.facet;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.FacetConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessor;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Filters DDM structures by key.
 *
 * @author Petteri Karttunen
 */
@Component(immediate = true, service = FacetProcessor.class)
public class FilterDDMStructuresByKeyFacetProcessor
	extends BaseFacetProcessor implements FacetProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return _FIELD_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject processFacetResults(
			QueryContext queryContext, AggregationResult aggregationResult,
			JSONObject facetConfiguration)
		throws Exception {

		TermsAggregationResult facetResult = (TermsAggregationResult)aggregationResult;
		
		JSONObject processorParams = facetConfiguration.getJSONObject(
				FacetConfigurationKeys.PROCESSOR_PARAMS);

		Locale locale = (Locale)queryContext.getParameter(
			ParameterNames.LOCALE);

		JSONArray termArray = JSONFactoryUtil.createJSONArray();

		JSONArray ddmStructureKeys = processorParams.getJSONArray(
				"ddm_structure_key");

		if ((ddmStructureKeys == null) || (ddmStructureKeys.length() == 0)) {
			return null;
		}
		
        for (Bucket bucket : facetResult.getBuckets()) {
        	
			for (int i = 0; i < ddmStructureKeys.length(); i++) {
				
				if (bucket.getKey().equals(
						ddmStructureKeys.getString(i))) {
					try {
						JSONObject item =
							JSONFactoryUtil.createJSONObject();

						item.put(FacetConfigurationKeys.FREQUENCY, 
								bucket.getDocCount());
						
						item.put(FacetConfigurationKeys.NAME,
							_getDDMStructureName(bucket.getKey(), locale));
						
						item.put(FacetConfigurationKeys.VALUE,
								bucket.getKey());

						termArray.put(item); 

					} catch (Exception e) {
						_log.error(e.getMessage(), e);
					}
				}
			}
        }
		return createResultObject(termArray, getParamName(facetConfiguration), facetConfiguration);
	}				

	
	/**
	 * Gets ddm structure key.
	 * 
	 * @param ddmStructureKey
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	private String _getDDMStructureName(String ddmStructureKey, Locale locale)
		throws Exception {

		DDMStructure structure = _portalCache.get(ddmStructureKey);

		if (structure != null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Structure for key " + ddmStructureKey + " found from cache.");
			}
		} else {

			DynamicQuery structureQuery = _ddmStructureLocalService.dynamicQuery();
	
			structureQuery.add(
				RestrictionsFactoryUtil.eq("structureKey", ddmStructureKey));
	
			List<DDMStructure> structures =
				DDMStructureLocalServiceUtil.dynamicQuery(structureQuery);
	
			structure = structures.get(0);
		}

		return structure.getName(locale, true);
	}

	private static final String _FIELD_NAME = "ddmStructureKey";

	private static final Logger _log = LoggerFactory.getLogger(
		FilterDDMStructuresByKeyFacetProcessor.class);

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;
	
	@Reference(unbind = "-")
	@SuppressWarnings("unchecked")
	private void setSingleVMPool(SingleVMPool singleVMPool) {
		_portalCache =
			(PortalCache<String, DDMStructure>)singleVMPool.getPortalCache(
				DDMStructureFacetProcessor.class.getName());
	}

	private PortalCache<String, DDMStructure> _portalCache;

}