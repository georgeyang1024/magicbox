package cn.georgeyang.loader;


import android.util.Log;

import java.util.HashMap;

import dalvik.system.DexClassLoader;

/**
 * Created by george.yang on 15/11/19.
 */
public class PlugClassLoder extends DexClassLoader {
//    public String packageName;
    public String dexPath;
    public static final String TAG = "PlugManger";

    private static final HashMap<String, PlugClassLoder> plugLoderMap = new HashMap<>();

    public PlugClassLoder(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, libraryPath, parent);
        this.dexPath = dexPath;
//        this.packageName = packageName;
        if (!plugLoderMap.containsKey(dexPath)) {
            plugLoderMap.put(dexPath, this);
        }
    }
//        @Override
//    protected Class<?> loadClass(String className, boolean resolve)
//            throws ClassNotFoundException {
//            PLog.i("loadClass:" + className);
//            int RIndex = className.indexOf(".R");
//            if (RIndex>0) {
//                className = packageName+"."+className.substring(RIndex+1,className.length());
//                PLog.i(className);
//            }
//            return super.loadClass(className,resolve);
//    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve)
            throws ClassNotFoundException {
//        int RIndex = className.indexOf(".R$");
//            if (RIndex>0) {
//                className = packageName+"."+className.substring(RIndex+1,className.length());
//                Log.i(TAG,className);
//            }

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



//    @Override
//    protected Class<?> loadClass(String className, boolean resolve)
//            throws ClassNotFoundException {
//        Class<?> clazz = findLoadedClass(className);
//
//        Logutil.showlog("findLoadedClass:" + className + "   -   "+ resolve);
//        if (clazz!=null) {
//            if (!(clazz.getClassLoader() instanceof PlugClassLoder)) {
//                clazz = null;
//            }
//        }
//        if (clazz == null) {
//            try {
//                clazz = findClass(className);
//            } catch (Exception e) {
//                Logutil.showlog("findClass error:" + e.getMessage());
//                e.printStackTrace();
//            }
//
//            Logutil.showlog("resolveClass" + clazz);
//            if (resolve && (clazz != null)) {
//                resolveClass(clazz);
//                Logutil.showlog("resolveClass ret" + clazz);
//            }
//            return clazz;
//        }
//        Logutil.showlog("loadClass final:" + clazz + "," + clazz.getClassLoader());
//
//        return clazz;
//    }

//    @Override
//    protected Class<?> loadClass(String className, boolean resolve)
//            throws ClassNotFoundException {
//        Class<?> clazz = findLoadedClass(className);
//        if (clazz != null)
//            return clazz;
//        try {
//            clazz = getParent().loadClass(className);
//        } catch (ClassNotFoundException e) {
//        }
//        if (clazz != null)
//            return clazz;
//        clazz = findClass(className);
//        return clazz;
//    }

//    static final HashMap<String, PlugClassLoder> loaders = new HashMap<String, PlugClassLoder>();
//    /**
//     * return null if not available on the disk
//     */
//    public static PlugClassLoder getClassLoader(String dexPath) {
//        File dexFile = new File(dexPath);
//        if (dexFile==null || dexFile.isDirectory()) {
//            return null;
//        }
//        PlugClassLoder cl = loaders.get(dexPath);
//        if (cl != null)
//            return cl;
//
//        cl.loadClass()
//
//
//
//        String[] deps = cl.deps();
//        PlugClassLoder[] ps = null;
//        if (deps != null) {
//            ps = new MyClassLoader[deps.length];
//            for (int i = 0; i < deps.length; i++) {
//                FileSpec pf = site.getFile(deps[i]);
//                if (pf == null)
//                    return null;
//                MyClassLoader l = getClassLoader(site, pf);
//                if (l == null)
//                    return null;
//                ps[i] = l;
//            }
//        }
//        File dir = MyApplication.instance().getFilesDir();
//        dir = new File(dir, "repo");
//        if (!dir.isDirectory())
//            return null;
//        dir = new File(dir, file.id());
//        File path = new File(dir, TextUtils.isEmpty(file.md5()) ? "1.apk"
//                : file.md5() + ".apk");
//        if (!path.isFile())
//            return null;
//        File outdir = new File(dir, "dexout");
//        outdir.mkdir();
//        cl = new MyClassLoader(file, path.getAbsolutePath(),
//                outdir.getAbsolutePath(), null, MyApplication.instance()
//                .getClassLoader(), ps);
//        loaders.put(file.id(), cl);
//        return cl;
//    }
}
