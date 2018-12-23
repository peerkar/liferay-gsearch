
// This class localizes Control panel System Settings category but requires 
// high kernel version.

/*

package fi.soveltia.liferay.gsearch.core.api.configuration.category;


import com.liferay.portal.kernel.util.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.util.CacheResourceBundleLoader;
import com.liferay.portal.kernel.util.ClassResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleLoader;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    property = {
       "bundle.symbolic.name=com.liferay.configuration.admin.web",
       "resource.bundle.base.name=content.Language",
       "servlet.context.name=configuration-admin-web"
    }
)
public class GSearchConfigurationAdminResourceBundleLoader
	implements ResourceBundleLoader {

	@Override
	public ResourceBundle loadResourceBundle(Locale locale) {

		return _resourceBundleLoader.loadResourceBundle(locale);
	}

	@Reference(
		target = "(&(bundle.symbolic.name=com.liferay.configuration.admin.web)" +
			"(!(component.name=fi.soveltia.liferay.gsearch.core.api.configuration.category.GSearchConfigurationAdminResourceBundleLoader))" +
			"(!(component.name=com.liferay.content.targeting.lang.internal.resource.bundle.ConfigurationAdminResourceBundleLoader)))"
	)
	public void setResourceBundleLoader(
		ResourceBundleLoader resourceBundleLoader) {

		_resourceBundleLoader = new AggregateResourceBundleLoader(
			new CacheResourceBundleLoader(
				new ClassResourceBundleLoader(
					"content.Language",
					GSearchConfigurationAdminResourceBundleLoader.class.getClassLoader())),
			resourceBundleLoader);
	}  

	private AggregateResourceBundleLoader _resourceBundleLoader;
}

*/
