package com.softwarejoint.chatdemo;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;

import com.softwarejoint.chatdemo.AppPrefs.AppPreferences;
import com.softwarejoint.chatdemo.DBHelper.AppDbHelper;
import com.softwarejoint.chatdemo.constant.AppConstant;
import com.softwarejoint.chatdemo.gcm.RegistrationIntentService;
import com.softwarejoint.chatdemo.utils.AppUtils;
import com.softwarejoint.chatdemo.xmpp.XMPPManager;

public class MainApplication extends Application {

    private static MainApplication sInstance;
    private AppPreferences mPrefs;
    public static Handler appHandler;
    private AppDbHelper mAppDBHelper;
    private BaseActivity currentActivity;

    public boolean isForeGround() {
        return (currentActivity != null
                && !currentActivity.isFinishing());
    }

    public void onCreate() {
        sInstance = this;
        appHandler = new Handler(sInstance.getMainLooper());
        mPrefs = AppPreferences.getInstance(getApplicationContext());

        if(mPrefs.getRegitrationState() == AppConstant.ACCOUNT_STATE_PROFILE_SET){

            if(AppUtils.isConnectingToInternet(this)){
                startService(new Intent(this,RegistrationIntentService.class));
            }

            mAppDBHelper = AppDbHelper.getInstance(getApplicationContext());
        }
    }

    public synchronized static MainApplication getInstance() {
        return sInstance;
    }

    public AppPreferences getAppPreferences() {
        if(mPrefs == null){
            mPrefs = AppPreferences.getInstance(getApplicationContext());
        }
        return mPrefs;
    }

    public AppDbHelper getAppDBHelper(){
        if(mAppDBHelper == null){
            mAppDBHelper = AppDbHelper.getInstance(getApplicationContext());
        }
        return mAppDBHelper;
    }

    public XMPPManager getXMPPManager(){
        return XMPPManager.getInstance(getApplicationContext());
    }

    public void setCurrentActivity(BaseActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public BaseActivity getCurrentActivity() {
        return currentActivity;
    }
}
