package cn.georgeyang.magicbox.lib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by george.yang on 2016-3-30.
 */
public abstract class PluginBuilder  {
    private Context mContext;


    public final void setPluginContext(Context context) {
        this.mContext = context;
    }

    public final Context getContext() {
        return mContext;
    }

    public abstract View createView(Context context);

}
