package cn.georgeyang.csdnblog.util;

import android.util.Log;

/**
 * Created by george.yang on 2016-4-6.
 */
public class Logutil {
    private static final boolean isDebug = true;
    private static final String TAG = "test";
    public static final void i (String tag,String content) {
        if (isDebug) {
            Log.i(tag,content);
        }
    }
    public static final void showlog (String content) {
        if (isDebug) {
            Log.i(TAG,content);
        }
    }
}
