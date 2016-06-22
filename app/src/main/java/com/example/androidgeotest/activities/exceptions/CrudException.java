package com.example.androidgeotest.activities.exceptions;

public class CrudException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Exception exception;

	public CrudException(Exception exception) {
		this.setException(exception);
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
	

}
