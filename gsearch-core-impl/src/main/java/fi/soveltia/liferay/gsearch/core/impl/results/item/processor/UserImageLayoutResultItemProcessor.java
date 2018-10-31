
package fi.soveltia.liferay.gsearch.core.impl.results.item.processor;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.item.processor.ResultItemProcessor;
import fi.soveltia.liferay.gsearch.core.impl.results.layout.UserImageListResultLayout;

@Component(
	immediate = true, 
	service = ResultItemProcessor.class
)
public class UserImageLayoutResultItemProcessor implements ResultItemProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {

		return true;
	}

	@Override
	public void process(
		PortletRequest portletRequest, QueryParams queryParams, Document document,
		ResultItemBuilder resultItemBuilder, JSONObject resultItem)
		throws Exception {

		if (UserImageListResultLayout.KEY.equals(queryParams.getResultsLayout())) {
		
			long userId = GetterUtil.getLong(document.get(Field.USER_ID));
			User user = _userLocalService.getUser(userId);
	
			ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(
				GSearchWebKeys.THEME_DISPLAY);
	
			if (user.getPortraitId() == 0) {
				resultItem.put(
					"userInitials", user.getFirstName().substring(0, 1) +
						user.getLastName().substring(0, 1));
			}
			else {
				resultItem.put(
					"userPortraitUrl", user.getPortraitURL(themeDisplay));
			}
		}
	}

	@Reference
	private UserLocalService _userLocalService;
}
