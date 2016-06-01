package online.magicbox.lib;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;
import online.magicebox.devlibrary.R;


/**
 * Created by george.yang on 2016-3-30.
 */
public class PluginActivity extends Activity {
    private String packageName = null, animType = null, className = null, version = null;
    public Slice mSlice;//切片
    private Context mContext;//

    private static String DefACTION = "online.magicbox.plugin";
    private static String DefSCHEME = "magicbox";
    private static String DefVersion = "1";
    private static String ACTION = DefACTION;
    private static String SCHEME = DefSCHEME;
    private static String VERSION = DefVersion;


    public static void init(String action, String scheme) {
        PluginActivity.ACTION = action;
        PluginActivity.SCHEME = scheme;
    }

    public static void init(String action, String scheme,String version) {
        PluginActivity.ACTION = action;
        PluginActivity.SCHEME = scheme;
        PluginActivity.VERSION = version;
    }


    private static Context getPluginContent(Context context,String packageName,String version) {
        try {
//            String pluginPath = AssetUtils.copyAsset(this,String.format("%s_%s.apk",new Object[]{packageName,version}), getFilesDir());
            String pluginPath = new File(context.getFilesDir(),String.format("%s_%s.apk",new Object[]{packageName,version})).getAbsolutePath();

            Class pluginContextClass = context.getClassLoader().loadClass("online.magicbox.app.PluginContext");
            Constructor<?> localConstructor = pluginContextClass.getConstructor(new Class[]{Context.class});
            Object pluginContext = localConstructor.newInstance(new Object[]{context});
            Method loadResourcesMethod = pluginContextClass.getMethod("loadResources",new Class[]{String.class,String.class});
            loadResourcesMethod.invoke(pluginContext,new Object[]{pluginPath,packageName});
            return (Context)pluginContext;

//            Log.i("test","load plugin:" + pluginPath);
//            PluginContext proxyContext = new PluginContext(context);
//            proxyContext.loadResources(pluginPath,packageName);
//            return proxyContext;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context;
    }


    public void startLocation () {
        //模拟定位，以插件运行的时候是真实位置
        Object location = "ampLocation Object";
        String[] info = new String[]{"0","0.0","0.0","广东省珠海市",""};
        callMethodByCache(mSlice, "onReceiveLocation", new Class[]{Object.class,String[].class}, new Object[]{location,info});
    }

    public void stopLocation () {

    }

    public void httpGet(String flag, final String url, final Map<String,Object> params) {
        callMethodByCache(mSlice, "onReceiveHttpData", new Class[]{String.class, String.class}, new Object[]{flag, ""});
    }

    public void httpPost(String flag, final String url, final Map<String,Object> params) {
        callMethodByCache(mSlice, "onReceiveHttpData", new Class[]{String.class, String.class}, new Object[]{flag, ""});
    }

    private static final List<PluginActivity> allActivity = new ArrayList<>();

    public static void pushMessage(int type, Object object) {
        Log.i("test", "push:" + object);
        for (PluginActivity activity : allActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (activity.isDestroyed()) {
                    Log.i("test", "isDestroyed:" + activity);
                    continue;
                }
            }
            if (activity.isFinishing()) {
                Log.i("test", "isFinishing:" + activity);
                continue;
            }

            callMethodByCache(activity.mSlice, "onReceiveMessage", new Class[]{int.class, Object.class}, new Object[]{type, object});
        }
    }


    public static Intent buildIntent(Context context,Class clazz) {
        return buildIntent(context,clazz.getPackage().getName(), clazz.getSimpleName(), PluginActivity.VERSION);
    }

    public static Intent buildIntent(Context context,Class clazz, String animType) {
        HashMap<String, String> params = new HashMap<>();
        params.put("animType", animType);
        return buildIntent(context,clazz.getPackage().getName(), clazz.getSimpleName(), params);
    }

    public static Intent buildIntent(Context context,Class clazz, Map<String, String> params) {
        return buildIntent(context,clazz.getPackage().getName(), clazz.getSimpleName(), params);
    }

    public static Intent buildIntent(Context context,String packageName, String className,String version) {
        HashMap<String, String> params = new HashMap<>();
        params.put("animType", "System");
        params.put("version", version);
        return buildIntent(context,packageName, className, params);
    }

    public static Intent buildIntent(Context context,String packageName, String className,String animType,String version) {
        HashMap<String, String> params = new HashMap<>();
        params.put("animType", animType);
        params.put("version", version);
        return buildIntent(context,packageName, className, params);
    }

