
package fi.soveltia.liferay.gsearch.core.impl.facet;

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

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetTranslator;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Facet translator implementation for document type. 
 * 
 * @author Petteri Karttunen
 * 
 */
@Component(
	immediate = true,
	service = FacetTranslator.class
)
public class FileEntryTypeFacetTranslator implements FacetTranslator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canProcess(String translatorName) {

		return NAME.equals(translatorName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray fromResults(
		QueryContext queryContext, FacetCollector facetCollector,
		JSONObject configuration)
		throws Exception {

		Locale locale  = (Locale)queryContext.getParameter(ParameterNames.LOCALE);
		
		JSONArray facetArray = JSONFactoryUtil.createJSONArray();

		List<TermCollector> termCollectors = facetCollector.getTermCollectors();

		for (TermCollector tc : termCollectors) {

			JSONObject item =
				parseDocumentTypeData(tc, locale);

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
	public String[] toQuery(String value, JSONObject configuration) {

		return new String[] {
			value
		};
	}

	/**
	 * Parse document type data.
	 * 
	 * @param fileEntryTypeId
	 * @return JSON object
	 * @throws PortalException
	 */
	protected JSONObject parseDocumentTypeData(TermCollector tc, Locale locale)
		throws PortalException {

		long fileEntryTypeId = Long.valueOf(tc.getTerm());

		if (fileEntryTypeId == 0) {
			return null;
		}

		DLFileEntryType type =
			_dLFileEntryTypeService.getFileEntryType(fileEntryTypeId);

		JSONObject item = JSONFactoryUtil.createJSONObject();

		item.put("frequency", tc.getFrequency());
		item.put(
			"group_name", _groupLocalService.getGroup(type.getGroupId()).getName(
				locale, true));
		item.put("name", type.getName(locale, true));
		item.put("term", fileEntryTypeId);

		return item;
	}

	@Reference
	private DLFileEntryTypeService _dLFileEntryTypeService;

	@Reference
	private GroupLocalService _groupLocalService;
	
	private static final String NAME = "file_entry_type";
}
