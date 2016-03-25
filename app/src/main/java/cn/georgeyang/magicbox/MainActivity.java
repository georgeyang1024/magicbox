package cn.georgeyang.magicbox;

import android.app.Activity;
import android.os.Bundle;

import com.mingle.widget.LoadingView;


import cn.georgeyang.lib.HttpUtil;
import cn.georgeyang.lib.UiThread;


public class MainActivity extends Activity {
    private  LoadingView loadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingView = (LoadingView) findViewById(R.id.loadingView);
        loadingView.setLoadingText("loading");
        //Class ref in pre-verified class resolved to unexpected implementation
        //不能有接口,其实是冲突

        UiThread.init(this).start(new UiThread.UIThreadEvent() {
            @Override
            public Object runInThread(String flag, Object obj, UiThread.Publisher publisher) {
                return HttpUtil.get("http://georgeyang.cn:8080/ptool/getip");
            }

            @Override
            public void runInUi(String flag, Object obj, boolean ispublish, float progress) {
                loadingView.setLoadingText(obj.toString());
            }
        });
    }
}
