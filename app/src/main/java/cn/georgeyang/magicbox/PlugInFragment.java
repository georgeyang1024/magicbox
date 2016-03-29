package cn.georgeyang.magicbox;

import android.app.Activity;
import android.app.Fragment;
import android.view.KeyEvent;

import java.lang.reflect.Method;

/**
 * Created by george.yang on 16/3/29.
 */
public abstract  class PluginFragment extends Fragment {
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

    public boolean onBackPressed () {
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
