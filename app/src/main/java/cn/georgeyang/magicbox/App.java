package cn.georgeyang.magicbox;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.georgeyang.loader.AssetUtils;
import cn.georgeyang.loader.BundlePathLoader;

/**
 * Created by george.yang on 2016-3-24.
 */
public class App extends Application {
    private static final String TAG = App.class.getName();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        File dexDir = new File(base.getFilesDir(), "dexOut");
        dexDir.mkdir();

        try {
            List<File> dexFiles = new ArrayList<>();

            String baseDexPath = "AntilazyLoad_dex.jar";
            baseDexPath = AssetUtils.copyAsset(base, baseDexPath, dexDir);
            dexFiles.add(new File(baseDexPath));


            try {
                String fixDexPath = "debug2.apk";
                fixDexPath = AssetUtils.copyAsset(base, fixDexPath, dexDir);
                dexFiles.add(new File(fixDexPath));
            } catch (Exception e) {
                Log.e(TAG, "copy failed:" + e.getMessage());
                e.printStackTrace();
            }

            Log.e(TAG, "start load");
            BundlePathLoader.installBundleDexs(this,getClassLoader(),dexDir,dexFiles,"AntilazyLoad",true);
            Log.e(TAG, "load success");
        } catch (Exception e) {
            Log.e(TAG, "load error:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

}
