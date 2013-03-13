package com.gpvision.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * get HTTP response by given URL, config and parameters
 * 
 * @author damien_dai
 */
public class HttpRequest {

	private static final String CHARSET = "utf-8";

	private String mUrl;
	private HashMap<String, String> mHeaders = null;
	private HttpEntity mBody = null;
	private int mStatusCode = -1;

	private String mHost = "";
	private String mScheme = "";
	private int mPort = 80;
	private int mTimeoutConnection = 10000;
	private String mUsername = "";
	private String mPassword = "";

	private static DefaultHttpClient client = null;

	public HttpRequest(String url) {

		// check URL
		if (isEmpty(url))
			throw new IllegalArgumentException("invalid url");

		mUrl = url;

		// parse scheme
		int pos = url.indexOf("://");
		if (pos > 0) {
			setScheme(url.substring(0, pos));
		}
	}

	public void addHeader(String key, String value) {
		if (mHeaders == null)
			mHeaders = new HashMap<String, String>();

		mHeaders.put(key, value);
	}

	public void clearHeader() {
		if (mHeaders != null)
			mHeaders.clear();

		mHeaders = null;
	}

	public void setCredential(String host, int port, String username,
			String password) {
		mHost = host;
		mPort = port;
		mUsername = username;
		mPassword = password;
	}

	public void setScheme(String scheme) {
		mScheme = scheme;
	}

	public void setTimeout(int timeout) {
		mTimeoutConnection = timeout;

		if (client != null) {
			createHttpClient();
		}
	}

	private void createHttpClient() {
		BasicHttpParams httpParameters = new BasicHttpParams();
		HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);

		// set connection timeout
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				mTimeoutConnection);

		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			final SSLSocketFactory socketFactory = new MySSLSocketFactory(
					trustStore);
			socketFactory
					.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			SchemeRegistry sr = new SchemeRegistry();
			sr.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			sr.register(new Scheme("https", socketFactory, 443));

			ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
					httpParameters, sr);
			client = new DefaultHttpClient(cm, httpParameters);

			// retry 3 times
			DefaultHttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(
					3, true);
			client.setHttpRequestRetryHandler(retryHandler);
		} catch (Exception e) {
		}
	}

	public void setPostBody(List<BasicNameValuePair> body) {
		try {
			mBody = new UrlEncodedFormEntity(body, CHARSET);
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

	public void setPostBody(byte[] body) {
		mBody = new ByteArrayEntity(body);
	}

	public void execute() throws ClientProtocolException, IOException,
			IllegalArgumentException {
		HttpEntity entity = null;
		try {
			entity = httprequest();
		} finally {
			release(entity);
		}
	}

	public int getStatusCode() {
		return mStatusCode;
	}

	protected HttpEntity getPostBody() {
		return mBody;
	}

	/**
	 * get "Stream" as response
	 * 
	 * @return response in stream
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public InputStream getStream() throws ClientProtocolException, IOException,
			IllegalArgumentException {

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
	public String getString() throws ClientProtocolException, IOException,
			IllegalArgumentException {

		HttpEntity entity = httprequest();
		String ret = null;

		if (entity != null) {
			try {
				ret = EntityUtils.toString(entity, CHARSET);
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
			if (entity != null) {
				entity.consumeContent();
			}
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

		HttpEntity entity = null;

		if (client == null) {
			createHttpClient();
		}

		// set certification if available
		if (!isEmpty(mUsername)) {
			AuthScope as = new AuthScope(mHost, mPort);
			UsernamePasswordCredentials upc = new UsernamePasswordCredentials(
					mUsername, mPassword);

			client.getCredentialsProvider().setCredentials(as, upc);
		}

		// check get or post method by body
		HttpRequestBase method = null;
		HttpEntity body = getPostBody();
		if (body == null) {
			method = new HttpGet(mUrl);
		} else {
			method = new HttpPost(mUrl);
			((HttpPost) method).setEntity(body);
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
		if (isEmpty(mHost) || isEmpty(mScheme)) {
			// only URL is available
			try {
				response = client.execute(method);
			} catch (Exception e) {
				if (e.getMessage() != null) {
					Log.e("HttpClient", e.getMessage());
				}
				return null;
			}
		} else {
			BasicHttpContext localContext = new BasicHttpContext();

			BasicScheme basicAuth = new BasicScheme();
			localContext.setAttribute("preemptive-auth", basicAuth);

			HttpHost targetHost = new HttpHost(mHost, mPort, mScheme);

			response = client.execute(targetHost, method, localContext);
		}

		mStatusCode = response.getStatusLine().getStatusCode();
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
}
