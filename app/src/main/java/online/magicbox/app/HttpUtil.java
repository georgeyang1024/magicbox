package online.magicbox.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by george.yang on 16/3/26.
 */
public class HttpUtil {
    public static String get(String urlPath,Map<String, Object> params) {
        StringBuffer body = getParamString(params);
        return get(urlPath + "?" + body.toString());
    }

    public static String get(String urlPath) {
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            URL url = new URL(urlPath);
            //获得URL对象
            connection = (HttpURLConnection) url.openConnection();
            //获得HttpURLConnection对象
            connection.setRequestMethod("GET");
            // 默认为GET
            connection.setUseCaches(false);
            //不使用缓存
            connection.setConnectTimeout(10000);
            //设置超时时间
            connection.setReadTimeout(10000);
            //设置读取超时时间
            connection.setDoInput(true);
            //设置是否从httpUrlConnection读入，默认情况下是true;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //相应码是否为200
                is = connection.getInputStream();
                //获得输入流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //包装字节流为字符流
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
            if (is != null) {
                try {
                    is.close();
                    is = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String post(String urlPath, Map<String, Object> params) {
        if (params == null || params.size() == 0) {
            return get(urlPath);
        }
        OutputStream os = null;
        InputStream is = null;
        HttpURLConnection connection = null;
        StringBuffer body = getParamString(params);
        byte[] data = body.toString().getBytes();
        try {
            URL url = new URL(urlPath);
            //获得URL对象
            connection = (HttpURLConnection) url.openConnection();
            //获得HttpURLConnection对象
            connection.setRequestMethod("POST");
            // 设置请求方法为post
            connection.setUseCaches(false);
            //不使用缓存
            connection.setConnectTimeout(10000);
            //设置超时时间
            connection.setReadTimeout(10000);
            //设置读取超时时间
            connection.setDoInput(true);
            //设置是否从httpUrlConnection读入，默认情况下是true;
            connection.setDoOutput(true);
            //设置为true后才能写入参数
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(data.length));
            os = connection.getOutputStream();
            os.write(data);
            //写入参数
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //相应码是否为200
                is = connection.getInputStream();
                //获得输入流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //包装字节流为字符流
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        }
        return null;
    }

    private static StringBuffer getParamString(Map<String, Object> params) {
        StringBuffer result = new StringBuffer();
        Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> param = iterator.next();
            String key = param.getKey();
            Object value = param.getValue();
            result.append(key).append('=').append(value);
            if (iterator.hasNext()) {
                result.append('&');
            }
        }
        return result;
    }


//    public void downloadFile(String url,String locPath,String filename,UiThread.Publisher publisher) {
//        HttpClient client = new DefaultHttpClient();
//        HttpGet get = new HttpGet(url);
//        HttpResponse response;
//        try {
//            response = client.execute(get);
//
//            HttpEntity entity = response.getEntity();
//            float length = entity.getContentLength();
//
//            InputStream is = entity.getContent();
//            FileOutputStream fileOutputStream = null;
//            if (is != null) {
//
//                String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/"+locPath;
//
//                File dir = new File(sdcard);
//                if (!dir.exists()) { // 不存在则创建
//                    dir.mkdirs();
//                }
//
//                File file = new File(sdcard + "/" + filename);
//                if(file.exists()){
//                    file.delete();
//                }else{
//                    file.createNewFile();
//                }
//                fileOutputStream = new FileOutputStream(file);
//                byte[] buf = new byte[1024];
//                int ch = -1;
//                float count = 0;
//                while ((ch = is.read(buf)) != -1) {
//                    fileOutputStream.write(buf, 0, ch);
//                    count += ch;
//                    float progress = count*100f/length;
//
//                    //发布进度
//                    publisher.publishProgress(progress);
//                }
//            }
//
//            //发布成功
//            publisher.publishProgress(100f);
//
//            fileOutputStream.flush();
//            if (fileOutputStream != null) {
//                fileOutputStream.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            //发布下载失败
//            publisher.publishProgress(-1);
//        }
//    }

    public final static Map<String,Object> buildBaseParams(Context context) {
        HashMap<String,Object> baseParams = new HashMap<>();
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            // 当前应用的版本名称
            String versionName = info.versionName;
            baseParams.put("appVersion", versionName);

            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            baseParams.put("deviceID", tm.getDeviceId());

            String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            baseParams.put("androidId", android_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseParams;
    }


    private static final int BUFFER_SIZE = 1024 * 10;// 10k缓存
    public static void downLoadFile (String urlString,File file) throws Exception {
        // 重置开始点
        long startPosition = file.length();
        int filesize;
        long curPosition = 0;

        BufferedInputStream bis = null;
        RandomAccessFile fos = null;
        byte[] buf = new byte[BUFFER_SIZE];
        URLConnection con = null;
        URLConnection testcon = null;
        URL url  = new URL(urlString);
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
            if (!file.exists()) {
                file.createNewFile();
            }

            con = url.openConnection();
            con.setRequestProperty("Connection", "Keep-Alive");
             con.setRequestProperty("Charset", "UTF-8");
        con.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");

            testcon = url.openConnection();
            filesize = testcon.getContentLength();
        Log.i("test","filezie:" + filesize);
            if (con.getReadTimeout() == 5) {
                // 网络错误(超时)
                throw new TimeoutException("connect timeout");
            }

            con.setAllowUserInteraction(true);
            // 设置当前线程下载的起点，终点
            con.setRequestProperty("Range", "bytes=" + startPosition + "-");

            if (startPosition >= filesize) {
                //下载完成
                return;
            }

//            Log.i(TAG, "con.getContentLength() =" + con.getContentLength()
//                    + " filesize;" + filesize + " startPosition;"
//                    + startPosition);

            // 使用java中的RandomAccessFile 对文件进行随机读写操作
            fos = new RandomAccessFile(file, "rw");

            // 设置开始写文件的位置
            fos.seek(startPosition);
            bis = new BufferedInputStream(con.getInputStream());
            // 开始循环以流的形式读写文件
        int len = 0;
        while ((len = bis.read(buf, 0, BUFFER_SIZE)) != -1) {
//            while (curPosition < filesize) {
//            int len = bis.read(buf, 0, BUFFER_SIZE);
                if (len == -1) {
                    break;
                }
                fos.write(buf, 0, len);
                curPosition = curPosition + len;
//                    int x = (int) (curPosition * 1.0 / filesize * 10000);
//                    Log.i(TAG, "curPosition/filesize=" + curPosition + "/"
//                            + filesize + "=" + x);
            }
            bis.close();
            fos.close();
    }

    public static void downLoadFile2 (String urlStr,File file) throws Exception {
        URL url=new URL(urlStr);
        HttpURLConnection conn=(HttpURLConnection)url.openConnection();
        OutputStream output;

        InputStream input=conn.getInputStream();
        if(file.exists()){
            if (file.length()==0) {
                file.delete();
            }
            int length = conn.getContentLength();
            if (length!=0) {
                if (file.length()!=length) {
                    file.delete();
                }
            }

            if (length==-1 || file.length()>0) {
                return;
            }
        }


            file.createNewFile();//新建文件
            output=new FileOutputStream(file);
            //读取大文件
            byte[] buffer=new byte[BUFFER_SIZE];
            int len=0;
            while ((len = input.read(buffer, 0, BUFFER_SIZE)) != -1) {
                output.write(buffer,0,len);
            }
            output.flush();

    }
}
