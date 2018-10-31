
package fi.soveltia.liferay.gsearch.morelikethis.portlet.action;

import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.morelikethis.constants.GSearchMoreLikeThisPortletKeys;

@Component(
	immediate = true,
	property = "javax.portlet.name=" + GSearchMoreLikeThisPortletKeys.MORE_LIKE_THIS_PORTLET,
	service = ConfigurationAction.class
)
public class MoreLikeThisConfigurationAction
	extends DefaultConfigurationAction {

}
