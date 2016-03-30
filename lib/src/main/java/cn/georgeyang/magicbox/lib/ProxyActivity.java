package cn.georgeyang.magicbox.lib;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;


/**
 * Created by george.yang on 2016-3-30.
 */
public class ProxyActivity extends Activity {
    private String packageName,animType=null,className = null,version=null;
    private Object viewController;

    private static String ACTION = "cn.magicbox.plugin";
    private static String SCHEME = "magicbox";

    public static void init (String action,String scheme) {
        ProxyActivity.ACTION = action;
        ProxyActivity.SCHEME = scheme;
    }

    public static Intent buildIntent(Class clazz) {
        return buildIntent(clazz.getPackage().getName(),clazz.getSimpleName(),null);
    }

    public static Intent buildIntent(Class clazz, Map<String,String> params) {
        return buildIntent(clazz.getPackage().getName(),clazz.getSimpleName(),params);
    }

    public static Intent buildIntent(String packageName,String className, Map<String,String> params) {
        Uri.Builder builder = new Uri.Builder().scheme(SCHEME).path(packageName+"." + className);
        String animType = (params==null || !params.containsKey("animType"))?PluginConfig.System:params.get("animType");
        String version = (params==null || !params.containsKey("version"))?PluginConfig.pluginVersion:params.get("version");
        builder.appendQueryParameter("animType",animType);
        builder.appendQueryParameter("version",version);
        if (params!=null) {
            for (String key:params.keySet()) {
                builder.appendQueryParameter(key,params.get(key));
            }
        }
        Uri uri = builder.build();
        Intent intent = new Intent(ACTION);
        intent.setData(uri);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Intent intent = getIntent();
            if (intent==null || intent.getData()==null) {
                Toast.makeText(this,"缺少参数",Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Uri uri = intent.getData();

            String path = uri.getPath();
            packageName = path.substring(1,path.lastIndexOf('.'));
            Log.i("test","packageName:" + packageName);
            if (TextUtils.isEmpty(packageName)) {
                Toast.makeText(this,"未指定插件名",Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            className = path.substring(path.lastIndexOf('.')+1,path.length());
            Log.i("test","className:" + className);
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

//            String pluginPath = AssetUtils.copyAsset(this,String.format("%s_%s.apk",new Object[]{packageName,version}), getFilesDir());
////            initWithApkPathAndPackName(pluginPath,packageName);
//            proxyContext = new PluginProxyContext(this);
//            proxyContext.loadResources(pluginPath,packageName);

            Class pluginActivityClass = this.getClassLoader().loadClass(String.format("%s.%s",new Object[]{packageName,className}));
            Constructor<?> localConstructor = pluginActivityClass.getConstructor(new Class[] {});
            viewController = localConstructor.newInstance();

//            Class pluginActivityClass = Class.forName(packageName + "." + className);
//            fragment = (Fragment) pluginActivityClass.newInstance();

            Method method = viewController.getClass().getMethod("setPluginContext",new Class[]{Context.class});
            method.invoke(viewController,new Object[]{this});
        } catch (Exception e) {
            Toast.makeText(this,"加载失败:" + e.getMessage(),Toast.LENGTH_SHORT).show();
            Log.d("demo",Log.getStackTraceString(e).toString());
            e.printStackTrace();

            finish();
        }

        Log.d("demo","animType:" + animType);

//        loadAnim(false);

        super.onCreate(savedInstanceState);

//        allActivity.add(this);

//        FrameLayout rootView = new FrameLayout(this);
//        rootView.setLayoutParams(new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT));
//        rootView.setBackgroundColor(Color.GRAY);
//        rootView.setId(android.R.id.content);


        try {
            Method method = viewController.getClass().getMethod("createView",new Class[]{Context.class});
            View rootView = (View) method.invoke(viewController,new Object[]{this});

            setContentView(rootView);
        } catch (Exception e) {
            Toast.makeText(this,"加载失败:" + e.getMessage(),Toast.LENGTH_SHORT).show();
            Log.d("demo",Log.getStackTraceString(e).toString());
            e.printStackTrace();

            finish();
        }


//        viewController
    }
}
