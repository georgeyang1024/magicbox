package online.magicbox.desktop;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import online.magicbox.lib.PluginActivity;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        PluginActivity.init("online.magicbox.desktop", "magicbox", "2");
        Intent intent = PluginActivity.buildIntent(this, MainSlice.class);
        startActivity(intent);

//        Toast.makeText(this,"请用魔盒app安装打开!",Toast.LENGTH_LONG).show();
        finish();


//        //检查权限
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.CALL_PHONE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.CALL_PHONE)) {
//                // No explanation needed, we can request the permission.
//
//                ActivityCompat.requestPermissions(thisActivity,
//                        new String[]{Manifest.permission.READ_CONTACTS},
//                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//            }
//        }
    }
}
