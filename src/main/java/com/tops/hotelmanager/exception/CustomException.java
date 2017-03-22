package com.tops.hotelmanager.exception;

public class CustomException extends Exception {

	public CustomException() {
		super();
	}

	public CustomException(String message) {
		super(message);
	}

	public CustomException(Throwable th) {
		super(th);
	}

	public CustomException(String message, Throwable th) {
		super(message, th);
	}
}
