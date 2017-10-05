package fi.soveltia.liferay.gsearch.web.portlet;

import com.liferay.portal.portlet.bridge.soy.SoyPortlet;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.web.constants.GsearchWebPortletKeys;

/**
 * GSearch Portlet
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.application-type=full-page-application",
		"com.liferay.portlet.application-type=widget",
        "com.liferay.portlet.css-class-wrapper=gsearch-web-portlet",
		"com.liferay.portlet.display-category=category.gsearch",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.layout-cacheable=false",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.requires-namespaced-parameters=false",
		"com.liferay.portlet.scopeable=false",
		"com.liferay.portlet.single-page-application=false",
		"com.liferay.portlet.use-default-template=true",
		"javax.portlet.display-name=gsearch-web-portlet",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.copy-request-parameters=true",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=View",
		"javax.portlet.name=" + GsearchWebPortletKeys.SEARCH_PORTLET,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = Portlet.class
)
public class GsearchWebPortlet extends SoyPortlet {

}

