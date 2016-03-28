package cn.georgeyang.magicbox.lib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by george.yang on 2016-3-28.
 */
public abstract class PlugActivity extends Activity {
    public Activity mProxyActivity;

    public abstract View createView (LayoutInflater inf, Bundle bundle);

    public void setProxy(Activity proxyActivity) {
        mProxyActivity = proxyActivity;
    }

    @Override
    public void finish() {
        if (mProxyActivity==null) {
            finish();
        } else {
            mProxyActivity.finish();
        }
    }

}
