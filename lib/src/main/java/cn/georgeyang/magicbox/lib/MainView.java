package cn.georgeyang.magicbox.lib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by george.yang on 2016-3-30.
 */
public class MainView extends PluginBuilder {
    @Override
    public View createView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.fragment_main,null);
    }
}
