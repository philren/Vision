package com.gpvision.service;

import java.util.ArrayList;
import com.gpvision.R;
import com.gpvision.activity.MainActivity;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.GetMediaListRequset;
import com.gpvision.api.response.GetMediaListResponse;
import com.gpvision.datamodel.Notification;
import com.gpvision.datamodel.Video;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class NotificationService extends Service {

	private static final int DELAY = 1000 * 60 * 60;
	private static final int MESSAGE_WHAT = 1544;
	private static final int NOTIFICATION_ID = 2115;
	private ArrayList<Video> old;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		handler.sendEmptyMessage(MESSAGE_WHAT);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_WHAT:
				checkChanges();
				sendEmptyMessageDelayed(MESSAGE_WHAT, DELAY);
				break;

			default:
				break;
			}
		}

	};

	private void checkChanges() {
		new GetMediaListRequset()
				.start(new APIResponseHandler<GetMediaListResponse>() {

					@Override
					public void handleResponse(GetMediaListResponse response) {
						ArrayList<Video> list = response.getVideos();
						int size = list.size();
						ArrayList<Notification> notifications = new ArrayList<Notification>();
						for (int i = 0; i < size; i++) {
							Video newVideo = list.get(i);
							if (old == null || old.size() <= i) {
								Notification notification = new Notification();
								notification.setTitle(newVideo
										.getOriginalName());
								notification.setMessage(newVideo.getStatus()
										.name());
								notifications.add(notification);
							} else {
								Video oldVideo = old.get(i);
								if (newVideo.getStatus() != oldVideo
										.getStatus()) {
									Notification notification = new Notification();
									notification.setTitle(newVideo
											.getOriginalName());
									notification.setMessage(String
											.format(getString(R.string.notification_fragment_notification_message),
													oldVideo.getStatus().name(),
													newVideo.getStatus().name()));
									notifications.add(notification);
								}
							}
						}
						pupNotification(notifications);
						old = list;
					}

					@Override
					public void handleError(Long errorCode, String errorMessage) {

					}
				});
	}

	private void pupNotification(ArrayList<Notification> notifications) {

		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setClass(getApplicationContext(), MainActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(MainActivity.ARGS_NOTIFICATION_KEY,
				notifications);
		intent.putExtras(bundle);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		android.app.Notification notification = new android.app.Notification(
				R.drawable.ic_launcher, "Status changed",
				System.currentTimeMillis());
		StringBuilder builder = new StringBuilder();
		for (Notification not : notifications) {
			builder.append(not.getTitle() + not.getMessage() + '\n');
		}
		notification.setLatestEventInfo(getApplicationContext(),
				"Status changed", builder.toString(), pendingIntent);
		notification.flags |= android.app.Notification.FLAG_AUTO_CANCEL;
		nm.notify(NOTIFICATION_ID, notification);

	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_SCREEN_ON)) {
				handler.removeMessages(MESSAGE_WHAT);
				handler.sendEmptyMessage(MESSAGE_WHAT);
			} else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				handler.removeMessages(MESSAGE_WHAT);
			}
		}
	};
}
