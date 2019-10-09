
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.search.document.Document;

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
public class NonLiferaySampleItemBuilder
	extends BaseResultItemBuilder implements ResultItemBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBuild(Document document) {
		return _NAME.equals(
				document.getString(Field.ENTRY_CLASS_NAME));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription(QueryContext queryContext, Document document)
		throws SearchException {

		return document.getString(Field.DESCRIPTION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLink(QueryContext queryContext, Document document)
		throws Exception {

		// Dummy example.
		
		return document.getString("treePath");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getThumbnail(QueryContext queryContext, Document document)
		throws Exception {

		return _DEFAULT_IMAGE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle(
			QueryContext queryContext, Document document)
		throws NumberFormatException, PortalException {

		return document.getString(Field.TITLE);
	}

	private static final String _DEFAULT_IMAGE =
		"/images/asset-types/non-liferay-type.png";

	private static final String _NAME = "non-liferay-type";

}