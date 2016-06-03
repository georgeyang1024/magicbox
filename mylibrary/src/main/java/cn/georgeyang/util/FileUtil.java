package cn.georgeyang.util;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by george.yang on 16/4/16.
 */
public class FileUtil {
    /**
     * 获取文件夹大小
     * @param file File实例
     * @return long 单位为M
     * @throws Exception
     */
    public static long getFolderSize(java.io.File file) {
        long size = 0;
        java.io.File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++)
        {
            if (fileList[i].isDirectory())
            {
                size = size + getFolderSize(fileList[i]);
            } else
            {
                size = size + fileList[i].length();
            }
        }
        return size;
    }


    /**
     * 文件大小单位转换
     *
     * @param size
     * @return
     */
    public static String setFileSize(long size) {
        DecimalFormat df = new DecimalFormat("###.##");
        float f = ((float) size / (float) (1024 * 1024));

        if (f < 1.0) {
            float f2 = ((float) size / (float) (1024));

            return df.format(new Float(f2).doubleValue()) + "KB";

        } else {
            return df.format(new Float(f).doubleValue()) + "M";
        }
    }


    /**
     * 删除指定目录下文件及目录
     *
     * @param deleteThisPath
     * @param deleteThisPath
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath){
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);

            if (file.isDirectory()) {// 处理目录
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFolderFile(files[i].getAbsolutePath(), true);
                }
            }
            if (deleteThisPath) {
                if (!file.isDirectory()) {// 如果是文件，删除
                    file.delete();
                } else {// 目录
                    if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                        file.delete();
                    }
                }
            }
        }
    }

}
