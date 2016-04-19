package online.magicbox.app;


import android.os.Build;
import android.util.Log;

import java.io.File;
import java.util.HashMap;

import dalvik.system.DexClassLoader;

/**
 * Created by george.yang on 15/11/19.
 */
public class PlugClassLoder extends DexClassLoader {
    public String dexPath;

    public static final HashMap<String, PlugClassLoder> plugClassLoderCache = new HashMap<>();

    public PlugClassLoder(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, libraryPath, parent);
        File file = new File(dexPath);
        this.dexPath = dexPath;
        if (!(file==null || !file.exists())) {
            if (!plugClassLoderCache.containsKey(dexPath)) {
                plugClassLoderCache.put(dexPath, this);
            }
        }
    }

    @Override
    public String findLibrary(String name) {
        return super.findLibrary(name);
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

//        if (plugLoderMap != null) {
//            for (PlugClassLoder c : plugLoderMap.values()) {
//                try {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                        clazz = c.findClass(className);
//                    }
//                    break;
//                } catch (Exception e) {
//
//                }
//            }
//        }
//        if (clazz != null)
//            return clazz;

        clazz = findClass(className);
        if (clazz != null)
            return clazz;

        return clazz;
    }

}
