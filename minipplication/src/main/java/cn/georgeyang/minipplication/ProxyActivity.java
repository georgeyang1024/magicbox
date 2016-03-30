package cn.georgeyang.minipplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProxyActivity extends Activity {
    private static String ACTION = "cn.magicbox.plugin";
    private static String SCHEME = "magicbox";

    public static void init (String action,String scheme) {
        ProxyActivity.ACTION = action;
        ProxyActivity.SCHEME = scheme;
    }

    public static Intent buildIntent(Class<? extends Fragment> clazz) {
        return buildIntent(clazz.getPackage().getName(),clazz.getSimpleName(),null);
    }

    public static Intent buildIntent(Class<? extends Fragment> clazz, Map<String,String> params) {
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

    protected String packageName,animType=null,className = null,version=null;
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
//        if (isExit) {
//            setUsePluginResources(false);
//        }
//        switch (animType) {
//            case PluginConfig.LeftInRightOut:
//                if (isExit) {
//                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
//                } else {
//                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
//                }
//                break;
//            case PluginConfig.AlphaShow:
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                break;
//            case PluginConfig.TopOut:
//                overridePendingTransition(R.anim.push_in_down,R.anim.push_no_ani);
//                break;
//            case PluginConfig.BottomInTopOut:
//                overridePendingTransition(R.anim.push_in_down,R.anim.push_out_down);
//                break;
//            case PluginConfig.ZoomShow:
//                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
//                break;
//            case PluginConfig.NONE:
//                overridePendingTransition(0, 0);
//                break;
//            case PluginConfig.System:
//            default:
//                break;
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("demo","onCreate!!");

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
//            initWithApkPathAndPackName(pluginPath,packageName);
//            Class pluginActivityClass = mPluginData.classLoder.loadClass(String.format("%s.%s",new Object[]{packageName,className}));
//            Constructor<?> localConstructor = pluginActivityClass.getConstructor(new Class[] {});
//            fragment = (Fragment) localConstructor.newInstance();

            Class pluginActivityClass = Class.forName(packageName + "." + className);
            fragment = (Fragment) pluginActivityClass.newInstance();

            Method method = fragment.getClass().getMethod("setProxyActivity",new Class[]{Activity.class});
            method.invoke(fragment,new Object[]{this});
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



    /**
     * 获取插件资源对应的id
     *
     * @param type
     * @param name
     * @return
     * @author 小姜
     * @time 2015-4-16 上午11:31:56
     */
    public int getIdentifier(String type,String name){
        return getResources().getIdentifier(name, type, packageName);
    }
    public int getId(String name){
        return getResources().getIdentifier(name, "id", packageName);
    }
    /**
     * 获取插件中的layout布局
     *
     * @param name
     * @return
     * @author 小姜
     * @time 2015-4-16 上午11:32:12
     */
    public View getLayout(String name){
        return LayoutInflater.from(this).inflate(getIdentifier("layout",name), null);
    }
    public String getString(String name){
        return getResources().getString(getIdentifier("string", name));
    }
    public int getColor(String name){
        return getResources().getColor(getIdentifier("color", name));
    }
    public Drawable getDrawable(String name){
        return getResources().getDrawable(getIdentifier("drawable", name));
    }
    public int getStyle(String name){
        return getIdentifier("style", name);
    }
    public float getDimen(String name){
        return getResources().getDimension(getIdentifier("dimen", name));
    }
}
