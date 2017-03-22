package com.tops.hotelmanager.exception;

public class CustomValidationException extends CustomException {
	public CustomValidationException() {
		super();
	}

	public CustomValidationException(String message) {
		super(message);
	}

	public CustomValidationException(Throwable th) {
		super(th);
	}

	public CustomValidationException(String message, Throwable th) {
		super(message, th);
	}
}
