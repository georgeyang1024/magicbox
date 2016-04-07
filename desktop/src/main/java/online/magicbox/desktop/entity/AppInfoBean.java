package online.magicbox.desktop.entity;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Created by george.yang on 2016-4-6.
 */
public class AppInfoBean implements Comparable<AppInfoBean>{
    public static final int Type_plugin = 1,Type_Normal=2,Type_System=3;
    public int type;
    public String packageName,mainName,imagUrl,name;
    public String downUrl;
    public Drawable icon;
    public Intent intent;
    public boolean isInstall;//已安装?
    public boolean downloading;//下载中
    public int versionCodeid,id;
    public String updateTime,addTime,version,isDel;


    @Override
    public int compareTo(AppInfoBean another) {
        if (this.type!=another.type) {
            return (this.type-another.type) * -1000 + this.name.toLowerCase().compareTo(another.name.toLowerCase());
        }
        return this.name.toLowerCase().compareTo(another.name.toLowerCase());
    }
}
