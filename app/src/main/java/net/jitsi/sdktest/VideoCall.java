package net.jitsi.sdktest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.se.omapi.Session;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.react.modules.core.PermissionListener;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetViewListener;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class VideoCall extends AppCompatActivity {

    public String roomId = "";
    public String doctorId = "5ec24dc94654ca56795b7e4c";
    public String individualId = "5ea65976bacf51402fc32ff8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        URL serverURL;
        try {
            serverURL = new URL("https://meet.jit.si/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }
        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .setWelcomePageEnabled(false)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);
        String sessionId = getIntent().getStringExtra("EXTRA_SESSION_ID");
        Log.d("SESSION_ID",sessionId);
        doctorId = sessionId;
        roomId = sessionId;
        joinRoom(sessionId);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                broadcastReceiver, new IntentFilter("NotificationIntent"));
        receiveCall();
    }

    private void joinRoom(String roomId) {
        JitsiMeetConferenceOptions options
                = new JitsiMeetConferenceOptions.Builder()
                .setRoom(roomId)
                .build();
        JitsiMeetActivity.launch(VideoCall.this, options);
        finish();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           String message = intent.getStringExtra("message");  //get the type of message from MyGcmListenerService 1 - lock or 0 -Unlock
           Log.d("InBroadcasr",message);
//            if(message.equals("disconnected")){
               Log.d("Disconnected","");
               finish();
//               JitsiMeetActivity.finish();
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Call has been ended. Please exit the Room",
                    Toast.LENGTH_SHORT);
            toast.show();
//           }
        }
    };


    public void receiveCall() {
        try {

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject caller = new JSONObject();
            try {
                caller.accumulate("_id", VideoCall.this.doctorId);
                caller.accumulate("role", "doctor");

            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject receiver = new JSONObject();
            try {
                receiver.accumulate("_id", VideoCall.this.individualId);
                receiver.accumulate("role", "individual");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject requestData = new JSONObject();
            try {
                requestData.accumulate("caller", caller);
                requestData.accumulate("receiver", receiver);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String url = "https://api.mefy.care/eConsult/acceptcall";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestData,
                    response -> {
                        try {
                            JSONObject obj = response;
                            Boolean msg = obj.getBoolean("error");
                            if(msg == false){
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Call has been received",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                                joinRoom(roomId);
                            }else{
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Can not receive call",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } catch (JSONException e) {
                            // If an error occurs, this prints the error to the log
                            e.printStackTrace();
                        }
                    }, error -> {
                System.out.println("Error getting response------------------------");
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Can not receive call",
                        Toast.LENGTH_SHORT);
                toast.show();
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestData.toString().getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rejectCall() {
        try {

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject caller = new JSONObject();
            try {
                caller.accumulate("_id", VideoCall.this.doctorId);
                caller.accumulate("role", "doctor");

            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject receiver = new JSONObject();
            try {
                receiver.accumulate("_id", VideoCall.this.individualId);
                receiver.accumulate("role", "individual");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject requestData = new JSONObject();
            try {
                requestData.accumulate("caller", caller);
                requestData.accumulate("receiver", receiver);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String url = "https://api.mefy.care/eConsult/rejectcall";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestData,
                    response -> {
                        try {
                            JSONObject obj = response;
                            Boolean msg = obj.getBoolean("error");
                            if(msg == false){
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Call has been rejected",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                                finish();
                            }else{
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Can not reject call",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                                finish();
                            }
                        } catch (JSONException e) {
                            // If an error occurs, this prints the error to the log
                            e.printStackTrace();
                        }
                    }, error -> {
                System.out.println("Error getting response------------------------");
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Can not reject call",
                        Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestData.toString().getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
