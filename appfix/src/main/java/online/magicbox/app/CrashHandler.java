package online.magicbox.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**   
 * UncaughtException processing class, when the program Uncaught exception occurs, 
 * there is the class to take over the program, and record sends an error report.
 *  
 *  Application needs to be registered in order to be in the application launcher will monitor the entire process.
 */      
public class CrashHandler implements UncaughtExceptionHandler {      

	public static final String TAG = "CrashHandler";      

	//System default UncaughtException processing class 
	private UncaughtExceptionHandler mDefaultHandler;
	//CrashHandler examples
	private static CrashHandler instance;
	//Context object program
	private Context mContext;
	//Equipment used to store information and exception information
	private Map<String, String> infos = new HashMap<String, String>();

	//For formatting dates, as part of the log file name
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private String  ExternalPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/skilltemp/crash/";
	/** Ensure that only one instance of CrashHandler*/
	private CrashHandler() {}

	/** Get CrashHandler instance, single-case model */
	public synchronized  static CrashHandler getInstance() {
		if(instance == null)
			instance = new CrashHandler();
		return instance;
	}

	private File uploadFile;
    private static boolean hasInit = false;
	public void init(Context context) {
        if (hasInit) {
            return;
        }
        hasInit = true;
		mContext = context;
        ExternalPath = mContext.getCacheDir() + "/crash/";
		//Get the system default UncaughtException processor
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		//The default processor set for the program that CrashHandler
		Thread.setDefaultUncaughtExceptionHandler(this);


        Log.i("test","start!!!!!!!");
		//when application create,upload error info to my website,you can open this link to see history error:http://georgeyang.cn:8080/ptool/appError.list.do?packageName=zuihuasuan
		UiThread.init(context).start(new UiThread.UIThreadEvent() {
			@Override
			public Object runInThread(String flag, Object obj, UiThread.Publisher publisher) {
				try {
					File dir = new File(ExternalPath);
					File[] files = dir.listFiles();
					if (files == null ) {
						return null;
					}
					for (File file:files) {
						if (file.isFile()) {
							uploadFile = file;
							break;
						}
					}
//					Logutil.showlog("ExternalPath file:" + ExternalPath);
//					Logutil.showlog("upload file:" + uploadFile.getAbsolutePath());

					String repost = readFileContent(uploadFile);
					repost = repost.replace("\n","</br>").replace("&"," * ");
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("appname",getPackageInfo(mContext).packageName);
					params.put("version", getPackageInfo(mContext).versionName);
					params.put("device","android" +  Build.VERSION.RELEASE+ "(" + Build.MODEL + ")");
					params.put("content", repost);
					return HttpUtil.post("http://georgeyang.cn:8080/ptool/postapperror",params);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public void runInUi(String flag, Object obj, boolean ispublish, float progress) {
				if (obj == null || "".equals(obj.toString())) {

				} else {
					try {
                        JSONObject jsonObject = new JSONObject(obj.toString());
						if (jsonObject.getInt("errorcode")==0) {
							uploadFile.delete();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}


	private static String readFileContent(File file) {
		String fileContent = "";
		try {
			String line = "";
			BufferedReader br = new BufferedReader(new FileReader(file));
			StringBuffer buffer = new StringBuffer();
			while ((line = br.readLine()) != null) {
				buffer.append(line + "\n");
			}
			fileContent = buffer.toString();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileContent;
	}

	/**
	 * 获取App安装包信息
	 *
	 * @return
	 */
	private static PackageInfo getPackageInfo(Context context) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (info == null) {
			info = new PackageInfo();
		}

		return info;
	}

	/**   
	 * UncaughtException occurs when the function will be transferred to handle
	 */      
	@Override      
	public void uncaughtException(Thread thread, Throwable ex)
	{      
		if(handleException(ex))
		{
			System.out.println(ex.toString());
			deletefile(ExternalPath);
			String fileName = saveCatchInfo2File(ex); 
			sendCrashLog2PM(ExternalPath+fileName);  
		}
		if (mDefaultHandler != null) 
		{         
			mDefaultHandler.uncaughtException(thread, ex); 

		}
		android.os.Process.killProcess(android.os.Process.myPid());      
		System.exit(1);  
		
	}      

	/**   
	 * Custom error handling, error messages sent to collect error reports and other operations were performed here.
	 *    
	 * @param ex   
	 * @return true:If handled the exception information; otherwise false. 
	 */      
	private boolean handleException(Throwable ex) {      
		if (ex == null)
		{      
			return false;      
		}      
		//Collecting device parameter information      
		collectDeviceInfo(mContext);      
		
		return true;      
	}      

	/**   
	 * Collecting device parameter information
	 * @param ctx   
	 */      
	public void collectDeviceInfo(Context ctx) {      
		try {      
			PackageManager pm = ctx.getPackageManager();      
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);      
			if (pi != null) {      
				String versionName = pi.versionName == null ? "null" : pi.versionName;      
				String versionCode = pi.versionCode + "";      
				infos.put("versionName", versionName);      
				infos.put("versionCode", versionCode);      
			}      
		} catch (NameNotFoundException e) {      
			Log.e(TAG, "an error occured when collect package info", e);      
		}      
		Field[] fields = Build.class.getDeclaredFields();      
		for (Field field : fields) {      
			try {      
				field.setAccessible(true);      
				infos.put(field.getName(), field.get(null).toString());      
				Log.d(TAG, field.getName() + " : " + field.get(null));      
			} catch (Exception e) {      
				Log.e(TAG, "an error occured when collect crash info", e);      
			}      
		}      
	}      

	/**   
	 * 	Save the error information to a file
	 *    
	 * @param ex   
	 * @return  Returns the file name, easy to transfer files to the server
	 */      
	private String saveCatchInfo2File(Throwable ex) {      

		StringBuffer sb = new StringBuffer();      
		for (Map.Entry<String, String> entry : infos.entrySet()) {      
			String key = entry.getKey();      
			String value = entry.getValue();      
			sb.append(key + "=" + value + "\n"); 
		}      

		Writer writer = new StringWriter();      
		PrintWriter printWriter = new PrintWriter(writer);      
		ex.printStackTrace(printWriter);      
		Throwable cause = ex.getCause();      
		while (cause != null) {      
			cause.printStackTrace(printWriter);      
			cause = cause.getCause();      
		}      
		printWriter.close();      
		String result = writer.toString();      
		sb.append(result);      
		try {      
			long timestamp = System.currentTimeMillis();      
			String time = formatter.format(new Date());      
			String fileName = "crash-" + time + "-" + timestamp + ".log";      
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {      
				File dir = new File(ExternalPath);    

				if (!dir.exists()) {      
					dir.mkdirs();      
				}

				FileOutputStream fos = new FileOutputStream(ExternalPath + fileName);      
				fos.write(sb.toString().getBytes());    
				fos.close();      
			}      
			return fileName;      
		} catch (Exception e) {      
			Log.e(TAG, "an error occured while writing file...", e);      
		}      
		return null;      
	}      

	/** 
	 * Delete all files in a folder under the folder and files
	 * 
	 * @param delpath 
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 * @return boolean 
	 */  
	public static boolean deletefile(String delpath) {  

		File file = new File(delpath);  
		// If and only if the file represented by this abstract pathname exists and is a directory, returns true
		if (!file.isDirectory())
		{  
			file.delete();  
		} 
		else if (file.isDirectory())
		{  
			String[] filelist = file.list();  
			for (int i = 0; i < filelist.length; i++)
			{  
				File delfile = new File(delpath+ filelist[i]);  
				if (!delfile.isDirectory()) 
				{  
					delfile.delete();  
				} else if (delfile.isDirectory()) {  
					deletefile(delpath + filelist[i]);  
				}  
			}  
			file.delete();  
		}  


		return true;  
	}  


	/** 
	 * The crash resulted in the capture of information to developers
	 *  
	 * Currently only the log is saved in the log and output to LogCat sdcard, and not sent to the background. 
	 */  
	private void sendCrashLog2PM(String fileName){  
		if(!new File(fileName).exists()){  
			return;  
		}  
		FileInputStream fis = null;  
		BufferedReader reader = null;  
		String s = null;  
		try {  
			fis = new FileInputStream(fileName);  
			reader = new BufferedReader(new InputStreamReader(fis, "GBK"));  
			while(true){  
				s = reader.readLine();  
				if(s == null) break;  
				//There is yet to identify ways to send, so the first shot log log.
				Log.i("info", s.toString());  
			}  
		} catch (FileNotFoundException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}finally{   
			try {  
				reader.close();  
				fis.close();  
			} catch (IOException e) {  
				e.printStackTrace();  
			}  
		}  
	}  
}      
