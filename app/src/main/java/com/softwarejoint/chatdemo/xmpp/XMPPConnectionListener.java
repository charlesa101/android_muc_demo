package com.softwarejoint.chatdemo.xmpp;

import com.softwarejoint.chatdemo.BaseActivity;
import com.softwarejoint.chatdemo.DBHelper.AppDbHelper;
import com.softwarejoint.chatdemo.MainApplication;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;


public class XMPPConnectionListener implements ConnectionListener{

	@Override
	public void connected(XMPPConnection connection)
	{

	}

	@Override
	public void authenticated(XMPPConnection connection, boolean resumed)
	{
		BaseActivity baseActivity = getCurrentActivity();
		if(baseActivity != null){
			baseActivity.onAuthenticated();
		}

		AppDbHelper.sendPendingMessages();
	}

	@Override
	public void connectionClosed()
	{

	}

	@Override
	public void connectionClosedOnError(Exception e)
	{

	}

	@Override
	public void reconnectionSuccessful()
	{

	}

	@Override
	public void reconnectingIn(int seconds)
	{

	}

	@Override
	public void reconnectionFailed(Exception e)
	{

	}

	public void onError(Exception e) {
		BaseActivity baseActivity = getCurrentActivity();
		if(baseActivity != null){
			baseActivity.onError(e);
		}
	}

	private BaseActivity getCurrentActivity(){
		MainApplication mainApplication = MainApplication.getInstance();
		if(mainApplication.isForeGround()
				&& mainApplication.getCurrentActivity() != null){
			return mainApplication.getCurrentActivity();
		}

		return null;
	}
}
