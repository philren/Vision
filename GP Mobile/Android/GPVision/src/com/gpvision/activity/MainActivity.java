package com.gpvision.activity;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gpvision.R;

import android.content.Intent;
import android.os.Bundle;

import com.gpvision.datamodel.Notification;
import com.gpvision.fragment.NotificationFragment;
import com.gpvision.fragment.SettingFragment;
import com.gpvision.fragment.VideoInfoFragment;
import com.gpvision.service.NotificationService;
import com.gpvision.utils.LocalDataBuffer;
import com.gpvision.utils.Message;
import com.gpvision.utils.MessageCenter;
import com.gpvision.utils.MessageCenter.MessageListener;

public class MainActivity extends BaseActivity {

	public static final int MESSAGE_UPDATE_FRAGMENT = 1;
	public static final String ARGS_NOTIFICATION_KEY = "notification";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TextView account = (TextView) findViewById(R.id.main_activity_logined_user_name);
		if (LocalDataBuffer.getInstance().getAccount() != null)
			account.setText(LocalDataBuffer.getInstance().getAccount()
					.getAccount());

		Button video = (Button) findViewById(R.id.main_activity_videos_btn);
		video.setOnClickListener(this);
		Button notification = (Button) findViewById(R.id.main_activity_notifications_btn);
		notification.setOnClickListener(this);
		Button setting = (Button) findViewById(R.id.main_activity_settings_btn);
		setting.setOnClickListener(this);
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		VideoInfoFragment fragment = new VideoInfoFragment();
		transaction.replace(R.id.main_activity_fragment_content, fragment,
				VideoInfoFragment.TAG);
		transaction.commit();

		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), NotificationService.class);
		startService(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MessageCenter.getInstance().removeMessageListener(
				MESSAGE_UPDATE_FRAGMENT);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MessageCenter.getInstance().addMessageListener(MESSAGE_UPDATE_FRAGMENT,
				updateContent);
		Intent intent = getIntent();
		if (intent.hasExtra(ARGS_NOTIFICATION_KEY)) {
			FragmentManager manager = getSupportFragmentManager();
			NotificationFragment fragment = (NotificationFragment) manager
					.findFragmentByTag(NotificationFragment.TAG);
			if (fragment == null)
				fragment = new NotificationFragment();
			ArrayList<Notification> notifications = (ArrayList<Notification>) intent
					.getExtras().getSerializable(ARGS_NOTIFICATION_KEY);
			fragment.setNotifications(notifications);
			MessageCenter.getInstance().sendMessage(
					new Message(MESSAGE_UPDATE_FRAGMENT, fragment,
							NotificationFragment.TAG));
		}
	}

	private MessageListener updateContent = new MessageListener() {

		@Override
		public void onMessageReceived(Message message) {
			Fragment fragment = (Fragment) message.getObject();
			if (fragment != null) {
				FragmentTransaction transaction = getSupportFragmentManager()
						.beginTransaction();
				transaction.replace(R.id.main_activity_fragment_content,
						fragment, message.getTag());
				transaction.addToBackStack(null).commit();
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_activity_notifications_btn:
			Fragment fragment = getSupportFragmentManager().findFragmentById(
					R.id.main_activity_fragment_content);
			if (!(fragment instanceof NotificationFragment)) {
				NotificationFragment notificationFragment = (NotificationFragment) getSupportFragmentManager()
						.findFragmentByTag(NotificationFragment.TAG);
				if (notificationFragment == null)
					notificationFragment = new NotificationFragment();
				MessageCenter.getInstance()
						.sendMessage(
								new Message(MESSAGE_UPDATE_FRAGMENT,
										notificationFragment,
										NotificationFragment.TAG));
			}
			break;
		case R.id.main_activity_videos_btn:
			Fragment fragment2 = getSupportFragmentManager().findFragmentById(
					R.id.main_activity_fragment_content);
			if (!(fragment2 instanceof VideoInfoFragment)) {
				VideoInfoFragment videoInfoFragment = (VideoInfoFragment) getSupportFragmentManager()
						.findFragmentByTag(VideoInfoFragment.TAG);
				if (videoInfoFragment == null)
					videoInfoFragment = new VideoInfoFragment();
				MessageCenter.getInstance().sendMessage(
						new Message(MESSAGE_UPDATE_FRAGMENT, videoInfoFragment,
								VideoInfoFragment.TAG));
			}
			break;
		case R.id.main_activity_settings_btn:
			Fragment fragment3 = getSupportFragmentManager().findFragmentById(
					R.id.main_activity_fragment_content);
			if (!(fragment3 instanceof SettingFragment)) {
				SettingFragment settingFragment = (SettingFragment) getSupportFragmentManager()
						.findFragmentByTag(SettingFragment.TAG);
				if (settingFragment == null)
					settingFragment = new SettingFragment();
				MessageCenter.getInstance().sendMessage(
						new Message(MESSAGE_UPDATE_FRAGMENT, settingFragment,
								SettingFragment.TAG));
			}
			break;
		default:
			break;
		}
	}

}
