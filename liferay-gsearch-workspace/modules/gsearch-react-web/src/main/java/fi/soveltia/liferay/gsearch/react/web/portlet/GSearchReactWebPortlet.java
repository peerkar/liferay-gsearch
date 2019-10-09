package fi.soveltia.liferay.gsearch.react.web.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.react.web.constants.GSearchReactWebPortletKeys;

/**
 * @author liferay
 */
@Component(
	immediate = true, 
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=gsearch-portlet",
		"com.liferay.portlet.display-category=category.gsearch",
		"com.liferay.portlet.header-portlet-css=/css/index.css",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.requires-namespaced-parameters=false",
		"com.liferay.portlet.scopeable=false",
		"javax.portlet.init-param.copy-request-parameters=false",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + GSearchReactWebPortletKeys.GSEARCH_REACT_PORTLET,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user" 
	}, 
	service = Portlet.class
)
public class GSearchReactWebPortlet extends MVCPortlet {
}