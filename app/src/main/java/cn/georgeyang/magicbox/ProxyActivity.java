package cn.georgeyang.magicbox;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Vector;

import cn.georgeyang.loader.AssetUtils;
import cn.georgeyang.loader.PluginProxyContext;
import dalvik.system.DexClassLoader;

/**
 * Created by george.yang on 2016-3-28.
 */
public class ProxyActivity extends Activity {
    private PluginProxyContext pluginProxyContext;
    private Class pluginActivityClass;
    private Object pluginActivity;
    private HashMap<String,Method> methodMap = new HashMap<>();

//    @Override
//    public Resources getResources() {
//        return pluginProxyContext==null?super.getResources():pluginProxyContext.getResources();
//    }
//
//    @Override
//    public AssetManager getAssets() {
//        return pluginProxyContext==null?super.getAssets():pluginProxyContext.getAssets();
//    }
//
//    @Override
//    public ClassLoader getClassLoader() {
//        return pluginProxyContext==null?super.getClassLoader():pluginProxyContext.getClassLoader();
//    }
//
//    @Override
//    public Resources.Theme getTheme() {
//        return pluginProxyContext==null?super.getTheme():pluginProxyContext.getTheme();
//    }

//    @Override
//    public Object getSystemService(String name) {
//        return pluginProxyContext==null?super.getSystemService(name):pluginProxyContext.getSystemService(name);
//    }

//    public ProxyActivity() {
//        try {
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.i("demo", "load activity error:"+ Log.getStackTraceString(e));
//        }
//    }

    private DexClassLoader initClassLoader(String pluginPath){
        String filesDir = this.getCacheDir().getAbsolutePath();
        Log.i("inject", "fileexist:"+new File(pluginPath).exists());
//        loadResources(libPath);
        DexClassLoader loader = new DexClassLoader(pluginPath, filesDir,null , getClass().getClassLoader());
        return loader;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            String plugPath = AssetUtils.copyAsset(this, "plug.apk", getFilesDir());
            pluginProxyContext = new PluginProxyContext(this,plugPath);
//            ClassLoader loader = pluginProxyContext.getClassLoader();
            ClassLoader loader = initClassLoader(plugPath);

            //动态加载插件Activity
            pluginActivityClass = loader.loadClass("cn.georgeyang.magicbox.lib.MainActivity");
//            pluginActivityClass = loader.loadClass("Test");
//            pluginActivityClass = loader.loadClass("cn.georgeyang.library.MainActivity");
            Constructor<?> localConstructor = pluginActivityClass.getConstructor(new Class[] {});
            pluginActivity = localConstructor.newInstance(new Object[] {});

            //将代理对象设置给插件Activity
            Method setProxy = pluginActivityClass.getMethod("setProxy",new Class[] { Activity.class });
            setProxy.setAccessible(true);
            setProxy.invoke(pluginActivity, new Object[] { this });

//            //调用它的onCreate方法
//            Method onCreate = pluginActivityClass.getDeclaredMethod("onCreate",
//                    new Class[] { Bundle.class });
//            onCreate.setAccessible(true);
//            onCreate.invoke(pluginActivity, new Object[] { new Bundle() });



//            Method[] methods = pluginActivityClass.getMethods();
//            for (Method method:methods) {
//                Log.i("demo",method.getName());
//            }


                Method onCreate = pluginActivityClass.getMethod("createView",new Class[]{LayoutInflater.class,Bundle.class});
                onCreate.setAccessible(true);
                View view = (View) onCreate.invoke(pluginActivity, new Object[] {pluginProxyContext.getSystemService(LAYOUT_INFLATER_SERVICE),new Bundle()});
                setContentView(view);

//            View rootView = pluginProxyContext.getLayout("activity_main");
//            setContentView(rootView);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("demo", "load activity error:"+ Log.getStackTraceString(e));
        }
    }

    public boolean callCacheMethod (String methodName) {
        return callCacheMethod(methodName,new Object[]{});
    }

    public boolean callCacheMethod (String methodName,Object[] params) {
        Log.i("demo", "callCacheMethod:"+ methodName);
        Method method = methodMap.get(methodName);
        try {
            if (method==null) {
                Class[] classes = new Class[params.length];
                for (int i=0;i<classes.length;i++) {
                    classes[i] = params[i].getClass();
                }
                method = pluginActivityClass.getMethod(methodName,classes);
                method.setAccessible(true);
                methodMap.put(methodName, method);
            }
            method.invoke(pluginActivity, params);
            return true;
        } catch (Exception e) {
//            Log.i("demo", "call method error:"+ methodName +  ":" + Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        callCacheMethod("onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        callCacheMethod("onStart");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        callCacheMethod("onKeyDown",new Object[]{keyCode,event});
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        callCacheMethod("onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        callCacheMethod("onResume");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        callCacheMethod("onPostResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        callCacheMethod("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        callCacheMethod("onStop");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        callCacheMethod("onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        callCacheMethod("onLowMemory",new Object[]{level});
    }

}
