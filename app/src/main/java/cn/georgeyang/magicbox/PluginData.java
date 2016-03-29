package cn.georgeyang.magicbox;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.view.LayoutInflater;

/**
 * Created by george.yang on 2016-3-29.
 */
public class PluginData {
    public Context parentContext = null;
    public AssetManager assetManager = null;
    public Resources resources = null;
    public LayoutInflater layoutInflater = null;
    public Resources.Theme theme = null;
    public ClassLoader classLoder = null;
}
