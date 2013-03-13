package com.gpvision.api.request;

import org.json.JSONException;
import org.json.JSONObject;

import com.gpvision.api.CallAPI;
import com.gpvision.api.response.SignUpResponse;

public class SignUpRequest extends CallAPI<SignUpResponse> {

	private String userName, email, pass;

	public SignUpRequest(String userName, String email, String pass) {
		super();
		this.userName = userName;
		this.email = email;
		this.pass = pass;
	}

	@Override
	protected String serviceComponent() {
		return "/api/signup";
	}

	@Override
	protected String getContent() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("userName", userName);
		object.put("email", email);
		object.put("password", pass);
		return object.toString();
	}

	@Override
	protected void onResponseReceived(String respString) throws JSONException {

	}

}
