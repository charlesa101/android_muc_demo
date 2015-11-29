package com.softwarejoint.chatdemo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.softwarejoint.chatdemo.AppPrefs.AppPreferences;
import com.softwarejoint.chatdemo.BaseActivity;
import com.softwarejoint.chatdemo.MainApplication;
import com.softwarejoint.chatdemo.R;
import com.softwarejoint.chatdemo.constant.AppConstant;
import com.softwarejoint.chatdemo.services.XMPPMainService;

/**
 * Created by bhartisharma on 29/11/15.
 */
public class LoginActivity extends BaseActivity
{
	private AppPreferences appPreferences;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		appPreferences = MainApplication.getInstance().getAppPreferences();

		if(appPreferences.getRegitrationState() == AppConstant.ACCOUNT_STATE_PROFILE_SET){
			startService(new Intent(LoginActivity.this, XMPPMainService.class));
			onAuthenticated();
		}

		setContentView(R.layout.activity_login);
		TextView logIn = (TextView)findViewById(R.id.txtlogin);
		final EditText username = (EditText)findViewById(R.id.txtusername);
		final EditText password = (EditText)findViewById(R.id.txtpassword);

		logIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				appPreferences.setUserName(username.getText().toString());
				appPreferences.setPassword(password.getText().toString());
				startService(new Intent(LoginActivity.this, XMPPMainService.class));
			}
		});
	}

	@Override
	public void onAuthenticated()
	{
		appPreferences.setRegistrationState(AppConstant.ACCOUNT_STATE_PROFILE_SET);
		Intent intent = new Intent(LoginActivity.this, GroupListActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onError(Exception e)
	{
		//tOdo password wrong
		appPreferences.clearAllPreferences();
		Toast.makeText(this, "Please check the credentials", Toast.LENGTH_LONG).show();
		stopService(new Intent(LoginActivity.this, XMPPMainService.class));
	}
}
