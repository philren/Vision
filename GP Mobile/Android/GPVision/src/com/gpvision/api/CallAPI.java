package com.gpvision.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.gpvision.http.HttpRequest;
import com.gpvision.http.HttpResponse;
import com.gpvision.utils.LocalDataBuffer;
import com.gpvision.utils.LogUtil;

import android.net.Uri;
import android.os.AsyncTask;

public abstract class CallAPI<RESPONSE extends APIResponse> extends
		AsyncTask<Void, Void, HttpResponse> {

	private static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

	protected APIResponseHandler<RESPONSE> responseHandler;

	public void start(APIResponseHandler<RESPONSE> handler) {
		this.responseHandler = handler;
		execute();
	}

	protected boolean isSecuredConnection() {
		return true;
	}

	protected String getServiceHost() {
		return LocalDataBuffer.getInstance().getEnvironment().getHost();
	}

	protected String getServiceHostPath() {
		return LocalDataBuffer.getInstance().getEnvironment().getBasePath();
	}

	protected HashMap<String, String> getHeaders() {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON);
		return headers;
	}

	protected abstract String serviceComponent();

	protected HashMap<String, String> getParameters() {
		HashMap<String, String> parameters = new HashMap<String, String>();
		return parameters;
	}

	protected HashMap<String, String> getUnescapedParameters() {
		HashMap<String, String> parameters = new HashMap<String, String>();
		return parameters;
	}

	protected String getContent() throws JSONException {
		return null;
	}

	private String getUrl() {
		StringBuilder requestString = new StringBuilder();

		if (isSecuredConnection()) {
			requestString.append("https://");
		} else {
			requestString.append("http://");
		}
		requestString.append(getServiceHost());

		String path = getServiceHostPath();
		if (path != null) {
			requestString.append(path);
		}

		String serviceComponent = serviceComponent();
		if (serviceComponent != null) {
			requestString.append(serviceComponent);
		}

		appendParameters(requestString, getParameters(), true, true);
		appendParameters(requestString, getUnescapedParameters(), false, false);

		return requestString.toString();
	}

	private void appendParameters(StringBuilder requestString,
			Map<String, String> parameters, boolean initialFirst, boolean escape) {
		boolean first = initialFirst;
		// Append each of the parameters onto the URL string
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			if (first) {
				requestString.append("?");
				first = false;
			} else {
				requestString.append("&");
			}

			if (escape) {
				requestString.append(Uri.encode(entry.getKey()) + "="
						+ Uri.encode(entry.getValue()));
			} else {
				requestString.append(entry.getKey() + "=" + entry.getValue());
			}
		}
	}

	@Override
	protected HttpResponse doInBackground(Void... params) {
		String url = getUrl();
		HttpRequest request = new HttpRequest(url);
		LogUtil.logI(url);
		try {
			String content = getContent();
			if (content != null) {
				request.setPostBody(content);
				LogUtil.logI(content);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		// set request header
		HashMap<String, String> headeres = getHeaders();
		if (headeres != null)
			for (Entry<String, String> header : headeres.entrySet())
				request.addHeader(header.getKey(), header.getValue());

		String response = null;
		int responseCode = 0;

		try {
			response = request.getString();
			responseCode = request.getStatusCode();
			LogUtil.logI(response);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new HttpResponse(response, responseCode);
	}

	@Override
	protected void onPostExecute(HttpResponse result) {
		super.onPostExecute(result);
		if (responseHandler == null)
			return;

		if (result.isSuccess()) {
			try {
				onResponseReceived(result.getData());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			handleError(result.getData());
		}
	}

	private void handleError(String response) {
		JSONObject json;
		try {
			json = new JSONObject(response);
			long errorCode = json.getLong("errorCode");
			String errorMessage = json.getString("error");
			responseHandler.handleError(errorCode, errorMessage);
		} catch (JSONException e) {
			e.printStackTrace();
			responseHandler.handleError(APIError.NETWORK_ERROR, "");
		}
	}

	protected abstract void onResponseReceived(String respString)
			throws JSONException;
}
