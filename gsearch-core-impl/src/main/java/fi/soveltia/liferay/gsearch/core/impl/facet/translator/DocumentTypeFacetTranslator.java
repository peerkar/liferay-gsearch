package fi.soveltia.liferay.gsearch.core.impl.facet.translator;

import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeService;
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
import fi.soveltia.liferay.gsearch.core.api.query.QueryParams;

/**
 * Facet translator implementation for document type. 
 * 
 * {@see FacetTranslator}
 * 
 * @author Petteri Karttunen
 *
 */
@Component(
	immediate = true
)
public class DocumentTypeFacetTranslator implements FacetTranslator {

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

			JSONObject item = parseDocumentTypeData(tc, queryParams.getLocale());
			
			if (item != null) {
				facetArray.put(item);
			}
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
	 * Parse document type data.
	 * 
	 * @param fileEntryTypeId
	 * @return JSON object
	 * @throws PortalException 
	 */
	protected  JSONObject parseDocumentTypeData(TermCollector tc, Locale locale) throws PortalException {

		long fileEntryTypeId = Long.valueOf(tc.getTerm());
		
		if (fileEntryTypeId == 0) {
			return null;
		}

		DLFileEntryType type = _dLFileEntryTypeService.getFileEntryType(fileEntryTypeId);
		
		JSONObject item = JSONFactoryUtil.createJSONObject();

		item.put("frequency", tc.getFrequency());
		item.put("groupName", _groupLocalService.getGroup(type.getGroupId()).getName(
					locale, true));
		item.put("name", type.getName(locale, true));
		item.put("term", fileEntryTypeId);
		
		return item;
	}

	@Reference(unbind = "-")
	protected void setDLFileEntryTypeService(
		DLFileEntryTypeService dLFileEntryTypeService) {

		_dLFileEntryTypeService = dLFileEntryTypeService;
	}

	@Reference(unbind = "-")
	protected void setGroupLocalService(GroupLocalService groupLocalService) {

		_groupLocalService = groupLocalService;
	}

	protected String _facetName;

	private static DLFileEntryTypeService _dLFileEntryTypeService;

	private static GroupLocalService _groupLocalService;
}
