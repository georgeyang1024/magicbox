package cn.georgeyang.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by george.yang on 16/4/16.
 */
public class NetUtil {
    // 判断当前是否使用的是 WIFI网络
    public static boolean isWifiActive(Context icontext){
        Context context = icontext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info;
        if (connectivity != null) {
            info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
