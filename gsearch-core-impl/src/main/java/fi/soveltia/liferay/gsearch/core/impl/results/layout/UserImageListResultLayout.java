package fi.soveltia.liferay.gsearch.core.impl.results.layout;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.results.layout.ResultLayout;

/**
 * User image result list layout.
 * 
 * @author Petteri Karttunen
 *
 */
@Component(
	immediate = true,
	service = ResultLayout.class
)
public class UserImageListResultLayout implements ResultLayout {

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
		return null;
	}
	
	public static final String KEY = "userImageList";
}
