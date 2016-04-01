package online.magicbox.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainFixActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("test",new Test().getString());
    }
}
