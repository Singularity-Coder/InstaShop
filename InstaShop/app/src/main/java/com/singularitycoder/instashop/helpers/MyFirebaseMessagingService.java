package com.singularitycoder.instashop.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.singularitycoder.instashop.R;

import java.io.IOException;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @NonNull
    private static final String TAG = "MyFirebaseMessagingServ";

    @Nullable
    HelperSharedPreference helperSharedPreference;

    // todo send image message to show ads in home viewpager
    // todo firebase phone auth
    // todo firebase email auth

    @Override
    public void onNewToken(String newToken) {
        super.onNewToken(newToken);

        Log.d(TAG, "onNewToken: " + newToken);
        helperSharedPreference = HelperSharedPreference.getInstance(this);
        helperSharedPreference.setFcmToken(newToken);

        // On initial startup of your app, the FCM SDK generates a registration token for the client app instance. If you want to target single devices, or create device groups, you'll need to access this token.
        // Get updated InstanceID token.
        String refreshedToken = null;
        try {
            refreshedToken = FirebaseInstanceId.getInstance().getToken("", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (null == remoteMessage) return;
        // todo Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());


            // todo Handle FCM messages here.
            // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
            Log.d(TAG, "From: " + remoteMessage.getFrom());

            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());

                if (/* Check if data needs to be processed by long running job */ true) {
                    // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                    scheduleJob();
                } else {
                    // Handle message within 10 seconds
//                    handleNow();
                }

            }

            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            }

            // Also if you intend on generating your own notifications as a result of a received FCM
            // message, here is where that should be initiated. See sendNotification method below.

        checkNotificationInMessage(remoteMessage);
    }

    private void checkNotificationInMessage(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            Map<String, String> data = remoteMessage.getData();
            if (data.containsKey("title") && data.containsKey("message")) {
                showNotification(data.get("title"), data.get("message"));
            }
        }
    }

    private void showNotification(final String title, final String message) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "channelId";
            final int id = 0;
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setChannelId(CHANNEL_ID);
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Channel Title", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (null != notificationManager) {
                notificationManager.createNotificationChannel(notificationChannel);
                notificationManager.notify(id, mBuilder.build());
            }
        } else {
            // Android 4 to Android 7
            final int id = 0;
            Notification.Builder mBuilder =
                    new Notification.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(title)
                            .setContentText(message);
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (null != notificationManager) {
                notificationManager.notify(id, mBuilder.build());
            }
        }
    }
}