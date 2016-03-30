package cn.georgeyang.magicbox.lib;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiEnterpriseConfig;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by george.yang on 16/3/29.
 */
public abstract  class PluginFragment extends Fragment {
    private Activity mProxyActivity;

    public final void setProxyActivity(Activity activity) {
        mProxyActivity = activity;
        try {
            Field[] fields = Fragment.class.getFields();
            for (Field field:fields) {
                Log.d("test",field.getName());
            }
            Log.d("test","==================");
            fields = Fragment.class.getDeclaredFields();
            for (Field field:fields) {
                Log.d("test",field.getName());
            }
        } catch (Exception e){

        }
        try {
            Field activityField = Fragment.class.getDeclaredField("mActivity");
            activityField.setAccessible(true);
            activityField.set(this,activity);
            Log.d("test","success@@@");
            return;
        } catch (Exception e) {
            Log.d("test",Log.getStackTraceString(e).toString());
            e.printStackTrace();
        }

        try {
            Field hostField = Fragment.class.getField("mHost");
            hostField.setAccessible(true);
            Class fragmentHostClass = hostField.getClass();
            Field activityField = fragmentHostClass.getField("mActivity");
            activityField.set(this,activity);
            return;
        } catch (Exception e) {
            Log.d("test",Log.getStackTraceString(e).toString());
            e.printStackTrace();
        }
    }

    public View getLayout (String name) {
        try {
             Method method = getActivity().getClass().getMethod("getLayout",String.class);
            return (View) method.invoke(getActivity(),new Object[]{name});
        }catch (Exception e) {
            Log.d("test",Log.getStackTraceString(e).toString());
            e.printStackTrace();
        }
        return null;
    }

    private static Method pushMethod;
    public final void pushMessage (int type,Object object) {
        try {
            if (pushMethod==null) {
                Activity activity = getActivity();
                pushMethod = activity.getClass().getMethod("pushMessage",new Class[]{Integer.class,Object.class});
            }
            pushMethod.invoke(null,new Object[]{type,object});
        } catch (Exception e) {
        }
    }

    private Method buildIntentMethod;
    public final Intent buildIntent (Class<? extends Fragment> clazz) {
        return buildIntent(clazz,null);
    }
    public final Intent buildIntent (Class<? extends Fragment> clazz,Map<String,String> params) {
        try {
            if (buildIntentMethod==null) {
                Activity activity = getActivity();
                buildIntentMethod = activity.getClass().getMethod("buildIntent",new Class[]{Class.class, Map.class});
            }
            if (params==null) {
                params = new HashMap<>();
            }
            if (!params.containsKey("version")) {
                params.put("version",PluginConfig.pluginVersion);
            }
            Intent intent = (Intent) buildIntentMethod.invoke(getActivity(),new Object[]{clazz,params});
            return intent;
        } catch (Exception e) {

        }
        return null;
    }

    public final void loadFragment (Class<? extends Fragment> clazz) {
        loadFragment(clazz,PluginConfig.System);
    }
    public final void loadFragment (Class<? extends Fragment> clazz,String animType) {
        HashMap<String,String> params = new HashMap<>();
        params.put("animType",animType);
        loadFragment(clazz,params);
    }
    public final void loadFragment (Class<? extends Fragment> clazz,Map<String,String> params) {
        try {
            Intent intent = buildIntent(clazz,params);
            startActivity(intent);
        } catch (Exception e) {
        }
    }

    public void onReciveMessage (Integer type,Object object) {

    }

    public boolean onBackPressed () {
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
