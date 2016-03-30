package cn.georgeyang.minipplication;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by george.yang on 2016-3-29.
 */
public class MainFragment extends PluginFragment {
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        view =  inflater.inflate(R.layout.fragment_main,container,false);
        LayoutInflater layoutInflater = LayoutInflater.from(mProxyActivity);
        view = layoutInflater.inflate(R.layout.fragment_main,container,false);
//        view = getLayout("fragment_main");

        return view;

    }

}
