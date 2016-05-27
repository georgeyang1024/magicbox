package online.magicbox.desktop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import online.magicbox.lib.PluginActivity;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        PluginActivity.init("online.magicbox.desktop","magicbox","2");
        Intent intent = PluginActivity.buildIntent(this,MainSlice.class);
        startActivity(intent);

//        Toast.makeText(this,"请用魔盒app安装打开!",Toast.LENGTH_LONG).show();

        finish();
    }
}
