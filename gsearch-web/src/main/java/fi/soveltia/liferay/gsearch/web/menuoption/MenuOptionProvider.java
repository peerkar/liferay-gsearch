package fi.soveltia.liferay.gsearch.web.menuoption;

import javax.portlet.PortletRequest;

public interface MenuOptionProvider {

	public void setOptions(PortletRequest portletRequest) throws Exception;
	
}
