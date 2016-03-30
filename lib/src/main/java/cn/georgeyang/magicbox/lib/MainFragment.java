package cn.georgeyang.magicbox.lib;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

import cn.georgeyang.UiThread;
import cn.georgeyang.adapter.AppListAdapter;
import cn.georgeyang.adapter.NormalRecyclerViewAdapter;
import cn.georgeyang.widget.SwipeRefreshLoadMoreLayout;

/**
 * Created by george.yang on 2016-3-29.
 */
public class MainFragment extends PluginFragment implements UiThread.UIThreadEvent {
    private View view;
    private RecyclerView mRecyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        view =  inflater.inflate(R.layout.fragment_main,container,false);
//        getLayout
        view = getLayout("fragment_main");
        UiThread.init(getActivity()).setFlag("init").start(this);

//        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));//这里用线性宫格显示 类似于grid view
//        mRecyclerView.setAdapter(new NormalRecyclerViewAdapter(getActivity()));

        return view;

    }

    @Override
    public Object runInThread(String flag, Object obj, UiThread.Publisher publisher) {
        switch (flag) {
            case "init":

//                Log.d("test","method!!!!!!");
//                Method[] methods = getActivity().getClass().getMethods();
//                for (Method method:methods) {
//                    Log.d("test",method.getName());
//                }
//                Log.d("test","method!!!!!!@@");
//                methods = getActivity().getClass().getDeclaredMethods();
//                for (Method method:methods) {
//                    Log.d("test",method.getName());
//                }

                break;
        }
        return null;
    }

    @Override
    public void runInUi(String flag, Object obj, boolean ispublish, float progress) {
        switch (flag) {
            case "init":
                break;
        }

    }
}
