package share.photo.view;

import share.photo.mydebug.MyDebugUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Debug;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * 遮罩视图
 * 使用方法:
 * 1.初始化(init())
 * 2.设置比例(setScale(int scale))(提示也可以不设置,默认是100)
 * 3.设置背景
 * 4.设置前景(setForeground(Bitmap bmp))
 * 
 * 
 * @author 黄世凡
 * 
 */
public class BlowView extends View
{
	private int mWidth;
	private int mHeight;
	private Bitmap mBmpForeground;
	// 画纸
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;

	private int mStrokeHeight;

	private Paint mPaint;

	private int mYStart;
	
	/**
	 * 比例
	 */
	private int mScale;

	public BlowView(Context context)
	{
		super(context);
	}

	public BlowView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public void init()
	{
		
		mWidth = getLayoutParams().width;
		mHeight = getLayoutParams().height;
		MyDebugUtil.i(mWidth + "," + mHeight);
		setScale(100);
	}
	
	

	public BlowView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public void setForeground(Bitmap bmp)
	{
		mBmpForeground = Bitmap.createScaledBitmap(bmp, mWidth, mHeight, true);
		// init Paint
		mPaint = new Paint();
		mPaint.setAlpha(0);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		mPaint.setAntiAlias(true);

		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(mStrokeHeight);

		mPath = new Path();

		mBitmap = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
		mCanvas = new Canvas();
		mCanvas.setBitmap(mBitmap);
		mCanvas.drawBitmap(mBmpForeground, 0, 0, null);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if (null != mBitmap)
			canvas.drawBitmap(mBitmap, 0, 0, null);
		if (null != mCanvas)
			mCanvas.drawPath(mPath, mPaint);
	}

	public void up()
	{
		if (mYStart < 0)
			return;
		mPath.reset();
		// start
		mPath.moveTo(0, mYStart);
		// move
		mPath.quadTo(0, mYStart, mWidth, mYStart);
		// commit the path to our offscreen
		mPath.lineTo(mWidth, mYStart);
		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);
		mPath.reset();
		mYStart -= mStrokeHeight;
		invalidate();
	}

	public int getmScale()
	{
		return mScale;
	}

	public void setScale(int scale)
	{
		this.mScale = scale;
		mStrokeHeight = mHeight / mScale;
		mYStart = mHeight;
	}
	
	public void reset()
	{
		mYStart = mHeight;
	}
	
	

}
