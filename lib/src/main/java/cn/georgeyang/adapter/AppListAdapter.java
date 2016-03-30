package cn.georgeyang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.georgeyang.magicbox.lib.R;

/**
 * Created by george.yang on 2016-3-30.
 */
public class AppListAdapter extends BaseAdapter {
    private List<String> mList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    public AppListAdapter (Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList==null?0:mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList==null?null:mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null) {
            convertView = mLayoutInflater.inflate(R.layout.fragment_main,null);
        }
        return convertView;
    }
}
