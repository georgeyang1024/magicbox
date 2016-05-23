package com.zijunlin.Zxing.Demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import cn.georgeyang.qrcode.CaptureSlice;
import cn.georgeyang.qrcode.R;

public class MainActivity extends Activity{

	private Button button;
	private TextView resultText;
	private final static int SCANNIN_GREQUEST_CODE = 1;
	private final static int SELECTFILE_GREQUEST_CODE = 2;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		resultText = (TextView) this.findViewById(R.id.result);

		button = (Button) this.findViewById(R.id.scanCodeButton);
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, CaptureSlice.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});

		findViewById(R.id.button_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.scanFileCodeButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//选择文件
			}
		});

		findViewById(R.id.greanlCodeButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//生成
			}
		});

		findViewById(R.id.saveCodeButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//保存

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
			case SCANNIN_GREQUEST_CODE:
				if(resultCode == RESULT_OK){
					Bundle bundle = data.getBundleExtra("bundle");
					String resultString = bundle.getString("result");
					resultText.setText(resultString);
				}
				break;
			case SELECTFILE_GREQUEST_CODE:
				if(resultCode == RESULT_OK){
					Bundle bundle = data.getBundleExtra("bundle");
					String resultString = bundle.getString("result");
					resultText.setText(resultString);
				}
				break;
		}
	}


}
