
package fi.soveltia.liferay.gsearch.core.impl.query.clause.condition;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseConditionHandler;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseConditionHandlerFactory;

/**
 * Clause condition handler factory.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseConditionHandlerFactory.class
)
public class ClauseConditionHandlerFactoryImpl
	implements ClauseConditionHandlerFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClauseConditionHandler getHandler(String handlerName) {

		if (_clauseConditionHandlers == null) {

			_log.error("No clause condition handlers found.");

			return null;
		}

		for (ClauseConditionHandler clauseConditionHandler : _clauseConditionHandlers) {
			if (clauseConditionHandler.canProcess(handlerName)) {
				return clauseConditionHandler;
			}
		}

		_log.error(
			"No clause condition handler found for " + handlerName +
				". Check configuration.");

		return null;
	}

	/**
	 * Add a clause condition handler.
	 * 
	 * @param clauseConditionHandler
	 */
	protected void addClauseConditionHandler(
		ClauseConditionHandler clauseConditionHandler) {

		if (_clauseConditionHandlers == null) {
			_clauseConditionHandlers = new ArrayList<ClauseConditionHandler>();
		}
		_clauseConditionHandlers.add(clauseConditionHandler);
	}

	/**
	 * Remove a clause condition handler.
	 * 
	 * @param clauseConditionHandler
	 */
	protected void removeClauseConditionHandler(
		ClauseConditionHandler clauseConditionHandler) {

		_clauseConditionHandlers.remove(clauseConditionHandler);
	}

	@Reference(
		bind = "addClauseConditionHandler", 
		cardinality = ReferenceCardinality.MULTIPLE, 
		policy = ReferencePolicy.DYNAMIC, 
		service = ClauseConditionHandler.class, 
		unbind = "removeClauseConditionHandler"
	)
	private volatile List<ClauseConditionHandler> _clauseConditionHandlers =
		null;
	
	private static final Logger _log =
					LoggerFactory.getLogger(ClauseConditionHandlerFactoryImpl.class);
}
