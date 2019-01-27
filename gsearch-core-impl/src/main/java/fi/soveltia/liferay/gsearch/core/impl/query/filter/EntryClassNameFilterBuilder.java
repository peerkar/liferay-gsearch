
package fi.soveltia.liferay.gsearch.core.impl.query.filter;

import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.filter.FilterBuilder;

/**
 * Entry class name (asset type) filter builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = FilterBuilder.class
)
public class EntryClassNameFilterBuilder implements FilterBuilder {

	@Override
	public void addFilters(
		PortletRequest portletRequest, BooleanFilter preBooleanfilter,
		BooleanFilter postFilter, QueryContext queryContext)
		throws Exception {

		List<String> entryClassNames = getEntryClassNames(queryContext);
		
		BooleanQuery query = new BooleanQueryImpl();

		for (String className : entryClassNames) {

			// Handle journal article separately.

			if (className.equals(JournalArticle.class.getName())) {
				addJournalArticleClassCondition(query);

			}
			else {

				TermQuery condition =
					new TermQueryImpl(Field.ENTRY_CLASS_NAME, className);
				query.add(condition, BooleanClauseOccur.SHOULD);
			}
		}

		QueryFilter queryFilter = new QueryFilter(query);
		preBooleanfilter.add(queryFilter, BooleanClauseOccur.MUST);

	}

	/**
	 * Try to get entry class names in this order 1) QueryContext parameters. If
	 * not found then 2) QueryContext configurations. If not found then 3)
	 * Global entryclassname configuration
	 * 
	 * @param queryContext
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws JSONException 
	 */
	@SuppressWarnings("unchecked")
	protected List<String> getEntryClassNames(QueryContext queryContext) 
					throws JSONException, ClassNotFoundException {

		List<String> entryClassNames = (List<String>) queryContext.getParameter(
			ParameterNames.ENTRY_CLASS_NAMES);
		
		if (entryClassNames == null) {

			if (queryContext.getConfiguration(
				ConfigurationKeys.ENTRY_CLASS_NAME) != null) {
				entryClassNames = parseEntryClassNames(
					queryContext.getConfiguration(
						ConfigurationKeys.ENTRY_CLASS_NAME));
			}
			else {
				entryClassNames = parseEntryClassNames(
					_configurationHelper.getAssetTypeConfiguration());

			}
		}
		return entryClassNames;
	}

	/**
	 * Add journal article class condition.
	 * 
	 * @param query
	 * @throws ParseException
	 */
	protected void addJournalArticleClassCondition(BooleanQuery query)
		throws ParseException {

		BooleanQuery journalArticleQuery = new BooleanQueryImpl();

		// Classname condition.

		TermQuery classNamecondition = new TermQueryImpl(
			Field.ENTRY_CLASS_NAME, JournalArticle.class.getName());
		journalArticleQuery.add(classNamecondition, BooleanClauseOccur.MUST);

		// Add display date limitation.

		Date now = new Date();

		journalArticleQuery.addRangeTerm(
			"displayDate_sortable", Long.MIN_VALUE, now.getTime());

		journalArticleQuery.addRangeTerm(
			"expirationDate_sortable", now.getTime(), Long.MAX_VALUE);

		// Add version limitation.

		TermQuery versionQuery =
			new TermQueryImpl("head", Boolean.TRUE.toString());
		journalArticleQuery.add(versionQuery, BooleanClauseOccur.MUST);

		query.add(journalArticleQuery, BooleanClauseOccur.SHOULD);
	}

	/**
	 * Parse asset class names from configuration.
	 * 
	 * @param configuration
	 * @return
	 * @throws ClassNotFoundException
	 * @throws JSONException
	 */
	protected List<String> parseEntryClassNames(String[] configuration)
		throws ClassNotFoundException, JSONException {

		List<String> entryClassNames = new ArrayList<String>();

		for (int i = 0; i < configuration.length; i++) {

			JSONObject item =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			entryClassNames.add(item.getString("entry_class_name"));
		}

		return entryClassNames;
	}

	@Reference
	private ConfigurationHelper _configurationHelper;

}
