package share.photo.view;

import java.text.DecimalFormat;

import share.photo.listener.INetworkListener;
import share.photo.network.AsyncDrawableLoader;
import share.photo.sharephoto.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class PorterDuffView extends ImageView
{
	private static final String TAG = "PorterDuffView";
	/** 前景Bitmap高度为1像素。采用循环多次填充进度区域。 */
	public static final int FG_HEIGHT = 1;
	/** 下载进度前景色 */
	public static final int FOREGROUND_COLOR = 0x50333333;
	/** 下载进度条的颜色。 */
	public static final int TEXT_COLOR = Color.parseColor("#d7525e");
	/** 进度百分比字体大小。 */
	public int mFontSize = 30;
	private Bitmap mBitmapBg, mBitmapFg;
	private Paint mPaint;
	/** 标识当前进度。 */
	private float mProgress;
	/** 标识进度图片的宽度与高度。 */
	private int mWidth = 0;
	private int mHeight = 0;
	/** 格式化输出百分比。 */
	private DecimalFormat mDecimalFormat;
	/** 进度百分比文本的锚定Y中心坐标值。 */
	private float mTxtBaseY = 0;
	/** 标识是否使用PorterDuff模式重组界面。 */
	private boolean isPorterduffMode;
	/** 标识是否正在下载图片。 */
	private boolean isLoading;

	private ScaleType mType;

	private Paint.FontMetrics mFontMetrics;

	public PorterDuffView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context, attrs);
	}

	/** 生成一宽与背景图片等同高为1像素的Bitmap，。 */
	private static Bitmap createForegroundBitmap(int w)
	{
		Bitmap bm = Bitmap.createBitmap(w, FG_HEIGHT, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bm);
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setColor(FOREGROUND_COLOR);
		c.drawRect(0, 0, w, FG_HEIGHT, p);
		return bm;
	}

	private void init(Context context, AttributeSet attrs)
	{
		if (attrs != null)
		{
			// //////////////////////////////////////////
			// int count = attrs.getAttributeCount();
			// for (int i = 0; i < count; i++) {
			// LogOut.out(this, "attrNameRes:" +
			// Integer.toHexString(attrs.getAttributeNameResource(i))//
			// + " attrName:" + attrs.getAttributeName(i)//
			// + " attrResValue:" + attrs.getAttributeResourceValue(i, -1)//
			// + " attrValue:" + attrs.getAttributeValue(i)//
			// );
			// }
			// //////////////////////////////////////////

			TypedArray typedArr = context.obtainStyledAttributes(attrs,
					R.styleable.PorterDuffView);
			mFontSize = typedArr.getInteger(
					R.styleable.PorterDuffView_fontSize, 32);
			isPorterduffMode = typedArr.getBoolean(
					R.styleable.PorterDuffView_porterduffMode, false);
		}
		Drawable drawable = getDrawable();
		if (isPorterduffMode && drawable != null
				&& drawable instanceof BitmapDrawable)
		{
			mBitmapBg = ((BitmapDrawable) drawable).getBitmap();
			getSize();
			// mWidth = mBitmapBg.getWidth();
			// mHeight = mBitmapBg.getHeight();
			// LogOut.out(this, "width=" + width + " height=" + height);
			// mBitmapFg = createForegroundBitmap(mWidth);
		} else
		{
			// 不符合要求，自动设置为false。
			isPorterduffMode = false;
		}

		mPaint = new Paint();
		mPaint.setFilterBitmap(false);
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(mFontSize);

		mFontMetrics = mPaint.getFontMetrics();
		// 注意观察本输出：
		// ascent:单个字符基线以上的推荐间距，为负数
		Log.i(TAG, "ascent:" + mFontMetrics.ascent//
				// descent:单个字符基线以下的推荐间距，为正数
				+ " descent:" + mFontMetrics.descent //
				// 单个字符基线以上的最大间距，为负数
				+ " top:" + mFontMetrics.top //
				// 单个字符基线以下的最大间距，为正数
				+ " bottom:" + mFontMetrics.bottom//
				// 文本行与行之间的推荐间距
				+ " leading:" + mFontMetrics.leading);

		if (mHeight != 0)
		{
			// 在此处直接计算出来，避免了在onDraw()处的重复计算
			mTxtBaseY = (mHeight - mFontMetrics.bottom - mFontMetrics.top) / 2;
		}

		mDecimalFormat = new DecimalFormat("0.0%");

		mType = getScaleType();
	}

	/**
	 * 获取前景尺寸
	 */
	public void getSize()
	{
		if (mBitmapFg == null)
		{
			if (getWidth() != 0)
			{
				mBitmapFg = createForegroundBitmap(getWidth());
			}
		}
		if ((mWidth == 0 || mHeight == 0)
				&& (getWidth() != 0 && getHeight() != 0))
		{
			mWidth = mBitmapBg.getWidth();
			mHeight = mBitmapBg.getHeight();
			float scale = (float) mWidth / (float) mHeight;
			if (((float) getWidth() / getHeight()) > scale)
			{
				mHeight = getHeight();
				mWidth = (int) (mHeight * scale);
			} else
			{
				mWidth = getWidth();
				mHeight = (int) (mWidth / scale);
			}
			Log.d("", getWidth() + "," + getHeight() + ".." + mWidth + ","
					+ mHeight);
		}
	}

	public void onDraw(Canvas canvas)
	{
		if (isPorterduffMode)
		{
			getSize();
			int tmpW = (getWidth() - mWidth) / 2;
			int tmpH = (getHeight() - mHeight) / 2;
			mBitmapBg = Bitmap.createScaledBitmap(mBitmapBg, mWidth, mHeight,
					true);
			// 画出背景图
			canvas.drawBitmap(mBitmapBg, tmpW, tmpH, mPaint);
			// 设置PorterDuff模式
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DARKEN));
			// canvas.drawBitmap(bitmapFg, tmpW, tmpH - progress * height,
			// paint);
			// int tH = mHeight - (int) (mProgress * mHeight);
			int tH = getHeight() - (int) (mProgress * getHeight());
			for (int i = 0; i < tH; i++)
			{
				// canvas.drawBitmap(mBitmapFg, tmpW, tmpW + i, mPaint);
				canvas.drawBitmap(mBitmapFg, 0, i, mPaint);
			}

			// 立即取消xfermode
			mPaint.setXfermode(null);
			int oriColor = mPaint.getColor();
			mPaint.setColor(TEXT_COLOR);
			mPaint.setTextSize(mFontSize);
			String tmp = mDecimalFormat.format(mProgress);
			float tmpWidth = mPaint.measureText(tmp);
			if (mTxtBaseY == 0 && mHeight != 0)
			{
				// 在此处直接计算出来，避免了在onDraw()处的重复计算
				mTxtBaseY = (mHeight - mFontMetrics.bottom - mFontMetrics.top) / 2;
			}
			canvas.drawText(mDecimalFormat.format(mProgress), tmpW
					+ (mWidth - tmpWidth) / 2, tmpH + mTxtBaseY, mPaint);
			// 恢复为初始值时的颜色
			mPaint.setColor(oriColor);
		} else
		{
			super.onDraw(canvas);
		}
	}

	public void setProgress(float progress)
	{
		if (isPorterduffMode)
		{
			this.mProgress = progress;
			// 刷新自身。
			invalidate();
		}
	}

	public void setBitmap(Bitmap bg)
	{
		if (isPorterduffMode)
		{
			mBitmapBg = bg;

			// mWidth = mBitmapBg.getWidth();
			// mHeight = mBitmapBg.getHeight();

			// mBitmapFg = createForegroundBitmap(mWidth);
			getSize();

			Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
			mTxtBaseY = (mHeight - fontMetrics.bottom - fontMetrics.top) / 2;

			setImageBitmap(bg);
		}
	}

	public boolean isLoading()
	{
		return isLoading;
	}

	public void setLoading(boolean loading)
	{
		this.isLoading = loading;
	}

	public void setPorterDuffMode(boolean bool)
	{
		isPorterduffMode = bool;
	}

	public void setFontSize(int size)
	{
		mFontSize = size;
	}

	public void setImgUrl(String url)
	{
		// DownloadImgTask task = new DownloadImgTask(this);
		// task.execute(url);
		AsyncDrawableLoader.getInstance().loadBigDrawable(url, this);
	}

	public Drawable setImgUrl(String url, INetworkListener listener)
	{
		return AsyncDrawableLoader.getInstance().loadBigDrawable(url, listener,
				this);
	}

	public void setScaleTypeAfterDown(ScaleType type)
	{
		mType = type;
	}

	public void finish(Drawable drawable)
	{
		setPorterDuffMode(false);
		setLoading(false);
		setScaleType(getScaleType());
		setImageDrawable(drawable);
	}

	// public class DownloadImgTask extends AsyncTask<String, Float, Bitmap>
	// {
	// private static final String TAG = "DownloadImgTask";
	// private PorterDuffView pdView;
	//
	// public DownloadImgTask(PorterDuffView pdView)
	// {
	// this.pdView = pdView;
	// }
	//
	// /** 下载准备工作。在UI线程中调用。 */
	// protected void onPreExecute()
	// {
	// Log.i(TAG, "onPreExecute");
	// }
	//
	// /** 执行下载。在背景线程调用。 */
	// protected Bitmap doInBackground(String... params)
	// {
	// Log.i(TAG, "doInBackground:" + params[0]);
	// HttpClient httpClient = new DefaultHttpClient();
	// HttpGet httpGet = new HttpGet(params[0]);
	// InputStream is = null;
	// ByteArrayOutputStream baos = null;
	// try
	// {
	// HttpResponse httpResponse = httpClient.execute(httpGet);
	// printHttpResponse(httpResponse);
	// HttpEntity httpEntity = httpResponse.getEntity();
	// long length = httpEntity.getContentLength();
	// is = httpEntity.getContent();
	// if (is != null)
	// {
	// baos = new ByteArrayOutputStream();
	// byte[] buf = new byte[128];
	// int read = -1;
	// int count = 0;
	// while ((read = is.read(buf)) != -1)
	// {
	// baos.write(buf, 0, read);
	// count += read;
	// publishProgress(count * 1.0f / length);
	// }
	// Log.i(TAG, "count=" + count + " length=" + length);
	// byte[] data = baos.toByteArray();
	// Bitmap bit = BitmapFactory.decodeByteArray(data, 0,
	// data.length);
	// return bit;
	// }
	// } catch (ClientProtocolException e)
	// {
	// e.printStackTrace();
	// } catch (IOException e)
	// {
	// e.printStackTrace();
	// } finally
	// {
	// try
	// {
	// if (baos != null)
	// {
	// baos.close();
	// }
	// if (is != null)
	// {
	// is.close();
	// }
	// } catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	// }
	// return null;
	// }
	//
	// /** 更新下载进度。在UI线程调用。onProgressUpdate */
	// protected void onProgressUpdate(Float... progress)
	// {
	// pdView.setProgress(progress[0]);
	// }
	//
	// /** 通知下载任务完成。在UI线程调用。 */
	// protected void onPostExecute(Bitmap bit)
	// {
	// Log.i(TAG, "onPostExecute");
	// pdView.setPorterDuffMode(false);
	// pdView.setLoading(false);
	// pdView.setScaleType(mType);
	// pdView.setImageBitmap(bit);
	// }
	//
	// protected void onCancelled()
	// {
	// Log.i(TAG, "DownloadImgTask cancel...");
	// super.onCancelled();
	// }
	//
	// private void printHttpResponse(HttpResponse httpResponse)
	// {
	// Header[] headerArr = httpResponse.getAllHeaders();
	// for (int i = 0; i < headerArr.length; i++)
	// {
	// Header header = headerArr[i];
	// Log.i(TAG,
	// "name[" + header.getName() + "]value["
	// + header.getValue() + "]");
	// }
	// HttpParams params = httpResponse.getParams();
	// Log.i(TAG, String.valueOf(params));
	// Log.i(TAG, String.valueOf(httpResponse.getLocale()));
	// }
	// }
}
