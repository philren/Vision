package com.gpvision.service;

import java.util.ArrayList;
import java.util.HashSet;

import com.gpvision.datamodel.Video;
import com.gpvision.datamodel.Video.Status;
import com.gpvision.db.DBUtil;
import com.gpvision.utils.UploadManage;
import com.gpvision.utils.UploadManage.UploadStatusCallback;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class UploadService extends Service {

	private HashSet<IUploadService.Stub> clients = new HashSet<IUploadService.Stub>();

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder.asBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		UploadManage manage = UploadManage.getInstance();
		manage.cancelAllTask();
		ArrayList<Video> videos = manage.getUploadStatus();
		if (videos != null && videos.size() > 0) {
			DBUtil dbUtil = new DBUtil(getApplicationContext());
			for (Video video : videos) {
				if (video.getStatus() == Status.uploading) {
					video.setStatus(Status.paused);
				}
				if (dbUtil.update(video) < 1)
					dbUtil.addVideo(video);
			}
			dbUtil.close();
		}
	}

	private final IUploadService.Stub mBinder = new IUploadService.Stub() {

		@Override
		public void message(DataMessage message) throws RemoteException {
			switch (message.what) {
			case DataManage.MSG_CONNECT:
				clients.add((Stub) message.data);
				UploadManage.getInstance().setCallback(callback);
				break;
			case DataManage.MSG_DISS_CONNECT:
				clients.remove((Stub) message.data);
				break;
			case DataManage.MSG_ADD_TASK:
				Video video = (Video) message.data;
				UploadManage manage = UploadManage.getInstance();
				manage.addTask(video);
				break;
			case DataManage.MSG_CANCEL_TASK:
				Video video2 = (Video) message.data;
				UploadManage manage2 = UploadManage.getInstance();
				manage2.cancelTask(video2.getMd5());
				break;
			case DataManage.MSG_ABORT_TASK:
				Video video3 = (Video) message.data;
				UploadManage manage3 = UploadManage.getInstance();
				manage3.abortTask(video3.getMd5());
				break;
			case DataManage.MSG_DELETE_TASK:
				Video video4 = (Video) message.data;
				DBUtil db = new DBUtil(getApplicationContext());
				db.delete(video4);
				db.close();
				break;

			default:
				break;
			}
		}

	};

	private UploadStatusCallback callback = new UploadStatusCallback() {

		@Override
		public void onError(int errorCode, Video video) {
			DBUtil db = new DBUtil(getApplicationContext());
			if (db.update(video) < 1)
				db.addVideo(video);
			db.close();
			for (IUploadService.Stub client : clients) {
				try {
					client.message(new DataMessage(DataManage.MSG_ERROR, video));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void finished(Video video) {
			DBUtil db = new DBUtil(getApplicationContext());
			db.delete(video);
			db.close();
			for (IUploadService.Stub client : clients) {
				try {
					client.message(new DataMessage(DataManage.MSG_FINISHED,
							video));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void changed(Video video) {
			DBUtil db = new DBUtil(getApplicationContext());
			if (db.update(video) < 1)
				db.addVideo(video);
			db.close();
			for (IUploadService.Stub client : clients) {
				try {
					client.message(new DataMessage(DataManage.MSG_CHANGED,
							video));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	};
}
