package fi.soveltia.liferay.gsearch.core.api.configuration.category;

import com.liferay.configuration.admin.category.ConfigurationCategory;

import org.osgi.service.component.annotations.Component;

/**
 * Liferay GSearch configuration category.
 * 
 * @author Petteri Karttunen
 */
@Component(
	service = ConfigurationCategory.class
)
public class GSearchConfigurationCategory implements ConfigurationCategory {

    @Override
    public String getCategoryIcon() {
        return "cog";
    }

    @Override
    public String getCategoryKey() {
        return _KEY;
    }

    @Override
    public String getCategorySection() {
        return _CATEGORY_SET_KEY;
    }

    private static final String _CATEGORY_SET_KEY = "other";

    private static final String _KEY = "gsearch";

}