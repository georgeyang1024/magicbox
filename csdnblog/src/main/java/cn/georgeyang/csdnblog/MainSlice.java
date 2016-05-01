package cn.georgeyang.csdnblog;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.georgeyang.csdnblog.adapter.CNKFixedPagerAdapter;
import cn.georgeyang.csdnblog.adapter.Madapter;
import cn.georgeyang.csdnblog.config.CategoryManager;
import cn.georgeyang.csdnblog.fragment.ListFragment;
import cn.georgeyang.database.Mdb;
import cn.georgeyang.network.OkHttpRequest;
import online.magicbox.lib.Slice;

/**
 * Created by george.yang on 16/5/1.
 */
public class MainSlice extends Slice implements View.OnClickListener {
    public MainSlice(Context base, Object holder) {
        super(base, holder);
    }


    private TabLayout tab_layout;
    private ViewPager info_viewpager;
    private List<Fragment> fragments;
    private CNKFixedPagerAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(android.support.design.R.style.Theme_AppCompat_Light,true);
//        getActivity().setTheme(android.support.design.R.style.Theme_AppCompat_Light);

        setContentView(R.layout.slice_main);
        OkHttpRequest.init(this);
        Mdb.init(this);

        tab_layout=(TabLayout)findViewById(R.id.tab_layout);
        info_viewpager=(ViewPager)findViewById(R.id.info_viewpager);

        fragments = new ArrayList<>();
        for (int i=0;i< CategoryManager.urls.length;i++) {
            ListFragment fragment = new ListFragment();
            fragment.setPlugInContext(this);
            Bundle bundle = new Bundle();
            bundle.putString("url",CategoryManager.urls[i]);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }

        findViewById(R.id.btn_back).setOnClickListener(this);
//        Fragmentpag
        //创建Fragment的 ViewPager 自定义适配器
        adapter = new CNKFixedPagerAdapter(getActivity().getFragmentManager());
        //设置显示的标题
        adapter.setTitles(CategoryManager.categoryNames);
        //设置需要进行滑动的页面Fragment
        adapter.setFragments(fragments);

        info_viewpager.setAdapter(adapter);
        tab_layout.setupWithViewPager(info_viewpager);


        //设置Tablayout
        //设置TabLayout模式 -该使用Tab数量比较多的情况
        tab_layout.setTabMode(TabLayout.MODE_SCROLLABLE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }
}
