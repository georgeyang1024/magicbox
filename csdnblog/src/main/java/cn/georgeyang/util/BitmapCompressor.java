package cn.georgeyang.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * 图片压缩工具类
 * @author touch_ping
 * 2015-1-5 下午1:29:59
 */
public class BitmapCompressor {
	/**
	 * 质量压缩
	 * @author ping 2015-1-5 下午1:29:58
	 * @param image
	 * @param maxkb
	 * @return
	 */
	public static Bitmap compressBitmap(Bitmap image,int maxkb) {
		//L.showlog("压缩图片");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
//		Log.i("test","原始大小" + baos.toByteArray().length);
		while (baos.toByteArray().length / 1024 > maxkb) { // 循环判断如果压缩后图片是否大于(maxkb)50kb,大于继续压缩
//			Log.i("test","压缩一次!");
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
//		Log.i("test","压缩后大小" + baos.toByteArray().length);
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}
	
	/**
	 * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
	 * 官网：获取压缩后的图片
	 * 
	 * @param res
	 * @param resId
	 * @param reqWidth
	 *            所需图片压缩尺寸最小宽度
	 * @param reqHeight
	 *            所需图片压缩尺寸最小高度
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * 官网：获取压缩后的图片
	 * 
	 * @param reqWidth
	 *            所需图片压缩尺寸最小宽度
	 * @param reqHeight
	 *            所需图片压缩尺寸最小高度
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromFile(String filepath,
			int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filepath, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		
		return BitmapFactory.decodeFile(filepath, options);
	}

	public static Bitmap decodeSampledBitmapFromBitmap(Bitmap bitmap,
			int reqWidth, int reqHeight) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
		byte[] data = baos.toByteArray();
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	/*
	压缩图片，处理某些手机拍照角度旋转的问题
	*/
	public static void compressImage(String filePath,String toFilePath,int maxkb,int reqWidth, int reqHeight) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, options);
			
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);
			options.inJustDecodeBounds = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			
			Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
			
			File saveFile = new File(toFilePath);
	        FileOutputStream fout = new FileOutputStream(saveFile);  
	        
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int quality = 100;
			while (baos.toByteArray().length / 1024 > maxkb) { // 循环判断如果压缩后图片是否大于(maxkb)kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				quality -= 10;// 每次都减少10
				if (quality <= 10) {
					break;
				}
				bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			}
			baos.close();
			fout.write(baos.toByteArray());
			fout.flush();  
			fout.close();
			
            if(!bitmap.isRecycled()){  
                bitmap.recycle();//记得释放资源，否则会内存溢出  
            }
            bitmap=null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 计算压缩比例值(改进版 by touch_ping)
	 * 
	 * 原版2>4>8...倍压缩
	 * 当前2>3>4...倍压缩
	 * 
	 * @param options
	 *            解析图片的配置信息
	 * @param reqWidth
	 *            所需图片压缩尺寸最小宽度O
	 * @param reqHeight
	 *            所需图片压缩尺寸最小高度
	 * @return
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		
		final int picheight = options.outHeight;
		final int picwidth = options.outWidth;
		int targetheight = picheight;
		int targetwidth = picwidth;
		int inSampleSize = 1;
		
		if (targetheight > reqHeight || targetwidth > reqWidth) {
			while (targetheight  >= reqHeight
					&& targetwidth>= reqWidth) {
				inSampleSize += 1;
				targetheight = picheight/inSampleSize;
				targetwidth = picwidth/inSampleSize;
			}
		}
		return inSampleSize;
	}
}
