package fi.soveltia.liferay.gsearch.geolocation.results.layout;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.results.layout.ResultLayout;

/**
 * Maps result layout.
 * 
 * @author Petteri Karttunen
 *
 */
@Component(
	immediate = true,
	service = ResultLayout.class
)
public class MapsResultLayout implements ResultLayout {

	@Override
	public String getKey() {
		return "maps";
	}

	@Override
	public Map<String, String> getParamFiltersAND() {
		return null;
	}

	@Override
	public Map<String, String> getParamFiltersOR() {
		return null;
	}

}
