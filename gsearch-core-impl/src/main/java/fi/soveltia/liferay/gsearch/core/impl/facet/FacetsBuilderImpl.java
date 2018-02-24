
package fi.soveltia.liferay.gsearch.core.impl.facet;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.SimpleFacet;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import fi.soveltia.liferay.gsearch.core.api.facet.FacetsBuilder;
import fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration;

/**
 * Facets builder implementation. This service sets the configured
 * facets (aggregations) to searchcontext.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration", 
	immediate = true, 
	service = FacetsBuilder.class
)
public class FacetsBuilderImpl implements FacetsBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFacets(SearchContext searchContext)
		throws JSONException {
		
		JSONArray configuration = JSONFactoryUtil.createJSONArray(_gSearchConfiguration.facetConfiguration());

		for (int i = 0; i < configuration.length(); i++) {

			JSONObject item = configuration.getJSONObject(i);

			String fieldName = item.getString("fieldName");
			
			Facet facet = new SimpleFacet(searchContext);
			
			FacetConfiguration facetConfiguration = new FacetConfiguration();

			facetConfiguration.setFieldName(fieldName);
			facetConfiguration.setStatic(false);

			JSONObject dataObject = JSONFactoryUtil.createJSONObject();
			dataObject.put("maxTerms", MAX_TERMS);

			facetConfiguration.setDataJSONObject(dataObject);
			
			facet.setFacetConfiguration(facetConfiguration);

			searchContext.addFacet(facet);
		}
	}
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_gSearchConfiguration = ConfigurableUtil.createConfigurable(
			GSearchConfiguration.class, properties);
	}	

	private volatile GSearchConfiguration _gSearchConfiguration;
	
	private int MAX_TERMS = 20;
}
