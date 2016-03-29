package cn.georgeyang.magicbox.lib;

import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class MainActivity extends PlugActivity  {



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        setContentView(createView(inflater,savedInstanceState));
    }

    @Override
    public View createView(LayoutInflater inf, Bundle bundle) {
        FrameLayout rootView = new FrameLayout(inf.getContext());
        rootView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.setBackgroundColor(Color.GRAY);
        rootView.setId(android.R.id.background);

        rootView.postDelayed(new Runnable() {
            @Override
            public void run() {
               FragmentTransaction ft =  getFragmentManager().beginTransaction();
                ft.add(android.R.id.background,new MainFragment(),"main");
                ft.commit();
            }
        },2000);


        return rootView;
//        return inf.inflate(R.layout.activity_main,null);
    }

}
