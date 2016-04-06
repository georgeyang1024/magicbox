package online.magicbox.desktop.entity;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Created by george.yang on 2016-4-6.
 */
public class AppInfoBean {
    public static final int Type_plugin = 1,Type_Normal=2,Type_System=3;
    public int type;
    public String packageName,mainName,imagUrl,name;
    public String downloadUrl;
    public Drawable icon;
    public Intent intent;

    public int versionCodeid,id;
    public String updateTime,addTime,version,isDel;

//    @Override
//    public int compareTo(AppInfoBean another) {
//        if (this.type!=another.type) {
//            return (this.type-another.type) * 1000 + this.name.compareTo(another.name);
//        }
//        return this.name.compareTo(another.name);
//    }
}
