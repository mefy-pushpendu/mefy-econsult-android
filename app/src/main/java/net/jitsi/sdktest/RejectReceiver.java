package net.jitsi.sdktest;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static net.jitsi.sdktest.app.getApplicationCntx;

public class RejectReceiver extends BroadcastReceiver {

    Context context = getApplicationCntx();
    public String roomId = "";
    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            Ringtone r = RingtoneManager.getRingtone(getApplicationCntx(), notification);
            r.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Integer notifyId = intent.getIntExtra("notifyID",0);
        Log.d("NotificationID",notifyId.toString());
        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(notifyId);
        String action=intent.getStringExtra("action");
        roomId = intent.getStringExtra("doctorID");
        Log.d("Action on service",action);
        if(action.equals("accepted")){
            Log.d("MAKE CALL","==============================================");
            Intent videoCall = new Intent(context, VideoCall.class);
            videoCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            videoCall.putExtra("EXTRA_SESSION_ID", roomId);
            context.startActivity(videoCall);
//            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//            context.sendBroadcast(it);
//            Intent newintent = new Intent("IncomingCall");
            // You can also include some extra data.
//            newintent.putExtra("roomID", roomId);
//            LocalBroadcastManager.getInstance(context).sendBroadcast(newintent);
//            NotificationManager.;
        }
        else if(action.equals("rejected")){
            performAction2();

        }
        //This is used to close the notification tray
    }

    public void performAction1(){
        Log.d("Accepted","=========================");
    }

    public void performAction2(){
        Log.d("Rejected","=========================");
        rejectCall();
    }


    public void rejectCall() {
        try {

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationCntx());
            JSONObject caller = new JSONObject();
            try {
                caller.accumulate("_id", roomId);
                caller.accumulate("role", "doctor");

            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject receiver = new JSONObject();
            try {
                receiver.accumulate("_id", "5ea65976bacf51402fc32ff8");
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
                                Toast toast = Toast.makeText(getApplicationCntx(),
                                        "Call has been rejected",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            }else{
                                Toast toast = Toast.makeText(getApplicationCntx(),
                                        "Can not reject call",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } catch (JSONException e) {
                            // If an error occurs, this prints the error to the log
                            e.printStackTrace();
                        }
                    }, error -> {
                System.out.println("Error getting response------------------------");
                Toast toast = Toast.makeText(getApplicationCntx(),
                        "Can not reject call",
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

}