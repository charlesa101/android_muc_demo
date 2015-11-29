package com.softwarejoint.chatdemo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.softwarejoint.chatdemo.AppPrefs.AppPreferences;
import com.softwarejoint.chatdemo.constant.AppConstant;
import com.softwarejoint.chatdemo.services.XMPPMainService;
import com.softwarejoint.chatdemo.utils.AppUtils;
import com.softwarejoint.chatdemo.xmpp.XMPPManager;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            if (AppUtils.isConnectingToInternet(context)) {
                AppPreferences appPreferences = AppPreferences.getInstance(context);
                if (appPreferences.getRegitrationState() == AppConstant.ACCOUNT_STATE_PROFILE_SET) {
                    context.startService(new Intent(context, XMPPMainService.class));
                }
            } else {
                // stop service
                XMPPManager.getInstance(context.getApplicationContext()).cleanUpConnection();
                context.stopService(new Intent(context, XMPPMainService.class));
            }
        }
    }

}
