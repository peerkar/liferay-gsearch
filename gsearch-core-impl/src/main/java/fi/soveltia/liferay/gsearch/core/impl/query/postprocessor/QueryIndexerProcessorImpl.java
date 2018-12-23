
package fi.soveltia.liferay.gsearch.core.impl.query.postprocessor;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.suggest.SuggestionConstants;

import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.query.postprocessor.QueryPostProcessor;
import fi.soveltia.liferay.gsearch.core.impl.configuration.ModuleConfiguration;

/**
 * Query indexer processor. Originally
 * com.liferay.portal.search.internal.hits.QueryIndexingHitsProcessor
 * 
 * @author Michael C. Han
 * @author Josef Sustacek
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.impl.configuration.ModuleConfiguration", 
	immediate = true, 
	service = QueryPostProcessor.class
)
public class QueryIndexerProcessorImpl implements QueryPostProcessor {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}

	@Override
	public boolean process(
		PortletRequest portletRequest, SearchContext searchContext,
		QueryParams queryParams, Hits hits)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Processing QueryIndexer");
		}

		if (!_moduleConfiguration.isQuerySuggestionsEnabled()) {
			return true;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("QueryIndexer is enabled");
		}

		if (hits.getLength() >= _moduleConfiguration.queryIndexingThreshold()) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"QueryIndexing threshold exceeded. Indexing keywords: " +
						queryParams.getKeywords());
			}

			addDocument(
				queryParams.getCompanyId(), queryParams.getKeywords(),
				queryParams.getLocale());
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"QueryIndexing threshold wasn't exceeded. Not indexing keywords.");
			}
		}
		return true;
	}

	protected void addDocument(long companyId, String keywords, Locale locale)
		throws SearchException {

		_indexWriterHelper.indexKeyword(
			companyId, keywords, 0, SuggestionConstants.TYPE_QUERY_SUGGESTION,
			locale);
	}

	private static final Logger _log =
		LoggerFactory.getLogger(QueryIndexerProcessorImpl.class);

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	private volatile ModuleConfiguration _moduleConfiguration;
}
