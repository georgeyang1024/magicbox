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
//                loadingView.setLoadingText(obj.toString());


                try {
                    Field field = getFragmentManager().getClass().getDeclaredField("mAdded");
                    field.setAccessible(true);
//                    Log.i("ping", field.get(getFragmentManager()).toString());

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("ping",Log.getStackTraceString(e));
                }

                Uri uri = Uri.parse("magicbox://plugin?packageName=cn.georgeyang.magicbox.lib&action=MainFragment&animType=LeftInRightOut");
                Intent intent = new Intent("magicbox.plugin");
                intent.setData(uri);
                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

//                try {
//                    String plugPath = AssetUtils.copyAsset(MainActivity.this, "plug.apk", getFilesDir());
//                    PluginProxyContext pluginProxyContext = new PluginProxyContext(MainActivity.this,plugPath);
//                    ClassLoader loader = initClassLoader(plugPath);
//
//                    //动态加载插件Activity
//                    Class pluginActivityClass = loader.loadClass("cn.georgeyang.magicbox.ProxyActivity");
//                    startActivity(new Intent(pluginProxyContext,pluginActivityClass));
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.i("demo", "load activity error:"+ Log.getStackTraceString(e));
//                }

//                finish();
            }
        });
    }

}
