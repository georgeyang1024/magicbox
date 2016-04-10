package cn.georgeyang.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import online.magicbox.desktop.Vars;

/**
 * Created by george.yang on 16/4/9.
 */
public class AlwaysMarqueeTextView extends TextView {
    public AlwaysMarqueeTextView(Context context) {
        super(context);
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AlwaysMarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private boolean posted;
    @Override
    public boolean isFocused() {
        if (Vars.scrolling) {
            if (!posted) {
                posted = true;
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        posted = false;
                        requestLayout();
                    }
                },1000);
            }
            return false;
        }
        return true;
    }


}
