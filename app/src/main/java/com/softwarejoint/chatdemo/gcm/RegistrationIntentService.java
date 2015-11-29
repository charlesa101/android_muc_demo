package com.softwarejoint.chatdemo.gcm;

/**
 * Created by pankajsoni on 8/26/15.
 */

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.softwarejoint.chatdemo.AppPrefs.AppPreferences;
import com.softwarejoint.chatdemo.MainApplication;
import com.softwarejoint.chatdemo.constant.AppConstant;
import com.softwarejoint.chatdemo.utils.AppUtils;

import java.util.Timer;
import java.util.TimerTask;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = "not_found";
            //String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i(TAG, "GCM Registration Token: " + token);

            final AppPreferences mPrefs = AppPreferences.getInstance(this);

            if (AppUtils.isConnectingToInternet(getApplicationContext())
                    && mPrefs.getRegitrationState() == AppConstant.ACCOUNT_STATE_PROFILE_SET
                    && (mPrefs.getPushSubscriptionToken() == null
                        || !mPrefs.getPushSubscriptionToken().equalsIgnoreCase(token))) {

                //TODO: send push token to server
                mPrefs.setPushSubscriptionToken(token);
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(MainApplication.getInstance() != null
                            && AppUtils.isConnectingToInternet(MainApplication.getInstance())){
                        startService(new Intent(MainApplication.getInstance(), RegistrationIntentService.class));
                    }
                }
            }, 10000);
        }
    }
}