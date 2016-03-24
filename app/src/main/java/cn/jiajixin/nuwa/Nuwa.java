package cn.jiajixin.nuwa;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

import cn.jiajixin.nuwa.util.AssetUtils;
import cn.jiajixin.nuwa.util.DexUtils;

/**
 * Created by jixin.jia on 15/10/31.
 */
public class Nuwa {
    private static final String TAG = "nuwa";

    private static final String DEX_DIR = "nuwa";
    private static final String DEX_OPT_DIR = "nuwaopt";


    public static void loadPatch(Context context, String dexPath) {
        if (context == null) {
            Log.e(TAG, "context is null");
            return;
        }

        if (TextUtils.isEmpty(dexPath)) {
            Log.e(TAG, dexPath + " is null");
            return;
        }

        if (!dexPath.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            File dexDir = new File(context.getFilesDir(), DEX_DIR);
            dexDir.mkdir();

            try {
                dexPath = AssetUtils.copyAsset(context, dexPath, dexDir);
            } catch (IOException e) {
                Log.e(TAG, "copy " + dexPath + " failed:" + e.getMessage());
                e.printStackTrace();
            }
        }

        if (!new File(dexPath).exists()) {
            Log.e(TAG, dexPath + " is not exist");
            return;
        }
        File dexOptDir = new File(context.getFilesDir(), DEX_OPT_DIR);
        dexOptDir.mkdir();
        try {
            DexUtils.injectDexAtFirst(dexPath, dexOptDir.getAbsolutePath());
            Log.i(TAG, "inject " + dexPath + " success!");
        } catch (Exception e) {
            Log.e(TAG, "inject " + dexPath + " failed:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
