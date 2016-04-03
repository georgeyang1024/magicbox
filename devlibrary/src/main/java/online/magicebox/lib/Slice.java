package online.magicebox.lib;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

    public void finish () {

    }

    public void onDestroy() {

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
                method =receiver.getClass().getMethod(methodName,parameterTypes);
                methodCache.put(key,method);
            }
            return method.invoke(receiver,args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
