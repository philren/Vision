package com.gpvision.service;

import com.gpvision.datamodel.Video;
import com.gpvision.utils.LogUtil;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class UploadService extends Service {

	private IUploadService.Stub client;

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder.asBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	private final IUploadService.Stub mBinder = new IUploadService.Stub() {

		@Override
		public void message(DataMessage message) throws RemoteException {
			switch (message.what) {
			case DataManage.MSG_CONNECT:
				client = (Stub) message.data;
				break;
			case DataManage.MSG_ADD_TASK:
				Video video = (Video) message.data;
				LogUtil.logI("addtask:" + video.getOriginalName());
				client.message(new DataMessage(DataManage.MSG_CHANGED, null));
				break;

			default:
				break;
			}
		}

	};

	public interface Callback {
		public void onChange(Video video);
	}
}
