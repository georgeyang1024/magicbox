package online.magicbox.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import online.magicbox.bugfix.AssetUtils;
import online.magicbox.bugfix.BundlePathLoader;

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

            SharedPreferences sp = getSharedPreferences("app", Context.MODE_PRIVATE);
            baseDexPath = sp.getString("hotfixDex","");
            Log.d("test","bug fix file:" + baseDexPath);
            if (!TextUtils.isEmpty(baseDexPath)) {
                baseDexPath = AssetUtils.copyAsset(base, baseDexPath, base.getFilesDir().getAbsoluteFile());
                dexFiles.add(new File(baseDexPath));
            }

            BundlePathLoader.installBundleDexs(this,getClassLoader(),base.getCacheDir().getAbsoluteFile(),dexFiles,"AntilazyLoad",true);

            Log.d("test","app had loaded!!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("test",Log.getStackTraceString(e));
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Class crashClass = Class.forName("online.magicbox.app.CrashHandler");
            Method getInstance = crashClass.getMethod("getInstance",new Class[]{});
            Object instance = getInstance.invoke(null,new Object[]{});
            Method initMethod = crashClass.getMethod("init",new Class[]{Context.class});
            initMethod.invoke(instance,new Object[]{this});
        } catch (Exception e) {

        }
    }

    @Override
    public AssetManager getAssets() {
        AssetManager assetManager = super.getAssets();
        try {
            Method addAssetPath = assetManager.getClass().getMethod(
                    "addAssetPath", String.class);
            addAssetPath.invoke(assetManager, "outFile");
        } catch (Exception e) {

        }
        Log.i("test","getAssets!");
        return assetManager;
    }
}
