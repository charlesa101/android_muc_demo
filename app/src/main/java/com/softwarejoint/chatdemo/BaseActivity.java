package com.softwarejoint.chatdemo;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

public abstract class BaseActivity extends FragmentActivity {

	private String TAG;

    protected void onResume() {
        super.onResume();
        MainApplication.getInstance().setCurrentActivity(this);
	    TAG = this.getClass().getName();
    }

    protected void onPause() {
        clearReferences();
        super.onPause();
    }
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currActivity = MainApplication.getInstance().getCurrentActivity();
        if (this.equals(currActivity)){
            MainApplication.getInstance().setCurrentActivity(null);
        }
    }

	public void onAuthenticated(){}

	public void onError(Exception e){}

	public void onInvitationReceivedForMuc(MultiUserChat room, String inviter,
	                                       String reason, String password, Message message) {
		Log.i(TAG, "invitations received for group: " + room.getRoom() + " by : " + inviter);
	}

	public boolean isShowNotification(String groupId, String userId)
	{
		return true;
	}
}
