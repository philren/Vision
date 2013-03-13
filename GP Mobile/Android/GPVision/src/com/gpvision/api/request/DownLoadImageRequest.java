package com.gpvision.api.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;

import com.gpvision.api.APIResponse;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.datamodel.Index;
import com.gpvision.http.HttpRequest;
import com.gpvision.utils.AppUtils;
import com.gpvision.utils.Environment;
import com.gpvision.utils.ImageCacheUtil;
import com.gpvision.utils.LocalDataBuffer;
import com.gpvision.utils.LogUtil;

public class DownLoadImageRequest<RESPONSE extends APIResponse> extends
		AsyncTask<Void, Void, Void> {

	private APIResponseHandler<RESPONSE> responseHandler;

	private HashMap<Integer, Index> indexMap;
	private DownLoadStatusCallBack callBack;

	public DownLoadImageRequest(HashMap<Integer, Index> indexMap) {
		super();
		this.indexMap = indexMap;
	}

	public void start(APIResponseHandler<RESPONSE> handler) {
		this.responseHandler = handler;
		execute();
	}

	public void setCallBack(DownLoadStatusCallBack callBack) {
		this.callBack = callBack;
	}

	@Override
	protected Void doInBackground(Void... params) {
		String userToken = LocalDataBuffer.getInstance().getAccount()
				.getUserToken();
		Environment environment = LocalDataBuffer.getInstance()
				.getEnvironment();
		StringBuilder builder = new StringBuilder();
		builder.append("http://");
		builder.append(environment.getHost());
		if (!AppUtils.isEmpty(environment.getBasePath())) {
			builder.append(environment.getBasePath());
		}
		String baseUrl = builder.toString();
		int size = indexMap.size(), index = 0, n = 0;

		while (n < size) {
			if (indexMap.containsKey(index)) {
				ArrayList<String> urlList = indexMap.get(index).getImageUrls();
				for (String url : urlList) {
					HttpRequest request = new HttpRequest(baseUrl + url);
					LogUtil.logI(baseUrl + url);
					request.addHeader("endUserToken", userToken);
					String fileName = ImageCacheUtil.getFileNameFromUrl(url);
					InputStream inputStream;
					try {
						inputStream = request.getStream();
						if (inputStream != null)
							ImageCacheUtil.write2SDCard(fileName, inputStream);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				n++;
				index++;
			} else {
				index++;
			}
			callBack.downLoadStatus(index);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		responseHandler.handleResponse(null);
	}

	public interface DownLoadStatusCallBack {
		public void downLoadStatus(int index);
	}
}
