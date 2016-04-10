package cn.georgeyang.network;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;

import com.squareup.okhttp.Request;

import java.lang.reflect.Type;
import java.util.Map;

import cn.georgeyang.lib.UiThread;
import cn.georgeyang.util.ReflectUtil;

/**
 * 网络请求，带取消
 * Created by george.yang on 16/2/24.
 */
public class OkHttpRequest extends OkRequestBase {
    private static OkHttpRequest instance;

    public static OkHttpRequest getInstance() {
        synchronized (OkRequestBase.class) {
            if (instance == null) {
                instance = new OkHttpRequest();
            }
        }
        return instance;
    }

    public  <T> void get (final Object activityOrFragment,String flag,final String url, final Map<String,Object> params, final NetCallback<T> listener) {
        get(activityOrFragment,false,flag,url,params,null,listener);
    }

        public  <T> void get (final Object activityOrFragment, boolean showDialog, String flag, final String url, final Map<String,Object> params, UiThread.Processor postProcessor, final NetCallback<T> listener) {
        UiThread uiThread = UiThread.init(mContext);
        if (showDialog) {
            uiThread = uiThread.showDialog("加载中",true);
        }
        uiThread.setFlag(flag).setPostProcessor(postProcessor).start(new UiThread.UIThreadEvent() {
            @Override
            public Object runInThread(String flag, Object obj, UiThread.Publisher publisher) {
                Type cls = listener==null?String.class:ReflectUtil.getTypeFromInterface(listener.getClass());
                return get(activityOrFragment,url,params,cls);
            }

            @Override
            public void runInUi(String flag, Object obj, boolean ispublish, float progress) {
                if (isShutdown(activityOrFragment)) {
                    return;
                }
                if (listener!=null) {
                    Resp<T> resp = (Resp<T>) obj;
                    if (resp.success) {
                        listener.onSuccess(flag,resp.data);
                    } else {
                        listener.onFail(flag,resp.code,resp.error);
                    }
                }

            }
        });
    }

    public  <T> void post (final Object activityOrFragment,String flag,final String url, final Map<String,Object> params, final NetCallback<T> listener) {
        post(activityOrFragment,false,flag,url,params,null,listener);
    }

    public  <T> void post (final Object activityOrFragment,String flag,final String url, final Map<String,Object> params,UiThread.Processor postProcessor, final NetCallback<T> listener) {
        post(activityOrFragment,false,flag,url,params,postProcessor,listener);
    }

    public <T> void post(final Object activityOrFragment,boolean showDialog,String flag,final String url, final Map<String, Object> params,UiThread.Processor postProcessor, final NetCallback<T> listener) {
        UiThread  uiThread = UiThread.init(mContext);
        if (showDialog) {
            uiThread = uiThread.showDialog("加载中",true);
        }

        uiThread.setFlag(flag).setPostProcessor(postProcessor).start(new UiThread.UIThreadEvent() {
            @Override
            public Object runInThread(String flag, Object obj, UiThread.Publisher publisher) {
                Type cls = listener==null?String.class: ReflectUtil.getTypeFromInterface(listener.getClass());
                return post(activityOrFragment,url, params,cls);
            }

            @Override
            public void runInUi(String flag, Object obj, boolean ispublish, float progress) {
                if (isShutdown(activityOrFragment)) {
                    return;
                }
                if (listener!=null) {
                    Resp<T> resp = (Resp<T>) obj;
                    if (resp.success) {
                        listener.onSuccess(flag,resp.data);
                    } else {
                        listener.onFail(flag,resp.code,resp.error);
                    }
                }
            }
        });
    }

    private boolean isShutdown (Object activityOrFragment) {
        boolean ret = false;
        if(activityOrFragment instanceof Activity) {
            Activity activity = (Activity) activityOrFragment;
            if (activity.isFinishing()) {
                ret =  true;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (activity.isDestroyed()) {
                    ret =  true;
                }
            }
        } else if (activityOrFragment instanceof Fragment) {
            Fragment fragment = (Fragment) activityOrFragment;
            if (fragment.isRemoving()) {
                ret = true;
            }
        }
        return ret;
    }
}
