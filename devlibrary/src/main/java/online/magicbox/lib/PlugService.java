package online.magicbox.lib;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

/**
 * Created by george.yang on 16/4/16.
 */
public abstract class PlugService {
    public void onRebind(Intent intent) {
    }

    public IBinder onBind(Intent intent) {
        return null;
    }


}
