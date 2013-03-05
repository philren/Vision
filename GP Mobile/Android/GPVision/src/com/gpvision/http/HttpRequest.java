package com.gpvision.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

public class HttpRequest {

	private static final String CHAR_SET = "utf-8";

	private String mUrl;
	private UserAgentConfig mConfig = null;
	private HashMap<String, String> mHeaders = null;
	private HttpEntity mBody = null;
	private int mStatus = -1;

	public HttpRequest(String url) {
		if (isEmpty(url))
			throw new IllegalArgumentException("url is empty");
		mUrl = url;
	}

	public void addHeader(String key, String value) {
		if (mHeaders == null)
			mHeaders = new HashMap<String, String>();

		mHeaders.put(key, value);
	}

	public void clearHeader() {
		mHeaders.clear();
		mHeaders = null;
	}

	public void setConfig(UserAgentConfig config) {
		mConfig = config;
	}

	public void setPostBody(List<BasicNameValuePair> body) {
		try {
			mBody = new UrlEncodedFormEntity(body, CHAR_SET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void setPostBody(String body) {
		try {
			mBody = new StringEntity(body);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void execute() throws ClientProtocolException, IOException {
		httprequest();
	}

	public int getStatusCode() {
		return mStatus;
	}

	/**
	 * get "Stream" as response
	 * 
	 * @return response in stream
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public InputStream getStream() throws ClientProtocolException, IOException {

		HttpEntity entity = httprequest();
		InputStream ret = null;

		if (entity != null) {
			try {
				byte[] b = EntityUtils.toByteArray(entity);
				ret = new ByteArrayInputStream(b);
			} finally {
				release(entity);
			}
		}

		return ret;
	}

	/**
	 * get "String" as response
	 * 
	 * @return response in string
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public String getString() throws ClientProtocolException, IOException {

		HttpEntity entity = httprequest();
		String ret = null;

		if (entity != null) {
			try {
				ret = EntityUtils.toString(entity);
			} finally {
				release(entity);
			}
		}

		return ret;
	}

	/**
	 * release connection resource
	 * 
	 * @param entity
	 */
	private static void release(HttpEntity entity) {
		try {
			entity.consumeContent();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * get "HttpEntity" as response
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	private HttpEntity httprequest() throws ClientProtocolException,
			IOException {

		System.out.println(mUrl);

		DefaultHttpClient client = null;
		HttpEntity entity = null;

		BasicHttpParams httpParameters = new BasicHttpParams();

		if (mConfig != null) {
			// set connection timeout
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					mConfig.timeoutConnection);
		}

		client = new DefaultHttpClient(httpParameters);

		DefaultHttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(
				3, true);
		client.setHttpRequestRetryHandler(retryHandler);

		// set username & password if available
		if (mConfig != null
				&& !(isEmpty(mConfig.username) && isEmpty(mConfig.password))) {
			AuthScope as = new AuthScope(mConfig.host, mConfig.port);
			UsernamePasswordCredentials upc = new UsernamePasswordCredentials(
					mConfig.username, mConfig.password);

			client.getCredentialsProvider().setCredentials(as, upc);
		}

		// check get or post method by params
		HttpRequestBase method = null;
		if (mBody == null) {
			method = new HttpGet(mUrl);
		} else {
			method = new HttpPost(mUrl);
			((HttpPost) method).setEntity(mBody);
		}

		// set request header
		if (mHeaders != null) {
			Iterator<?> iter = mHeaders.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<?, ?> entry = (Entry<?, ?>) iter.next();
				String key = (String) entry.getKey();
				String val = (String) entry.getValue();
				method.setHeader(key, val);
			}
		}

		// get response
		HttpResponse response = null;
		if (mConfig == null || isEmpty(mConfig.host) || isEmpty(mConfig.scheme)) {
			// only URL is available
			response = client.execute(method);
		} else {
			BasicHttpContext localContext = new BasicHttpContext();

			BasicScheme basicAuth = new BasicScheme();
			localContext.setAttribute("preemptive-auth", basicAuth);

			HttpHost targetHost = new HttpHost(mConfig.host, mConfig.port,
					mConfig.scheme);

			response = client.execute(targetHost, method, localContext);
		}

		mStatus = response.getStatusLine().getStatusCode();
		entity = response.getEntity();

		return entity;
	}

	/**
	 * Check the string is valuable or not
	 * 
	 * @param s
	 * @return true if s is null or s.length() == 0 false otherwise
	 */
	private boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	class UserAgentConfig {
		public String host;
		public String scheme = "http";
		public int port = 80;
		public int timeoutConnection = 10000;
		public int timeoutSocket = 20000;
		public String username = "";
		public String password = "";
	}

}
