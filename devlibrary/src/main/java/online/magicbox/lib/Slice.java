package online.magicbox.lib;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 切片，模擬activity的操作
 * Created by george.yang on 2016-3-30.
 */
public abstract class Slice extends ContextWrapper {
    private Object mHolder;
    private View mView;

    public Slice(Context base, Object holder) {
        super(base);
        mHolder = holder;
    }

    public Activity getActivity () {
        return (Activity) mHolder;
    }

    public final LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(this);
    }

    public final void setContentView (View contentView) {
        mView = contentView;
        callMethodByCache(mHolder,"setContentView",new Class[]{View.class},new Object[]{contentView});
    }

    public final View getView() {
        return mView;
    }

    public final void setContentView (int layoutId) {
        mView = LayoutInflater.from(this).inflate(layoutId,null);
        setContentView(mView);
    }

    public void onCreate(Bundle savedInstanceState) {

    }

    public void startProxyActivity() {
        callMethodByCache(mHolder,"startProxyActivity",new Class[]{},new Object[]{});
    }

    public final void pushMessage (int type,Object object) {
        callMethodByCache(mHolder,"pushMessage",new Class[]{int.class,Object.class},new Object[]{type,object});
    }

    public final Intent buildIntent (Class<? extends Slice>  clazz) {
        return buildIntent(clazz,"System");
    }
    public final Intent buildIntent (Class<? extends Slice> clazz,String animType) {
        HashMap<String,String> params = new HashMap<>();
        params.put("animType",animType);
        return buildIntent(clazz.getPackage().getName(),clazz.getSimpleName(),params);
    }

    public final Intent buildIntent (Class<? extends Slice> clazz,Map<String,String> params) {
        return buildIntent(clazz.getPackage().getName(),clazz.getSimpleName(),params);
    }

    public final Intent buildIntent(String packageName,String className, Map<String,String> params) {
        return (Intent) callMethodByCache(mHolder,"buildIntent",new Class[]{String.class,String.class,Map.class},new Object[]{packageName,className,params});
    }

    public final void loadFragment (Class<? extends Slice> clazz) {
        loadFragment(clazz,PluginConfig.System);
    }
    public final void loadFragment (Class<? extends Slice> clazz,String animType) {
        HashMap<String,String> params = new HashMap<>();
        params.put("animType",animType);
        loadFragment(clazz,params);
    }
    public final void loadFragment (Class<? extends Slice> clazz,Map<String,String> params) {
        try {
            Intent intent = buildIntent(clazz,params);
            startActivity(intent);
        } catch (Exception e) {
        }
    }

    public final View findViewById (int id) {
        try {
            return mView.findViewById(id);
        } catch (Exception e) {

        }
        return null;
    }

    public void onReceiveMessage (int type,Object object) {

    }

    public void onResume () {

    }

    public final void finish () {
        callMethodByCache(mHolder, "finish", new Class[]{}, new Object[]{});
    }

    public void onDestroy() {

    }

    public void onPause () {

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    public boolean onOptionsMenuClosed(Menu menu) {
        return false;
    }

    public void onSaveInstanceState(Bundle outState) {

    }

    public boolean onBackPressed () {
        //true表示攔截,false表示可以finish
        return false;
    }

    public void onStart() {
    }

    public void onStop() {
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    private static final Map<String,Method> methodCache = new WeakHashMap<>();
    private static Object callMethodByCache(Object receiver,String methodName,Class[] parameterTypes,Object[] args) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return false;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public boolean onMenuOpened(int featureId, Menu menu) {
        return false;
    }

    public boolean onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        return false;
    }

    public boolean closeOptionsMenu() {
        return false;
    }

    public boolean openOptionsMenu() {
        return false;
    }

    public boolean onContextMenuClosed(Menu menu) {
        return false;
    }

    public boolean onContextItemSelected(MenuItem item) {
        return false;
    }



    public final void startLocation() {
        callMethodByCache(mHolder,"startLocation",new Class[]{},new Object[]{});
    }

    public final void stopLocation() {
        callMethodByCache(mHolder,"stopLocation",new Class[]{},new Object[]{});
    }

    public void onReceiveLocation(Object location,String[] info) {

    }

    public final void httpGet(String flag, final String url, final Map<String,Object> params) {
        callMethodByCache(mHolder, "httpGet", new Class[]{String.class, String.class, Map.class}, new Object[]{flag, url,params});
    }

    public final void httpPost(String flag, final String url, final Map<String,Object> params) {
        callMethodByCache(mHolder, "httpPost", new Class[]{String.class,String.class, Map.class}, new Object[]{flag, url,params});
    }

    public void onReceiveHttpData (String flag,String data) {

    }

    /** Standard activity result: operation canceled. */
    public static final int RESULT_CANCELED    = 0;
    /** Standard activity result: operation succeeded. */
    public static final int RESULT_OK           = -1;
    /** Start of user-defined activity results. */
    public static final int RESULT_FIRST_USER   = 1;
    public final void setResult(int code) {
        callMethodByCache(mHolder,"setResult",new Class[]{int.class},new Object[]{code});
    }


    public final void setResult(int code,Intent intent) {
        callMethodByCache(mHolder,"setResult",new Class[]{int.class,Intent.class},new Object[]{code,intent});
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public FragmentManager getFragmentManager () {
        return (FragmentManager) callMethodByCache(mHolder,"getFragmentManager",new Class[]{},new Object[]{});
    }

    public Intent getIntent () {
        return (Intent) callMethodByCache(mHolder,"getIntent",new Class[]{},new Object[]{});
    }


    //请求权限
    public final void requestPermission (int requestCode,String permission) {
        Log.d("test","requestPermission:" + permission);
        callMethodByCache(mHolder,"requestPermission",new Class[]{int.class,String.class},new Object[]{requestCode,permission});
    }

    public void onPermissionGiven (int requestCode,String permission) {
    }
}
