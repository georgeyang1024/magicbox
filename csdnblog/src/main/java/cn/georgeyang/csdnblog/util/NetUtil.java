package cn.georgeyang.csdnblog.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class NetUtil {
	private static final String TAG = "MobileUtils";
	public static final String DEFAULT_WIFI_ADDRESS = "00-00-00-00-00-00";
	public static final String WIFI = "Wi-Fi";
	public static final String TWO_OR_THREE_G = "2G/3G";
	public static final String UNKNOWN = "Unknown";

	private static String convertIntToIp(int paramInt) {
		return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
				+ (0xFF & paramInt >> 24);
	}

	/***
	 * 获取当前网络类型
	 * 
	 * @param pContext
	 * @return type[0] WIFI , TWO_OR_THREE_G , UNKNOWN type[0] SubtypeName
	 */
	public static String[] getNetworkState(Context pContext) {
		String[] type = new String[2];
		type[0] = "Unknown";
		type[1] = "Unknown";
		if (pContext.getPackageManager().checkPermission("android.permission.ACCESS_NETWORK_STATE",
				pContext.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
			ConnectivityManager localConnectivityManager = (ConnectivityManager) pContext
					.getSystemService("connectivity");
			if (localConnectivityManager == null)
				return type;

			NetworkInfo localNetworkInfo1 = localConnectivityManager.getNetworkInfo(1);
			if ((localNetworkInfo1 != null) && (localNetworkInfo1.getState() == NetworkInfo.State.CONNECTED)) {
				type[0] = "Wi-Fi";
				type[1] = localNetworkInfo1.getSubtypeName();
				return type;
			}
			NetworkInfo localNetworkInfo2 = localConnectivityManager.getNetworkInfo(0);
			if ((localNetworkInfo2 == null) || (localNetworkInfo2.getState() != NetworkInfo.State.CONNECTED))
				type[0] = "2G/3G";
			type[1] = localNetworkInfo2.getSubtypeName();
			return type;
		}
		return type;
	}

	/***
	 * 获取wifi 地址
	 * 
	 * @param pContext
	 * @return
	 */

	public static String getWifiAddress(Context pContext) {
		String address = DEFAULT_WIFI_ADDRESS;
		if (pContext != null) {
			WifiInfo localWifiInfo = ((WifiManager) pContext.getSystemService("wifi")).getConnectionInfo();
			if (localWifiInfo != null) {
				address = localWifiInfo.getMacAddress();
				if (address == null || address.trim().equals(""))
					address = DEFAULT_WIFI_ADDRESS;
				return address;
			}

		}
		return DEFAULT_WIFI_ADDRESS;
	}

	/***
	 * 获取wifi ip地址
	 * 
	 * @param pContext
	 * @return
	 */
	public static String getWifiIpAddress(Context pContext) {
		WifiInfo localWifiInfo = null;
		if (pContext != null) {
			localWifiInfo = ((WifiManager) pContext.getSystemService("wifi")).getConnectionInfo();
			if (localWifiInfo != null) {
				String str = convertIntToIp(localWifiInfo.getIpAddress());
				return str;
			}
		}
		return "";
	}

	/**
	 * 获取WifiManager
	 * 
	 * @param pContext
	 * @return
	 */
	public static WifiManager getWifiManager(Context pContext) {
		return (WifiManager) pContext.getSystemService("wifi");
	}

	/**
	 * 网络可用 android:name="android.permission.ACCESS_NETWORK_STATE"/>
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}

	/***
	 * wifi状态
	 * 
	 * @param pContext
	 * @return
	 */
	public static boolean isWifi(Context pContext) {
		if ((pContext != null) && (getNetworkState(pContext)[0].equals("Wi-Fi"))) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * 判断网络连接是否已开 2012-08-20true 已打�? false 未打�?
	 */
	public static boolean isNetDeviceAvailable(Context context) {
		boolean bisConnFlag = false;
		ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = conManager.getActiveNetworkInfo();
		if (network != null) {
			bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
		}
		return bisConnFlag;
	}

	public static boolean isNetAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();

		if (info != null) {
			String typeName = info.getTypeName().toLowerCase(); // WIFI/MOBILE
			boolean isAvailable = false;
			if (typeName.equals("wifi")) {
				isAvailable = true;
			} else {
				String apnName = info.getExtraInfo().toLowerCase();
				if (apnName.contains("net")) {
					isAvailable = true;
				}
			}
			if (isAvailable && info.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
				return true;
			} else {
				ToastUtil.show(context, "网络已断开");
				return false;
			}
		} else {
			ToastUtil.show(context, "网络已断开");
			return false;
		}
	}

	public static enum ProviderName {
		chinaMobile("中国移动"), chinaUnicom("中国联�?"), chinaTelecom("中国电信"), chinaNetcom("中国网�?"), other("未知");
		private String text;

		private ProviderName(String text) {
			this.text = text;
		}

		public String getText() {
			return this.text;
		}
	}

	/**
	 * 获取SIM卡的IMSI�?SIM卡唯�?��识：IMSI 国际移动用户识别�?（IMSI：International Mobile
	 * Subscriber Identification Number）是区别移动用户的标志， 储存在SIM卡中，可用于区别移动用户的有效信息�?
	 * IMSI由MCC、MNC、MSIN组成，其中MCC为移动国家号码，�?位数字组成，
	 * 唯一地识别移动客户所属的国家，我国为460；MNC为网络id，由2位数字组成，
	 * 用于识别移动客户�?��属的移动网络，中国移动为00，中国联通为01, 中国电信�?3；MSIN为移动客户识别码，采用等�?1位数字构成�?
	 * 唯一地识别国内GSM移动通信网中移动客户�? �?��要区分是移动还是联�?，只�?��得SIM卡中的MNC字段即可
	 */
	public static ProviderName getProviderName(Context context) {
		String imsi = getIMSI(context);
		if (imsi != null) {
			// 因为移动网络编号46000下的IMSI已经用完,�?��虚拟了一�?6002编号�?34/159号段使用了此编号
//			LogUtil.log("imsi", Log.INFO, imsi);
			if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007")) {
				return ProviderName.chinaMobile;
			} else if (imsi.startsWith("46001")) {
				return ProviderName.chinaUnicom;
			} else if (imsi.startsWith("46003")) {
				return ProviderName.chinaTelecom;
			} else {
				return ProviderName.other;
			}
		} else {
			return ProviderName.other;
		}
	}

	/**
	 * IMEI 全称�?International Mobile Equipment Identity，中文翻译为国际移动装备辨识码，
	 * 即�?常所说的手机序列号，
	 * 用于在手机网络中识别每一部独立的手机，是国际上公认的手机标志序号，相当于移动电话的身份证。序列号共有15位数字，�?位（TAC）是型号核准号码�?
	 * 代表手机类型
	 * 。接�?位（FAC）是�?��装配号，代表产地。后6位（SNR）是串号，代表生产顺序号。最�?位（SP）一般为0，是�?��码，备用�?
	 * 国际移动装备辨识码一般贴于机身背面与外包装上，同时也存在于手机记忆体中，通过输入*#06#即可查询�?
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager ts = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return ts.getDeviceId();
	}

	/**
	 * IMSI 全称�?International Mobile Subscriber
	 * Identity，中文翻译为国际移动用户识别码�?它是在公众陆地移动电话网
	 * （PLMN）中用于唯一识别移动用户的一个号码�?在GSM网络，这个号码�?常被存放在SIM卡中
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context) {
		TelephonyManager ts = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return ts.getSubscriberId();
	}

	/**
	 * android.permission.ACCESS_WIFI_STATE android.permission.CHANGE_WIFI_STATE
	 * android.permission.WAKE_LOCK �?��wifi�?��使用异步
	 */
	public static void toggleWifi(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		if (wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(false);
		} else {
			wifiManager.setWifiEnabled(true);
		}
	}

	public static void getScanWifiResults(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> wifiResults = wifiManager.getScanResults();
		for (ScanResult wifi : wifiResults) {
//			LogUtil.log(TAG, Log.DEBUG, wifi.toString());
		}

		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

//		LogUtil.log(TAG, Log.DEBUG, TelephonyManager.PHONE_TYPE_GSM + "----" + tm.getPhoneType());
		List<NeighboringCellInfo> cellResults = tm.getNeighboringCellInfo();
		for (NeighboringCellInfo cell : cellResults) {
//			LogUtil.log(TAG, Log.DEBUG, cell.getCid() + "-" + cell.getLac() + "-" + cell.getRssi() + "-" + cell.getPsc()
//					+ "-" + cell.getNetworkType());
		}

//		LogUtil.log(TAG, Log.DEBUG, getProviderName(context).getText());
	}

	public static boolean isNetworkProvider(Context context) {
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	public static boolean isGpsProvider(Context context) {
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

//	/**
//	 * 获取本机的Ip地址
//	 *
//	 * @return
//	 */
//	public static String getLocalIpAddress() {
//		try {
//			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
//				NetworkInterface intf = en.nextElement();
//				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//					InetAddress inetAddress = enumIpAddr.nextElement();
//					if (!inetAddress.isLoopbackAddress()
//							&& InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
//						return inetAddress.getHostAddress().toString();
//					}
//				}
//			}
//		} catch (SocketException ex) {
//		}
//		return null;
//	}

	/**
	 * 枚举网络状态 NET_NO：没有网络 NET_2G:2g网络 NET_3G：3g网络 NET_4G：4g网络 NET_WIFI：wifi
	 * NET_UNKNOWN：未知网络
	 */
	public static enum NetState {
		NET_NO, NET_2G, NET_3G, NET_4G, NET_WIFI, NET_UNKNOWN
	};

	/**
	 * 判断当前是2g/3g/4g/wifi
	 * 
	 * @param context
	 * @return 状态码
	 */
	public static NetState connectType(Context context) {
		NetState stateCode = NetState.NET_NO;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isConnectedOrConnecting()) {
			switch (ni.getType()) {
			case ConnectivityManager.TYPE_WIFI:
				stateCode = NetState.NET_WIFI;
				break;
			case ConnectivityManager.TYPE_MOBILE:
				switch (ni.getSubtype()) {
				case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
				case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
				case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_IDEN:
					stateCode = NetState.NET_2G;
					break;
				case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
				case TelephonyManager.NETWORK_TYPE_UMTS:
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_EVDO_B:
				case TelephonyManager.NETWORK_TYPE_EHRPD:
				case TelephonyManager.NETWORK_TYPE_HSPAP:
					stateCode = NetState.NET_3G;
					break;
				case TelephonyManager.NETWORK_TYPE_LTE:
					stateCode = NetState.NET_4G;
					break;
				default:
					stateCode = NetState.NET_UNKNOWN;
				}
				break;
			default:
				stateCode = NetState.NET_UNKNOWN;
			}

		}
		return stateCode;
	}

	// /**
	// * 根据一个网络连接(String)获取bitmap图像
	// *
	// * @param imageUri
	// * @return
	// * @throws MalformedURLException
	// */
	// public static Bitmap getbitmap(String imageUri) {
	// Log.v(TAG, "getbitmap:" + imageUri);
	// // 显示网络上的图片
	// Bitmap bitmap = null;
	// try {
	// URL myFileUrl = new URL(imageUri);
	// HttpURLConnection conn = (HttpURLConnection) myFileUrl
	// .openConnection();
	// conn.setDoInput(true);
	// conn.connect();
	// InputStream is = conn.getInputStream();
	// bitmap = BitmapFactory.decodeStream(is);
	// is.close();
	//
	// Log.v(TAG, "image download finished." + imageUri);
	// } catch (IOException e) {
	// e.printStackTrace();
	// Log.v(TAG, "getbitmap bmp fail---");
	// return null;
	// }
	// return bitmap;
	// }
}
