package cn.georgeyang.hashever;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import online.magicbox.lib.PluginFragment;
import online.magicbox.lib.Slice;

/**
 * Created by george.yang on 16/5/8.
 */
public class AboutFragment extends PluginFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getPluginLayoutInflater().inflate(R.layout.fragment_about,null);
    }
}
