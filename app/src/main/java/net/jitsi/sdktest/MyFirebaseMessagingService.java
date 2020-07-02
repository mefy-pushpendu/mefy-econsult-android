/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.jitsi.sdktest;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.jitsi.sdktest.CallingScreen;
import net.jitsi.sdktest.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static net.jitsi.sdktest.app.getApplicationCntx;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 * <p>
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 * <p>
 * <intent-filter>
 * <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Context context = getApplicationCntx();


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("msg", "onMessageReceived: " + remoteMessage.getData());
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Default";

        if (remoteMessage.getNotification() != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody()).setAutoCancel(true).setContentIntent(pendingIntent);
            ;
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }
            manager.notify(0, builder.build());
        }
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            try {
                Map data =  remoteMessage.getData();
                Object msg = data.get("msg");
                JSONObject caller = new JSONObject(data.get("caller").toString());
                String callerId = caller.getString("_id");
                Log.d("msg",msg.toString());
                if (msg.toString().equals("CALL_MADE")) {

                    getIndividual(callerId);

//                    KeyguardManager myKM = (KeyguardManager) getApplicationCntx().getSystemService(Context.KEYGUARD_SERVICE);
//                    if( myKM.inKeyguardRestrictedInputMode() ) {
//                        // it is locked
//                        getIndividual(callerId);
//                    } else {
//                        //it is not locked
//                        Intent dialogIntent = new Intent(context, CallingScreen.class);
//                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                        dialogIntent.addCategory(Intent.CATEGORY_VOICE);
////                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        getApplicationCntx().startActivity(dialogIntent);
//                        sendMessageToActivity(context, remoteMessage.getData().toString());
//                    }

                }else if(msg.toString().equals("CALL_REJECTED")){
                    sendMessageToMainActivity(context,remoteMessage.getData().toString());
                }else if(msg.toString().equals("CALL_ACCEPTED")){
                    sendMessageToMainActivity(context,remoteMessage.getData().toString());
                }else if(msg.toString().equals("CALL_DISCONNECTED")){
                    Log.d("In Logic",msg.toString());
                    sendMessageToActivity(context,"disconnected");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }


    private static void sendMessageToMainActivity(Context context, String msg) {
        Intent intent = new Intent("CallIntent");
        // You can also include some extra data.
        intent.putExtra("message", msg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private static void sendMessageToActivity(Context context, String msg) {
        Intent intent = new Intent("NotificationIntent");
        // You can also include some extra data.
        intent.putExtra("message", msg);
        Log.d("In Intent",msg.toString());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    private NotificationManager notifManager;
    @SuppressLint("WrongConstant")
    public void createNotification(String aMessage, String picture, String doctorID) {
        final int NOTIFY_ID = 1002;

        aMessage+=".Tap to receive call";


        // There are hardcoding only for show it's just strings
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String description = "my_package_first_channel"; // The user-visible description of the channel.

        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        if (notifManager == null) {
            notifManager =
                    (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setSound(ringtoneUri, new AudioAttributes.Builder()
                        // Setting the AudioAttributes is important as it identifies the purpose of your
                        // notification sound.
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build());
                mChannel.setLightColor(Color.GREEN);
                mChannel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, id);


//            Intent receiveCallPendingIntent = new Intent(getApplicationCntx(),ActionReceiver.class);
//            receiveCallPendingIntent.putExtra("action","accepted");
//            receiveCallPendingIntent.putExtra("doctorID",doctorID);
//            receiveCallPendingIntent.putExtra("notifyID",NOTIFY_ID);
//
//            Intent rejectCallPendingIntent = new Intent(getApplicationCntx(),RejectReceiver.class);
//            rejectCallPendingIntent.putExtra("action","rejected");
//            rejectCallPendingIntent.putExtra("doctorID",doctorID);
//            rejectCallPendingIntent.putExtra("notifyID",NOTIFY_ID);
//
//            PendingIntent rcvCallPendingIntent = PendingIntent.getBroadcast(context,1,receiveCallPendingIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//            PendingIntent rejCallPendingIntent = PendingIntent.getBroadcast(context,1,rejectCallPendingIntent,PendingIntent.FLAG_UPDATE_CURRENT);

//            try {
//                URL url = new URL(picture);
//                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            } catch(IOException e) {
//                System.out.println(e);
//            }



            Intent fullscreenintent = new Intent(Intent.ACTION_MAIN, null);
            fullscreenintent.putExtra("doctorID",doctorID);
            fullscreenintent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NEW_TASK);
            fullscreenintent.setClass(context, CallingScreen.class);
            PendingIntent fullscreenpendingintent = PendingIntent.getActivity(context, 1, fullscreenintent, 0);
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

            builder.setContentTitle(aMessage)  // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(this.getString(R.string.app_name))  // required
                    .setAutoCancel(true)
                    .setTimeoutAfter(30000)
                    .setSound(soundUri)
                    .setFullScreenIntent(fullscreenpendingintent, true);
        } else {

//            Intent receiveCallPendingIntent = new Intent(getApplicationCntx(),ActionReceiver.class);
//            receiveCallPendingIntent.putExtra("action","accepted");
//            receiveCallPendingIntent.putExtra("doctorID",doctorID);
//            receiveCallPendingIntent.putExtra("notifyID",NOTIFY_ID);
//            PendingIntent rcvCallPendingIntent = PendingIntent.getBroadcast(context,1,receiveCallPendingIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            builder = new NotificationCompat.Builder(this);

            Intent fullscreenintent = new Intent(Intent.ACTION_MAIN, null);
            fullscreenintent.putExtra("doctorID",doctorID);
            fullscreenintent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NEW_TASK);
            fullscreenintent.setClass(context, CallingScreen.class);
            PendingIntent fullscreenpendingintent = PendingIntent.getActivity(context, 1, fullscreenintent, 0);

            builder.setContentTitle(aMessage)                           // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(this.getString(R.string.app_name))  // required
                    .setAutoCancel(true)
                    .setTicker(aMessage)
                    .setFullScreenIntent(fullscreenpendingintent, true);
        }

        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }
    public void getIndividual(String individualID) {
        try {

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            final String url = "https://api.mefy.care/doctor/getDoctorDetails?_id="+individualID;
            RequestQueue queue = Volley.newRequestQueue(this);

// Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            try {
                                final JSONObject object = new JSONObject(response);
                                JSONObject user = object.getJSONObject("registrationDetails");
                                String username = user.getString("name");
                                String image = user.getString("profileImage");
                                final String ImagePath = "https://api.mefy.care/file/fileShow?fileId="+image+"&select=thumbnail";
                                createNotification("Dr. "+username+" is calling",ImagePath,individualID);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

// Add the request to the RequestQueue.
            queue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}