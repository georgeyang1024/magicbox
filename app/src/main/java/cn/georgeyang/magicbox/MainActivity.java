package cn.georgeyang.magicbox;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.mingle.widget.LoadingView;
import java.lang.reflect.Field;

import cn.georgeyang.lib.HttpUtil;
import cn.georgeyang.lib.UiThread;
import cn.georgeyang.loader.PluginProxyContext;


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

        UiThread.init(this).setCallBackDelay(3000).start(new UiThread.UIThreadEvent() {
            @Override
            public Object runInThread(String flag, Object obj, UiThread.Publisher publisher) {
                return HttpUtil.get("http://georgeyang.cn:8080/ptool/getip");
            }

            @Override
            public void runInUi(String flag, Object obj, boolean ispublish, float progress) {
                ProxyActivity.init("cn.magicbox.plugin","magicbox");
                Intent intent = ProxyActivity.buildIntent("cn.georgeyang.magicbox.lib","MainView",null);
//                Intent intent = ProxyActivity.buildIntent("cn.georgeyang.minipplication","MainFragment",null);
                startActivity(intent);
                finish();


            }
        });

//        PluginProxyContext context = new PluginProxyContext(this);
//        context.loadResources(getFilesDir().getAbsolutePath() + "/cn.georgeyang.minipplication_1.0.apk","cn.georgeyang.minipplication" );
//        Log.i("test","加载的string:" + context.getString("app_name"));
    }

}
