package com.lily.authorize.fitbit;

public class FitbitException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FitbitException() {
	}

	public FitbitException(String message) {
		super(message);
	}

	public FitbitException(Throwable cause) {
		super(cause);
	}

	public FitbitException(String message, Throwable cause) {
		super(message, cause);
	}

}
