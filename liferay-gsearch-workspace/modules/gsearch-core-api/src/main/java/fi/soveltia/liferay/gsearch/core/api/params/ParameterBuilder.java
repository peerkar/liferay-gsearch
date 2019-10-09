
package fi.soveltia.liferay.gsearch.core.api.params;

import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

import java.util.Map;

/**
 * Takes care of validating and putting parameters to query context.
 *
 * @author Petteri Karttunen
 */
public interface ParameterBuilder {

	/**
	 * Processes parameter and add it to query context.
	 *
	 * @param queryContext
	 * @throws Exception
	 */
	public void addParameter(QueryContext queryContext) throws Exception;

	/**
	 * Processes parameter and adds it to query context.
	 *
	 * This overload is meant for processing parameters from headless call.
	 *
	 * @param parameters
	 * @param queryContext
	 * @throws Exception
	 */
	public void addParameterHeadless(
			QueryContext queryContext, Map<String, Object> parameters)
		throws Exception;

	/**
	 * Validates parameter.
	 *
	 * @param queryContext
	 * @return
	 * @throws ParameterValidationException
	 */
	public boolean validate(QueryContext queryContext)
		throws ParameterValidationException;

	/**
	 * Validate parameter.
     *
	 * This overload is meant for processing parameters from headless call.
	 *
	 * @param queryContext
	 * @param parameters
	 * @return
	 * @throws ParameterValidationException
	 */
	public boolean validateHeadless(
			QueryContext queryContext, Map<String, Object> parameters)
		throws ParameterValidationException;

}