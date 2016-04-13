package online.magicbox.app;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import online.magicbox.app.PlugClassLoder;

/**
 * 可直接加载插件布局和资源的自定义Context包装类
 * @author 小姜
 * @time 2015-4-16 上午11:03:47
 */
public class PluginContext extends ContextWrapper {

	//activity中使用:
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		proxyContext = new PluginProxyContext(this);
//		//加载插件资源
//		proxyContext.loadResources(Constant.PLUGINNAME, false);
//		//由于使用插件主题方式，所以不能再使用R文件，直接使用布局名就行
//		rootView = proxyContext.getLayout("activity_main");
//		setContentView(rootView);
//		httpUtils = new HttpUtils();
//		progressDialog = new ProgressDialog(this);
//	}




	private Context context;
	private AssetManager mAssetManager = null;
	private Resources mResources = null;
	private LayoutInflater mLayoutInflater = null;
	private Theme mTheme = null;
	private String packageName = null;
	private PlugClassLoder plugClassLoder;
	
	// *****************资源ID类型*******************
    public static final String LAYOUT = "layout";
    public static final String ID = "id";
    public static final String DRAWABLE = "drawable";
    public static final String STYLE = "style";
    public static final String STRING = "string";
    public static final String COLOR = "color";
    public static final String DIMEN = "dimen";


	public PluginContext(Context base) {
		super(base);
		this.context = base;
	}
	
	/**
	 * 单例模式(单利模式会有缓存，所以不使用单例)
	 *
	 * @param context
	 * @return
	 * @author 小姜
	 * @time 2015-4-16 上午11:30:37
	 *//*
	private static PluginProxyContext proxyContext;
	public static PluginProxyContext getInstance(Context context){
		if(proxyContext == null){
			proxyContext = new PluginProxyContext(context);
		}
		return proxyContext;
	}*/
	/**
	 * 加载插件中的资源
	 *
	 * @param dexPath 路徑
	 * @param packageName 是包名
	 * @author 小姜 change by georgeyang
	 * @time 2015-4-16 上午11:31:36
	 */
	public void loadResources(String dexPath, String packageName) {
		try {
//			File outFile = copy(resPluginName,true);
			String outFile = dexPath;

			AssetManager assetManager = AssetManager.class.newInstance();
			Method addAssetPath = assetManager.getClass().getMethod(
					"addAssetPath", String.class);
			addAssetPath.invoke(assetManager, outFile);
			mAssetManager = assetManager;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Resources superRes = super.getResources();
		mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(),
				superRes.getConfiguration());
//		this.packageName = mResources.getResourcePackageName(R.string.app_name);//获取插件包名
		this.packageName = packageName;

		Log.i("test","加载的packageName:" + packageName);
		getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //释放so文件
        String libPath = getCacheDir().getAbsolutePath() + "/lib";

		plugClassLoder = PlugClassLoder.plugClassLoderCache.get(dexPath);
		if (plugClassLoder==null) {
			ZipInputStream zipIn = null;
			int readedBytes = 0;
			byte buf[] = new byte[4096];
			new File(libPath).mkdirs();
			try {
				zipIn = new ZipInputStream(new BufferedInputStream(new FileInputStream(dexPath)));
				ZipEntry zipEntry = null;
				while ((zipEntry = zipIn.getNextEntry()) != null) {
					String name = zipEntry.getName();
					if (!TextUtils.isEmpty(name)) {
						if (name.startsWith("lib/" + Build.CPU_ABI)) {
							String fileName = name.substring(name.lastIndexOf("/")+1,name.length());
							try {
								FileOutputStream fileOut = new FileOutputStream(new File(libPath,fileName));
								while ((readedBytes = zipIn.read(buf)) > 0) {
									fileOut.write(buf, 0, readedBytes);
								}
								fileOut.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				zipIn.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					zipIn.closeEntry();
				} catch (Exception e) {
				}
			}

			plugClassLoder = new PlugClassLoder(dexPath,context.getCacheDir().getAbsolutePath(),libPath,context.getClassLoader());
		}
	}
	
	/**
	 * 获取插件资源对应的id
	 *
	 * @param type
	 * @param name
	 * @return
	 * @author 小姜
	 * @time 2015-4-16 上午11:31:56
	 */
	public int getIdentifier(String type, String name){
		return mResources.getIdentifier(name, type, packageName);
	}
	public int getId(String name){
		return mResources.getIdentifier(name, ID, packageName);
	}
	/**
	 * 获取插件中的layout布局
	 *
	 * @param name
	 * @return
	 * @author 小姜
	 * @time 2015-4-16 上午11:32:12
	 */
	public View getLayout(String name){
		return mLayoutInflater.inflate(getIdentifier(LAYOUT,name), null);
	}
	public String getString(String name){
		return mResources.getString(getIdentifier(STRING, name));
	}
	public int getColor(String name){
		return mResources.getColor(getIdentifier(COLOR, name));
	}
	public Drawable getDrawable(String name){
		return mResources.getDrawable(getIdentifier(DRAWABLE, name));
	}
	public int getStyle(String name){
		return getIdentifier(STYLE, name);
	}
	public float getDimen(String name){
		return mResources.getDimension(getIdentifier(DIMEN, name));
	}

	
	/**
	 * 创建一个当前类的布局加载器，用于专门加载插件资源
	 */
	@Override
	public Object getSystemService(String name) {
		if (LAYOUT_INFLATER_SERVICE.equals(name)) {
			if (mLayoutInflater == null) {
				try {
					Class<?> cls = Class
							.forName("com.android.internal.policy.PolicyManager");
					Method m = cls.getMethod("makeNewLayoutInflater",
							Context.class);
					//传入当前PluginProxyContext类实例，创建一个布局加载器
					mLayoutInflater = (LayoutInflater) m.invoke(null, this);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}else {
				return mLayoutInflater;
			}
		}
		return super.getSystemService(name);
	}

	
	@Override
	public AssetManager getAssets() {
		return mAssetManager;
	}
	
	@Override
	public Resources getResources() {
		return mResources;
	}
	
	@Override
	public ClassLoader getClassLoader() {
//		return context.getClassLoader();
		return plugClassLoder;
	}

	@Override
	public Resources.Theme getTheme() {
		if(mTheme == null){
			mTheme = mResources.newTheme();
		}
//		int theme = 0;
//		if (android.os.Build.VERSION.SDK_INT >= 21) {
//			theme = android.R.style.Theme_Material_Light_NoActionBar;
//		} else if (android.os.Build.VERSION.SDK_INT >= 13) {
//			theme = android.R.style.Theme_Holo_Light_NoActionBar;
//		} else {
//			theme = android.R.style.Theme_Black_NoTitleBar;
//		}
//		mTheme.applyStyle(theme,true);
		mTheme.applyStyle(android.R.style.Theme_DeviceDefault_Light_NoActionBar,true);
		return mTheme;
	}
	
	
	@Override
	public String getPackageName() {
		return packageName;
	}
}
