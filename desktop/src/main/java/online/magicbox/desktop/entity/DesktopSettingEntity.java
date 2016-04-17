package online.magicbox.desktop.entity;

import cn.georgeyang.database.Model;

/**
 * Created by george.yang on 16/4/16.
 */
public class DesktopSettingEntity extends Model {
    public int iconSize=3;
    public int iconSort=2;
    public boolean autoUpdate = true;
    public boolean autoCreateShortCut = true;
}
