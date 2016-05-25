package cn.georgeyang.csdnblog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import online.magicbox.lib.PluginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        PluginActivity.init("cn.georgeyang.csdnblog","magicbox","6");
        Intent intent = PluginActivity.buildIntent(this, MainSlice.class);
        startActivity(intent);

//        Toast.makeText(this,"请用魔盒app安装打开!",Toast.LENGTH_LONG).show();

        finish();
    }
}
