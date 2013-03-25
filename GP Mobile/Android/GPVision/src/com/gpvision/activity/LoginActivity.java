package com.gpvision.activity;

import com.gpvision.R;
import com.gpvision.fragment.LoginFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class LoginActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		FragmentManager manager = getSupportFragmentManager();
		LoginFragment fragment = (LoginFragment) manager
				.findFragmentByTag(LoginFragment.TAG);
		if (fragment == null)
			fragment = new LoginFragment();

		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.login_activity_content, fragment,
				LoginFragment.TAG);
		transaction.commit();

	}

}
