package cn.georgeyang.util;

import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.io.File;

import cn.georgeyang.lib.UiThread;

/**
 * Created by george.yang on 2016-4-7.
 */
public class ImageLoder {
    public static void loadImage (final ImageView imageView, File file, final int resizeWidth, int resizeHeight, final @DrawableRes int errorImg) {
        String filePath = file.getAbsolutePath();
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Bitmap cache  = ImageCache.getInstance().get("path-" + filePath);
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
                Bitmap bitmap  = ImageCache.getInstance().get(cachekey);
                if (bitmap==null) {
                    bitmap = BitmapCompressor.decodeSampledBitmapFromFile(flag,500,500);

                    String path = (String) imageView.getTag();
                    if (flag.equals(path)) {
                        ImageCache.getInstance().put(cachekey,bitmap);
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

    public static void loadImage (final ImageView imageView, @NonNull final String url, final int resizeWidth, int resizeHeight, final @DrawableRes int errorImg) {
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        final Bitmap cache  = ImageCache.getInstance().get("url-" + url.hashCode());
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
                String cachekey = "url-" + flag;
                Bitmap bitmap  = ImageCache.getInstance().get(cachekey);
                if (bitmap==null) {
                    File cacheDir = new File(imageView.getContext().getCacheDir(),"imageCache");
                    if (!cacheDir.exists()) {
                        cacheDir.mkdir();
                    }
                    File cacheFile = new File(cacheDir.getAbsoluteFile(),flag.toString()+".img");
                    if (!cacheFile.exists()) {
                        try {
                            HttpUtil.downLoadFile2(url,cacheFile);
                        } catch (Exception e) {
                            return null;
                        }
                    }

                    bitmap = BitmapCompressor.decodeSampledBitmapFromFile(cacheFile.getAbsolutePath(),500,500);

                    String path = (String) imageView.getTag();
                    if (flag.equals(path)) {
                        ImageCache.getInstance().put(cachekey,bitmap);
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
}
