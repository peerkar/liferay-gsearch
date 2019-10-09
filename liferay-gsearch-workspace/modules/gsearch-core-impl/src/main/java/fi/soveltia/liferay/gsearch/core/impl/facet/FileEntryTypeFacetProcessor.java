
package fi.soveltia.liferay.gsearch.core.impl.facet;

import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;

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
 * Facet processor for document type.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = FacetProcessor.class
)
public class FileEntryTypeFacetProcessor
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

        		JSONObject item = parseDocumentTypeData(bucket, locale);

				if (item != null) {
					termArray.put(item);
				}
        	} catch (Exception e) {
        		_log.error(e.getMessage(), e);
        	}
		}

		return createResultObject(termArray, getParamName(facetConfiguration), facetConfiguration);
	}

	/**
	 * Parse document type data.
	 * 
	 * @param bucket
	 * @param locale
	 * @return
	 * @throws PortalException
	 */
	protected JSONObject parseDocumentTypeData(Bucket bucket, Locale locale)
		throws PortalException {

		long fileEntryTypeId = Long.valueOf(bucket.getKey());

		if (fileEntryTypeId == 0) {
			return null;
		}

		DLFileEntryType type = _dLFileEntryTypeService.getFileEntryType(
			fileEntryTypeId);

		JSONObject item = JSONFactoryUtil.createJSONObject();

		item.put(FacetConfigurationKeys.FREQUENCY, bucket.getDocCount());
		item.put(
				FacetConfigurationKeys.GROUP_NAME,
			_groupLocalService.getGroup(
				type.getGroupId()
			).getName(
				locale, true
			));
		item.put(FacetConfigurationKeys.NAME, type.getName(locale, true));
		item.put(FacetConfigurationKeys.VALUE, fileEntryTypeId);

		return item;
	}

	private static final Logger _log = LoggerFactory.getLogger(
			FileEntryTypeFacetProcessor.class);

	private static final String _FIELD_NAME = "fileEntryTypeId";

	@Reference
	private DLFileEntryTypeService _dLFileEntryTypeService;

	@Reference
	private GroupLocalService _groupLocalService;

}