package cn.georgeyang.devtest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import online.magicbox.lib.PluginActivity;
import online.magicbox.lib.Slice;

/**
 * Created by george.yang on 16/6/1.
 */
public class MainSlice extends Slice implements View.OnClickListener {
    public MainSlice(Context base, Object holder) {
        super(base, holder);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.runAsPlugin).setOnClickListener(this);
        findViewById(R.id.sdcard).setOnClickListener(this);
        findViewById(R.id.location).setOnClickListener(this);
        findViewById(R.id.picSelect).setOnClickListener(this);
        findViewById(R.id.listPlugin).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.runAsPlugin:
                Class c = null;
                try {
                    c = Class.forName("online.magicbox.app.PluginActivity");
                } catch (Exception e ) {

                }
                if (c==null) {
                    showMsg("当前:独立运行");
                } else {
                    showMsg("当前:以插件运行");
                }
                break;
            case R.id.sdcard:
                requestPermission(234, Manifest.permission.READ_EXTERNAL_STORAGE);
                break;
            case R.id.location:
                startLocation();
                break;
            case R.id.picSelect:
                Intent picker = PluginActivity.buildIntent(getActivity(),"online.magicbox.desktop","ImageSelectorSlice","1");
                picker.putExtra("select_count_mode",0);
                getActivity().startActivityForResult(picker,100);
                break;
        }
    }

    @Override
    public void onReceiveLocation(Object location, String[] info) {
        super.onReceiveLocation(location, info);
        showMsg("位置信息:\n" + location + "\n" + Arrays.toString(info));
    }

    @Override
    public void onPermissionGiven(int code,String permission) {
        if (code==234) {
            showMsg("已获取权限!");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("test","onActivityResult:" + requestCode + " " + resultCode);
        if (requestCode==100) {
            if (resultCode==RESULT_OK) {
                ArrayList<String> list = data.getStringArrayListExtra("select_result");
                if (!(list==null || list.size()==0)) {
                    String path = list.get(0);
                    showMsg(path);
                }
            } else {
                showMsg("error");
            }
        }

    }

    private void showMsg(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
}
