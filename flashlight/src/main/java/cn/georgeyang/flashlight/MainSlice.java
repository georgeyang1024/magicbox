package cn.georgeyang.flashlight;

import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import online.magicbox.lib.Slice;


/**
 * Created by george.yang on 16/4/2.
 */
public class MainSlice extends Slice implements View.OnClickListener {
    public MainSlice(Context base, Object holder) {
        super(base, holder);
    }

    private Button lightBtn = null;
    private Camera camera = null;
    private Camera.Parameters parameters = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        lightBtn = (Button) findViewById(R.id.btn_light);
        lightBtn.setOnClickListener(this);
        camera = Camera.open();
        parameters = camera.getParameters();
        if (parameters.getFlashMode()==Camera.Parameters.FLASH_MODE_TORCH) {
            lightBtn.setBackgroundResource(R.drawable.shou_on);
        } else {
            lightBtn.setBackgroundResource(R.drawable.shou_off);
        }
        camera.stopPreview();
        camera.release();
        camera=null;
    }

    @Override
    public void onClick(View v) {
        if (camera==null) {
            try {
                camera = Camera.open();
            } catch (Exception e) {

            }
        }
        if (camera==null) {
            new AlertDialog.Builder(this).setTitle("提示").setMessage("无法打开摄像头!").setNegativeButton("确认",null).create().show();
            return;
        }
        parameters = camera.getParameters();
        Log.i("test", "mode:" + parameters.getFlashMode());
        String flashMode = parameters == null?null:parameters.getFlashMode();
        if (TextUtils.isEmpty(flashMode)) {
            new AlertDialog.Builder(this).setTitle("提示").setMessage("你的手机没有闪光灯,开启失败!").setNegativeButton("确认",null).create().show();
        } else if (parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
            lightBtn.setBackgroundResource(R.drawable.shou_off);
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
            camera.stopPreview();
            camera.release();
            camera = null;
        } else {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            lightBtn.setBackgroundResource(R.drawable.shou_on);
            camera.setParameters(parameters);
            camera.startPreview();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (camera!=null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
//        android.os.Process.killProcess(android.os.Process.myPid());// πÿ±’Ω¯≥Ã
    }

    //    class Mybutton implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {
//            if (camera==null) {
//
//            }
//            if (kaiguan) {
//
//                lightBtn.setBackgroundResource(R.drawable.shou_on);
//                camera = Camera.open();
//                parameters = camera.getParameters();
//                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);// ø™∆Ù
//                camera.setParameters(parameters);
//                camera.startPreview();
//                kaiguan = false;
//            } else {
//                // addContentView(adView, new ViewGroup.LayoutParams(-1, -2));
//                lightBtn.setBackgroundResource(R.drawable.shou_off);
//                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);// πÿ±’
//                camera.setParameters(parameters);
//                camera.stopPreview();
//                kaiguan = true;
//                camera.release();
//            }
//
//            // AdViewππ‘Ï∫Ø ˝ø…“‘Ω” ’»˝∏ˆ≤Œ ˝£∫context(…œœ¬Œƒ), AdSize¿‡–Õ(π„∏Ê—˘ Ω),
//            // π„∏ÊŒªID(∑«∏ﬂº∂π„∏ÊŒªÃÓnullº¥ø…)
//            // AdView adView = new AdView(this, AdSize.Square, null);
//            // AdView adView = new AdView(this, AdSize.Banner, null);
//
//            // …Ë÷√adViewŒ™µ±«∞ActivityµƒView
//
//        }
//    }
//
//    public void Myback() { // πÿ±’≥Ã–Ú
//        if (kaiguan) {// ø™πÿπÿ±’ ±
//            this.finish();
//            android.os.Process.killProcess(android.os.Process.myPid());// πÿ±’Ω¯≥Ã
//        } else {// ø™πÿ¥Úø™ ±
//            camera.release();
//            this.finish();
////            android.os.Process.killProcess(android.os.Process.myPid());// πÿ±’Ω¯≥Ã
//            kaiguan = true;// ±‹√‚£¨¥Úø™ø™πÿ∫ÛÕÀ≥ˆ≥Ã–Ú£¨‘Ÿ¥ŒΩ¯»Î≤ª¥Úø™ø™πÿ÷±Ω”ÕÀ≥ˆ ±£¨≥Ã–Ú¥ÌŒÛ
//        }
//    }

}
