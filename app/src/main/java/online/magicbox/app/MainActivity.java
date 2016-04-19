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

import com.mingle.widget.LoadingView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;


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

        Log.i("test","#:" + new Test().getString());
        Log.i("test","Id:" + R.string.app_name);

        sharedPreferences= getSharedPreferences("app", Context.MODE_PRIVATE);
        desktopApk = sharedPreferences.getString("desktopApk",App.defaultApkName);
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
        Log.i("test","desktopApk:" + desktopApk);
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

        File apkFile = new File(getFilesDir().getAbsolutePath(),desktopApk);
        boolean needCheck = false;
        needCheck = needCheck || TextUtils.isEmpty(desktopApk);
        needCheck = needCheck || (System.currentTimeMillis() - desktopUpdateTime) > UpdateDesktopGap;
        needCheck = needCheck || (desktopUpdateTime > System.currentTimeMillis());//系统时间变更
        needCheck = needCheck || (apkFile == null || !apkFile.exists());
        if (needCheck) {
            if (!(apkFile==null || !apkFile.exists())){
                //默认文件存在,上线专用
                intoDesktop();
            } else {
                setContentView(R.layout.activity_main);
                loadingView = (LoadingView) findViewById(R.id.loadingView);
                loadingView.setLoadingText(getString(R.string.load_resource));
            }


            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("desktopApk","");
            editor.putLong("desktopUpdateTime",0);
            editor.putString("desktopVersionCode","0");
            editor.commit();

            UiThread.init(this).setFlag("desktop").start(this);
            return;
        }

        intoDesktop();

        if ((System.currentTimeMillis() - hotfixUpdateTime) > UpdateHotFixGap || hotfixUpdateTime>System.currentTimeMillis()) {
            UiThread.init(this).setFlag("hotFix").start(this);
        } else {
            Log.i("test","no need update hotfix!");
        }
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

    @Override
    public Object runInThread(String flag, Object obj, UiThread.Publisher publisher) {
        if (flag.equals("hotFix")) {
            try {
                HashMap<String,Object> params = new HashMap<>();
                params.put("packageName",getPackageName());
                params.put("versionCode",dexVersionCode);
                String json = HttpUtil.post(Vars.HotFixGetterUrl,params);

                Log.i("test","post result:" + json);

                JSONArray jsonArray = new JSONArray(json);
                if (jsonArray.length()>0) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String packageName = jsonObject.optString("packageName", "");
                    String version = jsonObject.optInt("versionCode",1)+"";
                    String fileName = String.format("%s_%s.dex",new Object[]{packageName,version});

                    String downloadUrl = jsonObject.optString("downloadUrl");
                    File saveFile = new File(getFilesDir().getAbsolutePath(),fileName);
                    HttpUtil.downLoadFile(downloadUrl,saveFile);

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
                HashMap<String,Object> params = new HashMap<String, Object>();
                params.put("packageName",Vars.DesktopPackageName);
                params.put("versionCode",desktopVersionCode);
                String json = HttpUtil.post(Vars.DesktopGetterUrl,params);

                Log.i("test","post result:" + json);

                JSONArray jsonArray = new JSONArray(json);
                if (jsonArray.length()>0) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String packageName = jsonObject.optString("packageName","");
                    String versionCode = jsonObject.optInt("version",1)+"";
                    String fileName = packageName + "_" + versionCode + ".apk";

                    String downloadUrl = jsonObject.optString("downloadUrl");
                    File saveFile = new File(getFilesDir().getAbsolutePath(),fileName);
                    HttpUtil.downLoadFile2(downloadUrl,saveFile);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("desktopApk",fileName);
                    editor.putLong("desktopUpdateTime",System.currentTimeMillis());
                    editor.putString("desktopVersionCode",versionCode);
                    //每次更新完桌面都要重新启动
                    editor.putBoolean("needRestart",true);
                    editor.commit();

                    desktopVersionCode = versionCode;
                    desktopApk = fileName;
                    return fileName;
                }
            } catch (Exception e) {
                Log.i("test",Log.getStackTraceString(e));
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
