package com.gpvision.activity;

import com.gpvision.R;
import com.gpvision.fragment.LoginFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

public class LoginActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		LoginFragment fragment = new LoginFragment();
		transaction.replace(R.id.login_activity_content, fragment);
		transaction.commit();

	}

}
