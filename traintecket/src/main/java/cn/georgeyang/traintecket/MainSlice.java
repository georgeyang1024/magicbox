package cn.georgeyang.traintecket;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import online.magicbox.lib.Slice;

public class MainSlice extends Slice implements OnClickListener
{

	private ImageView ticket_back;
	private Button tiketFind;
	private List<Map<String, String>> list;
	private Map<String, String> map = new HashMap<String, String>();;
	private ProgressDialog dialog;
	private EditText tiketEndEdit,tiketStartEdit;
	private EditText tiketDay,tiketMonth,tiketYear;

	private SimpleAdapter simpleAdapter;
	private ListView tiketList;
	private LinearLayout tiketLinearButtoms;
	private LinearLayout ticket_main;
	private Animation upani,downani;
	private boolean flag = false;
	private SharedPreferences startCity,endCity;
	private InputMethodManager imm;
	public static int screenWidth;
	public static int screenHeight;

	public MainSlice(Context base, Object holder) {
		super(base, holder);
	}

	private void showIn () {
		tiketEndEdit.setEnabled(true);
		tiketStartEdit.setEnabled(true);
		tiketDay.setEnabled(true);
		tiketMonth.setEnabled(true);
		tiketYear.setEnabled(true);
		tiketFind.setEnabled(true);
		tiketLinearButtoms.setVisibility(View.VISIBLE);
		tiketLinearButtoms.startAnimation(upani);
		flag = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticket);
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		//1200 1824
		DisplayMetrics dm1 = getResources().getDisplayMetrics();
		screenWidth = dm1.widthPixels;
		screenHeight = dm1.heightPixels;

		startCity = getSharedPreferences("startCity", Activity.MODE_PRIVATE);
		endCity = getSharedPreferences("endCity", Activity.MODE_PRIVATE);

		tiketLinearButtoms = (LinearLayout)findViewById(R.id.tiket_linear_buttoms);

		upani = AnimationUtils.loadAnimation(this, R.anim.top2_in);
		downani = AnimationUtils.loadAnimation(this, R.anim.top1_in);

