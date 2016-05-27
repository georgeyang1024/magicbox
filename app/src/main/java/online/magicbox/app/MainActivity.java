package online.magicbox.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.mingle.widget.LoadingView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.jpush.android.api.JPushInterface;


public class MainActivity extends Activity implements UiThread.UIThreadEvent {
    private  LoadingView loadingView;
    private static final long UpdateHotFixGap = 1000 * 60 * 60 ;//一小时更新一次
    private static final long UpdateDesktopGap = UpdateHotFixGap * 24 * 3;//三天桌面更新一次
    private String desktopApk,desktopVersionCode;
    private String hotfixDex,dexVersionCode;
    private long desktopUpdateTime,hotfixUpdateTime;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences= getSharedPreferences("app", Context.MODE_PRIVATE);
//        desktopApk = sharedPreferences.getString("desktopApk",App.defaultApkName);
        desktopApk = sharedPreferences.getString("desktopApk","");
        desktopUpdateTime = sharedPreferences.getLong("desktopUpdateTime",0);
        desktopVersionCode = sharedPreferences.getString("desktopVersionCode","0");
        hotfixDex = sharedPreferences.getString("hotfixDex","");
        hotfixUpdateTime = sharedPreferences.getLong("hotfixUpdateTime",0);
        int versionCode = 0;
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(),0).versionCode;
        } catch (Exception e) {}
        dexVersionCode = sharedPreferences.getString("dexVersionCode",versionCode+"");

        boolean needRestart = sharedPreferences.getBoolean("needRestart",false);
        if (needRestart) {
            setContentView(R.layout.activity_main);
            loadingView = (LoadingView) findViewById(R.id.loadingView);
            loadingView.setLoadingText(getString(R.string.load_resource));
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle(R.string.load_fail_title).setMessage(R.string.bugFix_needRestart).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("needRestart",false);
                    editor.commit();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }).setCancelable(false).create();
            dialog.show();
            return;
        }

        boolean needClearCache = sharedPreferences.getBoolean("clearLayoutInflaterCache",false);
        if (needClearCache) {
            clearLayoutInflaterCache();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("clearLayoutInflaterCache",false);
            editor.commit();
        }

        if ((System.currentTimeMillis() - hotfixUpdateTime) > UpdateHotFixGap || hotfixUpdateTime>System.currentTimeMillis()) {
            UiThread.init(this).setFlag("hotFix").start(this);
        } else {
//            Log.i("test","no need update hotfix!");
//            Log.i("test","up:" + ((System.currentTimeMillis() - hotfixUpdateTime) > UpdateHotFixGap));
//            Log.i("test","up2:" + (hotfixUpdateTime>System.currentTimeMillis()));
        }

        File apkFile = new File(getFilesDir().getAbsolutePath(),desktopApk);
        boolean needCheck = false;
        needCheck = needCheck || TextUtils.isEmpty(desktopApk);
        needCheck = needCheck || (System.currentTimeMillis() - desktopUpdateTime) > UpdateDesktopGap;
        needCheck = needCheck || (desktopUpdateTime > System.currentTimeMillis());//系统时间变更
        needCheck = needCheck || (apkFile == null || !apkFile.exists());
        if (needCheck) {
//            if (!(apkFile==null || !apkFile.exists())){
//                //默认文件存在,上线专用
//                intoDesktop();
//            } else {
                setContentView(R.layout.activity_main);
                loadingView = (LoadingView) findViewById(R.id.loadingView);
                loadingView.setLoadingText(getString(R.string.load_resource));
//            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("desktopApk","");
            editor.putLong("desktopUpdateTime",0);
            editor.putString("desktopVersionCode","0");
            editor.commit();

            UiThread.init(this).setFlag("desktop").start(this);
        } else {
            intoDesktop();
        }

        //end
    }

    private boolean isIntoDesktop;
    public void intoDesktop () {
        if (isIntoDesktop) {
            return;
        }
        isIntoDesktop = true;
        PluginActivity.init("online.magicbox.plugin","magicbox");
        Intent intent = PluginActivity.buildIntent(this,Vars.DesktopPackageName,"MainSlice",desktopVersionCode);
        startActivity(intent);
        finish();
    }

    private void clearLayoutInflaterCache() {
        try {
            Field field = LayoutInflater.class.getDeclaredField("sConstructorMap");
            field.setAccessible(true);
            HashMap<String, Constructor<? extends View>> map = (HashMap<String, Constructor<? extends View>>) field.get(null);
            map.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object runInThread(String flag, Object obj, UiThread.Publisher publisher) {
        if (flag.equals("hotFix")) {
            try {
                Map<String,Object> params = HttpUtil.buildBaseParams(this);
                params.put("packageName",getPackageName());
                params.put("versionCode",dexVersionCode);
                String json = HttpUtil.post(Vars.HotFixGetterUrl,params);

//                Log.i("test","hotFix version:" + dexVersionCode);
                Log.i("test","hotFix post result:" + json);

                JSONArray jsonArray = new JSONArray(json);
                if (jsonArray.length()>0) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String packageName = jsonObject.optString("packageName", "");
                    String version = jsonObject.optInt("versionCode",1)+"";
                    String fileName = String.format("%s_%s.dex",new Object[]{packageName,version});

                    String downloadUrl = jsonObject.optString("downloadUrl");
                    File saveFile = new File(getFilesDir().getAbsolutePath(),fileName);
                    HttpUtil.downLoadFile(downloadUrl,saveFile);

//                    Log.i("test","saveFile:"  + saveFile.getAbsolutePath());

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("dexVersionCode",version);
                    editor.putString("hotfixDex",fileName);
                    editor.putLong("hotfixUpdateTime",System.currentTimeMillis());
                    editor.putBoolean("needRestart",true);
                    editor.commit();
                }
            } catch (Exception e) {
                Log.i("test",Log.getStackTraceString(e));
                e.printStackTrace();
            }
        } else if (flag.equals("desktop")) {
            //downLoad hotFix dex
            try {
                Map<String,Object> params = HttpUtil.buildBaseParams(this);
                params.put("packageName",Vars.DesktopPackageName);
                params.put("versionCode",desktopVersionCode);
                String json = HttpUtil.post(Vars.DesktopGetterUrl,params);

                Log.i("test","desktop post result:" + json);

                JSONArray jsonArray = new JSONArray(json);
                if (jsonArray.length()>0) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String packageName = jsonObject.optString("packageName","");
                    String versionCode = jsonObject.optInt("versionCode",1)+"";
                    String fileName = packageName + "_" + versionCode + ".apk";

                    String downloadUrl = jsonObject.optString("downloadUrl");
                    File saveFile = new File(getFilesDir().getAbsolutePath(),fileName);
                    HttpUtil.downLoadFile2(downloadUrl,saveFile);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("desktopApk",fileName);
                    editor.putLong("desktopUpdateTime",System.currentTimeMillis());
                    editor.putString("desktopVersionCode",versionCode);
                    //每次更新完桌面都需要清理LayoutInflater的Cache,不然会因为ClassLoder不同，ClassA转ClassA转化失败
                    editor.putBoolean("clearLayoutInflaterCache",true);
                    editor.commit();

                    desktopVersionCode = versionCode;
                    desktopApk = fileName;
                    return fileName;
                }
            } catch (Exception e) {
//                Log.i("test",Log.getStackTraceString(e));
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void runInUi(String flag, Object obj, boolean ispublish, float progress) {
        if (flag.equals("desktop")) {
            if (obj==null) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle(R.string.load_fail_title).setMessage(R.string.load_fail_content).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).create();
                dialog.show();
            } else {
                intoDesktop();
            }
        }
    }
}
