package cn.georgeyang.magicbox.lib;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProxyActivity extends Activity {
    public static final String ACTION = "cn.georgeyang.magicbox.lib";
    public static final String SCHEME = "magicbox";

    public static Intent buildIntent(Class<? extends Fragment> clazz, Map<String,String> params) {
        Uri.Builder builder = new Uri.Builder().scheme(SCHEME).path(clazz.getName());
        String animType = (params==null || !params.containsKey("animType"))?"LeftInRightOut":params.get("animType");
        builder.appendQueryParameter("animType",animType);
        builder.appendQueryParameter("version","1.0");
        if (params!=null) {
            for (String key:params.keySet()) {
                builder.appendQueryParameter(key,params.get(key));
            }
        }
        Uri uri = builder.build();

        Log.i("test","uri:" + uri.toString());
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
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.Anim_fade);

        try {
            animType = getIntent().getData().getQueryParameter("animType");
        } catch (Exception e) {
            Log.d("demo",Log.getStackTraceString(e));
        }

        if (TextUtils.isEmpty(animType)) {
            animType = "aaa";
        }

        switch (animType) {
            case "ww":

                break;
            default:
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

        }


        super.onCreate(savedInstanceState);
        allActivity.add(this);

        try {

        Intent intent = getIntent();
        if (intent==null || intent.getData()==null) {
            if (!"cn.georgeyang.magicbox.ProxyActivity".equals(this.getClass().getName())) {
                intent = buildIntent(MainFragment.class,null);
            } else {
                Toast.makeText(this,"缺少参数",Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        Uri uri = intent.getData();

        Log.i("test","path:" + uri.getPath());
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

        String pluginPath = "";

//            pluginPath = AssetUtils.copyAsset(this,String.format("%s_%s.apk",new Object[]{packageName,version}), getFilesDir());

            FrameLayout rootView = new FrameLayout(this);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            rootView.setBackgroundColor(Color.GRAY);
            rootView.setId(android.R.id.content);

            setContentView(rootView);

//            initWithApkPathAndPackName(pluginPath,packageName);
//            Class pluginActivityClass = mPluginData.classLoder.loadClass(String.format("%s.%s",new Object[]{packageName,action}));
//            Constructor<?> localConstructor = pluginActivityClass.getConstructor(new Class[] {});
//            fragment = (Fragment) localConstructor.newInstance();

            Class pluginActivityClass = Class.forName(packageName + "." + className);
            fragment = (Fragment) pluginActivityClass.newInstance();

            FragmentTransaction ft =  getFragmentManager().beginTransaction();
            ft.add(android.R.id.content,fragment,"main");
            ft.commit();

        } catch (Exception e) {
            Toast.makeText(this,"加载失败:" + e.getMessage(),Toast.LENGTH_SHORT).show();
            Log.d("demo",Log.getStackTraceString(e).toString());
            e.printStackTrace();
        }
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
}
