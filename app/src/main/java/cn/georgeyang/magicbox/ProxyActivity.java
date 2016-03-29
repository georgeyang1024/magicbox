package cn.georgeyang.magicbox;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import cn.georgeyang.lib.AnimType;
import cn.georgeyang.lib.FragmentOnlySupportActivity;
import cn.georgeyang.lib.PluginResources;
import cn.georgeyang.loader.AssetUtils;
import cn.georgeyang.loader.PlugClassLoder;

/**
 * Created by george.yang on 2016-3-29.
 */
public class ProxyActivity extends FragmentOnlySupportActivity {
    private PluginData mPluginData = null;
    private String packageName = null,action = null;
    private static final HashMap<String, PluginData> pluginChahe = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent==null) {
            Toast.makeText(this,"未指定插件名或動作",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Uri uri = intent.getData();
        if (uri == null) {
            return;
        }
        packageName = uri.getQueryParameter("packageName");
        action = uri.getQueryParameter("action");
        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(action)) {
            Toast.makeText(this,"未指定插件名或動作",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        String pluginPath = "";
        try {
            pluginPath = AssetUtils.copyAsset(this,packageName + ".apk", getFilesDir());

            FrameLayout rootView = new FrameLayout(this);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            rootView.setBackgroundColor(Color.GRAY);
            rootView.setId(android.R.id.content);
            setContentView(rootView);

            initWithApkPathAndPackName(pluginPath,packageName);



            rootView.postDelayed(new Runnable() {
                @Override
                public void run() {

                    try {
                        Class pluginActivityClass = mPluginData.classLoder.loadClass(String.format("%s.%s",new Object[]{packageName,action}));
                        Constructor<?> localConstructor = pluginActivityClass.getConstructor(new Class[] {});
                        Fragment fragment = (Fragment) localConstructor.newInstance();

                        loadFragment(fragment);


                    } catch (Exception e) {

                    }

                }
            },2000);


//            FragmentTransaction ft =  getFragmentManager().beginTransaction();
//            ft.add(android.R.id.background,fragment,"main");
//            ft.commit();

        } catch (Exception e) {
            Log.d("demo",Log.getStackTraceString(e).toString());
            e.printStackTrace();
        }
    }


    private void initWithApkPathAndPackName(String pluginPath,String packageName) throws Exception {
        PluginData pluginData = pluginChahe.get(packageName);
        if (pluginData!=null) {
            mPluginData = pluginData;
        } else {
                mPluginData  = new PluginData();
                mPluginData.parentContext = ProxyActivity.this.getBaseContext();


                Class<?> cls = Class
                        .forName("com.android.internal.policy.PolicyManager");
                Method m = cls.getMethod("makeNewLayoutInflater",
                        Context.class);
                mPluginData.layoutInflater = (LayoutInflater) m.invoke(null, this);


                File outFile = new File(pluginPath);
                AssetManager assetManager = AssetManager.class.newInstance();
                Method addAssetPath = assetManager.getClass().getMethod(
                        "addAssetPath", String.class);
                addAssetPath.invoke(assetManager, outFile.getPath());
                mPluginData.assetManager = assetManager;


                Resources superRes = super.getResources();
                PluginResources resources = new PluginResources(assetManager, superRes);
                mPluginData.resources = resources;


                Resources.Theme theme = super.getResources().newTheme();
                theme.applyStyle(android.R.style.Theme_Light,true);
                mPluginData.theme = theme;

                PlugClassLoder loader = new PlugClassLoder(pluginPath,getCacheDir().getAbsolutePath(),null,getClass().getClassLoader());
                mPluginData.classLoder = loader;

            pluginChahe.put(packageName,mPluginData);
        }
    }

    /**
     * 创建一个当前类的布局加载器，用于专门加载插件资源
     */
    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mPluginData==null || mPluginData.layoutInflater==null) {
                return super.getSystemService(name);
            } else {
                return mPluginData.layoutInflater;
            }
        }
        return super.getSystemService(name);
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        if (!(mPluginData==null || mPluginData.layoutInflater==null)) {
            return mPluginData.layoutInflater;
        }
        return super.getLayoutInflater();
    }

    @Override
    public AssetManager getAssets() {
        if (!(mPluginData==null || mPluginData.assetManager==null)) {
            return mPluginData.assetManager;
        }
        return super.getAssets();
    }

    @Override
    public Resources getResources() {
        if (!(mPluginData==null || mPluginData.resources==null)) {
            return mPluginData.resources;
        }
        return super.getResources();
    }

    @Override
    public ClassLoader getClassLoader() {
        if (!(mPluginData==null || mPluginData.classLoder==null)) {
            return mPluginData.classLoder;
        }
        return super.getClassLoader();
    }

    @Override
    public Resources.Theme getTheme() {
        if (!(mPluginData==null || mPluginData.theme==null)) {
            return mPluginData.theme;
        }
        return super.getTheme();
    }

    @Override
    public String getPackageName() {
        return packageName==null?super.getPackageName():packageName;
    }

    @Override
    public int getContainerId() {
        return android.R.id.content;
    }
}
