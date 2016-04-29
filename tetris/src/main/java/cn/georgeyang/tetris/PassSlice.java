package cn.georgeyang.tetris;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.georgeyang.tetris.util.GameData;
import online.magicbox.lib.Slice;

public class PassSlice extends Slice {
	private TextView goonTV,titleTV,quitTV;
	private RelativeLayout backgroundRL;
	
	private GameData gameData= GameData.getInstance();

	public PassSlice(Context base, Object holder) {
		super(base, holder);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	
		initData();
		initListen();
	}
	
	public void initData(){
		setContentView(R.layout.activity_pass);
		backgroundRL=(RelativeLayout)findViewById(R.id.backgroundRL);
		goonTV=(TextView)findViewById(R.id.goonTV);
		titleTV=(TextView)findViewById(R.id.titleTV);
		quitTV=(TextView)findViewById(R.id.quitTV);
		if(gameData.getIslose()==false){
			titleTV.setText(" 已经"+gameData.getLeve()+"关了");
		}else{
			Drawable drawable=getResources().getDrawable(R.drawable.lose);
			backgroundRL.setBackgroundDrawable(drawable);
			goonTV.setText("继续吗？");
			titleTV.setText("技术还得修炼啊!");
		}		
		new MyThread().start();
	}
	
	
	public void initListen(){		
		goonTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					Intent intent=new Intent();
					intent.setClass(getApplicationContext(), MainActivity.class);
					isrun=false;
					int leve=gameData.getLeve();
					if(gameData.getIslose()==false){
						leve=leve+1;
						Toast toast1=Toast.makeText(getApplicationContext(),
								"真男人！过100关", Toast.LENGTH_LONG); 
						toast1.show();
					}else{
						Toast toast1=Toast.makeText(getApplicationContext(),
								"失败是成功之母！加油", Toast.LENGTH_LONG); 
						toast1.show();
					}
					gameData.setLeve(leve);					
					startActivity(intent);
					finish();
				}
			});
		quitTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast toast1=Toast.makeText(getApplicationContext(),
						"你个战5渣！", Toast.LENGTH_LONG); 
				toast1.show();
					finish();
				}
			});
	
	}
	
	boolean isrun=true;
	 class  MyThread extends Thread
     {
                 @Override
                 public void run() {
                         // TODO Auto-generated method stub
                         super.run();
                         int count = 0;
                         while(isrun)
                         {                        	 
                         	try {
                         		
                         		handler.sendEmptyMessage(0);             		
                         		sleep(500);
                         		handler.sendEmptyMessage(1);
                         		sleep(500);
 							} catch (InterruptedException e) {
 								// TODO Auto-generated catch block
 								e.printStackTrace();
 							}
                         }
                 }  
     }
	
	   public Handler handler= new Handler()
		{       
			@SuppressLint("NewApi")
			@Override
			public void handleMessage(Message msg)
			{  
				int i=msg.what;
				if(i==0){
					titleTV.setTextColor(Color.parseColor("#ffff00"));
				}
				else{
					titleTV.setTextColor(Color.parseColor("#00ff00"));
				}
					super.handleMessage(msg); 
			}
		};
}
