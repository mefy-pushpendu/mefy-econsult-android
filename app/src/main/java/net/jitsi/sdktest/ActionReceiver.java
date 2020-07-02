package net.jitsi.sdktest;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

import static net.jitsi.sdktest.app.getApplicationCntx;

public class ActionReceiver extends BroadcastReceiver {

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
    }


}