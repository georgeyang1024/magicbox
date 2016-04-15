package cn.georgeyang.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.widget.Toast;

import online.magicbox.desktop.R;

/**
 * Created by george.yang on 2016-4-15.
 */
public class ShortCutUtil {
    /**
     * 创建快捷图标
     */
    public static void createShortCut(Context context,String name,Intent actionIntent) {
        // 先判断该快捷是否存在
        if (!isShortCutExist(context)) {
            Intent intent = new Intent();
            // 指定动作名称
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            // 指定快捷方式的图标
//            Intent.ShortcutIconResource icon = Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_launcher);
//            icon.packageName = "cn.georgeyang.wificonnect";
            //online.magicbox.desktop:mipmap/ic_launcher
//            Logutil.showlog("name:" + context.getResources().getResourceName(R.mipmap.ic_launcher));
//            icon.resourceName = "online.georgeyang.wificonnect:mipmap/ic_launcher";
//            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

            // 指定快捷方式的图标 bitmap
//            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, Bitmap);
            // 指定快捷方式的名称
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
            // 指定快捷图标激活哪个activity
//            Intent actionIntent = new Intent();
//            actionIntent.setAction(Intent.ACTION_MAIN);
//            actionIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//            ComponentName component = new ComponentName(this, MainActivity.class);
//            actionIntent.setComponent(component);
//            intent.putExtra(, actionIntent);
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,actionIntent);
            context.sendBroadcast(intent);
            Toast.makeText(context,"图标已创建",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context,"图标已存在",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断快捷图标是否在数据库中已存在
     */
    public static  boolean isShortCutExist(Context context) {
        boolean isExist = false;
        int version = android.os.Build.VERSION.SDK_INT;
        Uri uri = null;
        if (version < 2.0) {
            uri = Uri.parse("content://com.android.launcher.settings/favorites");
        } else {
            uri = Uri.parse("content://com.android.launcher2.settings/favorites");
        }
        String selection = " title = ?";
        String[] selectionArgs = new String[] { "YouTube" };
        Cursor c = context.getContentResolver().query(uri, null, selection, selectionArgs, null);

        if (c != null && c.getCount() > 0) {
            isExist = true;
        }

        if (c != null) {
            c.close();
        }

        return isExist;
    }
}
