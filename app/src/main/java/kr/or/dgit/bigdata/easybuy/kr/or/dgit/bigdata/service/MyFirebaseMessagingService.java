package kr.or.dgit.bigdata.easybuy.kr.or.dgit.bigdata.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import kr.or.dgit.bigdata.easybuy.R;
import kr.or.dgit.bigdata.easybuy.StartActivity;

/**
 * Created by KDH on 2018-04-27.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final static String TAG = "JSA-FCM";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channel_Id = "EasyBuy Channel";
            String channel_Name = "EasyBuy";
            String channel_Description = "EasyBuy Description";

            NotificationChannel channel = new NotificationChannel(channel_Id, channel_Name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channel_Description);

            manager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, channel_Id);
        }else{
            builder = new NotificationCompat.Builder(this);
        }

        builder.setSmallIcon(R.drawable.message_logo);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(remoteMessage.getNotification().getTitle());
        builder.setContentText(remoteMessage.getNotification().getBody());
        builder.setDefaults(Notification.DEFAULT_VIBRATE| Notification.DEFAULT_SOUND);
        builder.setColor(getResources().getColor(R.color.whiteTextColor));

        Intent intent = new Intent(this, StartActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        manager.notify(1004, builder.build());


    }
}
