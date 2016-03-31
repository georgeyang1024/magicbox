package online.magicbox.app;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by george.yang on 2015/7/1.
 */
public class UiThread {
    private static final int MAXTHREADCOUNT = 20;//最大执行线程数量
    private static Handler mainHandler;
    private static ExecutorService pool;
    private Object obj;//运行时需要的obj
    private String flag = "";//防止null
    private long runDelayMillis;//运行前延迟
    private long callbackDelayMills;//回调前延时
    private Dialog dialog;
    private UIThreadEvent event;
    private UIpublisher publisher;
    private Object back;
    private Context mcontext;
    private Processor preProcessor;//数据预处理
    private Processor postProcessor;//数据后置处理
    private int threadPriority;//线程优先级，默认0，-1慢一些


    public UiThread(Context context) {
        mcontext = context;
        if (mainHandler==null ){
//			if (Looper.myLooper() != Looper.getMainLooper()) {
//				throw new InternalError("uiThread cannot init from thread!");
//			}
            mainHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg  == null) return;

                    Object obj = msg.obj;
                    if (obj instanceof UiThread) {
                        UiThread data = (UiThread)obj;
                        //如果是activity,finish后就不回调mainthread
                        if (data.mcontext instanceof Activity) {
                            if (((Activity)data.mcontext).isFinishing()) {
                                return;
                            }
                        }

                        if (data.dialog !=null) {
                            //关闭加载窗
                            data.dialog.dismiss();
                        }
                        data.event.runInUi(data.flag, data.back, false, -1);

                        //清理
                        data.dialog = null;
                        data.event = null;
                        data.publisher = null;
                        data = null;
                    } else if (obj instanceof PublishData) {
                        PublishData data = (PublishData)obj;

                        if (data.uithread.dialog instanceof ProgressDialog) {
                            //如果设置显示了ProgressDialog,自动更新dialog的进度
                            if (dialog.isShowing() && data.progress > 0 && data.progress < 100) {
                                ((ProgressDialog)data.uithread.dialog).setMessage(data.progress + "%");
                            }
                        }

                        data.uithread.event.runInUi(data.uithread.flag, data.obj, true, data.progress);

                        //清理
                        data.uithread = null;
                        data.obj = null;
                        data=null;
                    }
                    msg.obj = null;
                }
            };
        }
        if (pool==null) {
            pool = Executors.newFixedThreadPool(MAXTHREADCOUNT);//固定线程池
        }
    }

    public static UiThread init (Context context) {
        return new UiThread(context);
    }

    public UiThread setFlag (String flag) {
        this.flag = flag;
        return this;
    }

    public UiThread setObject (Object obj) {
        this.obj = obj;
        return this;
    }

    public UiThread showDialog(Dialog dialog) {
        if (this.dialog!=null) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }

        this.dialog = dialog;
        return this;
    }

    public UiThread setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
        return this;
    }

    public UiThread setPreProcessor(Processor preProcessor) {
        this.preProcessor = preProcessor;
        return this;
    }

    public UiThread setPostProcessor(Processor postProcessor) {
        this.postProcessor = postProcessor;
        return this;
    }

    public UiThread showDialog(String tip, boolean canCancel) {
        if (dialog!=null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        if (tip == null) {
            dialog = ProgressDialog.show(mcontext, null, "加载中", true,canCancel, null);
        } else {
            dialog = ProgressDialog.show(mcontext, null, tip, true, canCancel, null);
        }
        return this;
    }

    public UiThread setRunDelay(long delayMillis) {
        this.runDelayMillis =delayMillis;
        return this;
    }

    public UiThread setCallBackDelay(long delayMillis) {
        this.callbackDelayMills =delayMillis;
        return this;
    }

    public void start(UIThreadEvent event) {
        this.event = event;
        publisher = new UIpublisher(this);

        if (dialog!=null) {
            dialog.show();
        }

        pool.execute(new Runnable() {
            @Override
            public void run() {
                //kernel
                android.os.Process.setThreadPriority(threadPriority);
                SystemClock.sleep(runDelayMillis);
                Object tagObj = obj;
                if (preProcessor!=null) {
                    tagObj = preProcessor.doSomething(obj);
                }
                Object retObj = UiThread.this.event.runInThread(flag,tagObj,publisher);
                if (postProcessor!=null) {
                    retObj = postProcessor.doSomething(retObj);
                }
                UiThread.this.back = retObj;
                Message msg = Message.obtain();
                msg.obj = UiThread.this;
                mainHandler.sendMessageDelayed(msg, callbackDelayMills);
            }
        });
    }

    public interface UIThreadEvent {
        Object runInThread(String flag, Object obj, Publisher publisher);
        void runInUi(String flag, Object obj, boolean ispublish, float progress);
    }

    public interface Publisher {
        void publishProgress(float progress);
        void publishObject(Object object);
    }

    public interface Processor {
        Object doSomething(Object obj);
    }

    public class PublishData  {
        Object obj;
        float progress;
        UiThread uithread;
    }

    public class UIpublisher implements Publisher{
        public UiThread uithread;

        public UIpublisher (UiThread uithread) {
            this.uithread = uithread;
        }

        @Override
        public void publishProgress(float progress) {
            PublishData data = new PublishData();
            data.uithread = uithread;
            data.progress = progress;
            data.obj = null;

            Message msg = Message.obtain();
            msg.obj = data;
            mainHandler.sendMessage(msg);
        }

        @Override
        public void publishObject(Object object) {
            PublishData data = new PublishData();
            data.uithread = uithread;
            data.progress = -1;
            data.obj = object;

            Message msg = Message.obtain();
            msg.obj = data;
            mainHandler.sendMessage(msg);
        }
    }
}
