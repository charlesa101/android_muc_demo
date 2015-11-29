package com.softwarejoint.chatdemo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.softwarejoint.chatdemo.BaseActivity;
import com.softwarejoint.chatdemo.MainApplication;
import com.softwarejoint.chatdemo.constant.AppConstant;
import com.softwarejoint.chatdemo.constant.UtilField;

/**
 * Created by soni on 28-08-2015.
 */
public class MessageEventsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String event = intent.getStringExtra(AppConstant.RECEIVER_EVENT);
        String username = intent.getStringExtra(AppConstant.USERID);
        UtilField.XMPP_MESSAGE_TYPE messageType = (UtilField.XMPP_MESSAGE_TYPE) intent.getSerializableExtra(AppConstant.MSG_TYPE);
        String data = intent.getStringExtra(AppConstant.MSG_DATA);

        BaseActivity baseActivity = MainApplication.getInstance().getCurrentActivity();

        // TODO: notify current activity
    }
}
