package com.lily.exception;

public class LoaderException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LoaderException() {
	}

	public LoaderException(String message) {
		super(message);
	}

	public LoaderException(Throwable cause) {
		super(cause);
	}

	public LoaderException(String message, Throwable cause) {
		super(message, cause);
	}

}
