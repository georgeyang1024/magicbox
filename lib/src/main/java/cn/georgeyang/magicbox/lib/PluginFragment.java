package cn.georgeyang.magicbox.lib;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by george.yang on 16/3/29.
 */
public abstract  class PluginFragment extends Fragment {
    private static Method pushMethod;
    public final void pushMessage (int type,Object object) {
        try {
            if (pushMethod==null) {
                Activity activity = getActivity();
                Log.i("test","class:" + activity.getClass());
                pushMethod = activity.getClass().getMethod("pushMessage",new Class[]{Integer.class,Object.class});
            }
            pushMethod.invoke(null,new Object[]{type,object});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Method buildIntentMethod;
    public final void loadFragment (Class<? extends Fragment> clazz) {
        loadFragment(clazz,null);
    }
    public final void loadFragment (Class<? extends Fragment> clazz,Map<String,String> params) {
        try {
            if (buildIntentMethod==null) {
                Activity activity = getActivity();
                buildIntentMethod = activity.getClass().getMethod("buildIntent",new Class[]{Class.class, Map.class});
            }
            Intent intent = (Intent) buildIntentMethod.invoke(getActivity(),new Object[]{clazz,params});
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
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
