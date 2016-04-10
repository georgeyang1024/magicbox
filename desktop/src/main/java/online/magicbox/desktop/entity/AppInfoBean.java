package online.magicbox.desktop.entity;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Created by george.yang on 2016-4-6.
 */
public class AppInfoBean implements Comparable<AppInfoBean>{
    public static final int Type_plugin = 1,Type_Normal=2,Type_System=3;
    public int type;
    public String packageName,mainName,imageUrl,name;
    public String downUrl;
    public Drawable icon;
    public Intent intent;
    public boolean isInstall;//已安装?
    public boolean downloading;//下载中
    public int id;
    public float size;
    public String updateTime,addTime,isDel;
    public int installVersionCode,lastVersionCode;


    @Override
    public int compareTo(AppInfoBean another) {
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
    }
}
