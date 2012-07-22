package org.teameos.apps.cameralauncher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

public class CameraLauncherActivity extends Activity{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent cameraIntent = new Intent("android.intent.action.MAIN");
        cameraIntent.setComponent(new ComponentName("com.google.android.gallery3d",
                "com.android.camera.Camera"));
        startActivity(cameraIntent);
        finish();
    };
}
