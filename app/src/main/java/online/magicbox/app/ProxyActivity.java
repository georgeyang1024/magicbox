package online.magicbox.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import cn.georgeyang.magicbox.R;

/**
 * Created by george.yang on 2016-3-30.
 */
public class ProxyActivity extends Activity {
    private String packageName = null, animType = null, className = null, version = null;
    public Object mSlice;//切片
    private Context mContext;//

    private static String ACTION = "cn.magicbox.plugin";
    private static String SCHEME = "magicbox";

    public static void init(String action, String scheme) {
        ProxyActivity.ACTION = action;
        ProxyActivity.SCHEME = scheme;
    }

    private Context getPluginContent() {
        try {
            String pluginPath = AssetUtils.copyAsset(this,String.format("%s_%s.apk",new Object[]{packageName,version}), getFilesDir());
            PluginProxyContext proxyContext = new PluginProxyContext(this);
            proxyContext.loadResources(pluginPath,packageName);
            return proxyContext;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }


    private static final List<ProxyActivity> allActivity = new ArrayList<>();

    public static void pushMessage(int type, Object object) {
        Log.i("test", "push:" + object);
        for (ProxyActivity activity : allActivity) {
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


    public static Intent buildIntent(Class clazz) {
        return buildIntent(clazz.getPackage().getName(), clazz.getSimpleName(), null);
    }

    public static Intent buildIntent(Class clazz, String animType) {
        HashMap<String, String> params = new HashMap<>();
        params.put("animType", animType);
        return buildIntent(clazz.getPackage().getName(), clazz.getSimpleName(), params);
    }

    public static Intent buildIntent(Class clazz, Map<String, String> params) {
        return buildIntent(clazz.getPackage().getName(), clazz.getSimpleName(), params);
    }

    public static Intent buildIntent(String packageName, String className, Map<String, String> params) {
        Uri.Builder builder = new Uri.Builder().scheme(SCHEME).path(packageName + "." + className);
        String animType = (params == null || !params.containsKey("animType")) ? PluginConfig.System : params.get("animType");
        String version = (params == null || !params.containsKey("version")) ? PluginConfig.pluginVersion : params.get("version");
        builder.appendQueryParameter("animType", animType);
        builder.appendQueryParameter("version", version);
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
            Class pluginActivityClass = mContext.getClassLoader().loadClass(String.format("%s.%s", new Object[]{packageName, className}));
            Constructor<?> localConstructor = pluginActivityClass.getConstructor(new Class[]{Context.class,Object.class});
            mSlice = localConstructor.newInstance(new Object[]{mContext,ProxyActivity.this});

        } catch (Exception e) {
            Toast.makeText(this, "加载失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("demo", Log.getStackTraceString(e).toString());
            e.printStackTrace();

            finish();
        }

        Log.d("demo", "animType:" + animType);

        loadAnim(false);

        super.onCreate(savedInstanceState);

        allActivity.add(this);
        callMethodByCache(mSlice, "onCreate", new Class[]{Bundle.class}, new Object[]{savedInstanceState});
    }

    @Override
    protected void onResume() {
        super.onResume();
        callMethodByCache(mSlice, "onResume", new Class[]{}, new Object[]{});
    }

    private static final Map<String, Method> methodCache = new WeakHashMap<>();
    private static Object callMethodByCache(Object receiver, String methodName, Class[] parameterTypes, Object[] args) {
        try {
            String key = receiver.getClass() + "#" + methodName + "&" + Arrays.toString(parameterTypes);
            Log.i("test", "cache key:" + key);
            Method method = methodCache.get(key);
            if (method == null) {
                method = receiver.getClass().getMethod(methodName, parameterTypes);
                methodCache.put(key, method);
            } else {
                Log.i("test", "use cache:" + key);
            }
            return method.invoke(receiver, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        callMethodByCache(mSlice, "finish", new Class[]{}, new Object[]{});
        super.finish();
        loadAnim(true);
    }

    /**
     * 虚拟方法,如果fragment有boolean onBackPressed()方法，调用
     */
    @Override
    public void onBackPressed() {
        boolean ret = (boolean)callMethodByCache(mSlice, "onBackPressed", new Class[]{}, new Object[]{});
        if (ret) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        callMethodByCache(mSlice, "onDestroy", new Class[]{}, new Object[]{});
        super.onDestroy();
    }

    private void loadAnim(boolean isExit) {
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
