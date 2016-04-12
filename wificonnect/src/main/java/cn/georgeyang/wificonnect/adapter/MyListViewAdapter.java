package cn.georgeyang.wificonnect.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.georgeyang.wificonnect.R;

/**
 * Created by Xiho on 2016/2/1.
 */
public class MyListViewAdapter extends BaseAdapter {

    private List<ScanResult> datas;
    private Context context;
    // 取得WifiManager对象
    private WifiManager mWifiManager;
    private ConnectivityManager cm;

    public void setDatas(List<ScanResult> datas) {
        this.datas = datas;
    }

    public MyListViewAdapter(Context context, List<ScanResult> datas) {
        super();
        this.datas = datas;
        this.context = context;
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public int getCount() {
        if (datas == null) {
            return 0;
        }
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder tag = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.mylist_wifi_item, null);
            tag = new Holder();
            tag.txtWifiName = (TextView) convertView
                    .findViewById(R.id.txt_wifi_name);
            tag.txtWifiDesc = (TextView) convertView
                    .findViewById(R.id.txt_wifi_desc);
            tag.imgWifiLevelIco = (ImageView) convertView
                    .findViewById(R.id.img_wifi_level_ico);
            convertView.setTag(tag);
        }

        // 设置数据
        Holder holder = (Holder) convertView.getTag();
        // Wifi 名字
        holder.txtWifiName.setText(datas.get(position).SSID);
        // Wifi 描述
        String desc = "";
        String descOri = datas.get(position).capabilities;
        if (descOri.toUpperCase().contains("WPA-PSK")) {
            desc = "WPA";
        }
        if (descOri.toUpperCase().contains("WPA2-PSK")) {
            desc = "WPA2";
        }
        if (descOri.toUpperCase().contains("WPA-PSK")
                && descOri.toUpperCase().contains("WPA2-PSK")) {
            desc = "WPA/WPA2";
        }
        int level = datas.get(position).level;
        if (TextUtils.isEmpty(desc)) {
            // desc = "未受保护的网络";
            // 网络信号强度
            int imgId = R.mipmap.wifi_none_4;
            if (Math.abs(level) > 100) {
                imgId = R.mipmap.wifi_none_3;
            } else if (Math.abs(level) > 80) {
                imgId = R.mipmap.wifi_none_3;
            } else if (Math.abs(level) > 70) {
                imgId = R.mipmap.wifi_none_3;
            } else if (Math.abs(level) > 60) {
                imgId = R.mipmap.wifi_none_2;
            } else if (Math.abs(level) > 50) {
                imgId = R.mipmap.wifi_none_2;
            } else {
                imgId = R.mipmap.wifi_none_1;
            }
            holder.imgWifiLevelIco.setImageResource(imgId);
        } else {
            int imgId = R.mipmap.wifi_4;
            if (Math.abs(level) > 100) {
                imgId = R.mipmap.wifi_3;
            } else if (Math.abs(level) > 80) {
                imgId = R.mipmap.wifi_3;
            } else if (Math.abs(level) > 70) {
                imgId = R.mipmap.wifi_3;
            } else if (Math.abs(level) > 60) {
                imgId = R.mipmap.wifi_2;
            } else if (Math.abs(level) > 50) {
                imgId = R.mipmap.wifi_2;
            } else {
                imgId = R.mipmap.wifi_1;
            }
            holder.imgWifiLevelIco.setImageResource(imgId);

        }
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        String g1 = wifiInfo.getSSID();
        Log.e("g1============>", g1);
        Log.e("g2============>", datas.get(position).SSID);
        String g2 = "\"" + datas.get(position).SSID + "\"";
        NetworkInfo.State wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        if (wifi == NetworkInfo.State.CONNECTING) {
            if (g2.endsWith(g1)) {
                desc = "连接中...";
            }
        } else if (wifi == NetworkInfo.State.CONNECTED) {
            if (g2.endsWith(g1)) {
                desc = "已连接";
            }
        }
        holder.txtWifiDesc.setText(desc);
        return convertView;
    }

    public static class Holder {
        public TextView txtWifiName;
        public TextView txtWifiDesc;
        public ImageView imgWifiLevelIco;
    }
}
