package cn.georgeyang.csdnblog;

import java.util.Collections;
import java.util.List;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.georgeyang.csdnblog.adapter.CommentAdapter;
import cn.georgeyang.csdnblog.bean.BlogComment;
import cn.georgeyang.csdnblog.bean.CommentComparator;
import cn.georgeyang.csdnblog.config.AppConstants;
import cn.georgeyang.csdnblog.util.DateUtil;
import cn.georgeyang.csdnblog.util.JsoupUtil;
import cn.georgeyang.csdnblog.util.Logutil;
import cn.georgeyang.csdnblog.util.ToastUtil;
import cn.georgeyang.database.Mdb;
import cn.georgeyang.network.NetCallback;
import cn.georgeyang.network.OkHttpRequest;
import me.maxwin.view.IXListViewLoadMore;
import me.maxwin.view.IXListViewRefreshListener;
import me.maxwin.view.XListView;
import online.magicbox.lib.Slice;

/**
 * 博客评论列表
 * 
 * @author tangqi
 * @data 2015年7月20日下午8:20:20
 *
 */
public class BlogCommentSlice extends Slice implements IXListViewRefreshListener, IXListViewLoadMore, NetCallback<String> {

	private XListView mListView;
	private CommentAdapter mAdapter;
	private ImageView mReLoadImageView;
	private ImageView mBtnBack;
	private TextView mTvComment;
	private ProgressBar mPbLoading;

//	private HttpAsyncTask mAsyncTask;
	private String mFileName;
	private int mPage = 1;
	private int mPageSize = 20;
//	private BlogCommentDao mBlogCommentDao;

    public BlogCommentSlice(Context base, Object holder) {
        super(base, holder);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);

		initData();
		initComponent();
	}

	private void initData() {
		mFileName = getIntent().getExtras().getString("filename"); // 获得文件名
		mAdapter = new CommentAdapter(this);

//		mBlogCommentDao = DaoFactory.getInstance().getBlogCommentDao(this, mFileName);
	}

	private void initComponent() {
		mPbLoading = (ProgressBar) findViewById(R.id.pb_loading);
		mReLoadImageView = (ImageView) findViewById(R.id.reLoadImage);
		mReLoadImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("click");
				mReLoadImageView.setVisibility(View.INVISIBLE);
				onRefresh();
			}
		});

		mBtnBack = (ImageView) findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTvComment = (TextView) findViewById(R.id.comment);

		mListView = (XListView) findViewById(R.id.listview);
		mListView.setAdapter(mAdapter);
		mListView.NotRefreshAtBegin();
		mListView.setPullRefreshEnable(this);

		// 先预加载数据，再请求最新数据
		mHandler.sendEmptyMessage(AppConstants.MSG_PRELOAD_DATA);
	}

	@Override
	public void onLoadMore() {
		mPage++;
		requestData(mPage);
	}

	@Override
	public void onRefresh() {
		mPage = 1;
		requestData(mPage);
	}

//	@Override
//	public void finish() {
//		super.finish();
//		overridePendingTransition(R.anim.push_no, R.anim.push_right_out);
//	}

	private void requestData(int page) {
//		if (mAsyncTask != null) {
//			mAsyncTask.cancel(true);
//		}
//
//		mAsyncTask = new HttpAsyncTask(this);
		String url = getCommentListURL(mFileName, String.valueOf(page));
//		mAsyncTask.execute(url);
//		mAsyncTask.setOnResponseListener(onResponseListener);
        OkHttpRequest.getInstance().get(getActivity(),true,"",url,null,null,this);
	}

    public static String getCommentListURL(String filename, String pageIndex) {
        return "http://blog.csdn.net/wwj_748/comment/list/" + filename + "?page=" + pageIndex;
    }

    @Override
    public void onSuccess(String flag, String resultString) {
        if (resultString != null) {
            List<BlogComment> list = JsoupUtil.getBlogCommentList(resultString, mPage, mPageSize);
            CommentComparator comparator = new CommentComparator();
            Collections.sort(list, comparator);
            if (mPage == 1) {
                mAdapter.setList(list);
            } else {
                mAdapter.addList(list);
            }
            mAdapter.notifyDataSetChanged();
            mListView.setPullLoadEnable(BlogCommentSlice.this);// 设置可上拉加载
            mTvComment.setText(mAdapter.getCount() + "条");
            saveDB(list);
        }

        			mPbLoading.setVisibility(View.GONE);
			mReLoadImageView.setVisibility(View.GONE);
			mListView.stopRefresh(DateUtil.getDate());
			mListView.stopLoadMore();
    }

    @Override
    public void onFail(String flag, int code, Exception e) {
        ToastUtil.show(BlogCommentSlice.this, "网络已断开");
				mListView.disablePullLoad();

        			mPbLoading.setVisibility(View.GONE);
			mReLoadImageView.setVisibility(View.GONE);
			mListView.stopRefresh(DateUtil.getDate());
			mListView.stopLoadMore();
    }

//	private OnResponseListener onResponseListener = new OnResponseListener() {
//
//		@Override
//		public void onResponse(String resultString) {
//			// TODO Auto-generated method stub
//			// 解析html页面获取列表
//			if (resultString != null) {
//				List<Comment> list = JsoupUtil.getBlogCommentList(resultString, mPage, mPageSize);
//				CommentComparator comparator = new CommentComparator();
//				Collections.sort(list, comparator);
//				if (mPage == 1) {
//					mAdapter.setList(list);
//				} else {
//					mAdapter.addList(list);
//				}
//				mAdapter.notifyDataSetChanged();
//				mListView.setPullLoadEnable(BlogCommentSlice.this);// 设置可上拉加载
//				mTvComment.setText(mAdapter.getCount() + "条");
//				saveDB(list);
//
//			} else {
//				ToastUtil.show(BlogCommentSlice.this, "网络已断开");
//				mListView.disablePullLoad();
//			}
//
//			mPbLoading.setVisibility(View.GONE);
//			mReLoadImageView.setVisibility(View.GONE);
//			mListView.stopRefresh(DateUtil.getDate());
//			mListView.stopLoadMore();
//		}
//	};

	/**
	 * 保存数据库
	 * 
	 * @param list
	 */
	private void saveDB(final List<BlogComment> list) {
        Logutil.showlog("saveDb:" + list);
		new Thread(new Runnable() {
			@Override
			public void run() {
//				mBlogCommentDao.insert(list);
                if (list!=null) {
                    for (BlogComment comment:list) {
                        Logutil.showlog("comm:" + comment.getContent());
                        comment.fileName = mFileName;
                        comment.save();
                    }
                }
			}
		}).start();

	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
            Logutil.showlog("hand:" + msg.what);
			switch (msg.what) {
			case AppConstants.MSG_PRELOAD_DATA:
				mListView.setRefreshTime(DateUtil.getDate());
//				List<Comment> list = mBlogCommentDao.query(mPage);

                List<BlogComment> list = Mdb.getInstance().findAllbyWhere(BlogComment.class,String.format("fileName=%s",new Object[]{mFileName}));
				if (!(list == null || list.size()==0)) {
					mAdapter.setList(list);
					mAdapter.notifyDataSetChanged();
					mListView.setPullLoadEnable(BlogCommentSlice.this);
					mListView.setRefreshTime(DateUtil.getDate());
					mTvComment.setText(mAdapter.getCount() + "条");
				} else {
					// 不请求最新数据，让用户自己刷新或者加载
					mPbLoading.setVisibility(View.VISIBLE);
					requestData(mPage);
					mListView.disablePullLoad();
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};


}
