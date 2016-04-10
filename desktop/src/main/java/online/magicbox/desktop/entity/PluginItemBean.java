package online.magicbox.desktop.entity;

import cn.georgeyang.database.Mdb;
import cn.georgeyang.database.Model;

/**
 * Created by george.yang on 2016-4-6.
 */
public class PluginItemBean extends Model {
//    public static final int Type_plugin = 1,Type_Normal=2,Type_System=3;
//    public int type;
    public String packageName,mainClass,name;
    public String downUrl;
    public String imageUrl;
    public float size;
//    public Intent intent;

    public int versionCode,Id;
    public String updateTime,addTime,isDel;


    //local Var
    public int installVersionCode;


//    @Override
//    public int compareTo(AppInfoBean another) {
//        if (this.type!=another.type) {
//            return (this.type-another.type) * 1000 + this.name.compareTo(another.name);
//        }
//        return this.name.compareTo(another.name);
//    }
}
