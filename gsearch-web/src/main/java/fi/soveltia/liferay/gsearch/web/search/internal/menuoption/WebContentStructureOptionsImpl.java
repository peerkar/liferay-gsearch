
package fi.soveltia.liferay.gsearch.web.search.internal.menuoption;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.journal.model.JournalArticle;
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
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.web.search.internal.util.GSearchUtil;
import fi.soveltia.liferay.gsearch.web.search.menuoption.WebContentStructureOptions;

/**
 * Web content structure options implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = WebContentStructureOptions.class
)
public class WebContentStructureOptionsImpl
	implements WebContentStructureOptions {

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

		long classNameId = _portal.getClassNameId(JournalArticle.class);

		// Get group ids to get the structures from.

		long[] groupIds;

		if ("all".equals(scope)) {
			groupIds = GSearchUtil.getUserAccessibleSiteGroupIds(themeDisplay);
		}
		else {

			groupIds = new long[] {
				themeDisplay.getCompanyGroupId(), themeDisplay.getScopeGroupId()
			};
		}

		// Get structures and do sort.

		List<DDMStructure> structures = _ddmStructureService.getStructures(
			themeDisplay.getCompanyId(), groupIds, classNameId,
			WorkflowConstants.STATUS_APPROVED);

		List<DDMStructure> sortedList = new ArrayList<DDMStructure>();
		sortedList.addAll(structures);

		sortedList.sort(
			(DDMStructure s1, DDMStructure s2) -> s1.getName(
				locale, true).compareTo(s2.getName(locale, true)));

		for (DDMStructure structure : structures) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Added structure " +
						structure.getName(themeDisplay.getLocale(), true));
			}

			JSONObject item = JSONFactoryUtil.createJSONObject();
			item.put("key", structure.getStructureKey());
			item.put("name", structure.getName(themeDisplay.getLocale(), true));

			// Put group name if it's from not current group.

			if (structure.getGroupId() != themeDisplay.getScopeGroupId()) {
				item.put(
					"groupName",
					_groupLocalService.getGroup(structure.getGroupId()).getName(
						locale, true));
				item.put("scope", "all");

			}

			optionsArray.put(item);
		}

		return optionsArray;
	}

	@Reference(unbind = "-")
	protected void setDDMStructureService(
		DDMStructureService ddmStructureService) {

		_ddmStructureService = ddmStructureService;
	}

	@Reference(unbind = "-")
	protected void setGroupLocalService(GroupLocalService groupLocalService) {

		_groupLocalService = groupLocalService;
	}

	@Reference(unbind = "-")
	protected void setPortal(Portal portal) {

		_portal = portal;
	}

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	private static final Log _log =
		LogFactoryUtil.getLog(WebContentStructureOptionsImpl.class);
}
