package online.magicbox.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

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

/**
 * Created by george.yang on 2016-3-30.
 */
public class PluginActivity extends Activity {
    private String packageName = null, animType = null, className = null, version = null;
    public Object mSlice;//切片
    private Context mContext;//

    private static String ACTION = "online.magicbox.plugin";
    private static String SCHEME = "magicbox";

    public static void init(String action, String scheme) {
        PluginActivity.ACTION = action;
        PluginActivity.SCHEME = scheme;
    }


//    private static final WeakHashMap<String,PluginContext> contextWeakHashMap = new WeakHashMap<>();
    private static Context getPluginContent(Context context,String packageName,String version) {
        try {
//            String pluginPath = AssetUtils.copyAsset(this,String.format("%s_%s.apk",new Object[]{packageName,version}), getFilesDir());
            String pluginPath = new File(context.getFilesDir(),String.format("%s_%s.apk",new Object[]{packageName,version})).getAbsolutePath();

//            Class pluginContextClass = context.getClassLoader().loadClass("online.magicbox.app.PluginContext");
//            Constructor<?> localConstructor = pluginContextClass.getConstructor(new Class[]{Context.class});
//            Object pluginContext = localConstructor.newInstance(new Object[]{context});
//            Method loadResourcesMethod = pluginContextClass.getMethod("loadResources",new Class[]{String.class,String.class});
//            loadResourcesMethod.invoke(pluginContext,new Object[]{pluginPath,packageName});
//            return (Context)pluginContext;

//            PluginContext pluginContext = contextWeakHashMap.get(pluginPath);
//            if (pluginContext==null) {
//                pluginContext =  new PluginContext(context);
//                pluginContext.loadResources(pluginPath,packageName,version);
//                contextWeakHashMap.put(pluginPath,pluginContext);
//            }
//            return pluginContext;

            PluginContext proxyContext = new PluginContext(context);
            proxyContext.loadResources(pluginPath,packageName,version);
            return proxyContext;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context;
    }


    private AMapLocationClient mLocationClient = null;
    public void startLocation () {
        //声明定位回调监听器
        AMapLocationListener mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                String[] info = new String[]{aMapLocation.getErrorCode()+"",aMapLocation.getLatitude()+"",aMapLocation.getLongitude()+"",aMapLocation.getAddress()};
                callMethodByCache(mSlice, "onReceiveLocation", new Class[]{Object.class,String[].class}, new Object[]{aMapLocation,info});
            }
        };
        //初始化定位
        mLocationClient = new AMapLocationClient(this);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //声明mLocationOption对象
        AMapLocationClientOption mLocationOption = null;
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    public void stopLocation () {
        if (mLocationClient!=null) {
            mLocationClient.stopLocation();
        }
    }

    public void httpGet(String flag, final String url, final Map<String,Object> params) {
        UiThread.init(this).setFlag(flag).start(new UiThread.UIThreadEvent() {
            @Override
            public Object runInThread(String flag, Object obj, UiThread.Publisher publisher) {
                return HttpUtil.get(url,params);
            }

            @Override
            public void runInUi(String flag, Object obj, boolean ispublish, float progress) {
                callMethodByCache(mSlice, "onReceiveHttpData", new Class[]{String.class, String.class}, new Object[]{flag, obj});
            }
        });
    }

    public void httpPost(String flag, final String url, final Map<String,Object> params) {
        UiThread.init(this).setFlag(flag).start(new UiThread.UIThreadEvent() {
            @Override
            public Object runInThread(String flag, Object obj, UiThread.Publisher publisher) {
                return HttpUtil.post(url,params);
            }

            @Override
            public void runInUi(String flag, Object obj, boolean ispublish, float progress) {
                callMethodByCache(mSlice, "onReceiveHttpData", new Class[]{String.class, String.class}, new Object[]{flag, obj});
            }
        });
    }


    public Object getSlice() {
        return mSlice;
    }

    private static final List<PluginActivity> allActivity = new ArrayList<>();

