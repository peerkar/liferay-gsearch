
package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilderFactory;

/**
 * Clause builder service implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseBuilderFactory.class
)
public class ClauseBuilderFactoryImpl implements ClauseBuilderFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClauseBuilder getClauseBuilder(String queryType) {

		if (_clauseBuilders == null) {
			return null;
		}

		for (ClauseBuilder clauseBuilder : _clauseBuilders) {
			if (clauseBuilder.canBuild(queryType)) {
				return clauseBuilder;
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug("No clause builder found for " + queryType);
		}

		return null;
	}

	/**
	 * Add any registered clause builders to the list.
	 * 
	 * @param clauseBuilder
	 */
	protected void addClauseBuilder(ClauseBuilder clauseBuilder) {

		if (_clauseBuilders == null) {
			_clauseBuilders = new ArrayList<ClauseBuilder>();
		}
		_clauseBuilders.add(clauseBuilder);
	}

	/**
	 * Remove a clause builder from list.
	 * 
	 * @param clauseBuilder
	 */
	protected void removeClauseBuilder(ClauseBuilder clauseBuilder) {

		_clauseBuilders.remove(clauseBuilder);
	}

	@Reference(
		bind = "addClauseBuilder", 
		cardinality = ReferenceCardinality.MULTIPLE, 
		policy = ReferencePolicy.DYNAMIC, 
		service = ClauseBuilder.class, 
		unbind = "removeClauseBuilder"
	)
	private volatile List<ClauseBuilder> _clauseBuilders;

	private static final Log _log =
		LogFactoryUtil.getLog(ClauseBuilderFactoryImpl.class);
}
