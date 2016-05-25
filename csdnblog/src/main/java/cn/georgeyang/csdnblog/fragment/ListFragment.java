package cn.georgeyang.csdnblog.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.georgeyang.csdnblog.BlogContentSlice;
import cn.georgeyang.csdnblog.R;
import cn.georgeyang.csdnblog.adapter.BlogListAdapter;
import cn.georgeyang.csdnblog.adapter.ChannelListAdapter;
import cn.georgeyang.csdnblog.bean.BlogItem;
import cn.georgeyang.csdnblog.config.AppConstants;
import cn.georgeyang.csdnblog.util.DateUtil;
import cn.georgeyang.csdnblog.util.JsoupUtil;
import cn.georgeyang.csdnblog.util.Logutil;
import cn.georgeyang.csdnblog.util.NetUtil;
import cn.georgeyang.csdnblog.util.ToastUtil;
import cn.georgeyang.network.NetCallback;
import cn.georgeyang.network.OkHttpRequest;
import me.maxwin.view.IXListViewLoadMore;
import me.maxwin.view.IXListViewRefreshListener;
import me.maxwin.view.XListView;
import online.magicbox.lib.PluginActivity;

/**
 * web前端
 * Created by george.yang on 16/5/1.
 */
public class ListFragment extends BaseFragment implements NetCallback<String>,IXListViewRefreshListener, AdapterView.OnItemClickListener {
    private String url;

    private XListView mListView;
    private ImageView mReLoadImageView;
    private ProgressBar mPbLoading;
    private BlogListAdapter mAdapter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logutil.showlog("onCreateView:" + inflater.getClass().getClasses());


        Logutil.showlog("id:" + R.layout.activity_bloglist);

        View view = LayoutInflater.from(plugInContext).inflate(R.layout.activity_bloglist,null);
        url = getArguments().getString("url");

        mPbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
        mReLoadImageView = (ImageView) view.findViewById(R.id.reLoadImage);
        mReLoadImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mReLoadImageView.setVisibility(View.INVISIBLE);
                mPbLoading.setVisibility(View.VISIBLE);

                getData(false);
            }
        });

        mListView = (XListView) view.findViewById(R.id.listView_blog);
        mAdapter = new BlogListAdapter(view.getContext());

        mListView.setPullRefreshEnable(this);
        mListView.NotRefreshAtBegin();
        mListView.setRefreshTime(DateUtil.getDate());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        getData(true);

        return view;
    }


    private void getData (boolean showDia) {
        OkHttpRequest.getInstance().get(this,showDia,"",url,null,null,this);
    }

    private  List<BlogItem> mBlogList;
    @Override
    public void onSuccess(String flag, String resultString) {
//        List<BlogItem> mBlogList = JsoupUtil.getHotBlogList(AppConstants.BLOG_CATEGORY_ALL, resultString);
//        for (BlogItem item:mBlogList) {
//            Logutil.showlog("item:" + item.getTitle());
//        }
        mPbLoading.setVisibility(View.GONE);
        mReLoadImageView.setVisibility(View.GONE);
        mListView.stopRefresh(DateUtil.getDate());

        if (!TextUtils.isEmpty(resultString)) {
            mBlogList = JsoupUtil.getHotBlogList(AppConstants.BLOG_CATEGORY_ALL, resultString);
            mAdapter.setList(mBlogList);
        } else {
            if (NetUtil.isNetAvailable(getActivity())) {
                ToastUtil.show(getActivity(), "暂无最新数据");
            } else {
                ToastUtil.show(getActivity(), "网络已断开");
                mReLoadImageView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onFail(String flag, int code, Exception e) {
        mPbLoading.setVisibility(View.GONE);
        mReLoadImageView.setVisibility(View.GONE);
        mListView.stopRefresh(DateUtil.getDate());

        if (NetUtil.isNetAvailable(getActivity())) {
            ToastUtil.show(getActivity(), "暂无最新数据");
        } else {
            ToastUtil.show(getActivity(), "网络已断开");
            mReLoadImageView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BlogItem item = (BlogItem) parent.getAdapter().getItem(position);
        Intent i = PluginActivity.buildIntent(getActivity(), BlogContentSlice.class);
//        Intent i = new Intent();
//        i.setClass(HotListActivity.this, BlogContentActivity.class);
        i.putExtra("blogItem", JSONObject.toJSONString(item));
        startActivity(i);
    }

    @Override
    public void onRefresh() {
        getData(false);
    }
}