		tiketList = (ListView)findViewById(R.id.tiktefind_listview);
		tiketList.setOverScrollMode(View.OVER_SCROLL_NEVER);
		tiketList.setCacheColorHint(0);
		tiketList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,long arg3) {
				try
				{

					if(flag)
					{
						showIn();
					}
					else
					{
						tiketEndEdit.setEnabled(false);
						tiketStartEdit.setEnabled(false);
						tiketDay.setEnabled(false);
						tiketMonth.setEnabled(false);
						tiketYear.setEnabled(false);
						tiketFind.setEnabled(false);
						tiketLinearButtoms.setVisibility(View.GONE);
						tiketLinearButtoms.startAnimation(downani);
						flag = true;
					}
				}catch(Exception e)
				{

				}
			}

		});



		tiketEndEdit = (EditText)findViewById(R.id.tiket_end_edit);
		tiketStartEdit = (EditText)findViewById(R.id.tiket_start_edit);

		tiketYear = (EditText)findViewById(R.id.tiket_year);
		tiketMonth = (EditText)findViewById(R.id.tiket_month);
		tiketDay = (EditText)findViewById(R.id.tiket_day);


		tiketFind = (Button)findViewById(R.id.tiket_find);
		tiketFind.setOnClickListener(this);

		ticket_back = (ImageView)findViewById(R.id.ticket_back);
		ticket_back.setOnClickListener(this);

		dialog = new ProgressDialog(this);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setMessage("正在查询...");
		dialog.setCancelable(false);
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
								 KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dialog.dismiss();
				}
				return false;
			}
		});

		insert();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		String date = df.format(new Date());// new Date()为获取当前系统时间
		String[] date1 = date.split("-");

		tiketYear.setText(date1[0]);
		tiketMonth.setText(date1[1]);
		tiketDay.setText(date1[2]);

		String start = startCity.getString("startCity", null);
		String end = endCity.getString("endCity", null);

		if(start!=null)
		{
			tiketStartEdit.setText(start);
		}
		if(end!=null)
		{
			tiketEndEdit.setText(end);
		}


		ticket_main = (LinearLayout)findViewById(R.id.ticket_main);
		ticket_main.setOnClickListener(this);

	}

	@Override
	public void onClick(View v)
	{
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		if(v.getId()==R.id.ticket_back)
		{
			finish();
		}

		if(v.getId()==R.id.tiket_find)
		{
			String start = tiketStartEdit.getText().toString().trim();
			String end = tiketEndEdit.getText().toString().trim();

			String year = tiketYear.getText().toString().trim();
			String month = tiketMonth.getText().toString().trim();
			String day = tiketDay.getText().toString().trim();

			if("".equals(start)||start==null)
			{
				Toast.makeText(getApplicationContext(), "请输入起点位置!", Toast.LENGTH_SHORT).show();
				return;
			}
			if("".equals(end)||end==null)
			{
				Toast.makeText(getApplicationContext(), "请输入终点位置!", Toast.LENGTH_SHORT).show();
				return;
			}


			if("".equals(year)||year==null)
			{
				Toast.makeText(getApplicationContext(), "请输入年份!", Toast.LENGTH_SHORT).show();
				return;
			}
			if("".equals(month)||month==null)
			{
				Toast.makeText(getApplicationContext(), "请输入月份!", Toast.LENGTH_SHORT).show();
				return;
			}

			if(month.length()<2)
			{
				month = "0"+month;
				tiketMonth.setText(month);
			}

			if("".equals(day)||day==null)
			{
				Toast.makeText(getApplicationContext(), "请输入几号!", Toast.LENGTH_SHORT).show();
				return;
			}
			if(day.length()<2)
			{
				day = "0"+day;
				tiketDay.setText(day);
			}


			Editor eidt = startCity.edit();
			eidt.putString("startCity", start);
			eidt.commit();

			Editor eidt1 = endCity.edit();
			eidt1.putString("endCity", end);
			eidt1.commit();


			String[] city = getCityCode(start,end);
			String date = year+"-"+month+"-"+day;
			dialog.show();
			findTiket(date, city[0], city[1]);
		}

	}


	public void findTiket(final String date,final String start,final String end)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run() {
				try{


					String str = "https://kyfw.12306.cn/otn/lcxxcx/query?purpose_codes=ADULT&queryDate="+date+"&from_station="+start+"&to_station="+end+"";
					URL url = new URL(str);

					SSLContext sslctxt = SSLContext.getInstance("TLS");
					sslctxt.init(null, new TrustManager[]{new MyX509TrustManager()}, new java.security.SecureRandom());
					HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
					conn.setSSLSocketFactory(sslctxt.getSocketFactory());
					conn.setHostnameVerifier(new MyHostnameVerifier());
					conn.connect();

					int code = conn.getResponseCode();
					if (code == 200) {
						InputStream input = conn.getInputStream();
						String result = toString(input);
						if(result!=null) {
							list = TableName.parseJsonTiket(result);
							if(list!=null&&list.size()>0)
							{
								handler.sendEmptyMessage(1);
							}
							else
							{
								handler.sendEmptyMessage(0);
							}
						} else {
							handler.sendEmptyMessage(2);
						}
						input.close();
					}


					conn.disconnect();
				}catch(Exception e){
					e.printStackTrace();
					handler.sendEmptyMessage(3);
				}
			}
			private String toString(InputStream input){

				String content = null;
				try{
					InputStreamReader ir = new InputStreamReader(input);
					BufferedReader br = new BufferedReader(ir);

					StringBuilder sbuff = new StringBuilder();
					while(null != br){
						String temp = br.readLine();
						if(null == temp)break;
						sbuff.append(temp).append(System.getProperty("line.separator"));
					}

					content = sbuff.toString();



				}catch(Exception e){
					e.printStackTrace();
				}

				return content;
			}
		}).start();;
	}


	static class MyX509TrustManager implements X509TrustManager{

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {


		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {

		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {

			return null;
		}



	}

	static class MyHostnameVerifier implements HostnameVerifier{

		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}

	}






	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if(msg.what==0)
			{
				if(dialog!=null&&dialog.isShowing())
				{
					dialog.dismiss();
				}
				nodata("没有符合条件的数据,请更换日期!");
			}
			else if(msg.what==2)
			{
				if(dialog!=null&&dialog.isShowing())
				{
					dialog.dismiss();
				}
				nodata("服务器没响应!");
			} else if(msg.what==3) {
				if(dialog!=null&&dialog.isShowing())
				{
					dialog.dismiss();
				}
				nodata("没有网络!");
			} else if(msg.what==1)
			{
				tiketView(list);
			}
		}
	};


	public void insert() {
		for (int i = 0; i <TranDate.city.length&&i<TranDate.cityCode.length; i++) {
			map.put(TranDate.city[i], TranDate.cityCode[i]);
		}
	}

	public String[] getCityCode(String start,String end) {
		String[] code = new String[2];
		code[0] = map.get(start);
		code[1] = map.get(end);
		return code;
	}


	public void tiketView(List<Map<String,String>> list) {
		simpleAdapter = new SimpleAdapter(this, list, R.layout.tiket_listview, new String[]{"车次","起始站","终点站","出发时间","到达时间","历时","商务座","特等座","一等座","二等座","高级软卧","无座","软卧","硬卧","硬座","软座","日期","备注","info"},
				new int[]{R.id.tiket_listview_trannumber,R.id.tiket_listview_start,R.id.tiket_listview_end,
						R.id.tiket_listview_starttime,R.id.tiket_listview_endtime,R.id.tiket_listview_lishi,
						R.id.tiket_listview_swz,R.id.tiket_listview_tdz,R.id.tiket_listview_ydz,
						R.id.tiket_listview_edz,R.id.tiket_listview_gjrw,R.id.tiket_listview_wz,
						R.id.tiket_listview_rw,R.id.tiket_listview_yw,R.id.tiket_listview_yz,
						R.id.tiket_listview_rz,R.id.tiket_listview_date,R.id.tiket_listview_remark,R.id.tiket_listview_info
				});
		tiketList.setAdapter(simpleAdapter);
		if(dialog!=null&&dialog.isShowing()) {
			dialog.dismiss();
		}
		tiketEndEdit.setEnabled(false);
		tiketStartEdit.setEnabled(false);
		tiketDay.setEnabled(false);
		tiketMonth.setEnabled(false);
		tiketYear.setEnabled(false);
		tiketFind.setEnabled(false);
		tiketLinearButtoms.setVisibility(View.GONE);
		tiketLinearButtoms.startAnimation(downani);
		flag = true;
	}


	//没数据弹出的对话框
	public void nodata(String msg) {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setCancelable(true);
		dialog.show();
		Window window = dialog.getWindow();
		if(screenWidth>1000)
		{
			window.setLayout((int) (screenWidth * 0.7),LayoutParams.WRAP_CONTENT);
		}
		else
		{
			window.setLayout((int) (screenWidth * 0.85),LayoutParams.WRAP_CONTENT);
		}
		window.setContentView(R.layout.tiket_nodata_dialog);

		TextView exit_confirm = (TextView) window.findViewById(R.id.tiket_confirm);

		TextView title = (TextView) window.findViewById(R.id.tiket_dialog_title);
		TextView info = (TextView) window.findViewById(R.id.tiket_dialog_info);
		title.setText("提示");
		info.setText(msg);

		exit_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0)
			{
				dialog.dismiss();
			}
		});
	}

}

















