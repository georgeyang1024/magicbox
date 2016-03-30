package cn.georgeyang.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

/**
 * 嵌套listView,如果没有，自动创建
 * Created by george.yang on 16/3/21.
 */
public class SwipeRefreshLoadMoreLayout extends SwipeRefreshLayout {

    private static final boolean DeafaultEnablePullRefresh = true;
    private static final boolean DeafaultEnablePullLoad = true;

    private OnLVRequestDataListener mListener;

    public SwipeRefreshLoadMoreLayout(Context context) {
        super(context);
    }

    public SwipeRefreshLoadMoreLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private AutoLoadMoreListView listView;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        listView = findListView(this);
        if (listView == null) {
            listView = new AutoLoadMoreListView(getContext());
            listView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            addView(listView);
        }

        setRefreshEnable(DeafaultEnablePullRefresh);//允许头部刷新
        setLoadMoreEnable(DeafaultEnablePullLoad);//允许尾部加载更多

        setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mListener!=null) {
                    mListener.onRefresh();
                }
            }
        });
        if (mListener!=null) {
            listView.setOnLVRequestDataListener(mListener);
        }
    }

    public void setAdapter(ListAdapter adapter) {
        listView.setAdapter(adapter);
    }

    public void setRefreshEnable(boolean enable) {
        setEnabled(enable);
    }

    public void setLoadMoreEnable(boolean enable) {
        listView.setLoadMoreEnable(enable);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        super.setRefreshing(refreshing);
    }

    public void setLoadingMore(boolean loadingMore) {
        listView.setLoadingMore(loadingMore);
    }


    private AutoLoadMoreListView findListView(ViewGroup vg) {
        int count = vg.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = vg.getChildAt(i);
            if (view instanceof AutoLoadMoreListView) {
                return (AutoLoadMoreListView) view;
            } else if (view instanceof ViewGroup) {
                AutoLoadMoreListView listview = findListView((ViewGroup) view);
                if (listview != null) {
                    return listview;
                }
            }
        }
        return null;
    }

    public void setOnLVRequestDataListener(
            OnLVRequestDataListener listener) {
        this.mListener = listener;
        if (listView!=null) {
            listView.setOnLVRequestDataListener(listener);
        }
    }


    @Override
    public boolean isRefreshing() {
        return super.isRefreshing();
    }

    public boolean isLoadingMore() {
        if (listView==null) {
            return false;
        }
        return listView.isLoadingMore();
    }

}
