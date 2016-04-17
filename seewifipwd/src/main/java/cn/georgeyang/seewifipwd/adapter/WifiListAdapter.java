package cn.georgeyang.seewifipwd.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.georgeyang.seewifipwd.R;
import cn.georgeyang.seewifipwd.entity.NetWork;
import cn.georgeyang.seewifipwd.util.ViewHolder;

/**
 * Created by george.yang on 16/4/16.
 */
public class WifiListAdapter extends AppAdapter<NetWork> {

    public WifiListAdapter(Context activity) {
        super(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pwd,null);
        }
        NetWork vo = mlist.get(position);
        TextView name = ViewHolder.get(convertView,R.id.tv_title);
        name.setText(vo.getSsid());

        TextView pwd = ViewHolder.get(convertView,R.id.tv_pwd);
        pwd.setText(vo.getPsk());
        return convertView;
    }
}