    public static void pushMessage(int type, Object object) {
        for (PluginActivity activity : allActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (activity.isDestroyed()) {
                    continue;
                }
            }
            if (activity.isFinishing()) {
                continue;
            }

            callMethodByCache(activity.mSlice, "onReceiveMessage", new Class[]{int.class, Object.class}, new Object[]{type, object});
        }
    }

    public static Intent buildIntent(Context context,Class clazz) {
        return buildIntent(context,clazz.getPackage().getName(), clazz.getSimpleName(), PluginConfig.pluginVersion);
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
            params.put("version",PluginConfig.pluginVersion);
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

//        if ("ProxyActivity".equals(className)) {
//            String version = params.get("version");
//            Context plugInContent = getPluginContent(context.getApplicationContext(),packageName,version);
//            ClassLoader classLoader =  plugInContent.getClassLoader();
//            if (!(classLoader instanceof DexClassLoader)) {
//                try {
//                    Class<?> activity = classLoader.loadClass("online.magicbox.app.ProxyActivity");
//                    Intent intent = new Intent(context, activity);
//                    return intent;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else {
//                replaceClassLoader(context.getPackageName(), (DexClassLoader) classLoader);
//                try {
//                    Class<?> activity = classLoader.loadClass("online.magicbox.ProxyActivity");
//                    Intent intent = new Intent(context, activity);
//                    return intent;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        Uri.Builder builder = new Uri.Builder().scheme(SCHEME).path(packageName + "." + className);
        if (params != null) {
            for (String key : params.keySet()) {
                builder.appendQueryParameter(key, params.get(key));
            }
        }
        Uri uri = builder.build();
        Intent intent = new Intent(ACTION);
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
        String errMsg = "";

        Intent intent = getIntent();
        if (intent == null || intent.getData() == null) {
            errMsg = "缺少参数";
        } else {
            Uri uri = intent.getData();

            String path = uri.getPath();
            packageName = path.substring(1, path.lastIndexOf('.'));
            if (TextUtils.isEmpty(packageName)) {
                errMsg =  "未指定插件名";
            }

            className = path.substring(path.lastIndexOf('.') + 1, path.length());
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
        }

        loadAnim(false);

        //運行的是插件時，這段代碼無效
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            this.setTheme(android.R.style.Theme_Holo_Light_NoActionBar);
        } else if (android.os.Build.VERSION.SDK_INT >= 13) {
            this.setTheme(android.R.style.Theme_Holo_Light_NoActionBar);
        } else {
            this.setTheme(android.R.style.Theme_Black_NoTitleBar);
        }

        //先显示动画
        super.onCreate(savedInstanceState);
        if (!TextUtils.isEmpty(errMsg)) {
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        File pluginFile = new File(this.getFilesDir(),String.format("%s_%s.apk",new Object[]{packageName,version}));
        if (pluginFile==null || !pluginFile.exists()) {
            Toast.makeText(this, "插件已被删除，请重新下载！", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String pluginPath = pluginFile.getAbsolutePath();
        ClassLoader plugClassLoder = PlugClassLoder.plugClassLoderCache.get(pluginPath);
        float mb = ((float) pluginFile.length() / (float) (1024 * 1024));
        if (mb >= 1f && plugClassLoder==null) {
            setContentView(R.layout.activity_main);
            UiThread.init(this).setCallBackDelay(3000).setObject(savedInstanceState).start(uiThreadEvent);
        } else {
            uiThreadEvent.runInUi("",savedInstanceState,false,0f);
        }
    }

    UiThread.UIThreadEvent uiThreadEvent = new UiThread.UIThreadEvent() {
        @Override
        public Object runInThread(String flag, Object obj, UiThread.Publisher publisher) {
            try {
                mContext = getPluginContent(PluginActivity.this,packageName,version);
            } catch (Exception e) {
                e.printStackTrace();
                return e;
            }
            return obj;
        }

        @Override
        public void runInUi(String flag, Object obj, boolean ispublish, float progress) {
            if (obj instanceof Exception) {
                finishWithException((Exception) obj);
                return;
            }

            try {
                if (mContext==null) {
                    mContext = getPluginContent(PluginActivity.this,packageName,version);
                }
                Class pluginActivityClass = mContext.getClassLoader().loadClass(String.format("%s.%s", new Object[]{packageName, className}));
                Constructor<?> localConstructor = pluginActivityClass.getConstructor(new Class[]{Context.class,Object.class});
                mSlice = localConstructor.newInstance(new Object[]{mContext,PluginActivity.this});
            } catch (Exception e) {
                e.printStackTrace();
                finishWithException(e);
                return;
            }

            if (ReflectUtil.getClass("com.android.internal.policy.PolicyManager") == null) {
                new AlertDialog.Builder(mContext).setTitle("提示").setCancelable(false).setMessage("不支持的手机系统版本!请更换系统或更换手机以使用本软件~").setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create().show();
            } else {
                try {
                    callMethodByCacheWithException(mSlice, "onCreate", new Class[]{Bundle.class}, new Object[]{obj});
                    allActivity.add(PluginActivity.this);
                } catch (Exception e){
                    finishWithException(e);
                }
            }
        }
    };

    private void finishWithException(Exception e) {
        Log.i("test",Log.getStackTraceString(e));
        Toast.makeText(PluginActivity.this, "加载失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        finish();
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
    private static Object callMethodByCache(Object receiver, String methodName, Class[] parameterTypes, Object[] args) {
        try {
            callMethodByCacheWithException(receiver,methodName,parameterTypes,args);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    private static Object callMethodByCacheWithException(Object receiver, String methodName, Class[] parameterTypes, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            String key = receiver.getClass() + "#" + methodName + "&" + Arrays.toString(parameterTypes);
            Method method = methodCache.get(key);
            if (method == null) {
                method = receiver.getClass().getMethod(methodName, parameterTypes);
                methodCache.put(key, method);
            } else {
            }
            return method.invoke(receiver, args);
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
        if (mLocationClient!=null) {
            mLocationClient.onDestroy();//销毁定位客户端。
        }
        super.onDestroy();
    }

    private void loadAnim(boolean isExit) {
        Log.d("test","anim:" + animType);
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
        super.onSaveInstanceState(outState);
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
}
