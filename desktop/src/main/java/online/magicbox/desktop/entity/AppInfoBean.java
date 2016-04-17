package online.magicbox.desktop.entity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;

import online.magicbox.desktop.MainSlice;

/**
 * Created by george.yang on 2016-4-6.
 */
public class AppInfoBean implements Comparable<AppInfoBean>{
    public static final int Type_plugin = 1,Type_Normal=2,Type_System=3;
    public static final int Sort_time=2,Sort_name=1;
    public int type;
    public String packageName,mainClass,imageUrl,name;
    public String downUrl;
    public Drawable icon;
    public Intent intent;
    public boolean isInstall;//已安装?
    public boolean downloading;//下载中
    public int id;
    public float size;
    public String updateTime,addTime,isDel;
    public int installVersionCode,lastVersionCode;
    public long installTime;

    private static long Year = 1000 * 60 * 60 * 24 * 365;

    public boolean hasUpdate () {
       return isInstall && lastVersionCode!=installVersionCode;
    }


    @Override
    public int compareTo(AppInfoBean another) {
        if (MainSlice.settingEntity==null || MainSlice.settingEntity.iconSort!=2) {
            if (this.type!=another.type) {
                return (this.type-another.type) * -10000 + this.name.toLowerCase().compareTo(another.name.toLowerCase());
            } else if (this.type == Type_plugin && this.type==another.type) {
                int l = this.isInstall?1:0;
                int r = another.isInstall?1:0;
                if (l!=r) {
                    return (l-r) * -20000 + this.name.toLowerCase().compareTo(another.name.toLowerCase());
                }
            }
            return this.name.toLowerCase().compareTo(another.name.toLowerCase());
        } else {
            long type = (this.type-another.type) * Year * 1000;
            long time = this.installTime-another.installTime;
            long all = type + time;
            if (all>0) {
                return 1;
            } else if (all==0) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    @Override
    public String toString() {
        return hashCode() + " " + name + " " + installTime;
    }
}
