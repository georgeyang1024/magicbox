package cn.georgeyang.magicbox.lib;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
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

    @Override
    public Resources getResources() {
        if (mProxyActivity!=null) {
            return mProxyActivity.getResources();
        }
        return super.getResources();
    }

//
    @Override
    public AssetManager getAssets() {
        if (mProxyActivity!=null) {
            return mProxyActivity.getAssets();
        }
        return super.getAssets();
    }

//    @Override
//    public ClassLoader getClassLoader() {
//        if (mProxyActivity!=null) {
//            return mProxyActivity.getClassLoader();
//        }
//        return pluginProxyContext==null?super.getClassLoader():pluginProxyContext.getClassLoader();
//    }
//
    @Override
    public Resources.Theme getTheme() {
        if (mProxyActivity!=null) {
            return mProxyActivity.getTheme();
        }
        return super.getTheme();
    }

    @Override
    public Object getSystemService(String name) {
        if (mProxyActivity!=null) {
            return mProxyActivity.getSystemService(name);
        }
        return super.getSystemService(name);

//        return pluginProxyContext==null?super.getSystemService(name):pluginProxyContext.getSystemService(name);
    }

//    public ProxyActivity() {
//        try {
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.i("demo", "load activity error:"+ Log.getStackTraceString(e));
//        }
//    }
}


