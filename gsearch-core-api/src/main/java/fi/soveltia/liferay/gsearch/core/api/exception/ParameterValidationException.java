
package fi.soveltia.liferay.gsearch.core.api.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * Parameter validation exception class.
 * 
 * @author Petteri Karttunen
 */
public class ParameterValidationException extends PortalException {

	private static final long serialVersionUID = 1L;

	public ParameterValidationException() {

	}

	public ParameterValidationException(String msg) {

		super(msg);
	}

	public ParameterValidationException(String msg, Throwable cause) {

		super(msg, cause);
	}

	public ParameterValidationException(Throwable cause) {

		super(cause);
	}
}
