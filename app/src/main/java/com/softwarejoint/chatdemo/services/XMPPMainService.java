package com.softwarejoint.chatdemo.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.softwarejoint.chatdemo.AppPrefs.AppPreferences;
import com.softwarejoint.chatdemo.MainApplication;
import com.softwarejoint.chatdemo.constant.AppConstant;
import com.softwarejoint.chatdemo.utils.AppUtils;
import com.softwarejoint.chatdemo.xmpp.XMPPManager;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

/**
 * @author Pankaj Soni <pankajsoni@softwarejoint.com>
 *     This is a xmpp connection service use to keep the xmpp
 *         connection alive through out so that user can chat any time he needs
 */
public class XMPPMainService extends Service {

    private static final int RECONNECT_TRY_INTERVAL_MS = 10000; // 10 Seconds

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(getClass().getName(), "On create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("connectToService", "9" + MainApplication.getInstance().isForeGround());
        AppPreferences appPreferences = MainApplication.getInstance().getAppPreferences();
        if (appPreferences.getRegitrationState() == AppConstant.ACCOUNT_STATE_PROFILE_SET
                && AppUtils.isConnectingToInternet(getApplicationContext())
                && MainApplication.getInstance().isForeGround()) {
            Log.i(getClass().getName(), "onStartCommand 2");

            new XMPPConnectionDelayedCheckAndStartThread().start();
        }
        return Service.START_STICKY;
    }

    class XMPPConnectionDelayedCheckAndStartThread extends Thread{
        @Override
        public void run() {
            super.run();
            XMPPManager sXmppManager = MainApplication.getInstance().getXMPPManager();
            XMPPTCPConnection sXmppConnection = sXmppManager.getConnection();
            if (sXmppConnection != null && !sXmppConnection.isConnected()) {
                try {
                    sXmppConnection.connect();
                    sXmppConnection.login();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            setAlarmToReconnect();
        }
    }

    private void setAlarmToReconnect() {
        MainApplication mainApplication = MainApplication.getInstance();
        if (mainApplication != null
                && AppUtils.isConnectingToInternet(getApplicationContext())
                && mainApplication.getAppPreferences().getRegitrationState() == AppConstant.ACCOUNT_STATE_PROFILE_SET) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                            + RECONNECT_TRY_INTERVAL_MS,
                    makeSelfPendingIntent(getApplicationContext()));
        }
    }

    private PendingIntent makeSelfPendingIntent(Context context) {
        PendingIntent intent = PendingIntent.getService(context, 0,
                makeSelfIntentToConnect(context), 0);
        return intent;
    }

    private Intent makeSelfIntentToConnect(Context context) {
        Intent intent = new Intent(context, XMPPMainService.class);
        return intent;
    }
}
