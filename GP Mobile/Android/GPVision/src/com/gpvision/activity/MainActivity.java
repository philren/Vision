package com.gpvision.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.gpvision.R;

import android.os.Bundle;
import com.gpvision.fragment.VideoInfoFragment;
import com.gpvision.utils.Message;
import com.gpvision.utils.MessageCenter;
import com.gpvision.utils.MessageCenter.MessageListener;

public class MainActivity extends BaseActivity {

	public static final int MESSAGE_UPDATE_FRAGMENT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		VideoInfoFragment fragment = new VideoInfoFragment();
		transaction.replace(R.id.main_activity_fragment_content, fragment);
		transaction.addToBackStack(null).commit();
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
}
