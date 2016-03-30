package cn.georgeyang.magicbox;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.georgeyang.loader.AssetUtils;
import cn.georgeyang.loader.PlugClassLoder;

/**
 * Created by george.yang on 2016-3-29.
 */
public class ProxyActivity extends PluginActivity {
    private static String ACTION = "cn.magicbox.plugin";
    private static String SCHEME = "magicbox";

    public static void init (String action,String scheme) {
        ProxyActivity.ACTION = action;
        ProxyActivity.SCHEME = scheme;
    }

    public static Intent buildIntent(Class<? extends Fragment> clazz, Map<String,String> params) {
        return buildIntent(clazz.getPackage().getName(),clazz.getSimpleName(),params);
    }

    public static Intent buildIntent(String packageName,String className, Map<String,String> params) {
        Uri.Builder builder = new Uri.Builder().scheme(SCHEME).path(packageName+"." + className);
        String animType = (params==null || !params.containsKey("animType"))?PluginConfig.NONE:params.get("animType");
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

    private String packageName,animType=null,className = null,version=null;
    public Fragment fragment;


    private static final List<ProxyActivity> allActivity =new ArrayList<>();

    public static void pushMessage(Integer type,Object object) {
        Log.i("test","push:" + object);
        for (ProxyActivity proxyActivity:allActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (proxyActivity.isDestroyed()) {
                    Log.i("test","isDestroyed:" + proxyActivity);
                    continue;
                }
            }
            if (proxyActivity.isFinishing()) {
                Log.i("test","isFinishing:" + proxyActivity);
                continue;
            }


            Fragment fragment = proxyActivity.fragment;
            if (fragment==null || !fragment.isAdded()) {
                Log.i("test","null or not add:" + fragment);
                continue;
            }

            try {
                Method method = fragment.getClass().getMethod("onReciveMessage",new Class[]{Integer.class,Object.class});
                method.invoke(fragment,new Object[]{type,object});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        loadAnim(true);
    }

    private void loadAnim (boolean isExit) {
        if (isExit) {
            setUsePluginResources(false);
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
                overridePendingTransition(R.anim.push_in_down,R.anim.push_no_ani);
                break;
            case PluginConfig.BottomInTopOut:
                overridePendingTransition(R.anim.push_in_down,R.anim.push_out_down);
                break;
            case PluginConfig.ZoomShow:
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                break;
            case PluginConfig.NONE:
            default:
                overridePendingTransition(0, 0);
                break;
        }
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
                animType = PluginConfig.NONE;
            }

            String pluginPath = AssetUtils.copyAsset(this,String.format("%s_%s.apk",new Object[]{packageName,version}), getFilesDir());
            initWithApkPathAndPackName(pluginPath,packageName);
            Class pluginActivityClass = mPluginData.classLoder.loadClass(String.format("%s.%s",new Object[]{packageName,className}));
            Constructor<?> localConstructor = pluginActivityClass.getConstructor(new Class[] {});
            fragment = (Fragment) localConstructor.newInstance();

//            Class pluginActivityClass = Class.forName(packageName + "." + className);
//            fragment = (Fragment) pluginActivityClass.newInstance();
        } catch (Exception e) {
            Toast.makeText(this,"加载失败:" + e.getMessage(),Toast.LENGTH_SHORT).show();
            Log.d("demo",Log.getStackTraceString(e).toString());
            e.printStackTrace();

            finish();
        }

        Log.d("demo","animType:" + animType);

        loadAnim(false);

        super.onCreate(savedInstanceState);

        allActivity.add(this);

        FrameLayout rootView = new FrameLayout(this);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.setBackgroundColor(Color.GRAY);
        rootView.setId(android.R.id.content);

        setContentView(rootView);

        FragmentTransaction ft =  getFragmentManager().beginTransaction();
        ft.add(android.R.id.content,fragment,"main");
        ft.commit();
    }


    private Method backPressedMethond;

    /**
     * 虚拟方法,如果fragment有boolean onBackPressed()方法，调用
     */
    @Override
    public void onBackPressed() {
        try {
            if (backPressedMethond==null) {
                backPressedMethond = fragment.getClass().getMethod("onBackPressed",new Class[]{});
            }
            if (backPressedMethond!=null) {
                boolean ret = (boolean) backPressedMethond.invoke(fragment,new Object[]{});
                if (ret) {
                    return;
                }
            }
        } catch (Exception e) {
        }
        super.onBackPressed();
    }

    private Method keyDownMethond;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyDownMethond==null) {
                keyDownMethond = fragment.getClass().getMethod("onKeyDown",new Class[]{Integer.class,KeyEvent.class});
            }
            if (keyDownMethond!=null) {
                boolean ret = (boolean) keyDownMethond.invoke(fragment,new Object[]{keyCode,event});
                if (ret) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        allActivity.remove(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUsePluginResources(false);
    }
}
