package online.magicbox.desktop.subclass;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import cn.georgeyang.util.Logutil;
import online.magicbox.desktop.entity.AppInfoBean;
import online.magicbox.desktop.entity.PluginItemBean;
import online.magicbox.lib.PluginActivity;
import online.magicbox.lib.PluginConfig;

/**
 * Created by george.yang on 2016-4-6.
 */
public class PluginListProcessor {
    private Context mcontext;
    private String appDownloadDir;
    public PluginListProcessor (Context context) {
        mcontext = context;
        appDownloadDir = mcontext.getFilesDir().getAbsolutePath();
    }

    public List<AppInfoBean> doSomething(List<PluginItemBean> pluginList) {
        List<AppInfoBean> ret = new ArrayList<>();

        for (PluginItemBean pluginItemBean:pluginList) {
            AppInfoBean infoBean = new AppInfoBean();
            infoBean.imagUrl = pluginItemBean.icon;
            infoBean.name = pluginItemBean.name;
            infoBean.type = AppInfoBean.Type_plugin;
            infoBean.downUrl = pluginItemBean.downUrl;
            infoBean.packageName = pluginItemBean.packageName;
            infoBean.version = pluginItemBean.versionCode+"";
            Intent intent = PluginActivity.buildIntent(pluginItemBean.packageName,pluginItemBean.mainClass, PluginConfig.System,pluginItemBean.versionCode+"");
            infoBean.intent = intent;
            String apkFileName = String.format("%s_%s.apk",new Object[]{pluginItemBean.packageName,pluginItemBean.versionCode+""});
            infoBean.isInstall = new File(appDownloadDir,apkFileName).exists();
            ret.add(infoBean);
        }

        //列出普通应用程序
        PackageManager pm = mcontext.getPackageManager();
        //得到系统安装的所有程序包的PackageInfo对象
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for(PackageInfo pi:packages) {
            AppInfoBean infoBean = new AppInfoBean();
            infoBean.isInstall = true;
            infoBean.name = pi.applicationInfo.loadLabel(pm)+"";
            infoBean.packageName = pi.applicationInfo.packageName;
            infoBean.icon = pi.applicationInfo.loadIcon(pm);
            Intent intent = pm.getLaunchIntentForPackage(pi.packageName);
            if (intent !=null) {
                infoBean.intent = intent;
                if((pi.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)!=0) {
                    infoBean.type = AppInfoBean.Type_System;
                } else if((pi.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM) == 0) {
                    infoBean.type = AppInfoBean.Type_Normal;
                }
                ret.add(infoBean);
            }
        }
        Collections.sort(ret);
        return ret;
    }
}
