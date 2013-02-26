package com.gpvision.activity;

import android.support.v4.app.FragmentTransaction;
import com.gpvision.R;

import android.os.Bundle;
import com.gpvision.fragment.VideoInfoFragment;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        VideoInfoFragment fragment = new VideoInfoFragment();
        transaction.replace(R.id.main_activity_fragment_content,fragment);
        transaction.addToBackStack(null).commit();
	}

}
