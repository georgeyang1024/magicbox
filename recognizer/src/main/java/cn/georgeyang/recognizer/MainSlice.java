package cn.georgeyang.recognizer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.georgeyang.recognizer.widget.RecoShowVIew;
import online.magicbox.lib.Slice;

/**
 * Created by george.yang on 16/5/6.
 */
public class MainSlice extends Slice implements View.OnClickListener, RecoShowVIew.OnPhoneKeyDown {
    public MainSlice(Context base, Object holder) {
        super(base, holder);
    }


    private TextView textView;
    private RecoShowVIew recoShowVIew;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recoShowVIew = (RecoShowVIew) findViewById(R.id.showView);
        textView = (TextView) findViewById(R.id.textView);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        recoShowVIew.setListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                recoShowVIew.start();
                break;
            case R.id.btn_stop:
                recoShowVIew.stop();
                break;
        }
    }

    private char lastKey;
    StringBuffer sb = new StringBuffer();
    @Override
    public void onGetter(char chr) {
        if (chr != ' ' && lastKey != chr) {
            lastKey = chr;
            sb.append(chr);
            textView.setText(sb.toString());
        }
    }
}
