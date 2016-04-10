package online.magicbox.desktop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.georgeyang.database.Mdb;
import cn.georgeyang.lib.UiThread;
import cn.georgeyang.network.NetCallback;
import cn.georgeyang.network.OkHttpRequest;
import cn.georgeyang.util.Logutil;
import it.sephiroth.widget.MultiDirectionSlidingDrawer;
import online.magicbox.desktop.adapter.NormalRecyclerViewAdapter;
import online.magicbox.desktop.entity.AppInfoBean;
import online.magicbox.desktop.entity.PluginItemBean;
import online.magicbox.desktop.subclass.PluginListProcessor;
import online.magicbox.lib.Slice;

/**
 * Created by george.yang on 2016-3-30.
 */
public class MainSlice extends Slice implements View.OnClickListener, UiThread.UIThreadEvent, SwipeRefreshLayout.OnRefreshListener, NetCallback<List<PluginItemBean>> {
    public MainSlice(Context base, Object holder) {
        super(base, holder);
        adapter.setActivity((Activity) holder);
    }
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MultiDirectionSlidingDrawer multiDirectionSlidingDrawer;
    private PluginListProcessor processor;
    private NormalRecyclerViewAdapter adapter = new NormalRecyclerViewAdapter();
    private static List<AppInfoBean> cacheList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OkHttpRequest.init(this);
        Mdb.init(this);
        setContentView(R.layout.fragment_main);
        UiThread.init(this).setFlag("init").start(this);
        UiThread.init(this).setFlag("cache").start(this);
    }

    @Override
    public boolean onBackPressed() {
        if (multiDirectionSlidingDrawer.isOpened()) {
            multiDirectionSlidingDrawer.animateClose();
            return true;
        }
        return super.onBackPressed();
    }


    @Override
    public void onClick(View v) {
        Log.i("test","click:" + v.getId());
    }

    @Override
    public Object runInThread(String flag, Object obj, UiThread.Publisher publisher) {
        switch (flag) {
            case "init":
                processor = new PluginListProcessor(this);
                multiDirectionSlidingDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.drawer);
                swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
                mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                //如果确定每个item的内容不会改变RecyclerView的大小，设置这个选项可以提高性能
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        if (newState==RecyclerView.SCROLL_STATE_IDLE) {
                            Vars.scrolling = false;
                        } else {
                            Vars.scrolling = true;
                        }
                        Logutil.showlog("scrolling?" + Vars.scrolling);
                    }
                });
                break;
            case "deal":
                if (obj==null) {
                    return null;
                }
                cacheList = processor.doSomething((List<PluginItemBean>)obj);
                adapter.setDataInThread(cacheList);
                return cacheList;
            case "cache":
                if (cacheList!=null) {
                    adapter.setDataInThread(cacheList);
                    return cacheList;
                }
                try {
                    String json = OkHttpRequest.getGetRequestCache(getView(), Vars.ListPlugin,buildParams());
                    Type type = new TypeToken<List<PluginItemBean>>(){}.getType();
                    List<PluginItemBean> list = new Gson().fromJson(json, type);
                    return list;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return null;
    }

    @Override
    public void runInUi(String flag, Object obj, boolean ispublish, float progress) {
        switch (flag) {
            case "init":
                Log.i("test","jni:" + JniTest.hello());
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
                swipeRefreshLayout.setOnRefreshListener(this);
                findViewById(R.id.content).setOnClickListener(this);
                mRecyclerView.setAdapter(adapter);
                break;
            case "deal":
                swipeRefreshLayout.setRefreshing(false);
                if (obj!=null) {
                    adapter.notifyDataSetChanged();
                }
                if (dialog!=null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                break;
            case "cache":
                if (obj!=null) {
                    if (obj instanceof List) {
                        List list = (List) obj;
                        if (list.size()>0) {
                            Object item0 = ((List)obj).get(0);
                            if (item0 instanceof AppInfoBean) {
                                adapter.notifyDataSetChanged();
                                return;
                            }
                        }
                    }
                    UiThread.init(this).setFlag("deal").setObject(obj).start(this);
                    showDialog();
                } else {
                    showDialog();
                    onRefresh();
                }
                break;
        }
    }
    private ProgressDialog dialog;
    private void showDialog () {
        dialog = ProgressDialog.show(this, null, "加载中", true,false, null);
    }

    @Override
    public void onRefresh() {
        Map<String,Object> params = buildParams();
        OkHttpRequest.getInstance().get(getView(),"", Vars.ListPlugin,params,this);
    }

    private Map<String,Object> buildParams () {
        Map<String,Object> params = new HashMap<>();
        params.put("packageName","online.magicbox.desktop");
        params.put("versionCode","0");
        params.put("lastId","0");
        return params;
    }

    @Override
    public void onSuccess(String flag, List<PluginItemBean> object) {
        UiThread.init(this).setFlag("deal").setObject(object).start(this);
    }

    @Override
    public void onFail(String flag, int code, Exception e) {
        swipeRefreshLayout.setRefreshing(false);
    }
}
