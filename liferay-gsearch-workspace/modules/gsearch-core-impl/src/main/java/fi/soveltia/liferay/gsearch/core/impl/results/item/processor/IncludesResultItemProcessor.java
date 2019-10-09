package fi.soveltia.liferay.gsearch.core.impl.results.item.processor;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.Field;

import java.util.Map;

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
 * Includes result item processor.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ResultItemProcessor.class
)
public class IncludesResultItemProcessor implements ResultItemProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void process(
			QueryContext queryContext, Document document,
			ResultItemBuilder resultItemBuilder, JSONObject resultItem)
		throws Exception {

		// Include thumbnail.

		if (GetterUtil.getBoolean(
				queryContext.getParameter(ParameterNames.INCLUDE_THUMBNAIL))) {

			_includeThumbnail(
				queryContext, document, resultItemBuilder, resultItem);
		}

		// Include user portrait.

		if (GetterUtil.getBoolean(
				queryContext.getParameter(
					ParameterNames.INCLUDE_USER_PORTRAIT))) {

			_includeUserPortrait(queryContext, document, resultItem);
		}
		
		// Include document.

		if (GetterUtil.getBoolean(
				queryContext.getParameter(
					ParameterNames.INCLUDE_RAW_DOCUMENT))) {
			_includeRawDocument(document, resultItem);
		}

	}

	/**
	 * Includes raw result document.
	 * 
	 * @param document
	 * @param resultItem
	 */
	private void _includeRawDocument(
			Document document, JSONObject resultItem) {

		JSONObject doc = JSONFactoryUtil.createJSONObject();

		for (Map.Entry<String, Field> e :
				document.getFields().entrySet()) {
			doc.put(e.getKey(), e.getValue().getValue());
			
		}
		
		resultItem.put("document", doc);

	}
	
	/**
	 * Includes thumbnail. To optimize the speed and reduce unnecessary DB calls,
	 * thumbnails are not included in the result items by default.
	 *
	 * @param queryContext
	 * @param document
	 * @param resultItemBuilder
	 * @param resultItem
	 */
	private void _includeThumbnail(
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

	/**
	 * Includes user portrait in result item. We let this method to handle
	 * exceptions internally so that for example corrupt user references won't
	 * silence the whole processor.
	 *
	 * @param portletRequest
	 * @param document
	 * @param resultItem
	 */
	private void _includeUserPortrait(
		QueryContext queryContext, Document document, JSONObject resultItem) {

		try {
			long userId = document.getLong(com.liferay.portal.kernel.search.Field.USER_ID);

			User user = _userLocalService.getUser(userId);

			if (user.getPortraitId() != 0) {
				String userPortraitUrl = null;

				PortletRequest portletRequest =
					GSearchUtil.getPortletRequestFromContext(queryContext);

				if (portletRequest != null) {
					ThemeDisplay themeDisplay =
						(ThemeDisplay)portletRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

					userPortraitUrl = user.getPortraitURL(themeDisplay);
				}
				else {
					userPortraitUrl = UserConstants.getPortraitURL(
						(String)queryContext.getParameter(
							ParameterNames.PATH_IMAGE),
						user.isMale(), user.getPortraitId(),
						user.getUserUuid());
				}

				if (userPortraitUrl != null) {
					resultItem.put("userPortraitUrl", userPortraitUrl);
				}
			}

			resultItem.put(
				"userInitials",
				user.getFirstName(
				).substring(
					0, 1
				) +
					user.getLastName(
					).substring(
						0, 1
					));

			resultItem.put("userName", user.getFullName());
		}
		catch (PortalException pe) {
			_log.warn(pe.getMessage());

			String name = document.getString(
					com.liferay.portal.kernel.search.Field.USER_NAME);

			String[] nameParts = name.split(" ");

			resultItem.put(
				"userInitials",
				nameParts[0].substring(
					0, 1
				).toUpperCase() +
					nameParts[0].substring(
						0, 1
					).toUpperCase());
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(
		IncludesResultItemProcessor.class);

	@Reference
	private UserLocalService _userLocalService;

}