    public static Intent buildIntent(Context context,String packageName, String className, Map<String, String> params) {
        if (params==null) {
            params = new HashMap<>();
        }
        if (!params.containsKey("animType")) {
            params.put("animType",PluginConfig.System);
        }
        if (!params.containsKey("version")) {
            params.put("version",PluginActivity.VERSION);
        }



        Log.i("test","buildIntent=====");
        Log.i("test","context:" + context);
        Log.i("test","loder:" + context.getClassLoader());
        Log.i("test","packageName:" + packageName);
        Log.i("test","className:" + className);
        for (String key:params.keySet()) {
            Log.i("test","key:" + key + ">>" + params.get(key));
        }
        Log.i("test","buildIntent end=====");

        if ("ProxyActivity".equals(className)) {
            String version = params.get("version");
            Context plugInContent = getPluginContent(context,packageName,version);
            ClassLoader classLoader =  plugInContent.getClassLoader();
            if (!(classLoader instanceof DexClassLoader)) {
                try {
                    Class<?> activity = classLoader.loadClass("online.magicbox.app.ProxyActivity");
                    Intent intent = new Intent(context, activity);
                    return intent;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                replaceClassLoader(context.getPackageName(), (DexClassLoader) classLoader);
                try {
                    Class<?> activity = classLoader.loadClass("online.magicbox.ProxyActivity");
                    Intent intent = new Intent(context, activity);
                    return intent;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Class tagClazz = null;
        try {
            tagClazz = Class.forName(packageName + "." + className);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String tagScheme,tagAction;
        if (tagClazz==null) {
            tagAction = DefACTION;
            tagScheme = DefSCHEME;
        } else {
            tagAction = ACTION;
            tagScheme = SCHEME;
        }

        Uri.Builder builder = new Uri.Builder().scheme(tagScheme).path(packageName + "." + className);
        if (params != null) {
            for (String key : params.keySet()) {
                builder.appendQueryParameter(key, params.get(key));
            }
        }
        Uri uri = builder.build();
        Intent intent = new Intent(tagAction);
        intent.setData(uri);
        return intent;
    }

    //http://blog.csdn.net/cauchyweierstrass/article/details/51087198
    private static void replaceClassLoader(String tagPackage,DexClassLoader loader){
        try {
            Class clazz_Ath = Class.forName("android.app.ActivityThread");
            Class clazz_LApk = Class.forName("android.app.LoadedApk");

            Object currentActivityThread = clazz_Ath.getMethod("currentActivityThread").invoke(null);
            Field field1 = clazz_Ath.getDeclaredField("mPackages");
            field1.setAccessible(true);
            Map mPackages = (Map)field1.get(currentActivityThread);

            WeakReference ref = (WeakReference) mPackages.get(tagPackage);
            Field field2 = clazz_LApk.getDeclaredField("mClassLoader");
            field2.setAccessible(true);
            field2.set(ref.get(), loader);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

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
                version = PluginActivity.VERSION;
            }

            animType = uri.getQueryParameter("animType");
            if (TextUtils.isEmpty(animType)) {
                animType = PluginConfig.System;
            }

            mContext = getPluginContent(this,packageName,version);
            Class pluginActivityClass = mContext.getClassLoader().loadClass(String.format("%s.%s", new Object[]{packageName, className}));
            Constructor<?> localConstructor = pluginActivityClass.getConstructor(new Class[]{Context.class,Object.class});
            mSlice = (Slice) localConstructor.newInstance(new Object[]{mContext,PluginActivity.this});

        } catch (Exception e) {
            Toast.makeText(this, "加载失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("demo", Log.getStackTraceString(e).toString());
            e.printStackTrace();

            finish();
        }

        Log.d("demo", "animType:" + animType);

        loadAnim(false);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            this.setTheme(android.R.style.Theme_Material_Light_NoActionBar);
        } else if (android.os.Build.VERSION.SDK_INT >= 13) {
            this.setTheme(android.R.style.Theme_Holo_Light_NoActionBar);
        } else {
            this.setTheme(android.R.style.Theme_Black_NoTitleBar);
        }

        super.onCreate(savedInstanceState);

        allActivity.add(this);
        callMethodByCache(mSlice, "onCreate", new Class[]{Bundle.class}, new Object[]{savedInstanceState});
    }

    //必须用反射call
    public Slice getSlice() {
        return mSlice;
    }

    @Override
    protected void onResume() {
        super.onResume();
        callMethodByCache(mSlice, "onResume", new Class[]{}, new Object[]{});
    }

    @Override
    protected void onPause() {
        super.onPause();
        callMethodByCache(mSlice, "onPause", new Class[]{}, new Object[]{});
    }

    private static final Map<String, Method> methodCache = new WeakHashMap<>();
    protected static Object callMethodByCache(Object receiver, String methodName, Class[] parameterTypes, Object[] args) {
        try {
            return callMethodByCacheWithException(receiver,methodName,parameterTypes,args);
        } catch (Exception e) {
            Log.d("test",Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return null;
    }

    private static Object callMethodByCacheWithException(Object receiver, String methodName, Class[] parameterTypes, Object[] args) throws InvocationTargetException, IllegalAccessException {
        String key = receiver.getClass() + "#" + methodName + "&" + Arrays.toString(parameterTypes);
        Method method = methodCache.get(key);
        if (method==null) {
            Class currClass = receiver.getClass();
            while (method==null) {
                try {
                    method = currClass.getMethod(methodName,parameterTypes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (method==null) {
                    try {
                        method = receiver.getClass().getDeclaredMethod(methodName, parameterTypes);
                        method.setAccessible(true);
                    } catch (Exception e) {

                    }
                }
                currClass = currClass.getSuperclass();
                if (currClass==null) {
                    break;
                }
            }

            if (method!=null) {
                methodCache.put(key,method);
            }
        }
        return method.invoke(receiver,args);
    }

    private static Class findClass(Class clazz,Class tagClass) {
        if (clazz==tagClass) {
            return clazz;
        } else {
            Class sup = clazz.getSuperclass();
            if (sup!=null) {
                return findClass(sup,tagClass);
            }
        }
        return null;
    }

    @Override
    public void finish() {
        super.finish();
        loadAnim(true);
    }

    /**
     * 虚拟方法,如果fragment有boolean onBackPressed()方法，调用
     */
    @Override
    public void onBackPressed() {
        Object ret = callMethodByCache(mSlice, "onBackPressed", new Class[]{}, new Object[]{});
        if (ret!=null) {
            try {
                if ((boolean)ret) {
                    return;
                }
            } catch (Exception e) {

            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        callMethodByCache(mSlice, "onDestroy", new Class[]{}, new Object[]{});
        super.onDestroy();
    }

    private void loadAnim(boolean isExit) {
        if (animType==null) {
            animType = PluginConfig.System;
        }
        switch (animType) {
            case PluginConfig.LeftInRightOut:
                if (isExit) {
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                } else {
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }
                break;
            case PluginConfig.AlphaShow:
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case PluginConfig.TopOut:
                overridePendingTransition(R.anim.push_in_down, R.anim.push_no_ani);
                break;
            case PluginConfig.BottomInTopOut:
                overridePendingTransition(R.anim.push_in_down, R.anim.push_out_down);
                break;
            case PluginConfig.ZoomShow:
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                break;
            case PluginConfig.NONE:
                overridePendingTransition(0, 0);
                break;
            case PluginConfig.System:
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean bool = false;
        try {
            bool = (boolean) callMethodByCache(mSlice, "onCreateOptionsMenu", new Class[]{Menu.class}, new Object[]{menu});
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!bool) {
            return super.onCreateOptionsMenu(menu);
        }
        return bool;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        boolean bool = false;
        try {
            bool = (boolean) callMethodByCache(mSlice, "onOptionsMenuClosed", new Class[]{Menu.class}, new Object[]{menu});
        } catch (Exception e) {

        }
        if (!bool) {
            super.onOptionsMenuClosed(menu);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean bool = false;
        try {
            bool = (boolean) callMethodByCache(mSlice, "onKeyDown", new Class[]{int.class,KeyEvent.class}, new Object[]{keyCode,event});
        } catch (Exception e) {

        }
        return bool?true:super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        boolean bool = false;
        try {
            bool = (boolean) callMethodByCache(mSlice, "onMenuItemSelected", new Class[]{int.class,MenuItem.class}, new Object[]{featureId,item});
        } catch (Exception e) {

        }
        return bool?true:super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean bool = false;
        try {
            bool = (boolean) callMethodByCache(mSlice, "onPrepareOptionsMenu", new Class[]{MenuItem.class}, new Object[]{menu});
        } catch (Exception e) {

        }
        return bool?true:super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean bool = false;
        try {
            bool = (boolean) callMethodByCache(mSlice, "onOptionsItemSelected", new Class[]{MenuItem.class}, new Object[]{item});
        } catch (Exception e) {

        }
        return bool?true:super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        boolean bool = false;
        try {
            bool = (boolean) callMethodByCache(mSlice, "onMenuOpened", new Class[]{int.class,Menu.class}, new Object[]{featureId,menu});
        } catch (Exception e) {

        }
        return bool?true:super.onMenuOpened(featureId, menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        boolean bool = false;
        try {
            bool = (boolean) callMethodByCache(mSlice, "onCreateContextMenu", new Class[]{ContextMenu.class,View.class,ContextMenu.ContextMenuInfo.class}, new Object[]{menu,v,menuInfo});
        } catch (Exception e) {

        }
        if (!bool) {
            super.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    @Override
    public void closeOptionsMenu() {
        boolean bool = false;
        try {
            bool = (boolean) callMethodByCache(mSlice, "closeOptionsMenu", new Class[]{}, new Object[]{});
        } catch (Exception e) {

        }
        if (!bool) {
            super.closeOptionsMenu();
        }
    }

    @Override
    public void openOptionsMenu() {
        boolean bool = false;
        try {
            bool = (boolean) callMethodByCache(mSlice, "openOptionsMenu", new Class[]{}, new Object[]{});
        } catch (Exception e) {

        }
        if (!bool) {
            super.openOptionsMenu();
        }
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        boolean bool = false;
        try {
            bool = (boolean) callMethodByCache(mSlice, "onContextMenuClosed", new Class[]{Menu.class}, new Object[]{menu});
        } catch (Exception e) {

        }
        if (!bool) {
            super.onContextMenuClosed(menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean bool = false;
        try {
            bool = (boolean) callMethodByCache(mSlice, "onContextItemSelected", new Class[]{MenuItem.class}, new Object[]{item});
        } catch (Exception e) {

        }
        return bool?true:super.onContextItemSelected(item);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("test","onSaveInstanceState");
//        super.onSaveInstanceState(outState);
        callMethodByCache(mSlice, "onSaveInstanceState", new Class[]{Bundle.class}, new Object[]{outState});
    }


    @Override
    public void onStart() {
        super.onStart();
        callMethodByCache(mSlice, "onStart", new Class[]{}, new Object[]{});
    }

    @Override
    public void onStop() {
        super.onStop();
        callMethodByCache(mSlice, "onStop", new Class[]{}, new Object[]{});
    }

    @Override
    public Intent getIntent() {
        return super.getIntent();
    }


    @Override
    public FragmentManager getFragmentManager() {
        return super.getFragmentManager();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callMethodByCache(mSlice, "onActivityResult", new Class[]{int.class,int.class,Intent.class}, new Object[]{requestCode,resultCode,data});
    }

    public final void requestPermission (int requestCode,String permission) {
        Log.d("test","requestPermission in Activity:" + permission);
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }

        boolean hasPermission = false;
        boolean shouldShow = false;
        if (Build.VERSION.SDK_INT >= 23) {
            int cheResult = super.checkPermission(permission, android.os.Process.myPid(), Process.myUid());
            if (cheResult != PackageManager.PERMISSION_GRANTED) {
                shouldShow = super.shouldShowRequestPermissionRationale(permission);
                if (shouldShow) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    this.startActivity(intent);
                } else {
                    super.requestPermissions(new String[]{permission}, requestCode);
                    return;
                }
            } else {
                hasPermission = true;
            }
        } else {
            hasPermission = true;
        }



        if (hasPermission) {
            onPermissionGiven(requestCode,permission);
        } else {
            String pName = TextUtils.isEmpty(permission)?"error":permission.substring(permission.lastIndexOf('.')+1,permission.length());
            String tip;
            if (shouldShow) {
                tip = Strings.get(this,"openPermission",new Object[]{pName});
            } else {
                tip = Strings.get(this,"noPermission",new Object[]{pName});
            }
            Toast.makeText(this,tip,Toast.LENGTH_SHORT).show();
        }
    }

    public void onPermissionGiven (int requestCode,String permission) {
        callMethodByCache(mSlice, "onPermissionGiven", new Class[]{int.class,String.class}, new Object[]{requestCode,permission});

        List<Fragment> fragmentList = getFragments(getFragmentManager());
        if (!(fragmentList == null || fragmentList.size()==0)) {
            for (Fragment fragment:fragmentList) {
                if (!(fragment==null || !fragment.isAdded())) {
                    callMethodByCache(fragment, "onPermissionGiven", new Class[]{int.class,String.class}, new Object[]{requestCode,permission});
                }
            }
        }
    }

    public List<Fragment> getFragments(FragmentManager fragmentManager) {
        try {
            Field field = fragmentManager.getClass().getDeclaredField("mAdded");
            field.setAccessible(true);
            return (List<Fragment>) field.get(fragmentManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 ){
            for (int i=0;i<permissions.length;i++) {
                if (grantResults[i]== PackageManager.PERMISSION_GRANTED) {
                    onPermissionGiven(requestCode,permissions[i]);
                } else {
                    String pName = TextUtils.isEmpty(permissions[i])?"error":permissions[i].substring(permissions[i].lastIndexOf('.')+1,permissions[i].length());
                    Toast.makeText(this,Strings.get(this,"openPermission",new Object[]{pName}),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
