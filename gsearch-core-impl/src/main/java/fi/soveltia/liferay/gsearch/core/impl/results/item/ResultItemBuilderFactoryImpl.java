
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;

import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.liferay.portal.kernel.util.SortedArrayList;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

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
	public ResultItemBuilder getResultBuilder(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document) {

		String entryClassName = document.get(Field.ENTRY_CLASS_NAME);

		ResultItemBuilder resultItemBuilder = null;

		for (ResultItemBuilderReference r : _resultItemBuilderReferences) {
			if (r.getResultItemBuilder().canBuild(document)) {
				resultItemBuilder = r.getResultItemBuilder();
				break;
			}
		}

		if (resultItemBuilder == null) {
			String entryClassPK = document.get(Field.ENTRY_CLASS_PK);
			String ddmStructureKey = document.get("ddmStructureKey");
			_log.error(String.format("No result item builder found for '%s', entryClassPK %s, document ddmStructureKey '%s'", entryClassName, entryClassPK, ddmStructureKey));
		}

		return resultItemBuilder;
	}

	@Reference(
			cardinality = ReferenceCardinality.MULTIPLE,
			policy = ReferencePolicy.DYNAMIC,
			service = ResultItemBuilder.class,
			unbind = "removeResultItemBuilder"
	)
	protected synchronized void addResultItemBuilder(
			ResultItemBuilder resultItemBuilder,
			Map<String, Object> properties) {

		Integer serviceRanking = (Integer)properties.get("service.ranking");

		if (serviceRanking == null) {
			serviceRanking = 0;
		}

		_resultItemBuilderReferences.add(
				new ResultItemBuilderReference(resultItemBuilder, serviceRanking));
	}

	/**
	 * Remove a clause builder from list.
	 *
	 * @param resultItemBuilder
	 */
	protected synchronized void removeResultItemBuilder(
		ResultItemBuilder resultItemBuilder) {

		for (ResultItemBuilderReference reference : _resultItemBuilderReferences) {
			if (reference.getResultItemBuilder() == resultItemBuilder) {
				_resultItemBuilderReferences.remove(reference);
				break;
			}
		}
	}

	private volatile List<ResultItemBuilderReference> _resultItemBuilderReferences =
			new SortedArrayList<>();

	private static final Log _log =
		LogFactoryUtil.getLog(ResultItemBuilderFactoryImpl.class);
}
