package online.magicbox.desktop;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.georgeyang.lib.UiThread;
import cn.georgeyang.network.NetCallback;
import cn.georgeyang.network.OkHttpRequest;
import cn.georgeyang.network.Resp;
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
    }
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MultiDirectionSlidingDrawer multiDirectionSlidingDrawer;
    private PluginListProcessor processor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        UiThread.init(this).setFlag("init").start(this);
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
                break;
            case "deal":
                if (obj==null) {
                    return null;
                }
                return processor.doSomething((List<PluginItemBean>)obj);
        }
        return null;
    }

    @Override
    public void runInUi(String flag, Object obj, boolean ispublish, float progress) {
        switch (flag) {
            case "init":
                Log.i("test","jni:" + JniTest.hello());
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                mRecyclerView.setAdapter(new NormalRecyclerViewAdapter(this));
                swipeRefreshLayout.setOnRefreshListener(this);
                findViewById(R.id.content).setOnClickListener(this);
                onRefresh();
                break;
            case "deal":
                swipeRefreshLayout.setRefreshing(false);
                break;
        }
    }

    @Override
    public void onRefresh() {
        Logutil.showlog("onRefresh!");
        Map<String,Object> params = new HashMap<>();
        params.put("packageName","online.magicbox.desktop");
        params.put("versionCode","0");
        OkHttpRequest.getInstance().post(getView(),"","http://georgeyang.cn/magicbox/dex",params,this);
    }

    @Override
    public void onSuccess(String flag, List<PluginItemBean> object) {
        Logutil.showlog("obje:" + object.size());
        UiThread.init(this).setFlag("deal").setObject(object).start(this);
    }

    @Override
    public void onFail(String flag, int code, Exception e) {
        swipeRefreshLayout.setRefreshing(false);
    }
}
