package share.photo.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ImageData
{
	private Bitmap mBitmap;
	private int[] dst;//图片像素保存数组
	public ImageData(Bitmap bitmap)
	{
		this.mBitmap = bitmap;
		dst =  new int[getWidth() * getHeight()];
		bitmap.getPixels(dst, 0, getWidth(), 0, 0, getWidth(), getHeight()); 
	}
	
	public ImageData clone()
	{
		return new ImageData(mBitmap);
	}
	
	public Bitmap getDstBitmap()
	{
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(dst, 0, getWidth(), 0, 0, getWidth(), getHeight());
		return bitmap;
	}
	
	public void setPixelColor(int x, int y, int r, int g, int b)
	{
		dst[getPos(x, y)] = Color.rgb(r, g, b);
	}
	
	public int getRComponent(int x, int y)
	{
		return Color.red(getPixColor(x, y));		
	}
	
	public int getGComponent(int x, int y)
	{
		return Color.green(getPixColor(x, y));
	}
	
	public int getBComponent(int x, int y)
	{
		return Color.blue(getPixColor(x, y));
	}
	
	private int getPixColor(int x, int y)
	{
		int pos = getPos(x, y);
		int pixColor = dst[pos]; // 获取图片当前点的像素值
		return pixColor;
	}
	
	private int getPos(int x, int y)
	{
		return y * getWidth() + x;
	}
	
	public int getHeight()
	{
		return mBitmap.getHeight();
	}
	
	public int getWidth()
	{
		return mBitmap.getWidth();
	}
}
