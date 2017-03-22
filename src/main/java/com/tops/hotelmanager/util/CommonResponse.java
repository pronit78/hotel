package com.tops.hotelmanager.util;

public class CommonResponse {

	public static final int STATUS_ERROR = 0;
	public static final int STATUS_SUCCESS = 1;

	public static final String SERVER_ERROR_MESSAGE = "Wooha, something unexpected has happened, no worry we have take a note of it. it will be fixed soon.";

	private int status;
	private String message;
	private Object data;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
