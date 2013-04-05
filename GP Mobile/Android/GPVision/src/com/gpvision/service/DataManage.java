package com.gpvision.service;

import com.gpvision.datamodel.Video;
import com.gpvision.utils.UploadManage.UploadStatusCallback;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class DataManage {
	// base message
	public static final int MSG_CONNECT = 0;
	public static final int MSG_DISS_CONNECT = MSG_CONNECT + 1;
	// message send to service
	public static final int MSG_ADD_TASK = 10;
	public static final int MSG_CANCEL_TASK = MSG_ADD_TASK + 1;
	// message from service
	public static final int MSG_CHANGED = 20;
	public static final int MSG_FINISHED = MSG_CHANGED + 1;
	public static final int MSG_ERROR = MSG_CHANGED + 2;

	private boolean isBind = false;
	private UploadStatusCallback callback;

	public void setCallback(UploadStatusCallback callback) {
		this.callback = callback;
	}

	public DataManage() {
		super();
	}

	public void bindService(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, UploadService.class);
		context.startService(intent);
		isBind = context.bindService(new Intent(intent), serviceConnection,
				Context.BIND_AUTO_CREATE);
	}

	public void unBindService(Context context) {
		if (isBind)
			context.unbindService(serviceConnection);
	}

	private IUploadService mService;
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			try {
				mService.message(new DataMessage(MSG_DISS_CONNECT, receiver));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IUploadService.Stub.asInterface(service);
			try {
				mService.message(new DataMessage(MSG_CONNECT, receiver));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * handler message from service
	 */
	private IUploadService.Stub receiver = new IUploadService.Stub() {

		@Override
		public void message(DataMessage message) throws RemoteException {
			switch (message.what) {
			case MSG_CHANGED:
				callback.changed((Video) message.data);
				break;
			case MSG_ERROR:
				callback.onError(0, (Video) message.data);
				break;
			case MSG_FINISHED:
				callback.finished((Video) message.data);
				break;

			default:
				break;
			}
		}
	};

	public void sendMessage(DataMessage message) {
		try {
			mService.message(message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
