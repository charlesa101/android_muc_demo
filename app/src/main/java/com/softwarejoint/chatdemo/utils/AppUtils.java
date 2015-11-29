package com.softwarejoint.chatdemo.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;

import com.softwarejoint.chatdemo.MainApplication;
import com.softwarejoint.chatdemo.constant.AppConstant;
import com.softwarejoint.chatdemo.constant.UtilField;
import com.softwarejoint.chatdemo.xmpp.XMPPUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;


/**
 * Created by soni on 24-07-2015.
 */
public class AppUtils {

    //Check for connectivity
    public static boolean isConnectingToInternet(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static String getMsgTime(long msg_time) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(msg_time);  //here your time in miliseconds
        String hour = String.format("%02d", cl.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", cl.get(Calendar.MINUTE));
        String sec = String.format("%02d", cl.get(Calendar.SECOND));
        String time = hour + ":" + min;
        return time;
    }

    public static String getMsgDate(long msg_time) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(msg_time);  //here your time in miliseconds
        String date = "";

        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = monthNames[cl.get(Calendar.MONTH)];

        int day = cl.get(Calendar.DAY_OF_MONTH);
        int year = cl.get(Calendar.YEAR);

        int current_year = Calendar.getInstance().get(Calendar.YEAR);
        if (current_year == year) {
            date = month_name + " " + day;
        } else {
            date = month_name + " " + day + ", " + year;
        }

        return date;
    }

    public static void dialogCancelable(Dialog dialog) {
        dialog.setCancelable(false);
    }

    public static void vibrate() {
        Vibrator v = (Vibrator) MainApplication.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    public static boolean isValidNumber(String name) {
        return name.length() > 6 && name.length() < 20;
    }

    public static void showDialog(Dialog dialog) {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public static void dismissDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static String getUUID4() {
        return UUID.randomUUID().toString();
    }

    public static String getFormattedDateForReQuery(long timeStampInMilliSeconds) {

        DateFormat df = new SimpleDateFormat("yyyyy-MM-dd HH:mm:ssZ", Locale.getDefault());
        Date d = new Date(timeStampInMilliSeconds);
        return df.format(d);
    }

    public static String getFormattedDate(long timeStampInMilliSeconds) {

        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(timeStampInMilliSeconds);
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "Today " + getTime(smsTime.getTimeInMillis());
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday " + getTime(smsTime.getTimeInMillis());
        }

        return dateParse(timeStampInMilliSeconds);
    }

    public static String getTime(long date) {
        final String timeFormatString = "h:mm ";
        DateFormat df = new SimpleDateFormat(timeFormatString, Locale.getDefault());
        Date d = new Date(date);
        return df.format(d);
    }

    public static String dateParse(long timeStampInMilliSeconds) {

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy  HH:mm", Locale.getDefault());
        Date d = new Date(timeStampInMilliSeconds);

        return df.format(d);
    }

    public static long getLongDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getDefault());
            Date d = sdf.parse(date);
            return d.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

	//TODO: write reload for unread message count

//    public static void reloadUnReadMsgsData() {
//        reloadUnReadMsgsData(MainApplication.getInstance(), null, null, null);
//    }
//
//    public static void reloadUnReadMsgsData(Context context, String username,
//                                            XMPPUtils.XMPP_MESSAGE_TYPE msgType, String data) {
//        Intent intent = new Intent(AppConstant.RECEIVER_EVENT);
//        intent.putExtra(AppConstant.RECEIVER_EVENT, AppConstant.UNREAD_RECEIVER_EVENT);
//        intent.putExtra(AppConstant.USERID, username);
//        intent.putExtra(AppConstant.MSG_TYPE, msgType);
//        intent.putExtra(AppConstant.MSG_DATA, data);
//        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//    }

    public static String gettimeFromMili(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        return "" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }
}
