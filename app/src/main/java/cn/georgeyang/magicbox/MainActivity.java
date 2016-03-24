package cn.georgeyang.magicbox;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setText(new Test().call());
        //Class ref in pre-verified class resolved to unexpected implementation
        //不能有接口,其实是冲突

    }
}
