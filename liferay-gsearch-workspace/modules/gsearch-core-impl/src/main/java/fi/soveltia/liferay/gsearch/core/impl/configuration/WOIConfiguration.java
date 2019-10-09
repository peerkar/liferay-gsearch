
package fi.soveltia.liferay.gsearch.core.impl.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * WOI (Words Of Interest) configuration.
 * 
 * @author Petteri Karttunen
 */
@ExtendedObjectClassDefinition(category = "gsearch")
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.impl.configuration.WOIConfiguration",
	localization = "content/Language", 
	name = "woi-configuration"
)
public interface WOIConfiguration {

	@Meta.AD(
		deflt = "100", 
		description = "store-size-desc",
		name = "store-size-name", 
		required = false
	)
	public int storeSize();

	@Meta.AD(
		deflt = "5", 
		description = "pick-count-desc",
		name = "pick-count-name", 
		required = false
	)
	public int pickCount();

}
