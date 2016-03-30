package cn.georgeyang.loader;


import android.util.Log;

import java.util.HashMap;

import dalvik.system.DexClassLoader;

/**
 * Created by george.yang on 15/11/19.
 */
public class PlugClassLoder extends DexClassLoader {
    public String dexPath;
    public static final String TAG = "PlugManger";

    private static final HashMap<String, PlugClassLoder> plugLoderMap = new HashMap<>();

    public PlugClassLoder(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, libraryPath, parent);
        this.dexPath = dexPath;
        if (!plugLoderMap.containsKey(dexPath)) {
            plugLoderMap.put(dexPath, this);
        }
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve)
            throws ClassNotFoundException {
        Class<?> clazz = findLoadedClass(className);
        if (clazz != null)
            return clazz;
        try {
            clazz = getParent().loadClass(className);
        } catch (Exception e) {
        }
        if (clazz != null)
            return clazz;
        if (plugLoderMap != null) {
            for (PlugClassLoder c : plugLoderMap.values()) {
                try {
                    clazz = c.findClass(className);
                    break;
                } catch (Exception e) {

                }
            }
        }
        if (clazz != null)
            return clazz;
        clazz = findClass(className);
        return clazz;
    }

}
