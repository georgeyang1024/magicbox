package cn.georgeyang.tetris;



import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import cn.georgeyang.tetris.util.GameData;
import cn.georgeyang.tetris.util.Logical;
import cn.georgeyang.tetris.util.Mysurfaceview;
import online.magicbox.lib.PluginActivity;
import online.magicbox.lib.Slice;

public class MainSlice extends Slice {
	private TextView scoreTV,leveTV;
	private Button leftBT,rightBT,changeBT,downBT;
	//private Constant constant=Constant.getInstance(this);
	private Logical logical;
	private Mysurfaceview mysurfaceview;
	
	private GameData gameData=GameData.getInstance();

	public MainSlice(Context base, Object holder) {
		super(base, holder);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//logical=Logical.getInstance(this);
		logical=new Logical(this);
		
		setContentView(R.layout.activity_main);
		
		mysurfaceview=(Mysurfaceview)findViewById(R.id.mysurfaceview);
		scoreTV=(TextView)findViewById(R.id.scoreTV);
		leveTV=(TextView)findViewById(R.id.leveTV);
		leftBT=(Button)findViewById(R.id.leftBT);
		rightBT=(Button)findViewById(R.id.rightBT);
		changeBT=(Button)findViewById(R.id.changeBT);
		downBT=(Button)findViewById(R.id.downBT);
		
		initData();
		initListen();
	}
	
	public void initData(){
		int leve=gameData.getLeve();
		leveTV.setText("第 "+leve+" 关");
		mysurfaceview.loadLogical(logical,leve);
	}
	
	
	public void initListen(){		
		leftBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				logical.Left_press();
				mysurfaceview.ondraw();
				}
			});
		rightBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				logical.Right_press();
				mysurfaceview.ondraw();
				}
			});
		changeBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				logical.Change_press();
				mysurfaceview.ondraw();
				}
			});
		downBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				logical.Down_press();
				mysurfaceview.ondraw();
				}
			});
		
		
		logical.setLoadscoreLintener(new Logical.OnLoadscoreListener() {
			@Override
			public void OnLoadscores(int isBottom) {
				scoreTV.setText(isBottom+"");
				if(isBottom>=1500){
					mysurfaceview.isrun=false;

					Intent intent = PluginActivity.buildIntent(MainSlice.this,PassSlice.class);
					startActivity(intent);
					finish();
				}
			}
		});
		
		logical.setGameOverLintener(new Logical.OnGameOverListener() {
			@Override
			public void OnGameOver(int isBottom) {

				mysurfaceview.isrun=false;
				Intent intent = PluginActivity.buildIntent(MainSlice.this,PassSlice.class);
				startActivity(intent);
				finish();
				
			}
		});
	}
	

	
	
	
}
