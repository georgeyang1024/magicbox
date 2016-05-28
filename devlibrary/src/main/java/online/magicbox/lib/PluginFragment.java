package online.magicbox.lib;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;

import java.lang.reflect.Field;

/**
 * Created by george.yang on 16/5/8.
 */
public class PluginFragment extends Fragment {
    public LayoutInflater getPluginLayoutInflater() {
        try {
            Slice slice = (Slice) getPluginContext();
            return slice.getLayoutInflater();
        } catch (Exception e) {
            Log.d("test",Log.getStackTraceString(e));
            e.printStackTrace();
            return LayoutInflater.from(getActivity());
        }
    }

    public Context getPluginContext() {
        Slice slice = (Slice) PluginActivity.callMethodByCache(getActivity(),"getSlice",new Class[]{},new Object[]{});
        if (slice==null) {
            try {
                Field field = getActivity().getClass().getField("mSlice");
                slice = (Slice) field.get(getActivity());
            } catch (Exception e) {
                Log.d("test",Log.getStackTraceString(e));
                e.printStackTrace();
            }
        }
        return slice;
    }

    public void onPermissionGiven (int requestCode,String permission) {

    }

    public final void requestPermission (int requestCode,String permission) {
        Log.i("test","getPluginContext:" + getPluginContext());

        try {
            ((Slice) getPluginContext()).requestPermission(requestCode,permission);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Resources getPluginResources() {
        return getPluginContext().getResources();
    }
}
