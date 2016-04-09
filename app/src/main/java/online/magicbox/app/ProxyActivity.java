package online.magicbox.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Map;

import dalvik.system.DexClassLoader;

/**
 * Created by george.yang on 16/4/9.
 */
public class ProxyActivity extends Activity {
    private String packageName = null, animType = null, className = null, version = null;
    public Object mSlice;//切片
    private Context mContext;//

    private static String ACTION = "cn.magicbox.plugin";
    private static String SCHEME = "magicbox";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Intent intent = getIntent();
            if (intent == null || intent.getData() == null) {
                Toast.makeText(this, "缺少参数", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Uri uri = intent.getData();

            String path = uri.getPath();
            packageName = path.substring(1, path.lastIndexOf('.'));
            Log.i("test", "packageName:" + packageName);
            if (TextUtils.isEmpty(packageName)) {
                Toast.makeText(this, "未指定插件名", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            className = path.substring(path.lastIndexOf('.') + 1, path.length());
            Log.i("test", "className:" + className);
            if (TextUtils.isEmpty(className)) {
                className = "MainFragment";
            }

            version = uri.getQueryParameter("version");
            if (TextUtils.isEmpty(version)) {
                version = PluginConfig.pluginVersion;
            }

            animType = uri.getQueryParameter("animType");
            if (TextUtils.isEmpty(animType)) {
                animType = PluginConfig.System;
            }

            mContext = getPluginContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void replaceClassLoader(DexClassLoader loader){
        try {
            Class clazz_Ath = Class.forName("android.app.ActivityThread");
            Class clazz_LApk = Class.forName("android.app.LoadedApk");

            Object currentActivityThread = clazz_Ath.getMethod("currentActivityThread").invoke(null);
            Field field1 = clazz_Ath.getDeclaredField("mPackages");
            field1.setAccessible(true);
            Map mPackages = (Map)field1.get(currentActivityThread);

            String packageName = ProxyActivity.this.getPackageName();
            WeakReference ref = (WeakReference) mPackages.get(packageName);
            Field field2 = clazz_LApk.getDeclaredField("mClassLoader");
            field2.setAccessible(true);
            field2.set(ref.get(), loader);
        } catch (Exception e){
            System.out.println("-------------------------------------" + "click");
            e.printStackTrace();
        }
    }
}
