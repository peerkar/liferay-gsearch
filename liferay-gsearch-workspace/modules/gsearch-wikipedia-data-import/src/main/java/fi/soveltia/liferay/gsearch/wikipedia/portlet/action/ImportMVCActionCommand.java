package fi.soveltia.liferay.gsearch.wikipedia.portlet.action;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

import fi.soveltia.liferay.gsearch.wikipedia.constants.MVCActionCommandNames;
import fi.soveltia.liferay.gsearch.wikipedia.constants.WikiPediaDataImportPortletKeys;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simplistic Wikipedia article importer.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + WikiPediaDataImportPortletKeys.WIKIPEDIADATAIMPORT,
		"mvc.command.name=" + MVCActionCommandNames.IMPORT
	},
	service = MVCActionCommand.class
)
public class ImportMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		// Wikipedia article titles, like 'Train'

		String wikiArticles = ParamUtil.getString(
			actionRequest, "wikiArticles");

		List<String> articles = _createArticleList(wikiArticles);

		// List of userIds to be used as article creators.

		String users = ParamUtil.getString(actionRequest, "userIds");

		List<Long> userIds = _createIdList(users);

		// List of groupIds to be used as target groups.

		String groups = ParamUtil.getString(actionRequest, "groupIds");

		List<Long> groupIds = _createIdList(groups);

		// Language id

		String languageId = ParamUtil.getString(
			actionRequest, "languageId", "en_US");

		// Number of articles to be created.

		int limit = ParamUtil.getInteger(actionRequest, "limit", 100);

		_importArticles(
			actionRequest, articles, userIds, groupIds, languageId, 0, limit);
	}

	/**
	 * Adds a new Journal Article.
	 *
	 * @param actionRequest
	 * @param userId
	 * @param groupId
	 * @param languageId
	 * @param title
	 * @param content
	 * @param assetTagNames
	 * @throws Exception
	 */
	private void _addJournalArticle(
			ActionRequest actionRequest, long userId, long groupId,
			String languageId, String title, String content,
			String[] assetTagNames)
		throws Exception {

		long timer = System.currentTimeMillis();

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			JournalArticle.class.getName(), actionRequest);

		serviceContext.setAssetTagNames(assetTagNames);
		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		Locale locale = LocaleUtil.fromLanguageId(languageId);

		Map<Locale, String> titleMap = new HashMap<>();
		titleMap.put(locale, title);

		Map<Locale, String> descriptionMap = new HashMap<>();
		descriptionMap.put(locale, content.substring(0, _DESCRIPTION_LENGTH));

		String instanceId = _generateInstanceId();

		String xmlContent =
			"<root available-locales=\"en_US\" default-locale=\"" + languageId +
				"\">" +
					"<dynamic-element name=\"content\" type=\"text_area\" index-type=\"text\" instance-id=\"" +
						instanceId + "\">" + "<dynamic-content language-id=\"" +
							languageId + "\"><![CDATA[" + content +
								"]]></dynamic-content>" + "</dynamic-element>" +
									"</root>";

		 _journalArticleLocalService.addArticle(
				 userId,
				 groupId,
				 0, // folderId,
				titleMap,
				descriptionMap,
				xmlContent,
				"BASIC-WEB-CONTENT", // ddmStructureKey,
				"BASIC-WEB-CONTENT", // ddmTemplateKey,
				serviceContext);

		_log.info(
			"Success. Took " + (System.currentTimeMillis() - timer) + " ms");
	}

	/**
	 * Checks for valid tag value.
	 *
	 * Copied from com.liferay.portlet.asset.util.AssetUtil.
	 *
	 * @param value
	 * @return
	 */
	private boolean _checkTagValue(String value) {
		if (Validator.isBlank(value)) {
			return false;
		}

		char[] wordCharArray = value.toCharArray();

		for (char c : wordCharArray) {
			for (char invalidChar : _INVALID_CHARACTERS) {
				if (c == invalidChar) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Create a list from comma separated string list.
	 *
	 * @param ids
	 * @return
	 */
	private List<String> _createArticleList(String ids) {
		String[] arr = ids.split(",");
		List<String> values = new ArrayList<>();

		for (String s : arr) {
			values.add(s);
		}

		return values;
	}

	/**
	 * Creates a list from comma separated long list.
	 *
	 * @param ids
	 * @return
	 */
	private List<Long> _createIdList(String ids) {
		String[] arr = ids.split(",");
		List<Long> values = new ArrayList<>();

		for (String s : arr) {
			values.add(Long.valueOf(s));
		}

		return values;
	}

	/**
	 * Create a random instance-id for the content XML.
	 *
	 * @return
	 */
	private String _generateInstanceId() {
		StringBuilder instanceId = new StringBuilder(8);

		String key = PwdGenerator.KEY1 + PwdGenerator.KEY2 + PwdGenerator.KEY3;

		for (int i = 0; i < 8; i++) {
			int pos = (int)Math.floor(Math.random() * key.length());

			instanceId.append(key.charAt(pos));
		}

		return instanceId.toString();
	}

	/**
	 * Imports a list of articles.
	 *
	 * @param actionRequest
	 * @param articles
	 * @param userIds
	 * @param groupIds
	 * @param languageId
	 * @param counter
	 * @param limit
	 * @throws Exception
	 */
	private void _importArticles(
			ActionRequest actionRequest, List<String> articles,
			List<Long> userIds, List<Long> groupIds, String languageId,
			int counter, int limit)
		throws Exception {

		List<String> articleLinks = new ArrayList<>();

		int groupIdx = 0;
		int userIdx = 0;

		for (String article : articles) {
			if (counter >= limit) {
				return;
			}

			String apiURL =
				_API_URL + URLCodec.encodeURL(article) + "&format=json";

			_log.info("Calling:" + apiURL);

			URL url = new URL(apiURL);

			URLConnection request = url.openConnection();

			request.connect();

			JsonParser parser = new JsonParser();

			try {
				JsonElement root = parser.parse(
					new InputStreamReader((InputStream)request.getContent()));

				JsonObject rootobj = root.getAsJsonObject();

				JsonObject parse = rootobj.getAsJsonObject("parse");

				// Text

				JsonObject text = parse.getAsJsonObject("text");

				String content = text.get(
					"*"
				).getAsString();

				// Title

				String title = parse.get(
					"title"
				).getAsString();

				// Categories => tags

				List<String> tags = new ArrayList<>();

				JsonArray categories = parse.getAsJsonArray("categories");

				for (int i = 0; i < categories.size(); i++) {
					JsonObject category = categories.get(
						i
					).getAsJsonObject();

					JsonElement hidden = category.get("hidden");

					if (hidden != null) {
						continue;
					}

					String value = category.get(
						"*"
					).getAsString();

					if (_checkTagValue(value)) {
						tags.add(value);
					}
				}

				String[] assetTagNames = tags.stream(
				).toArray(
					String[]::new
				);

				// Links

				JsonArray links = parse.getAsJsonArray("links");

				for (int i = 0; i < links.size(); i++) {
					JsonObject link = links.get(
						i
					).getAsJsonObject();

					int ns = link.get(
						"ns"
					).getAsInt();

					if (ns != 0) {
						continue;
					}

					articleLinks.add(
						link.get(
							"*"
						).getAsString());
				}

				if (userIdx == userIds.size()) {
					userIdx = 0;
				}

				long userId = userIds.get(userIdx++);

				if (groupIdx == groupIds.size()) {
					groupIdx = 0;
				}

				long groupId = groupIds.get(groupIdx++);

				_log.info("Adding Wikipedia article " + title);

				_addJournalArticle(
					actionRequest, userId, groupId, languageId, title, content,
					assetTagNames);
			}
			catch (Exception e) {
				_log.error(e.getMessage(), e);
			}

			counter++;
		}

		_importArticles(
			actionRequest, articleLinks, userIds, groupIds, languageId, counter,
			limit);
	}

	private static final String _API_URL =
		"https://en.wikipedia.org/w/api.php?action=parse&page=";

	private static final int _DESCRIPTION_LENGTH = 300;

	private static final char[] _INVALID_CHARACTERS = {
		CharPool.AMPERSAND, CharPool.APOSTROPHE, CharPool.AT,
		CharPool.BACK_SLASH, CharPool.CLOSE_BRACKET, CharPool.CLOSE_CURLY_BRACE,
		CharPool.COLON, CharPool.COMMA, CharPool.EQUAL, CharPool.GREATER_THAN,
		CharPool.FORWARD_SLASH, CharPool.LESS_THAN, CharPool.NEW_LINE,
		CharPool.OPEN_BRACKET, CharPool.OPEN_CURLY_BRACE, CharPool.PERCENT,
		CharPool.PIPE, CharPool.PLUS, CharPool.POUND, CharPool.PRIME,
		CharPool.QUESTION, CharPool.QUOTE, CharPool.RETURN, CharPool.SEMICOLON,
		CharPool.SLASH, CharPool.STAR, CharPool.TILDE
	};

	private static final Logger _log = LoggerFactory.getLogger(
		ImportMVCActionCommand.class);

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

}