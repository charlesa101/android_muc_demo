package com.softwarejoint.chatdemo.gcm;

/**
 * Created by pankajsoni on 8/26/15.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.softwarejoint.chatdemo.MainApplication;
import com.softwarejoint.chatdemo.constant.AppConstant;

import com.softwarejoint.chatdemo.R;

public class MyGcmListenerService extends GcmListenerService {

    public static final int REQUEST_CODE = 11101;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        // send notification

    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(Class<?> cls, String userId, String message) {
        MyGcmListenerService.sendNotification(getApplicationContext(), cls, userId, message);
    }

    public static void sendNotification(Context context, Class<?> cls, String userId, String message){
        if(userId.equalsIgnoreCase(MainApplication.getInstance().getAppPreferences().getUserName())){
            return;
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setContentTitle(context.getResources().getString(R.string.app_name))

                .setAutoCancel(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        }else{
            notificationBuilder.setContentText(message);
        }

        if(!MainApplication.getInstance().isForeGround()){
            Intent intent = new Intent(context, cls);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(AppConstant.IS_NOTIFICATION, AppConstant.IS_NOTIFICATION);
            intent.putExtra(AppConstant.USERID, userId);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, MyGcmListenerService.REQUEST_CODE, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            notificationBuilder.setContentIntent(pendingIntent);
        }

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
        {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationBuilder.setSound(defaultSoundUri);
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
        }else if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}