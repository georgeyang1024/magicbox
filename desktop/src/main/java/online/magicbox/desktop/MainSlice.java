package online.magicbox.desktop;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import it.sephiroth.widget.MultiDirectionSlidingDrawer;
import online.magicbox.desktop.adapter.NormalRecyclerViewAdapter;
import online.magicbox.lib.Slice;

/**
 * Created by george.yang on 2016-3-30.
 */
public class MainSlice extends Slice {
    public MainSlice(Context base, Object holder) {
        super(base, holder);
    }
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MultiDirectionSlidingDrawer multiDirectionSlidingDrawer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        Log.i("test","jni:" + JniTest.hello());
        multiDirectionSlidingDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.drawer);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(new NormalRecyclerViewAdapter(this));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },3000);
            }
        });
        multiDirectionSlidingDrawer.setOnDrawerCloseListener(new MultiDirectionSlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                Log.i("test","close");
            }
        });
        multiDirectionSlidingDrawer.setOnDrawerOpenListener(new MultiDirectionSlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                Log.i("test","open");
            }
        });
    }
}
