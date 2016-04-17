package cn.georgeyang.seewifipwd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import java.util.List;

import cn.georgeyang.seewifipwd.adapter.WifiListAdapter;
import cn.georgeyang.seewifipwd.entity.NetWork;
import cn.georgeyang.seewifipwd.util.WifiUtil;
import online.magicbox.lib.Slice;

/**
 * Created by george.yang on 16/4/16.
 */
public class SeeSlice extends Slice {
    public SeeSlice(Context base, Object holder) {
        super(base, holder);
    }

    private ListView listView;
    WifiListAdapter adapter;
    List<NetWork> list;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new WifiListAdapter(this);
        new Thread(){
            @Override
            public void run() {
                list = WifiUtil.listAllWifiDate();
                handler.sendEmptyMessage(1);
            }
        }.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (list==null || list.size()==0) {
                new AlertDialog.Builder(SeeSlice.this).setCancelable(false).setTitle("提示").setMessage("没有权限,软件不能获取wifi密码数据").setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create().show();
            } else {
                adapter.setData(list);
                listView.setAdapter(adapter);
            }
        }
    };

}
