package com.zxing.android.decoding;

import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.Result;
import com.zxing.android.camera.CameraManager;
import com.zxing.android.view.ViewfinderView;

/**
 * Created by george.yang on 16/2/13.
 */
public interface CaptureEvent {
    CameraManager getCameraManager();
    boolean handleDecode (Result result, Bitmap bitmap);
    void drawViewfinder();
    ViewfinderView getViewfinderView();
    Handler getHandler();
}
