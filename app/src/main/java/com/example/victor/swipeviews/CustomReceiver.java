package com.example.victor.swipeviews;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;



/**
 * Tova e class koito da obrabotva pristigashtite push notifications
 */
public class CustomReceiver extends ParsePushBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Vic", "Custom receiver onReceive started");
        if (intent == null) {
            Log.d("Vic", "onReceive started but intent is empty");
        } else {
            // izvlichame infoto
            try {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

                    String message = (String) json.get(ParseConstants.KEY_PUSH_MESSAGE);
                    String typeOfMessage = (String) json.get(ParseConstants.KEY_ACTION);
                    String title="";
                    String messageType ="";
                    if(typeOfMessage.equals(ParseConstants.TYPE_PUSH_KISS)) {
                        title = context.getString(R.string.title_receive_a_kiss_message);
                        messageType=ParseConstants.TYPE_PUSH_KISS;
                    } else if (typeOfMessage.equals(ParseConstants.TYPE_PUSH_MESSAGE)) {
                        title = context.getString(R.string.title_receive_a_message_message);
                        messageType = ParseConstants.TYPE_PUSH_MESSAGE;
                    }  else if (typeOfMessage.equals(ParseConstants.TYPE_PUSH_CALENDAR)) {
                        //tr da se dobavi imeto na choveka deto si e updatenal calendara
                        title = context.getString(R.string.title_update_calendar_notification);
                        messageType = ParseConstants.TYPE_PUSH_CALENDAR;
                    }
                showNotification(context, title, message, messageType);

            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    }
    private void showNotification(Context context, String title, String message, String messageType) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, Main.class), 0);
        //NotificationManager.notify vzima kato argument ID. Ako ID e edno i sashto notification-a
        //samo se updateva. Ako ID e razlichno se dobavia nov notification
        int notifyID;
        if(messageType.equals(ParseConstants.TYPE_PUSH_KISS)) {
             notifyID = 1;
        } else if (messageType.equals(ParseConstants.TYPE_PUSH_MESSAGE)) {
            notifyID = 2;
        } else {
            notifyID = 3;
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_action_person)
                        .setContentTitle(title)
                        .setContentText(message);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notifyID, mBuilder.build());


    }


}
