package com.gpvision.service;

import com.gpvision.utils.LogUtil;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class DataManage {
	public static final int MSG_CONNECT = 0;
	public static final int MSG_DISS_CONNECT = 1;
	public static final int MSG_ADD_TASK = 2;
	public static final int MSG_CHANGED = 3;

	private Context context;
	private boolean isBind = false;

	public DataManage(Context context) {
		super();
		this.context = context;
		bindService(context);
	}

	private void bindService(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, UploadService.class);
		isBind = context.bindService(intent, serviceConnection,
				Context.BIND_AUTO_CREATE);
	}

	public void unBindService() {
		if (isBind)
			context.unbindService(serviceConnection);
	}

	private IUploadService mService;
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
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

	private IUploadService.Stub receiver = new IUploadService.Stub() {

		@Override
		public void message(DataMessage message) throws RemoteException {
			switch (message.what) {
			case MSG_CHANGED:
				LogUtil.logI("received from service");
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
