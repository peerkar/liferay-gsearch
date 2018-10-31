package fi.soveltia.liferay.gsearch.core.impl.results.layout;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.results.layout.ResultLayout;

/**
 * Image result list layout.
 * 
 * @author Petteri Karttunen
 *
 */
@Component(
	immediate = true,
	service = ResultLayout.class
)
public class ImageListResultLayout implements ResultLayout {

	@Override
	public String getKey() {
		return KEY;
	}
	
	@Override
	public Map<String, String> getParamFiltersAND() {
		return null;
	}

	@Override
	public Map<String, String> getParamFiltersOR() {
		Map<String, String> filters = new HashMap<String, String>();
		filters.put("type", "file");
		filters.put("extension", "Image");
		return filters;
	}
	
	public static final String KEY = "image";

}
