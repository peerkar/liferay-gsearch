package fi.soveltia.liferay.gsearch.wikipedia.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import fi.soveltia.liferay.gsearch.wikipedia.constants.WikiPediaDataImportPortletKeys;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

/**
 * Liferay GSearch Wikipedia article import portlet.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.gsearch",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=false",
		"javax.portlet.display-name=wikipediaimport-portlet",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + WikiPediaDataImportPortletKeys.WIKIPEDIADATAIMPORT,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class WikiPediaDataImportPortlet extends MVCPortlet {
}