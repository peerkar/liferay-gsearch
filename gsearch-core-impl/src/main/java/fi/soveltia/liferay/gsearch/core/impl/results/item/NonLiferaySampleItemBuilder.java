
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchException;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
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
		QueryContext queryContext,
		Document document)
		throws SearchException {

		return document.get("description");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getThumbnail(QueryContext queryContext, Document document)
		throws Exception {

		return DEFAULT_IMAGE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLink(
		QueryContext queryContext,
		Document document)
		throws Exception {

		return document.get("treePath");
	}

	@Override
	public String getTitle(
		QueryContext queryContext,
		Document document, boolean isHighlight)
		throws NumberFormatException, PortalException {

		return document.get("title");
	}

	private static final String DEFAULT_IMAGE =
		"/o/gsearch-web/images/asset-types/non-liferay-type.png";

	private static final String NAME = "non-liferay-type";

}
