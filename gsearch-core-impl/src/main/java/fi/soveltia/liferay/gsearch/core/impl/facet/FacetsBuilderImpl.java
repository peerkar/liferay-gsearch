
package fi.soveltia.liferay.gsearch.core.impl.facet;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.MultiValueFacet;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import fi.soveltia.liferay.gsearch.core.api.facet.FacetsBuilder;
import fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration;

/**
 * Facets builder implementation.
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
			
			Facet facet = new MultiValueFacet(searchContext);
			facet.setFieldName(fieldName);
			facet.setStatic(false);

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
}
