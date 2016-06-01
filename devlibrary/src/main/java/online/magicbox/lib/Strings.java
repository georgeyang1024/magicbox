package online.magicbox.lib;

import android.content.Context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 多语言支持
 * Created by george.yang on 16/5/28.
 */
public class Strings {
    public static final Map<String,String[]> localeMap = new HashMap<>();
    static {
        localeMap.put("openPermission",new String[]{"please give me permission:%s","请给应用开通%s权限以继续"});
        localeMap.put("noPermission",new String[]{"no permission given:%s","没有权限:%s"});
    }

    public static String get(Context context,String key) {
        return get(context,key,null);
    }

    public static String get(Context context,String key,Object[] params) {
        String ret = key;
        try {
            Locale locale = context.getResources().getConfiguration().locale;
            int index = 0;
            if (locale.equals(Locale.CHINA)) {
                index = 1;
            } else {
                index = 0;
            }
            ret = String.format(localeMap.get(key)[index],params);
        } catch (Exception e) {
        }
       return ret;
    }
}
