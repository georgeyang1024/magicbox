package cn.georgeyang.magicbox;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.jiajixin.nuwa.util.AssetUtils;
import ctrip.android.bundle.loader.BundlePathLoader;

/**
 * Created by george.yang on 2016-3-24.
 */
public class App extends Application {
    private static final String TAG = "nuwa";

    private static final String DEX_DIR = "nuwa";
    private static final String DEX_OPT_DIR = "nuwaopt";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        Log.e(TAG, "copy " +        this.getClass().getSuperclass().getName());


//        Nuwa.loadPatch(this, Environment.getExternalStorageDirectory().getAbsolutePath().concat("/AntilazyLoad_dex.jar.jar"));
//        Nuwa.loadPatch(this,"AntilazyLoad_dex.jar");


        File dexDir = new File(base.getFilesDir(), DEX_DIR);
        dexDir.mkdir();

        String dexPath = "AntilazyLoad_dex.jar";
        try {
            dexPath = AssetUtils.copyAsset(base, dexPath, dexDir);
        } catch (IOException e) {
            Log.e(TAG, "copy " + dexPath + " failed:" + e.getMessage());
            e.printStackTrace();
        }

        String fixPath = "debug.apk";
        try {
            fixPath = AssetUtils.copyAsset(base, fixPath, dexDir);
        } catch (IOException e) {
            Log.e(TAG, "copy " + fixPath + " failed:" + e.getMessage());
            e.printStackTrace();
        }

        List<File> files = new ArrayList<>();
        files.add(new File(fixPath));
        files.add(new File(dexPath));
        try {
            Log.e(TAG, "start load");
            BundlePathLoader.installBundleDexs(getClassLoader(),dexDir,files,true);
            Log.e(TAG, "load success");
        } catch (Exception e) {
            Log.e(TAG, "load error:" + e.getLocalizedMessage());
            e.printStackTrace();
        }






    }

}
