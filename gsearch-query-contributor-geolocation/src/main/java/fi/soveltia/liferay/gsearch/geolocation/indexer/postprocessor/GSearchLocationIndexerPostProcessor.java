package fi.soveltia.liferay.gsearch.querycontributor.geolocation.indexer.postprocessor;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.querycontributor.geolocation.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.querycontributor.geolocation.service.GeoLocationService;

/**
 * GSearch geolocation indexer postprocessor
 * 
 * @author Petteri Karttunen
 */

@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.querycontributor.geolocation.configuration.ModuleConfiguration",
	immediate = true,
	property = {
		"indexer.class.name=com.liferay.blogs.model.BlogsEntry",
		"indexer.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
		"indexer.class.name=com.liferay.journal.model.JournalArticle",
		"indexer.class.name=import com.liferay.message.boards.kernel.model.MBMessage",
		"indexer.class.name=com.liferay.wiki.model.WikiPage"
	},
	service = IndexerPostProcessor.class
)
public class GSearchLocationIndexerPostProcessor
	implements IndexerPostProcessor {

	@Override
	public void postProcessContextBooleanFilter(
		BooleanFilter booleanFilter, SearchContext searchContext)
		throws Exception {

	}

	@Override
	public void postProcessContextQuery(
		BooleanQuery contextQuery, SearchContext searchContext)
		throws Exception {

	}

	@Override
	public void postProcessDocument(Document document, Object obj)
		throws Exception {

		String ipAddress = getIpAddress();
		
		Float[]coordinates = _geolocationService.getCoordinates(ipAddress);

		if (coordinates == null) {
			return;
		}

		float latitude = coordinates[0];
		float longitude = coordinates[1];

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Indexing GSearch geolocation data for IP address " +
					ipAddress);
			_log.debug("Latitude " + latitude);
			_log.debug("Longitude " + longitude);
		}
		
		_log.info(longitude);
		
		// Add geolocation data to document

		document.addGeoLocation(_moduleConfiguration.indexField(), latitude, longitude);
	}

	@Override
	public void postProcessFullQuery(
		BooleanQuery fullQuery, SearchContext searchContext)
		throws Exception {

	}

	@Override
	public void postProcessSearchQuery(
		BooleanQuery searchQuery, BooleanFilter booleanFilter,
		SearchContext searchContext)
		throws Exception {

	}

	@Override
	public void postProcessSearchQuery(
		BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

	}

	@Override
	public void postProcessSummary(
		Summary summary, Document document, Locale locale, String snippet) {

	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}
	
	/**
	 * Get ipaddress to extract the geolocation data from.
	 */
	protected String getIpAddress() {

		// Check if there's a configured test address

		String ipAddress = _moduleConfiguration.testIpAddress();

		if (Validator.isNotNull(ipAddress)) {
			return ipAddress;
		}

		// Get the ip from servicecontext

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		return serviceContext.getRemoteAddr();
	}
	
	@Reference
	private GeoLocationService _geolocationService;

	private volatile ModuleConfiguration _moduleConfiguration;

	private static final Log _log =
		LogFactoryUtil.getLog(GSearchLocationIndexerPostProcessor.class);
}
