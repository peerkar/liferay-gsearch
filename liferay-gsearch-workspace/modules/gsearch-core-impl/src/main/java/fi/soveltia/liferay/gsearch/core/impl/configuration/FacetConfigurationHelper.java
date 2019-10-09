
package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * Facet configuration helper / facade.
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = FacetConfigurationHelper.CONFIGURATION_PID,
	immediate = true, 
	service = FacetConfigurationHelper.class
)
public class FacetConfigurationHelper {

	public static final String CONFIGURATION_PID =
		"fi.soveltia.liferay.gsearch.core.impl.configuration.FacetConfiguration";

	public String[] getFacets() {
		return _configuration.facets();
	}

	public int getMaxFacetTerms() {
		return _configuration.maxFacetTerms();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_configuration = ConfigurableUtil.createConfigurable(
			FacetConfiguration.class, properties);
	}

	private volatile FacetConfiguration _configuration;

}