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

import com.gpvision.api.APIResponse;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.utils.AppUtils;
import com.gpvision.utils.LocalDataBuffer;
import com.gpvision.utils.LogUtil;

public class UploadFileRequest<RESPONSE extends APIResponse> extends
		AsyncTask<Void, Void, Void> {

	private static final int CHUNKED_SIZE = 1024 * 1024;
	private static final String BOUNDARY = java.util.UUID.randomUUID()
			.toString();
	private static final String PREFIX = "--", LINEND = "\r\n";
	private static final String MULTIPART_FROM_DATA = "multipart/form-data";
	private APIResponseHandler<RESPONSE> responseHandler;

	private String mUrl;
	private String md5;
	private ArrayList<Long> mUploadedSizes;
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
		mUploadedSizes = new ArrayList<Long>();
	}

	private String getServiceHost() {
		return LocalDataBuffer.getInstance().getEnvironment().getHost();
	}

	private String getServiceHostPath() {
		return LocalDataBuffer.getInstance().getEnvironment().getBasePath();
	}

	public void addFile(String name, String type, String path, long uploadedSize) {
		mFiles.add(new String[] { name, type, path });
		mUploadedSizes.add(uploadedSize);
	}

	public void start(APIResponseHandler<RESPONSE> handler) {
		this.responseHandler = handler;
		execute();
	}

	@Override
	protected Void doInBackground(Void... params) {

		if (mFiles != null && mFiles.size() > 0)
			for (int i = 0, num = mFiles.size(); i < num; i++) {
				String[] param = mFiles.get(i);
				final String fileName = param[0];
				final String fileType = param[1];
				String filePath = param[2];
				md5 = AppUtils.getMd5(filePath);
				final File file = new File(filePath);
				long uploadedSize = mUploadedSizes.get(i);
				if (uploadedSize < file.length()) {
					int chunkedSize = (int) ((file.length() - uploadedSize)
							/ CHUNKED_SIZE + 1);
					for (int n = 0; n < chunkedSize; n++) {
						long offset = n * CHUNKED_SIZE + uploadedSize;
						if (running)
							upload(fileName, fileType, file, offset);
					}

				}

			}
		return null;
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

			conn.setRequestProperty("Content-Disposition",
					"attachment; filename=\"" + fileName + "\"");

			int to = (int) (offset + CHUNKED_SIZE - 1);
			if (to > file.length())
				to = (int) file.length() - 1;
			conn.setRequestProperty("content-range", "bytes " + offset + "-"
					+ to + "/" + file.length());
			LogUtil.logI("bytes " + offset + "-" + to + "/" + file.length());

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
			sb1.append("Content-Disposition: form-data; name=\"uuid\"" + LINEND);
			sb1.append(LINEND);
			sb1.append(md5);
			sb1.append(LINEND);

			sb1.append(PREFIX);
			sb1.append(BOUNDARY);
			sb1.append(LINEND);
			sb1.append("Content-Disposition: form-data; name=\"file[]\"; filename=\""
					+ fileName + "\"" + LINEND);
			sb1.append("Content-Type: " + fileType + LINEND);
			sb1.append(LINEND);
			outStream.write(sb1.toString().getBytes());
			LogUtil.logE(sb1.toString());

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
				if (callback != null) {
					long uploadSize = offset + CHUNKED_SIZE;
					callback.uploadedProgress(uploadSize);
					LogUtil.logE("uploadsize:" + uploadSize);
				}
			}

			outStream.close();
			conn.disconnect();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (responseHandler == null)
			return;

		// if (result == 200) {
		// responseHandler.handleResponse(null);
		// } else {
		// responseHandler.handleError(APIError.NETWORK_ERROR, "");
		// }
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
