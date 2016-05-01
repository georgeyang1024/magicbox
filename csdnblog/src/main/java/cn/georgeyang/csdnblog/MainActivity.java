package cn.georgeyang.csdnblog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import online.magicbox.lib.PluginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        PluginActivity.init("cn.georgeyang.csdnblog","magicbox");
        Intent intent = PluginActivity.buildIntent(this, MainSlice.class);
        startActivity(intent);

        finish();
    }
}
