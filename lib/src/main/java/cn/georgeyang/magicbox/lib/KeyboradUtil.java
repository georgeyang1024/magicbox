package cn.georgeyang.magicbox.lib;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 软键盘处理工具
 * 
 * @author ping
 * @create 2014-4-16 下午5:35:20
 */
public class KeyboradUtil {
	public static void showkeyboard(final EditText titleInput) {
		showkeyboard(titleInput, 150);
	}

	public static void showkeyboard(final EditText titleInput,long time) {
		try {
			titleInput.setFocusable(true);
			titleInput.requestFocus();
			(new Handler()).postDelayed(new Runnable() {
				@Override
				public void run() {
					InputMethodManager imm = (InputMethodManager) titleInput
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}, time);
		} catch (Exception e) {

		}
	}

	/**
	 * 关闭软键盘
	 */
	public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            // 得到InputMethodManager的实例
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
        }
	}
}
