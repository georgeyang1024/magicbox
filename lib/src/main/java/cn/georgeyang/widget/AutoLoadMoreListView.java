package cn.georgeyang.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;

import cn.georgeyang.magicbox.lib.R;


/**
 * Created by george.yang on 16/3/21.
 */
public class AutoLoadMoreListView extends ListView implements AbsListView.OnScrollListener {
    public AutoLoadMoreListView(Context context) {
        super(context);
        this.context = context;
        initListView();
    }

    public AutoLoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initListView();
    }

    public AutoLoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initListView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AutoLoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initListView();
    }


    private boolean loadMoreEnable;
    private boolean loadingMore;

    /** 底部显示正在加载的页面 */
    private View footerView = null;

//    CircularProgressBar mProgressView;
//    CircleProgressBar mProgressView;
    /** 存储上下文 */
    private Context context;
    /** 上拉刷新的ListView的回调监听 */
    private OnLVRequestDataListener myPullUpListViewCallBack;
//    /** 记录第一行Item的数值 */
//    private int firstVisibleItem;

    protected void setLoadMoreEnable(boolean enable) {
        this.loadMoreEnable = enable;
        if (!enable) {
            goneFooter();
        } else {
            visibleFooter();
        }
    }

    public boolean isLoadingMore() {
        return loadingMore;
    }

    /**
     * 初始化ListView
     */
    private void initListView() {
        // 为ListView设置滑动监听
        setOnScrollListener(this);
        // 去掉底部分割线
        setFooterDividersEnabled(false);
        initBottomView();
    }

    /**
     * 初始化话底部页面
     */
    public void initBottomView() {
        if (footerView == null) {
            footerView = LayoutInflater.from(this.context).inflate(
                    R.layout.listview_loadmorefooter, null);
//            mProgressView = (CircularProgressBar) footerView.findViewById(R.id.footer_progressbar);
        }
        FrameLayout footerLayoutHolder = new FrameLayout(getContext());
        footerLayoutHolder.addView(footerView, 0, new AbsListView.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));
        addFooterView(footerLayoutHolder);
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (!loadMoreEnable) {
            return;
        }

        //当滑动到底部时
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if (view.getLastVisiblePosition() == view.getCount() - 1) {
                if (!loadingMore) {
                    loadingMore = true;
                    if (myPullUpListViewCallBack!=null) {
                        myPullUpListViewCallBack.onLoadMore();
                    }
                }
            }
        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        if (!loadMoreEnable) {
            return;
        }

//        this.lastItemInde = firstVisibleItem + visibleItemCount - 1 ;

        if (footerView != null) {
            //判断可视Item是否能在当前页面完全显示
            if (visibleItemCount == totalItemCount) {
                goneFooter();
            } else {
                visibleFooter();
            }
        }
    }

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
            if (!loadingMore) {
                goneFooter();
            } else {
                visibleFooter();
//            mProgressView.stopProgress();
//            mProgressView.
        }
    }

    public void setOnLVRequestDataListener(
            OnLVRequestDataListener listener) {
        this.myPullUpListViewCallBack = listener;
    }


    private void goneFooter() {
        if (footerView!=null) {
//            removeFooterView(footerView);
            footerView.setVisibility(GONE);
        }
    }

    private void visibleFooter () {
        if (footerView!=null) {
//            if (getFooterViewsCount()==0) {
//                addFooterView(footerView);
//            }
            footerView.setVisibility(VISIBLE);
        }
    }
}
