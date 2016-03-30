package cn.georgeyang.magicbox.lib;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

/**
 * Created by george.yang on 2016-3-29.
 */
public class MainFragment extends PluginFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main,container,false);
        view.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Uri uri = Uri.parse("magicbox:/plugin?packageName=cn.georgeyang.magicbox.lib&action=TestFragment&animType=LeftInRightOut");
//                Intent intent = new Intent("lib");
//                intent.setData(uri);
//                startActivity(intent);
                loadFragment(TestFragment.class,PluginConfig.ZoomShow);
//                Intent intent = buildIntent(TestFragment.class,PluginConfig.ZoomShow);
//                startActivity(intent);
            }
        });
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("demo", JSONObject.toJSONString(new ArrayList()));
        Log.d("demo", JSONObject.VERSION);
        Log.d("demo","MainFragment resume!!!");
    }

    @Override
    public boolean onBackPressed() {
        Log.i("test","onBackPressed");
        return super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("test","onKeyDown:" + keyCode);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onReciveMessage(Integer type, Object object) {
        Log.i("test","rec:" + object);
        super.onReciveMessage(type, object);
    }
}
