package cn.georgeyang.magicbox.lib;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

/**
 * Created by george.yang on 2016-3-29.
 */
public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main,container,false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("demo", JSONObject.toJSONString(new ArrayList()));
        Log.d("demo", JSONObject.VERSION);
        Log.d("demo","MainFragment resume!!!");
    }
}
