package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;


@ExtendedObjectClassDefinition(
	category = "GSearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.impl.configuration.AssetTypeConfiguration",
	localization = "content/Language",
	name = "asset-type-configuration"
)
public interface AssetTypeConfiguration {

	
	@Meta.AD(
		deflt = "",
		description = "asset-types-desc",
	    name = "asset-types-name",
		required = false
	)
	public String[] assetTypes();
	
}
