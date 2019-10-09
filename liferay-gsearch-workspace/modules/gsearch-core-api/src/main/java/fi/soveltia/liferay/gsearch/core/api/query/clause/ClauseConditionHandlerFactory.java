
package fi.soveltia.liferay.gsearch.core.api.query.clause;

/**
 * Clause condition handler factory.
 *
 * @author Petteri Karttunen
 */
public interface ClauseConditionHandlerFactory {

	/**
	 * Gets the condition handler.
	 *
	 * @param handlerName
	 * @return
	 */
	public ClauseConditionHandler getHandler(String handlerName);

}