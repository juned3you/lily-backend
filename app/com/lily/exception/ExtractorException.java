package com.lily.exception;

/**
 * Extractor exception.
 * @author Mohammad
 *
 */
public class ExtractorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExtractorException() {
	}

	public ExtractorException(String message) {
		super(message);
	}

	public ExtractorException(Throwable cause) {
		super(cause);
	}

	public ExtractorException(String message, Throwable cause) {
		super(message, cause);
	}
}