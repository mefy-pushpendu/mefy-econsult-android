package net.jitsi.sdktest;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.modules.core.PermissionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.View;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.jitsi.meet.sdk.JitsiMeetView;
import org.jitsi.meet.sdk.JitsiMeetViewListener;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class MyJitsiMeet extends FragmentActivity implements JitsiMeetActivityInterface, JitsiMeetViewListener {
    private JitsiMeetView view;

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JitsiMeetActivityDelegate.onActivityResult(
                this, requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        JitsiMeetActivityDelegate.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = new JitsiMeetView(this);
        JitsiMeetConferenceOptions options = getOptions();
        view.join(options);

        setContentView(view);
        view.setListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        view.dispose();
        view = null;

        JitsiMeetActivityDelegate.onHostDestroy(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        JitsiMeetActivityDelegate.onNewIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            final String[] permissions,
            final int[] grantResults) {
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();

        JitsiMeetActivityDelegate.onHostResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        JitsiMeetActivityDelegate.onHostPause(this);
    }

    @Override
    public void requestPermissions(String[] strings, int i, PermissionListener permissionListener) {

    }

    protected JitsiMeetConferenceOptions getOptions(){
        URL serverURL;
        try {
            serverURL = new URL("https://meet.mefy.care");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }
        JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
        userInfo.setDisplayName("user");


        return  new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .setWelcomePageEnabled(false)
                .setFeatureFlag("chat.enabled",false)
                .setVideoMuted(false)
                .setUserInfo(userInfo)
                .setSubject("SAMPLE ROOM")//Set call subject here. use to display phone number here.
                .setRoom("helloall")
                .build();

    }

    @Override
    public void onConferenceJoined(Map<String, Object> map) {

    }

    @Override
    public void onConferenceTerminated(Map<String, Object> map) {
        Log.d("SAMPLE","TERMINATED");
        finish();
    }

    @Override
    public void onConferenceWillJoin(Map<String, Object> map) {

    }
}
