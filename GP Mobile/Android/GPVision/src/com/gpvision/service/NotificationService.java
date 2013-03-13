package com.gpvision.service;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.gpvision.R;
import com.gpvision.activity.MainActivity;
import com.gpvision.api.APIResponseHandler;
import com.gpvision.api.request.GetMediaListRequset;
import com.gpvision.api.response.GetMediaListResponse;
import com.gpvision.datamodel.Notification;
import com.gpvision.datamodel.Video;
import com.gpvision.fragment.NotificationFragment;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class NotificationService extends Service {

	private static final int PERIOD = 1000 * 60 * 5;
	private static final int NOTIFICATION_ID = 2115;
	private TimerTask task;
	private Timer timer;
	private ArrayList<Video> old;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		task = new TimerTask() {

			@Override
			public void run() {
				checkChanges();
			}
		};
		timer = new Timer();
		timer.schedule(task, 0, PERIOD);
	}

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
		bundle.putSerializable(NotificationFragment.ARGS_NOTIFICATION_KEY,
				notifications);
		intent.putExtras(bundle);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent, 0);

		android.app.Notification notification = new android.app.Notification(
				R.drawable.ic_launcher, "Status changed",
				System.currentTimeMillis());
		notification.setLatestEventInfo(getApplicationContext(),
				"Status changed", "", pendingIntent);
		notification.flags |= android.app.Notification.FLAG_AUTO_CANCEL;
		nm.notify(NOTIFICATION_ID, notification);

	}
}
