
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

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
		Document document, String assetPublisherPageFriendlyURL) {

		String entryClassName = document.get(Field.ENTRY_CLASS_NAME);

		ResultItemBuilder resultItemBuilder = null;

		for (ResultItemBuilder r : _resultItemBuilders) {
			if (r.canBuild(entryClassName)) {
				resultItemBuilder = r;
				break;
			}
		}

		if (resultItemBuilder == null) {
			_log.info("No result item builder found for " + entryClassName);
		}

		resultItemBuilder.setProperties(
			portletRequest, portletResponse, document,
			assetPublisherPageFriendlyURL);

		return resultItemBuilder;
	}

	/**
	 * Add result item builder to the list.
	 * 
	 * @param clauseBuilder
	 */
	protected void addResultItemBuilder(ResultItemBuilder resultItemBuilder) {

		if (_resultItemBuilders == null) {
			_resultItemBuilders = new ArrayList<ResultItemBuilder>();
		}
		_resultItemBuilders.add(resultItemBuilder);
	}

	/**
	 * Remove a clause builder from list.
	 * 
	 * @param clauseBuilder
	 */
	protected void removeResultItemBuilder(
		ResultItemBuilder resultItemBuilder) {

		_resultItemBuilders.remove(resultItemBuilder);
	}

	@Reference(bind = "addResultItemBuilder", cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, service = ResultItemBuilder.class, unbind = "removeResultItemBuilder")
	private List<ResultItemBuilder> _resultItemBuilders;

	private static final Log _log =
		LogFactoryUtil.getLog(ResultItemBuilderFactoryImpl.class);
}
