package cn.georgeyang.loader;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.util.LinkedHashMap;

/**
 * Created by george.yang on 15/11/19.
 */
public class PlugManger {
    public static final String plugMangerVersion = "1.0";
    public static final String TAG = "PlugManger";
    public static void init (Context context) {
        //网络下载apk

        //从目录里面,加载apk
        try {
            File dexOutputDir = context.getDir("dex", 0);

////            String path = Environment.getExternalStorageDirectory() + "/test.apk";
            String path = Environment.getExternalStorageDirectory() + "/code2.jar";
            PlugClassLoder cl = new PlugClassLoder(path,context.getPackageName(),
                    dexOutputDir.getAbsolutePath(), null,
                        context.getClassLoader());
            Log.i(TAG,"loder:" + cl);
            loderCache.put(path, cl);

//            PLog.i("out path:" + dexOutputDir.getAbsolutePath());

            String libpath = Environment.getExternalStorageDirectory() + "/lib2.jar";
            Log.i(TAG,"jar exist?" + new File(libpath).exists());
            PlugClassLoder libcl = new PlugClassLoder(libpath,context.getPackageName(),
                    dexOutputDir.getAbsolutePath(), null,
                    context.getClassLoader());
//            DexClassLoader dexClassLoader = new DexClassLoader(libpath,dexOutputDir.getAbsolutePath(),null,context.getClassLoader());
//            PLog.i("llib oder:" + libcl);

//            Class clazz = libcl.loadClass("com.orange.pluglib.locker.Locker");
//            PLog.d("clazz:" + clazz);
//            PLog.i("new:" + clazz.newInstance());

            loderCache.put(libpath,libcl);

        } catch (Exception e) {
            Log.i(TAG,"error:" + e.getMessage());
            e.printStackTrace();
        }
    }

    //apk路径-加载器
    private static final LinkedHashMap<String,PlugClassLoder> loderCache = new LinkedHashMap<>();
    //class名-class
    private static final LinkedHashMap<String,Class> classCache = new LinkedHashMap<>();
    public static Class<?> getClassFromPlug (String className) {
        Log.i(TAG,"getClassFromPlug:" + className);
        if (classCache.containsKey(className)) {
            Log.i(TAG,"is containsKey:" + className);
            return classCache.get(className);
        }
        Log.i(TAG,"not containsKey:" + className);
        Class clazz = null;
            synchronized (loderCache) {
                for (String apkPath:loderCache.keySet()) {
                    PlugClassLoder plugClassLoder = loderCache.get(apkPath);
                    if (plugClassLoder!=null) {
                        Log.i(TAG,"找到的loder:" + plugClassLoder);
//                        plugClassLoder.
                        try {
                            clazz = plugClassLoder.loadClass(className);
                            Log.i(TAG,"加载处理的class:" + clazz);
                            if (clazz != null) {
                                break;
                            }
                        } catch (Exception e) {
                            Log.i(TAG,"load error:" + e);
                            e.printStackTrace();
                        }
                    }
                }
            }
        Log.i(TAG,"plug class:" + clazz);
        classCache.put(className, clazz);
        return clazz;
    }

//    public static Fragment loadFragment(Class clazz) {
//        PLog.i("loadFragment:" + clazz);
//        String className = clazz.getName();
//        Class plugClass = getClassFromPlug(className);
//        PLog.i("getClassFromPlug resule:" + plugClass);
//        ClassLoader loader = null;
//        if (plugClass == null) {
//            loader = clazz.getClassLoader();
//        } else {
//            loader = plugClass.getClassLoader();
//            PLog.i("plug class loder:" + loader);
//        }
//        PLog.i("loadFragment final to load:" + loader + " " + className);
//        return loadFragment(loader,className);
//    }

//    public static Fragment loadFragment(ClassLoader loader,String className) {
//        PLog.i("loadFragment:" + loader.getClass() + " " + className);
//        try {
//            Class clazz = loader.loadClass(className);
//            try {
//                Object instance = clazz.newInstance();
//
//                if (instance instanceof Fragment) {
//                    return (Fragment)instance;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return  null;
//    }


//    private static PlugFragmentInterface loadPlug(Context context, File file,
//                                          String plugClassName) {
//        if (file.exists()) {
//            try {
//                DexClassLoader cl = new DexClassLoader(file.getAbsolutePath(),
//                        context.getCacheDir().getAbsolutePath(), null,
//                        context.getClassLoader());
//                Class libProviderClazz = cl.loadClass(context.getPackageName()
//                        + "." + plugClassName);
//                PlugFragmentInterface lib = (PlugFragmentInterface) libProviderClazz
//                        .newInstance();
//
//                return lib;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
}
