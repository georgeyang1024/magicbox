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

import cn.jpush.android.api.JPushInterface;
import online.magicbox.bugfix.AssetUtils;
import online.magicbox.bugfix.BundlePathLoader;

/**
 * Created by george.yang on 2016-3-24.
 */
public class App extends Application {
//    public static final String defaultApkName = "online.magicbox.desktop_1.apk";//默认桌面

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        try {
            List<File> dexFiles = new ArrayList<>();

            String baseDexPath = "AntilazyLoad_dex.jar";
            baseDexPath = AssetUtils.copyAsset(base, baseDexPath, base.getFilesDir().getAbsoluteFile());
            dexFiles.add(new File(baseDexPath));

//            AssetUtils.copyAsset(base, defaultApkName, base.getFilesDir().getAbsoluteFile());

            SharedPreferences sp = getSharedPreferences("app", Context.MODE_PRIVATE);
            baseDexPath = sp.getString("hotfixDex","bugfix.apk");//默认的bugfixAPK
            Log.d("test","bug fix file:" + baseDexPath);
            if (!TextUtils.isEmpty(baseDexPath)) {
                baseDexPath = AssetUtils.copyAsset(base, baseDexPath, base.getFilesDir().getAbsoluteFile());
                dexFiles.add(new File(baseDexPath));
            }

            BundlePathLoader.installBundleDexs(this,getClassLoader(),base.getCacheDir().getAbsoluteFile(),dexFiles,"AntilazyLoad",true);

            Log.d("bugfix","app had loaded!!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("bugfix",Log.getStackTraceString(e));
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //如果直接使用下面代码，出现:java.lang.IllegalAccessError: Class ref in pre-verified class resolved to unexpected implementation
//        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
//        JPushInterface.init(this);     		// 初始化 JPush

        //使用Class.forName加载
        try {
            Class jpushClass = Class.forName("cn.jpush.android.api.JPushInterface");
            Method initMethod = jpushClass.getMethod("init",new Class[]{Context.class});
            initMethod.invoke(null,new Object[]{this});

            Method setDebugModeMethod = jpushClass.getMethod("setDebugMode",new Class[]{boolean.class});
            setDebugModeMethod.invoke(null,new Object[]{false});

            Log.d("bugfix","success");
        } catch (Exception e) {
            Log.d("bugfix",Log.getStackTraceString(e));
            e.printStackTrace();
        }

        try {
            Class crashClass = Class.forName("online.magicbox.app.CrashHandler");
            Method getInstance = crashClass.getMethod("getInstance",new Class[]{});
            Object instance = getInstance.invoke(null,new Object[]{});
            Method initMethod = crashClass.getMethod("init",new Class[]{Context.class});
            initMethod.invoke(instance,new Object[]{this});

            Log.d("bugfix","success2");
        } catch (Exception e) {
            Log.d("bugfix",Log.getStackTraceString(e));
            e.printStackTrace();
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
        Log.i("bugfix","getAssets!");
        return assetManager;
    }
}
