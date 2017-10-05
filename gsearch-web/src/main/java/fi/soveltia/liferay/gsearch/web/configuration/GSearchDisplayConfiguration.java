package fi.soveltia.liferay.gsearch.web.configuration;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration"
)
public interface GSearchDisplayConfiguration {

	@Meta.AD(
		deflt = "<h1>GSearch Syntax Help</h1><p><i>See Control Panel / Configuration / System Settings / GSearch display configuration</i></p>", 
		required = false
	)
	public String helpText();

	@Meta.AD(
		deflt = "3", 
		required = false
	)
	public int queryMinLength();

	@Meta.AD(
		deflt = "10", 
		required = false
	)
	public int pageSize();

	@Meta.AD(
		deflt = "10000", 
		required = false
	)
	public int requestTimeout();
	
	@Meta.AD(
		deflt = "/viewasset", 
		required = false
	)
	public String assetPublisherPage();

}
