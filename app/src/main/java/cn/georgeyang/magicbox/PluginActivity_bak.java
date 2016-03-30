package cn.georgeyang.magicbox;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;

import java.lang.reflect.Method;
import java.util.HashMap;

import cn.georgeyang.loader.PlugClassLoder;

/**
 * Created by george.yang on 16/3/29.
 */
public abstract class PluginActivity_bak extends Activity {
    protected PluginData mPluginData = null;
    protected String packageName = null;
    protected static final HashMap<String, PluginData> pluginChahe = new HashMap<>();

    protected void initWithApkPathAndPackName(String pluginPath,String packageName) throws Exception {
        PluginData pluginData = pluginChahe.get(packageName);
        if (pluginData!=null) {
            mPluginData = pluginData;
        } else {
            mPluginData  = new PluginData();
            mPluginData.parentContext = this.getBaseContext();

            this.packageName = packageName;

            Class<?> cls = Class
                    .forName("com.android.internal.policy.PolicyManager");
            Method m = cls.getMethod("makeNewLayoutInflater",
                    Context.class);
            mPluginData.layoutInflater = (LayoutInflater) m.invoke(null, getApplicationContext());

            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod(
                    "addAssetPath", String.class);
            addAssetPath.invoke(assetManager, pluginPath);
            mPluginData.assetManager = assetManager;

            Resources superRes = super.getResources();
//                PluginResources resources = new PluginResources(assetManager, superRes);
            Resources resources = new Resources(assetManager, superRes.getDisplayMetrics(),superRes.getConfiguration());
            mPluginData.resources = resources;

            Resources.Theme theme = super.getResources().newTheme();
            theme.applyStyle(android.R.style.Theme_Light_NoTitleBar,true);
            mPluginData.theme = theme;

            PlugClassLoder loader = new PlugClassLoder(pluginPath,getCacheDir().getAbsolutePath(),null,getClass().getClassLoader());
            mPluginData.classLoder = loader;

            pluginChahe.put(packageName,mPluginData);
        }
    }

    private boolean usePluginResources = true;
    public void setUsePluginResources (boolean usePluginResources) {
//        this.usePluginResources = usePluginResources;
    }



    /**
     * 创建一个当前类的布局加载器，用于专门加载插件资源
     */
    @Override
    public Object getSystemService(String name) {
        Log.d("test","getSystemService:" + name);
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (!(mPluginData==null || mPluginData.layoutInflater==null || !usePluginResources)) {
                return mPluginData.layoutInflater;
            }
        }
        return super.getSystemService(name);
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        Log.d("test","getLayoutInflater!");
        if (!(mPluginData==null || mPluginData.layoutInflater==null || !usePluginResources)) {
            return mPluginData.layoutInflater;
        }
        return super.getLayoutInflater();
    }

    @Override
    public AssetManager getAssets() {
        if (!(mPluginData==null || mPluginData.assetManager==null || !usePluginResources)) {
            return mPluginData.assetManager;
        }
        return super.getAssets();
    }

    @Override
    public Resources getResources() {
        if (!(mPluginData==null || mPluginData.resources==null || !usePluginResources)) {
            return mPluginData.resources;
        }
        return super.getResources();
    }

    @Override
    public ClassLoader getClassLoader() {
        if (!(mPluginData==null || mPluginData.classLoder==null || !usePluginResources)) {
            return mPluginData.classLoder;
        }
        return super.getClassLoader();
    }

    @Override
    public Resources.Theme getTheme() {
        if (!(mPluginData==null || mPluginData.theme==null || !usePluginResources)) {
            return mPluginData.theme;
        }
        return super.getTheme();
    }

    @Override
    public String getPackageName() {
        return packageName==null?super.getPackageName():packageName;
    }
}
