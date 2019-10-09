
package fi.soveltia.liferay.gsearch.core.impl.facet;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.GroupLocalService;
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
 * Facet processor for web content structures.
 *
 * @author Petteri Karttunen
 */
@Component(immediate = true, service = FacetProcessor.class)
public class DDMStructureFacetProcessor
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

		Locale locale = (Locale)queryContext.getParameter(
				ParameterNames.LOCALE);

		JSONArray termArray = JSONFactoryUtil.createJSONArray();

        for (Bucket bucket : facetResult.getBuckets()) {

        	try {
				JSONObject item = parseStructureData(bucket, locale);
	
				termArray.put(item);
				
        	} catch (Exception e) {
        		_log.error(e.getMessage(), e);
        	}
		}

		return createResultObject(termArray, getParamName(facetConfiguration), facetConfiguration);
	}

	/**
	 * Set structure data. 
	 * 
	 * There's no method for fetching structure by key so we
	 * are using DynamicQuery here.
	 * 
	 * As dynamic queries are not cached, we are using the portalcache.
	 * 
	 * @param bucket
	 * @param locale
	 * @return
	 * @throws PortalException
	 */
	protected JSONObject parseStructureData(Bucket bucket, Locale locale)
		throws Exception {

		DDMStructure structure = _portalCache.get(bucket.getKey());

		if (structure != null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Structure for key " + bucket.getKey() + " found from cache.");
			}
		} else {

			DynamicQuery structureQuery = _ddmStructureLocalService.dynamicQuery();
	
			structureQuery.add(
				RestrictionsFactoryUtil.eq("structureKey", bucket.getKey()));
	
			List<DDMStructure> structures =
				DDMStructureLocalServiceUtil.dynamicQuery(structureQuery);
	
			structure = structures.get(0);

			// Put to cache.

			if (structure != null) {
				_portalCache.put(
					bucket.getKey(), structure, _CACHE_TIMEOUT);
			}
		}

		JSONObject item = JSONFactoryUtil.createJSONObject();

		item.put(FacetConfigurationKeys.FREQUENCY, bucket.getDocCount());
		item.put(
				FacetConfigurationKeys.GROUP_NAME,
			_groupLocalService.getGroup(
				structure.getGroupId()
			).getName(
				locale, true
			));
		item.put(FacetConfigurationKeys.NAME, structure.getName(locale, true));
		item.put(FacetConfigurationKeys.VALUE, bucket.getKey());

		return item;
	}

	private static final int _CACHE_TIMEOUT = 3600;
	
	private static final Logger _log = LoggerFactory.getLogger(
			DDMStructureFacetProcessor.class);

	private static final String _FIELD_NAME = "ddmStructureKey";

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private GroupLocalService _groupLocalService;
	
	@Reference(unbind = "-")
	@SuppressWarnings("unchecked")
	private void setSingleVMPool(SingleVMPool singleVMPool) {
		_portalCache =
			(PortalCache<String, DDMStructure>)singleVMPool.getPortalCache(
				DDMStructureFacetProcessor.class.getName());
	}

	private PortalCache<String, DDMStructure> _portalCache;
}