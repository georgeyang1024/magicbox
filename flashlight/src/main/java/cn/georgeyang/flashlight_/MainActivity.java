package cn.georgeyang.flashlight_;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import cn.georgeyang.flashlight.MainSlice;
import online.magicbox.lib.PluginActivity;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PluginActivity.init("cn.georgeyang.flashlight","magicbox");
        Intent intent = PluginActivity.buildIntent(this,MainSlice.class);
        startActivity(intent);

        finish();
    }
}
