package share.photo.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * 黑白过滤
 *
 */
public class ImageBlackWhiteFilter implements ImageFilterInterface
{
	private static float[] colorArray = { 0.393f, 0.768f, 0.189f, 0f, 0f,
			0.349f, 0.686f, 0.168f, 0f, 0f, 0.272f, 0.534f, 0.131f, 0f, 0f, 0f,
			0f, 0f, 1f, 0f };
	private Bitmap mBitmap;

	public ImageBlackWhiteFilter(Bitmap bitmap)
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
