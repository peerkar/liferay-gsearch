
package fi.soveltia.liferay.gsearch.core.impl.results.item.processor;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Map.Entry;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.item.processor.ResultItemProcessor;

/**
 * Result item processor for extra parameters. 
 * 
 * This is a result item processor for including a thumbnail 
 * or user image in a result item. 
 * 
 * As user image information is not included in index, 
 * we cannot use the "additionalFields" in QueryParams.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ResultItemProcessor.class
)
public class ExtraParamsResultItemProcessor implements ResultItemProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {

		return true;
	}

	@Override
	public void process(
		PortletRequest portletRequest, QueryParams queryParams,
		Document document, ResultItemBuilder resultItemBuilder,
		JSONObject resultItem)
		throws Exception {

		if (queryParams.getExtraParams() == null) {
			return;
		}

		for (Entry<String, Object> entry : queryParams.getExtraParams().entrySet()) {

			if (entry.getKey().equals("includeThumbnail")) {

				includeThumbnail(
					portletRequest, document, resultItemBuilder, resultItem);

			}
			else if (entry.getKey().equals("includeUserPortrait")) {

				includeUserPortrait(portletRequest, document, resultItem);
			}
		}
	}

	/**
	 * Include user portrait in result item. We let this method to handle
	 * exceptions internally so that for example corrupt user references won't
	 * silence the whole processor.
	 * 
	 * @param portletRequest
	 * @param document
	 * @param resultItem
	 */
	private void includeUserPortrait(
		PortletRequest portletRequest, Document document,
		JSONObject resultItem) {

		try {

			long userId = GetterUtil.getLong(document.get(Field.USER_ID));

			User user = _userLocalService.getUser(userId);

			ThemeDisplay themeDisplay =
				(ThemeDisplay) portletRequest.getAttribute(
					GSearchWebKeys.THEME_DISPLAY);

			if (user.getPortraitId() != 0) {
				resultItem.put(
					"userPortraitUrl", user.getPortraitURL(themeDisplay));
			}
			resultItem.put(
				"userInitials", user.getFirstName().substring(0, 1) +
					user.getLastName().substring(0, 1));

			resultItem.put(
				"userName", user.getFullName());
		}
		catch (PortalException e) {

			_log.warn(e.getMessage());
			
			String name = document.get(Field.USER_NAME);

			String[] nameParts = name.split(" ");
			
			resultItem.put(
				"userInitials", nameParts[0].substring(0, 1).toUpperCase() +
				nameParts[0].substring(0, 1).toUpperCase());
		}
	}

	/**
	 * Include thumbnail. To optimize the speed and reduce unnecessary DB calls,
	 * thumbnails are not included in the result items by default.
	 * 
	 * @param portletRequest
	 * @param document
	 * @param resultItemBuilder
	 * @param resultItem
	 */
	private void includeThumbnail(
		PortletRequest portletRequest, Document document,
		ResultItemBuilder resultItemBuilder, JSONObject resultItem) {

		try {
			resultItem.put(
				"imageSrc",
				resultItemBuilder.getThumbnail(portletRequest, document));
		}
		catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	private static final Logger _log =
		LoggerFactory.getLogger(ExtraParamsResultItemProcessor.class);

	@Reference
	private UserLocalService _userLocalService;
}
