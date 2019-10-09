
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.SortedArrayList;
import com.liferay.portal.search.document.Document;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilderFactory;

/**
 * Result item builder factory implementation.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ResultItemBuilderFactory.class
)
public class ResultItemBuilderFactoryImpl implements ResultItemBuilderFactory {

	/**
	 * {@inheritDoc}
	 */
	public ResultItemBuilder getResultBuilder(Document document) {
		ResultItemBuilder resultItemBuilder = null;

		for (ResultItemBuilderReference r : _resultItemBuilderReferences) {
			if (r.getResultItemBuilder().canBuild(document)) {
				resultItemBuilder = r.getResultItemBuilder();

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Found a result item builder " +
							r.getClass(
							).getName() + " for " +
								document.getString(
										Field.ENTRY_CLASS_NAME));
				}

				break;
			}
		}

		// Fall back to default builder.

		if (resultItemBuilder == null) {
			resultItemBuilder = new DefaultItemBuilder();
		}

		return resultItemBuilder;
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC, service = ResultItemBuilder.class,
		unbind = "removeResultItemBuilder"
	)
	protected synchronized void addResultItemBuilder(
		ResultItemBuilder resultItemBuilder, Map<String, Object> properties) {

		Integer serviceRanking = (Integer)properties.get("service.ranking");

		if (serviceRanking == null) {
			serviceRanking = 0;
		}

		_resultItemBuilderReferences.add(
			new ResultItemBuilderReference(resultItemBuilder, serviceRanking));
	}

	/**
	 * Remove a result item builder from list.
	 *
	 * @param resultItemBuilder
	 */
	protected synchronized void removeResultItemBuilder(
		ResultItemBuilder resultItemBuilder) {

		for (ResultItemBuilderReference reference :
				_resultItemBuilderReferences) {

			if (reference.getResultItemBuilder() == resultItemBuilder) {
				_resultItemBuilderReferences.remove(reference);

				break;
			}
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(
		ResultItemBuilderFactoryImpl.class);

	private volatile List<ResultItemBuilderReference>
		_resultItemBuilderReferences = new SortedArrayList<>();

}