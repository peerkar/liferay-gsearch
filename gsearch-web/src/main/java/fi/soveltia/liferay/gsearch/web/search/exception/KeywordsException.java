package fi.soveltia.liferay.gsearch.web.search.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * Keywords Exception Class
 * 
 * @author Petteri Karttunen
 */
public class KeywordsException extends PortalException {

	private static final long serialVersionUID = 1L;

	public KeywordsException() {
	}

	public KeywordsException(String msg) {
		super(msg);
	}

	public KeywordsException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public KeywordsException(Throwable cause) {
		super(cause);
	}
}
