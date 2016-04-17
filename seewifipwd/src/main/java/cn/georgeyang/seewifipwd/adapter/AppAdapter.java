package cn.georgeyang.seewifipwd.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 基本的适配器
 * @author touch_ping
 *
 * @param <T>
 */
public abstract class AppAdapter<T> extends BaseAdapter {
	public Context mActivity;
	public List<T> mlist;
	public static LayoutInflater mlayoutInflater;
	
	public AppAdapter(Context activity) {
		this.mActivity = activity;
		if (mlayoutInflater==null) {
			mlayoutInflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
	}
	
	public void setData(List<T> list) {
		this.mlist = list;
		notifyDataSetChanged();
	}



	@Override
	public int getCount() {
		return mlist==null?0:mlist.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mlist==null?null:mlist.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

}
