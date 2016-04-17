package org.jiggawatt.giffle;

import android.graphics.Bitmap;
import android.util.Log;


import java.io.File;
import java.util.List;

import cn.georgeyang.gifgenerate.BitmapCompressor;

/**
 * Created by touch_ping on 15/6/2.
 */
public class Giffle {
    static {
        System.loadLibrary("gifflen");
    }

    public static native int Init(String gifName,
                                  int w, int h, int numColors, int quality, int frameDelay);
    public static native void  Close();

    public static native  int AddFrame(int[] pixels);

    /**
     * 合成gif
     * encode the bitmaps to gif
     * @param fileName
     * @param files
     * @param delay
     */
    public static boolean Encode(String fileName, List<File> files, int delay)
    {
        if(files==null||files.size()==0)
        {
            Log.e("test","Bitmaps should have content!!!");
            return false;
        }

        boolean isInit = false;

        for(int i=0;i<files.size();i++)
        {
            File f = files.get(i);
            Bitmap bm = BitmapCompressor.decodeSampledBitmapFromFile(f.getAbsolutePath(), 1000, 1000);
            if (!isInit) {
                int width=bm.getWidth();
                int height=bm.getHeight();
                if (Init(fileName, width, height, 256, 90, delay / 10) != 0) {
                    Log.e("test", "GifUtil init failed");
                    return false;
                }
                isInit = true;
            }
            int pixels[]=new int[bm.getWidth()*bm.getHeight()];
            bm.getPixels(pixels, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());
            AddFrame(pixels);
        }

        Close();

        return  true;
    }

}
