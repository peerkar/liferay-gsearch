
package fi.soveltia.liferay.gsearch.web.search.internal.query.processor;

import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.suggest.SuggestionConstants;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.search.internal.queryparams.QueryParams;
import fi.soveltia.liferay.gsearch.web.search.query.processor.QueryIndexerProcessor;

/** 
 * Query indexer processor. 
 * 
 * Originally com.liferay.portal.search.internal.hits.QueryIndexingHitsProcessor
 * 
 * @author Michael C. Han
 * @author Josef Sustacek
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = QueryIndexerProcessor.class
)
public class QueryIndexerProcessorImpl implements QueryIndexerProcessor {

	@Override
	public boolean process(
		SearchContext searchContext,
		GSearchDisplayConfiguration gSearchDisplayConfiguration,
		QueryParams queryParams, Hits hits)
		throws Exception {

		if (!gSearchDisplayConfiguration.enableQuerySuggestions() &&
			!gSearchDisplayConfiguration.enableAutoComplete()) {
			return true;
		}

		if (hits.getLength() >= gSearchDisplayConfiguration.queryIndexingThreshold()) {
			addDocument(
				queryParams.getCompanyId(), queryParams.getKeywords(),
				queryParams.getLocale());
		}

		return true;
	}

	protected void addDocument(long companyId, String keywords, Locale locale)
		throws SearchException {

		_indexWriterHelper.indexKeyword(
			companyId, keywords, 0, SuggestionConstants.TYPE_QUERY_SUGGESTION, locale);
	}

	@Reference(unbind = "-")
	protected void setIndexWriterHelper(IndexWriterHelper indexWriterHelper) {

		_indexWriterHelper = indexWriterHelper;
	}

	@Reference
	protected IndexWriterHelper _indexWriterHelper;
}
