package com.gpvision.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gpvision.R;

import android.os.Bundle;

import com.gpvision.fragment.NotificationFragment;
import com.gpvision.fragment.SettingFragment;
import com.gpvision.fragment.VideoInfoFragment;
import com.gpvision.utils.LocalDataBuffer;
import com.gpvision.utils.Message;
import com.gpvision.utils.MessageCenter;
import com.gpvision.utils.MessageCenter.MessageListener;

public class MainActivity extends BaseActivity {

	public static final int MESSAGE_UPDATE_FRAGMENT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TextView account = (TextView) findViewById(R.id.main_activity_logined_user_name);
		account.setText(LocalDataBuffer.getInstance().getAccount().getAccount());

		Button video = (Button) findViewById(R.id.main_activity_videos_btn);
		video.setOnClickListener(this);
		Button notification = (Button) findViewById(R.id.main_activity_notifications_btn);
		notification.setOnClickListener(this);
		Button setting = (Button) findViewById(R.id.main_activity_settings_btn);
		setting.setOnClickListener(this);
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		VideoInfoFragment fragment = new VideoInfoFragment();
		transaction.replace(R.id.main_activity_fragment_content, fragment);
		transaction.commit();
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
	}

	private MessageListener updateContent = new MessageListener() {

		@Override
		public void onMessageReceived(Message message) {
			Fragment fragment = (Fragment) message.getObject();
			if (fragment != null) {
				FragmentTransaction transaction = getSupportFragmentManager()
						.beginTransaction();
				transaction.replace(R.id.main_activity_fragment_content,
						fragment);
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
				NotificationFragment notificationFragment = new NotificationFragment();
				MessageCenter.getInstance().sendMessage(
						new Message(MESSAGE_UPDATE_FRAGMENT,
								notificationFragment));
			}
			break;
		case R.id.main_activity_videos_btn:
			Fragment fragment2 = getSupportFragmentManager().findFragmentById(
					R.id.main_activity_fragment_content);
			if (!(fragment2 instanceof VideoInfoFragment)) {
				VideoInfoFragment videoInfoFragment = new VideoInfoFragment();
				MessageCenter.getInstance()
						.sendMessage(
								new Message(MESSAGE_UPDATE_FRAGMENT,
										videoInfoFragment));
			}
			break;
		case R.id.main_activity_settings_btn:
			Fragment fragment3 = getSupportFragmentManager().findFragmentById(
					R.id.main_activity_fragment_content);
			if (!(fragment3 instanceof SettingFragment)) {
				SettingFragment settingFragment = new SettingFragment();
				MessageCenter.getInstance().sendMessage(
						new Message(MESSAGE_UPDATE_FRAGMENT, settingFragment));
			}
			break;
		default:
			break;
		}
	}

}
