
package fi.soveltia.liferay.gsearch.web.search.internal.menuoption;

import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeService;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.web.search.internal.util.GSearchUtil;
import fi.soveltia.liferay.gsearch.web.search.menuoption.DocumentTypeOptions;

/**
 * Document type options implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = DocumentTypeOptions.class
)
public class DocumentTypeOptionsImpl implements DocumentTypeOptions {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getOptions(
		PortletRequest portletRequest,
		GSearchDisplayConfiguration gSearchDisplayConfiguration)
		throws Exception {

		JSONArray optionsArray = JSONFactoryUtil.createJSONArray();

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		Locale locale = themeDisplay.getLocale();

		String scope =
			ParamUtil.getString(portletRequest, GSearchWebKeys.SCOPE_FILTER);

		// Get group ids to get document types from.

		long[] groupIds;

		if ("all".equals(scope)) {
			groupIds = GSearchUtil.getUserAccessibleSiteGroupIds(themeDisplay);
		}
		else {
			groupIds = new long[] {
				themeDisplay.getCompanyGroupId(), themeDisplay.getScopeGroupId()
			};
		}

		// Get types in the groups and do sort.

		List<DLFileEntryType> types =
			_dLFileEntryTypeService.getFileEntryTypes(groupIds);

		List<DLFileEntryType> sortedList = new ArrayList<DLFileEntryType>();
		sortedList.addAll(types);

		sortedList.sort(
			(DLFileEntryType d1, DLFileEntryType d2) -> d1.getName(
				locale, true).compareTo(d2.getName(locale, true)));

		for (DLFileEntryType type : sortedList) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Added type " +
						type.getName(themeDisplay.getLocale(), true));
			}

			JSONObject item = JSONFactoryUtil.createJSONObject();
			item.put("key", type.getFileEntryTypeId());
			item.put("name", type.getName(themeDisplay.getLocale(), true));

			// Put group name if it's from not current group.
			
			if (type.getGroupId() != themeDisplay.getScopeGroupId()) {
				item.put("groupName", _groupLocalService.getGroup(type.getGroupId()).getName(locale, true));
				item.put("scope", "all");
			}
			optionsArray.put(item);
		}

		return optionsArray;
	}

	@Reference(unbind = "-")
	protected void setDLFileEntryTypeService(
		DLFileEntryTypeService dLFileEntryTypeService) {

		_dLFileEntryTypeService = dLFileEntryTypeService;
	}

	@Reference(unbind = "-")
	protected void setGroupLocalService(
		GroupLocalService groupLocalService) {

		_groupLocalService = groupLocalService;
	}
	
	@Reference(unbind = "-")
	protected void setPortal(Portal portal) {

		_portal = portal;
	}

	@Reference
	private DLFileEntryTypeService _dLFileEntryTypeService;

	@Reference
	private GroupLocalService _groupLocalService;	
	
	@Reference
	private Portal _portal;

	private static final Log _log =
		LogFactoryUtil.getLog(WebContentStructureOptionsImpl.class);
}
