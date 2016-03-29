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
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        try {
            List<File> dexFiles = new ArrayList<>();

            String baseDexPath = "AntilazyLoad_dex.jar";
            baseDexPath = AssetUtils.copyAsset(base, baseDexPath, base.getFilesDir().getAbsoluteFile());
            dexFiles.add(new File(baseDexPath));

            BundlePathLoader.installBundleDexs(this,getClassLoader(),base.getCacheDir().getAbsoluteFile(),dexFiles,"AntilazyLoad",true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
