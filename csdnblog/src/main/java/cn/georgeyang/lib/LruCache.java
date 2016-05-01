package cn.georgeyang.lib;

import android.graphics.Bitmap;

/**
 * Created by george.yang on 2015/10/13.
 */
public class LruCache extends android.support.v4.util.LruCache<String, Object> {
    private static LruCache lruCache;

    public static synchronized LruCache getInstance() {
        synchronized (LruCache.class) {
            if (lruCache == null) {
//                int maxSize = 10 * 1024 * 1024;//10MB

                // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
                // LruCache通过构造函数传入缓存值，以KB为单位。
                int maxMemory = (int) (Runtime.getRuntime().maxMemory());
                // 使用最大可用内存值的1/3作为缓存的大小。
                int maxSize = maxMemory / 3;
                lruCache = new LruCache(maxSize);
            }
        }
//        lruCache.p
        return lruCache;
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, Object oldValue, Object newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
    }

    @Override
    protected int sizeOf(String key, Object object) {
        // 重写此方法来衡量每张图片的大小，默认返回图片数量。
        if (object instanceof Bitmap) {
            Bitmap bitmap = (Bitmap) object;
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
        //1Kb
        return 1;
    }

    public <T> T getFast(String key) {
        Object object = get(key);
        try {
            return (T)object;
        } catch (Exception e) {

        }
        return null;
    }



    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public LruCache(int maxSize) {
        super(maxSize);
    }
}
