package cn.georgeyang.util;

import android.graphics.Bitmap;

/**
 * Created by george.yang on 2015/10/13.
 */
public class ImageCache extends android.support.v4.util.LruCache<String, Bitmap> {
    private static ImageCache imageCache;

    public static synchronized ImageCache getInstance() {
        synchronized (ImageCache.class) {
            if (imageCache == null) {
//                int maxSize = 10 * 1024 * 1024;//10MB

                // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
                // LruCache通过构造函数传入缓存值，以KB为单位。
                int maxMemory = (int) (Runtime.getRuntime().maxMemory());
                // 使用最大可用内存值的1/3作为缓存的大小。
                int maxSize = maxMemory / 3;
                imageCache = new ImageCache(maxSize);
            }
        }
        return imageCache;
    }

    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
        // 重写此方法来衡量每张图片的大小，默认返回图片数量。
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public ImageCache(int maxSize) {
        super(maxSize);
    }
}
