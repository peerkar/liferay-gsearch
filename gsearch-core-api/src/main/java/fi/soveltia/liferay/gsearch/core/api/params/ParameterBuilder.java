
package fi.soveltia.liferay.gsearch.core.api.params;

import java.util.Map;

import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Takes care of receiving request parameter, validating and putting it to query
 * context.
 * 
 * @author Petteri Karttunen
 */
public interface ParameterBuilder {

	/**
	 * Process parameter and add it to query context.
	 * 
	 * @param queryContext
	 * @throws Exception
	 */
	public void addParameter(
		QueryContext queryContext)
		throws Exception;

	/**
	 * Process parameter and add it to query context.
	 * 
	 * @param parameters
	 * @param queryContext
	 * @throws Exception
	 */
	public void addParameter(
		QueryContext queryContext, Map<String, Object>parameters)
		throws Exception;
	
	/**
	 * Validate parameter
	 * 
	 * @param queryContext
	 * @return
	 * @throws ParameterValidationException
	 */
	public boolean validate(QueryContext queryContext)
		throws ParameterValidationException;
	
	/**
	 * Validate parameter
	 * 
	 * @param queryContext
	 * @param parameters
	 * @return
	 * @throws ParameterValidationException
	 */
	public boolean validate(QueryContext queryContext, Map<String, Object>parameters)
		throws ParameterValidationException;

}
