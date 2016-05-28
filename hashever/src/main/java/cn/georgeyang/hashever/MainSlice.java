package cn.georgeyang.hashever;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import online.magicbox.lib.Slice;

/**
 * Created by george.yang on 16/5/8.
 */
public class MainSlice extends Slice implements View.OnClickListener {
    public MainSlice(Context base, Object holder) {
        super(base, holder);
    }



    private Fragment[] fragments;
    // ViewPager指示器布局
    private LinearLayout[] indicators;
    private ImageView[] imgs;
    private FrameLayout frame_layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frame_layout = (FrameLayout) findViewById(R.id.frame_layout);

        fragments = new Fragment[4];
        fragments[0] = new HashFragment();
        fragments[1] = new FileFragment();
        fragments[2] = new OtherFragment();
        fragments[3] = new AboutFragment();

        imgs = new ImageView[fragments.length];
        indicators = new LinearLayout[fragments.length];
        LinearLayout layout_bottom = (LinearLayout) findViewById(R.id.layout_bottom);
        for (int i=0;i<layout_bottom.getChildCount();i++) {
            indicators[i] = (LinearLayout) layout_bottom.getChildAt(i);
            imgs[i] = (ImageView) indicators[i].getChildAt(0);
            indicators[i].setOnClickListener(this);
        }

        selectMenu(0);
    }

    @Override
    public void onClick(View v) {
        for (int i=0;i<indicators.length;i++) {
            if (indicators[i]==v) {
                selectMenu(i);
            }
        }
    }



    private void selectMenu(int index) {
        Log.d("test","select:" + index);

        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        for (int i=0;i<indicators.length;i++) {
            transaction.hide(fragments[i]);
            if (i==index) {
                imgs[i].setVisibility(View.VISIBLE);
            } else {
                imgs[i].setVisibility(View.GONE);
            }
        }

        if (!fragments[index].isAdded()) {
            Log.d("test","add:" + fragments[index]);
            transaction.add(R.id.frame_layout,fragments[index]);
        }
        transaction.show(fragments[index]);
        transaction.commitAllowingStateLoss();
    }


}
