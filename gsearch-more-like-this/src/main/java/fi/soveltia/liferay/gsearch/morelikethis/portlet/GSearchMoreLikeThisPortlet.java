package fi.soveltia.liferay.gsearch.morelikethis.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.morelikethis.constants.GSearchMoreLikeThisPortletKeys;

/**
 * @author liferay
 */
@Component(
	immediate = true,
	property = {
        "com.liferay.portlet.css-class-wrapper=gsearch-more-like-this-portlet",
		"com.liferay.portlet.display-category=category.gsearch",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=false",
		"javax.portlet.display-name=gsearch-more-like-this-portlet",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.config-template=/configuration.jsp",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + GSearchMoreLikeThisPortletKeys.MORE_LIKE_THIS_PORTLET,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class GSearchMoreLikeThisPortlet extends MVCPortlet {
}
