package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;


@ExtendedObjectClassDefinition(
	category = "GSearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.impl.configuration.AssetTypeConfiguration",
	localization = "content/Language",
	name = "Asset Type Configuration"
)
public interface AssetTypeConfiguration {

	
	@Meta.AD(
		deflt = "[Please get the default configuration from the project GitHub page.",
		description = "asset-types-desc",
	    name = "asset-types-name",
		required = false
	)
	public String[] assetTypes();
	
}
