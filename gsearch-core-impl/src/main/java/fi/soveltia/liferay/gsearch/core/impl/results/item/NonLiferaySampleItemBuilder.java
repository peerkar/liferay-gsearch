
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchException;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;

/**
 * Non Liferay result item result builder sample.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	service = ResultItemBuilder.class
)
public class NonLiferaySampleItemBuilder extends BaseResultItemBuilder
	implements ResultItemBuilder {

	@Override
	public boolean canBuild(Document document) {

		return NAME.equals(document.get(Field.ENTRY_CLASS_NAME));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document)
		throws SearchException {

		return document.get("description");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getImageSrc(PortletRequest portletRequest, Document document)
		throws Exception {

		return DEFAULT_IMAGE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLink(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, String assetPublisherPageFriendlyURL)
		throws Exception {

		return document.get("treePath");
	}

	@Override
	public String getTitle(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document)
		throws NumberFormatException, PortalException {

		return document.get("title");
	}

	private static final String DEFAULT_IMAGE =
		"/o/gsearch-web/images/asset-types/non-liferay-type.png";

	private static final String NAME = "non-liferay-type";

}
