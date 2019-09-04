
package fi.soveltia.liferay.gsearch.core.impl.results.item.processor;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.item.processor.ResultItemProcessor;
import fi.soveltia.liferay.gsearch.core.impl.util.GSearchUtil;

/**
 * Check if any extra fields should be added to the results items.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ResultItemProcessor.class
)
public class AdditionalResultFieldsProcessor implements ResultItemProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process(
		QueryContext queryContext,
		Document document, ResultItemBuilder resultItemBuilder,
		JSONObject resultItem)
		throws Exception {

		Map<String, Class<?>> additionalResultFields =
			(Map<String, Class<?>>) queryContext.getParameter(
				ParameterNames.ADDITIONAL_RESULT_FIELDS);

		if (additionalResultFields != null) {

			// Loop for additional result fields. These have to be 1-1 index fields.
	
			for (Entry<String, Class<?>> entry : additionalResultFields.entrySet()) {

				if (entry.getValue().isAssignableFrom(String.class)) {
	
					String value = document.get(entry.getKey());
	
					if (Validator.isNotNull(value)) {
						resultItem.put(entry.getKey(), value);
					}
	
				}
				else if (entry.getValue().isAssignableFrom(String[].class)) {
	
					String[] values = document.getValues(entry.getKey());
	
					if (values != null && values.length > 0 &&
						values[0].length() > 0) {
						resultItem.put(entry.getKey(), values);
					}
				}
			}
		}

		// Other out of the box options.

		// Include thumbnail?

		if (GetterUtil.getBoolean(
			queryContext.getParameter(ParameterNames.INCLUDE_THUMBNAIL))) {

			includeThumbnail(
				queryContext, document, resultItemBuilder, resultItem);

		}

		// Include user portrait?

		if (GetterUtil.getBoolean(
			queryContext.getParameter(ParameterNames.INCLUDE_USER_PORTRAIT))) {

			includeUserPortrait(queryContext, document, resultItem);
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
		QueryContext queryContext, Document document,
		JSONObject resultItem) {

		try {

			long userId = GetterUtil.getLong(document.get(Field.USER_ID));

			User user = _userLocalService.getUser(userId);

			if (user.getPortraitId() != 0) {

				String userPortraitUrl = null;
	
				PortletRequest portletRequest = GSearchUtil.getPortletRequestFromContext(queryContext);
				
				if (portletRequest != null) {
				
					ThemeDisplay themeDisplay =
						(ThemeDisplay) portletRequest.getAttribute(
							WebKeys.THEME_DISPLAY);
		
					userPortraitUrl = user.getPortraitURL(themeDisplay);
	
				} else {
						userPortraitUrl = UserConstants.getPortraitURL((String)queryContext.getParameter(ParameterNames.PATH_IMAGE), user.isMale(), user.getPortraitId(), user.getUserUuid());
				}
	
				if (userPortraitUrl != null ) {
				
					resultItem.put(
						"userPortraitUrl", userPortraitUrl);
				}
			}
			
			resultItem.put(
				"userInitials", user.getFirstName().substring(0, 1) +
					user.getLastName().substring(0, 1));

			resultItem.put("userName", user.getFullName());
			
			
			
			
		}
		catch (PortalException e) {

			_log.debug(e.getMessage());

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
	 * @param queryContext
	 * @param document
	 * @param resultItemBuilder
	 * @param resultItem
	 */
	private void includeThumbnail(
		QueryContext queryContext, Document document,
		ResultItemBuilder resultItemBuilder, JSONObject resultItem) {

		try {
			resultItem.put(
				"imageSrc",
				resultItemBuilder.getThumbnail(queryContext, document));
		}
		catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	private static final Logger _log =
		LoggerFactory.getLogger(AdditionalResultFieldsProcessor.class);

	@Reference
	private UserLocalService _userLocalService;

}
