package share.photo.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

public class ImageDesaltFilter implements ImageFilterInterface
{
	private static float[] colorArray = {1f,0f,0f,0f,0f,0f,1f,0f,0f,0f,0f,0f,0.5f,0f,0f,0f,0f,0f,1f,0f};
	private Bitmap mBitmap;
	public ImageDesaltFilter(Bitmap bitmap)
	{
		mBitmap = bitmap;
	}
	@Override
	public Bitmap imageProcess()
	{
		Bitmap bitmap = Bitmap.createBitmap(mBitmap);
		Canvas canvas = new Canvas(bitmap);
		//新建颜色矩阵对象       
		ColorMatrix colorMatrix = new ColorMatrix();     
        //设置颜色矩阵的值   
        colorMatrix.set(colorArray);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap,0,0,paint);  
		return bitmap;
	}

}
