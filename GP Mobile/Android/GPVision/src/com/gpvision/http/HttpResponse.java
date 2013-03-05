package com.gpvision.http;

public class HttpResponse {

	private final String data;
	private final int responseCode;

	public HttpResponse(String data, int responseCode) {

		this.data = data;
		this.responseCode = responseCode;
	}

	public String getData() {
		return data;
	}

	public int getHttpResponseCode() {
		return responseCode;
	}

	public boolean isSuccess() {
		return responseCode >= 200 && responseCode < 300;
	}
}
