
package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilderFactory;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clause builder factory implementation.
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
			_log.error("No clause builders found.");

			return null;
		}

		for (ClauseBuilder clauseBuilder : _clauseBuilders) {
			if (clauseBuilder.canBuild(queryType)) {
				return clauseBuilder;
			}
		}

		_log.error(
			"No clause builder found for " + queryType +
				". Check configuration.");

		return null;
	}

	/**
	 * Adds any registered clause builders to the list.
	 *
	 * @param clauseBuilder
	 */
	protected void addClauseBuilder(ClauseBuilder clauseBuilder) {
		if (_clauseBuilders == null) {
			_clauseBuilders = new ArrayList<>();
		}

		_clauseBuilders.add(clauseBuilder);
	}

	/**
	 * Removes a clause builder from list.
	 *
	 * @param clauseBuilder
	 */
	protected void removeClauseBuilder(ClauseBuilder clauseBuilder) {
		_clauseBuilders.remove(clauseBuilder);
	}

	private static final Logger _log = LoggerFactory.getLogger(
		ClauseBuilderFactoryImpl.class);

	@Reference(
		bind = "addClauseBuilder", cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC, service = ClauseBuilder.class,
		unbind = "removeClauseBuilder"
	)
	private volatile List<ClauseBuilder> _clauseBuilders;

}