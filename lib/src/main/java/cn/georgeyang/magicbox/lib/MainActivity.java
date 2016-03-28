package cn.georgeyang.magicbox.lib;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class MainActivity extends PlugActivity  {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        setContentView(createView(inflater,savedInstanceState));
    }

    @Override
    public View createView(LayoutInflater inf, Bundle bundle) {
        return inf.inflate(R.layout.activity_main,null);
    }

}
