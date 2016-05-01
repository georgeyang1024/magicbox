package cn.georgeyang.csdnblog.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.georgeyang.csdnblog.R;


@SuppressLint("InflateParams")
public class ToastUtil {

	private static Toast mToast;
	private static TextView mMessageView;

	/**
	 * Toast显示消息(中间位置)
	 * 
	 * @param context
	 * @param message
	 */

	public static final void show(final Context context, final String message) {
		if (mToast != null) {
			mToast.cancel();
			mToast = null;
		}

		mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		View view = LayoutInflater.from(context).inflate(R.layout.toast_bg, null);
		mMessageView = (TextView) view.findViewById(R.id.tv_message);
		mToast.setView(view);

		mMessageView.setText(message);
		mToast.show();
	}

	/**
	 * Toast显示消息(底部)
	 * 
	 * @param context
	 * @param message
	 */

	public static final void showCenter(final Context context, final String message) {
		if (mToast != null) {
			mToast.cancel();
			mToast = null;
		}

		mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		View view = LayoutInflater.from(context).inflate(R.layout.toast_bg, null);
		mMessageView = (TextView) view.findViewById(R.id.tv_message);
		mToast.setView(view);

		mMessageView.setText(message);
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.show();
	}

}
