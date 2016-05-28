package cn.georgeyang.qrcode;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.zxing.android.CreateQRImageTest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import online.magicbox.lib.PluginActivity;
import online.magicbox.lib.Slice;

/**
 * Created by george.yang on 2016-5-23.
 */
public class MainSlice extends Slice implements View.OnClickListener {
    public MainSlice(Context base, Object holder) {
        super(base, holder);
    }


    private TextView tv_result;
    private ImageView imageView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slice_main);

        findViewById(R.id.scanCodeButton).setOnClickListener(this);
        findViewById(R.id.scanFileCodeButton).setOnClickListener(this);
        findViewById(R.id.greanlCodeButton).setOnClickListener(this);
        findViewById(R.id.saveCodeButton).setOnClickListener(this);
        findViewById(R.id.button_back).setOnClickListener(this);

        tv_result = (TextView) findViewById(R.id.result);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    Bitmap bitmap;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                finish();
                break;
            case R.id.scanCodeButton:
                requestPermission(123,Manifest.permission.CAMERA);
                break;
            case R.id.scanFileCodeButton:
                requestPermission(234,Manifest.permission.READ_EXTERNAL_STORAGE);
                break;
            case R.id.greanlCodeButton:
                bitmap = CreateQRImageTest.createQRImage(tv_result.getText().toString());
                if (bitmap==null) {
                    Toast.makeText(this,"生成失败!",Toast.LENGTH_SHORT).show();
                } else {
                    imageView.setImageBitmap(bitmap);
                }
                break;
            case R.id.saveCodeButton:
                if (bitmap!=null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dateFormat.format(new Date()) + ".png";
                    boolean ret = CreateQRImageTest.saveBitmap(path,bitmap);
                    if (ret) {
                        Toast.makeText(this,"文件已保存到:" + path,Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this,"保存失败!",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this,"未生成二维码!",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public void onPermissionGiven(int code,String permission) {
        super.onPermissionGiven(code,permission);
        if (code == 123) {
            Intent scan = PluginActivity.buildIntent(getActivity(), CaptureSlice2.class);
            getActivity().startActivityForResult(scan, 50);
        }
        if (code==234) {
            Intent picker = PluginActivity.buildIntent(getActivity(),"online.magicbox.desktop","ImageSelectorSlice","1");
            picker.putExtra("select_count_mode",0);
            getActivity().startActivityForResult(picker,100);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("test","onActivityResult:" + requestCode + " " + resultCode);
        if (requestCode==50) {
            String result = data.getStringExtra("result");
            Log.d("test",result);
            tv_result.setTag(result);
            tv_result.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tv_result.setText(tv_result.getTag().toString());
                }
            },200);
        } else if (requestCode==100) {
            if (resultCode==RESULT_OK) {
                ArrayList<String> list = data.getStringArrayListExtra("select_result");
                if (!(list==null || list.size()==0)) {
                    String path = list.get(0);
                    Log.i("test","path:" + path);
                    Result ret = CreateQRImageTest.scanningImage(path);
                    if (ret==null) {
                        Toast.makeText(this,"解析失败!",Toast.LENGTH_SHORT).show();
                    } else {
                        tv_result.setText(ret.getText());
                    }
                }
            } else {
                Toast.makeText(getActivity(),"图片选择器在desktop里面，没有正常安装，不能启动图片选择器",Toast.LENGTH_SHORT).show();
            }
        }

    }
}
