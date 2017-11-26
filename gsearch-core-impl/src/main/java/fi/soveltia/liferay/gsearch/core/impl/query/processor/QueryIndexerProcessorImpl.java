
package fi.soveltia.liferay.gsearch.core.impl.query.processor;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.suggest.SuggestionConstants;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.query.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.query.processor.QueryIndexerProcessor;
import fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration;

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
	configurationPid = "fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration", 
	immediate = true, 
	service = QueryIndexerProcessor.class
)
public class QueryIndexerProcessorImpl implements QueryIndexerProcessor {

	@Activate 
	@Modified
	protected void activate(Map<String, Object> properties) {
		_gSearchConfiguration = ConfigurableUtil.createConfigurable(
			GSearchConfiguration.class, properties);
	}	
	
	@Override
	public boolean process(
		SearchContext searchContext,
		QueryParams queryParams, Hits hits)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Processing QueryIndexer");
		}

		if (!_gSearchConfiguration.enableQuerySuggestions() &&
			!_gSearchConfiguration.enableAutoComplete()) {
			return true;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("QueryIndexer is enabled");
		}
		
		if (hits.getLength() >= _gSearchConfiguration.queryIndexingThreshold()) {

			if (_log.isDebugEnabled()) {
				_log.debug("QueryIndexing threshold exceeded. Indexing keywords: " + queryParams.getKeywords());
			}

			addDocument(
				queryParams.getCompanyId(), queryParams.getKeywords(),
				queryParams.getLocale());
		} else {
			if (_log.isDebugEnabled()) {
				_log.debug("QueryIndexing threshold wasn't exceeded. Not indexing keywords.");
			}
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
	
	private volatile GSearchConfiguration _gSearchConfiguration;

	private static final Log _log =
					LogFactoryUtil.getLog(QueryIndexerProcessorImpl.class);
	
}
