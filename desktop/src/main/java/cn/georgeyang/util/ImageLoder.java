package cn.georgeyang.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.io.File;

import cn.georgeyang.lib.LruCache;
import cn.georgeyang.lib.UiThread;

/**
 * Created by george.yang on 2016-4-7.
 */
public class ImageLoder {
    public static void loadImage (final ImageView imageView, File file, final int resizeWidth, int resizeHeight, final @DrawableRes int errorImg) {
        String filePath = file.getAbsolutePath();
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Bitmap cache  = LruCache.getInstance().getFast("path-" + filePath);
        if (cache!=null) {
            imageView.setImageBitmap(cache);
        } else if (!filePath.equals(imageView.getTag())) {
            imageView.setImageResource(errorImg);
        }
        imageView.setTag(filePath);
        if (cache!=null) {
            return;
        }
        UiThread.init(imageView.getContext()).setFlag(filePath).start(new UiThread.UIThreadEvent() {
            @Override
            public Object runInThread(String flag, Object obj, UiThread.Publisher publisher) {
                String cachekey = "path-" + flag;
                Bitmap bitmap  = LruCache.getInstance().getFast(cachekey);
                if (bitmap==null) {
                    bitmap = BitmapCompressor.decodeSampledBitmapFromFile(flag,500,500);

                    String path = (String) imageView.getTag();
                    if (flag.equals(path)) {
                        LruCache.getInstance().put(cachekey,bitmap);
                    }
                }
                Thread.currentThread().interrupt();
                return bitmap;
            }

            @Override
            public void runInUi(String flag, Object obj, boolean ispublish, float progress) {
                String path = (String) imageView.getTag();
                if (flag.equals(path)) {
                    if (obj instanceof Bitmap) {
                        imageView.setImageBitmap((Bitmap)obj);
                    } else {
                        imageView.setImageResource(errorImg);
                    }
                }
            }
        });
    }

    public static Bitmap loadImage (Context context,String url,int maxWidth, int maxHeight ) {
        if (context == null || url == null || url.length()<=1) {
           return null;
        }
        String cacheKey = "url-" + url;
        Bitmap bitmap = LruCache.getInstance().getFast(cacheKey);
        if (bitmap == null) {
            File cacheDir = new File(context.getCacheDir(), "imageCache");
            if (!cacheDir.exists()) {
                cacheDir.mkdir();
            }
            String cacheFileName = url.substring(0,url.length()/2).hashCode() + "#" + url.substring(url.length()/2,url.length()).hashCode();
            File cacheFile = new File(cacheDir.getAbsoluteFile(), cacheFileName + ".img");
            if (!cacheFile.exists()) {
                try {
                    HttpUtil.downLoadFile2(url, cacheFile);
                } catch (Exception e) {
                    return null;
                }
            }

            bitmap = BitmapCompressor.decodeSampledBitmapFromFile(cacheFile.getAbsolutePath(), maxWidth, maxHeight);
            if (bitmap!=null) {
                LruCache.getInstance().put(cacheKey,bitmap);
            }
        }
        return bitmap;
    }


    public static void loadImage (final ImageView imageView, @NonNull final String url, final int maxWidth, final int maxHeight, final @DrawableRes int errorImg) {
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        final Bitmap cache  = LruCache.getInstance().getFast("url-" + url.hashCode());
        if (cache!=null) {
            imageView.setImageBitmap(cache);
        } else if (!url.equals(imageView.getTag())) {
            imageView.setImageResource(errorImg);
        }
        imageView.setTag(url);
        if (cache!=null) {
            return;
        }
        UiThread.init(imageView.getContext()).setFlag(url).start(new UiThread.UIThreadEvent() {
            @Override
            public Object runInThread(String flag, Object obj, UiThread.Publisher publisher) {
                Bitmap bitmap = loadImage(imageView.getContext(),flag,maxWidth,maxHeight);
                Thread.currentThread().interrupt();
                return bitmap;
            }

            @Override
            public void runInUi(String flag, Object obj, boolean ispublish, float progress) {
                String path = (String) imageView.getTag();
                if (flag.equals(path)) {
                    if (obj instanceof Bitmap) {
                        imageView.setImageBitmap((Bitmap)obj);
                    } else {
                        imageView.setImageResource(errorImg);
                    }
                }
            }
        });
    }
}
