package share.photo.utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import share.photo.listener.MulitPointTouchListener;
import share.photo.network.AsyncDrawableLoader;
import share.photo.sharephoto.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * 图片工具
 * 
 * 
 */
public final class ImageUtility
{

	/**
	 * 缩放图片
	 * 
	 * @param bitmap
	 * @param width
	 *            宽
	 * @param height
	 *            高
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height)
	{
		final int w = bitmap.getWidth();
		final int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		bitmap.recycle();
		bitmap = null;
		return newbmp;
	}

	/**
	 * 放大缩小图片
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmapByFitCenter(Bitmap bitmap, int w, int h)
	{
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		if (scaleWidht > scaleHeight)
		{
			scaleWidht = scaleHeight;
		}
		matrix.postScale(scaleWidht, scaleWidht);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		bitmap.recycle();
		bitmap = null;
		return newbmp;
	}

	/**
	 * 放大缩小图片
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmapByCenterCrop(Bitmap bitmap, int w, int h)
	{
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		if (scaleWidht < scaleHeight)
		{
			scaleWidht = scaleHeight;
		}
		matrix.postScale(scaleWidht, scaleWidht);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		bitmap.recycle();
		bitmap = null;
		return newbmp;
	}
	
	/**
	 * 根据x、y缩放,载取图片中心
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmapCenterInside(Bitmap bitmap, int w, int h)
	{
		bitmap = zoomBitmapByCenterCrop(bitmap, w, h);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int xStart = (width - w)/2;
		xStart = xStart < 0 ? 0 : xStart;
		int yStart = (height - h)/2;
		yStart = yStart < 0 ? 0 : yStart;
		Bitmap newBmp = Bitmap.createBitmap(bitmap, xStart, yStart, w, h);
		bitmap.recycle();
		bitmap = null;
		return newBmp;
	}
	
	/**
	 * 缩放图片，布满x,y
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmapFitXY(Bitmap bitmap, int w, int h)
	{
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		bitmap.recycle();
		bitmap = null;
		return newbmp;
	}
	
	
	
	

	/**
	 * 将Drawable转化为Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable)
	{
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;

	}

	/**
	 * 获得圆角图片的方法
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx)
	{
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 获得带倒影的图片方法
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap)
	{
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	/**
	 * 将Bitmap转换成byte数组
	 * 
	 * @param bitmap
	 * @param format
	 *            图片格式
	 * @return
	 */
	public static byte[] bitmap2Byte(Bitmap bitmap, CompressFormat format)
	{

		ByteArrayOutputStream bos = null;
		byte[] buff = null;
		try
		{
			bos = new ByteArrayOutputStream();
			bitmap.compress(format, 100, bos);
			buff = bos.toByteArray();
			bos.flush();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			if (null != bos)
			{
				try
				{
					bos.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return buff;
	}

	/**
	 * 旋转
	 * 
	 * @param degree
	 *            度数
	 * @return
	 */
	public static Bitmap rotate(Bitmap bitmap, float degrees)
	{
		Matrix matrix = new Matrix();
		matrix.setRotate(degrees);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
	}

	public static Bitmap toGrayscale(Bitmap bitmap)
	{
		Bitmap buffer = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(buffer);
		Paint paint = new Paint();
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(0);
		ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
				colorMatrix);
		paint.setColorFilter(colorMatrixFilter);

		canvas.drawBitmap(bitmap, 0, 0, paint);
		canvas.save();
		return buffer;
	}

	/**
	 * 大图dialog
	 * 
	 * @param context
	 * @param res
	 *            图片资源ID
	 */
	public static void showPicByRes(Context context, int res)
	{
		final Dialog dialog = new Dialog(context, R.style.dialog);
		LayoutParams layoutParams = new LayoutParams();
		layoutParams.width = LayoutParams.MATCH_PARENT;
		layoutParams.height = LayoutParams.MATCH_PARENT;
		ImageView img = new ImageView(context);
		img.setLayoutParams(layoutParams);
		img.setScaleType(ScaleType.FIT_CENTER);
		img.setImageResource(res);
		img.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});
		// // 图片缩放拖动
		// img.setOnTouchListener(new MulitPointTouchListener());

		dialog.setContentView(img);
		// 设置dialog区域
		LayoutParams lay = dialog.getWindow().getAttributes();
		lay.height = LayoutParams.MATCH_PARENT;
		lay.width = LayoutParams.MATCH_PARENT;
		dialog.show();
	}

	/**
	 * 大图dialog
	 * 
	 * @param context
	 * @param url
	 *            图片路劲
	 */
	public static void showPicByUrl(Context context, String url)
	{
		final Dialog dialog = new Dialog(context, R.style.dialog);
		LayoutParams layoutParams = new LayoutParams();
		layoutParams.width = LayoutParams.MATCH_PARENT;
		layoutParams.height = LayoutParams.MATCH_PARENT;
		ImageView img = new ImageView(context);
		img.setLayoutParams(layoutParams);
		img.setScaleType(ScaleType.FIT_CENTER);
		// 可以缩放图片
		img.setOnTouchListener(new MulitPointTouchListener());
		AsyncDrawableLoader.getInstance().loadBigDrawable(url, img);

		img.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});
		// // 图片缩放拖动
		// img.setOnTouchListener(new MulitPointTouchListener());

		dialog.setContentView(img);
		// 设置dialog区域
		LayoutParams lay = dialog.getWindow().getAttributes();
		lay.height = LayoutParams.MATCH_PARENT;
		lay.width = LayoutParams.MATCH_PARENT;
		dialog.show();

	}

}