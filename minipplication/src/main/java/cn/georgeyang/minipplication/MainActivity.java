package cn.georgeyang.minipplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        SwipeRefreshLayout
        ProxyActivity.init("cn.magicbox.plugin","magicbox");
        Intent intent = ProxyActivity.buildIntent(MainFragment.class);
        startActivity(intent);

        finish();
    }
}
