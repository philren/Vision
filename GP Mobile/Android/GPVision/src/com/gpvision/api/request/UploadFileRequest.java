package com.gpvision.api.request;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import android.os.AsyncTask;

import com.gpvision.api.APIError;
import com.gpvision.api.APIResponse;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.utils.AppUtils;
import com.gpvision.utils.LocalDataBuffer;
import com.gpvision.utils.LogUtil;

public class UploadFileRequest<RESPONSE extends APIResponse> extends
		AsyncTask<Void, Void, Integer> {

	private static final int CHUNKED_SIZE = 1024 * 1024;
	private static final String BOUNDARY = java.util.UUID.randomUUID()
			.toString();
	private static final String PREFIX = "--", LINEND = "\r\n";
	private static final String MULTIPART_FROM_DATA = "multipart/form-data";
	private APIResponseHandler<RESPONSE> responseHandler;

	private String mUrl;
	private ArrayList<String[]> mFiles;
	private UploadedProgressCallback callback;
	private volatile boolean running = true;

	public void setCallback(UploadedProgressCallback callback) {
		this.callback = callback;
	}

	public UploadFileRequest() {

		StringBuilder requestString = new StringBuilder();

		requestString.append("https://");
		requestString.append(getServiceHost());

		String path = getServiceHostPath();
		if (path != null) {
			requestString.append(path);
		}

		requestString.append("/api/upload");

		mUrl = requestString.toString();
		mFiles = new ArrayList<String[]>();
	}

	private String getServiceHost() {
		return LocalDataBuffer.getInstance().getEnvironment().getHost();
	}

	private String getServiceHostPath() {
		return LocalDataBuffer.getInstance().getEnvironment().getBasePath();
	}

	public void addFile(String name, String type, String path) {
		mFiles.add(new String[] { name, type, path });
	}

	public void start(APIResponseHandler<RESPONSE> handler) {
		this.responseHandler = handler;
		execute();
	}

	@Override
	protected Integer doInBackground(Void... params) {

		int res = 0;

		if (mFiles != null && mFiles.size() > 0)
			for (int i = 0, num = mFiles.size(); i < num; i++) {
				String[] param = mFiles.get(i);
				String fileName = param[0];
				String fileType = param[1];
				String filePath = param[2];
				File file = new File(filePath);
				int chunkedSize = (int) (file.length() / CHUNKED_SIZE + 1);
				for (int n = 0; n < chunkedSize; n++) {
					long offset = n * CHUNKED_SIZE;
					res = upload(fileName, fileType, file, offset);
				}
			}
		return res;
	}

	private int upload(String fileName, String fileType, File file, long offset) {
		int res = 0;
		try {
			URL uri = new URL(mUrl);
			HttpsURLConnection conn = (HttpsURLConnection) uri.openConnection();

			conn.setReadTimeout(5 * 1000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");

			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);

			conn.setRequestProperty("accept",
					"application/json, text/javascript, */*; q=0.01");

			int to = (int) (offset + CHUNKED_SIZE - 1);
			if (to > file.length())
				to = (int) file.length();
			conn.setRequestProperty("content-range", "bytes " + offset + "-"
					+ to + "/" + file.length());
			LogUtil.logE("bytes " + offset + "-" + to + "/" + file.length());
//			conn.setRequestProperty("uuid",
//					AppUtils.getMd5(file, offset, CHUNKED_SIZE));
//			LogUtil.logE(AppUtils.getMd5(file, offset, CHUNKED_SIZE));

			if (LocalDataBuffer.getInstance().getAccount() != null) {
				conn.setRequestProperty("endUserToken", LocalDataBuffer
						.getInstance().getAccount().getUserToken());
			}
			DataOutputStream outStream = new DataOutputStream(
					conn.getOutputStream());

			StringBuilder sb1 = new StringBuilder();
			sb1.append(PREFIX);
			sb1.append(BOUNDARY);
			sb1.append(LINEND);
			sb1.append("Content-Disposition: form-data; name=\"file[]\"; filename=\""
					+ fileName + "\"" + LINEND);
			sb1.append("Content-Type: " + fileType + LINEND);
			sb1.append("uuid:" + AppUtils.getMd5(file, offset, CHUNKED_SIZE)
					+ LINEND);
			sb1.append(LINEND);
			outStream.write(sb1.toString().getBytes());

			RandomAccessFile is = new RandomAccessFile(file, "r");
			is.seek(offset);
			byte[] buffer = new byte[1024];
			int len = 0;
			int count = 0;
			while (running && (len = is.read(buffer)) != -1) {
				count += len;
				if (count < CHUNKED_SIZE) {
					outStream.write(buffer, 0, len);
				} else {
					outStream.write(buffer, 0, 1024 - (count - CHUNKED_SIZE));
					break;
				}
				if (callback != null) {
					callback.uploadedProgress(count);
				}
			}

			is.close();
			outStream.write(LINEND.getBytes());

			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();

			res = conn.getResponseCode();
			InputStream in = conn.getInputStream();

			if (res == 200) {
				int ch;
				StringBuilder sb2 = new StringBuilder();
				while ((ch = in.read()) != -1) {
					sb2.append((char) ch);
				}
				LogUtil.logI(sb2.toString());
			}

			outStream.close();
			conn.disconnect();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	@Override
	protected void onPostExecute(Integer result) {
		if (responseHandler == null)
			return;

		if (result == 200) {
			responseHandler.handleResponse(null);
		} else {
			responseHandler.handleError(APIError.NETWORK_ERROR, "");
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		running = false;
	}

	public interface UploadedProgressCallback {
		public void uploadedProgress(long uploadedBytes);
	}
}
