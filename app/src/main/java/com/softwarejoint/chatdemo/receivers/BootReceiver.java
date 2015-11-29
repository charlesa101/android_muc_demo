package com.softwarejoint.chatdemo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.softwarejoint.chatdemo.AppPrefs.AppPreferences;
import com.softwarejoint.chatdemo.constant.AppConstant;
import com.softwarejoint.chatdemo.services.XMPPMainService;
import com.softwarejoint.chatdemo.utils.AppUtils;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		AppPreferences appPreferences = AppPreferences.getInstance(context);
		if(appPreferences.getRegitrationState() == AppConstant.ACCOUNT_STATE_PROFILE_SET
				&& AppUtils.isConnectingToInternet(context))
            context.startService(new Intent(context, XMPPMainService.class));
}

}
