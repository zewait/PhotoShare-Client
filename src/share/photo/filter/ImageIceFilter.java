package share.photo.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * 冰冻滤镜
 * 
 * @author 黄世凡
 * 
 */
public class ImageIceFilter implements ImageFilterInterface
{
	private ImageData image = null; // 图片信息类

	public ImageIceFilter(Bitmap bmp)
	{
		image = new ImageData(bmp);
	}

	public Bitmap imageProcess()
	{
		int width = image.getWidth();
		int height = image.getHeight();
		int R, G, B, pixel;
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				R = image.getRComponent(x, y); // 获取RGB三原色
				G = image.getGComponent(x, y);
				B = image.getBComponent(x, y);

				pixel = R - G - B;
				pixel = pixel * 3 / 2;
				if (pixel < 0)
					pixel = -pixel;
				if (pixel > 255)
					pixel = 255;
				R = pixel;

				pixel = G - B - R;
				pixel = pixel * 3 / 2;
				if (pixel < 0)
					pixel = -pixel;
				if (pixel > 255)
					pixel = 255;
				G = pixel;

				pixel = B - R - G;
				pixel = pixel * 3 / 2;
				if (pixel < 0)
					pixel = -pixel;
				if (pixel > 255)
					pixel = 255;
				B = pixel;
				image.setPixelColor(x, y, R, G, B);
			} // x
		} // y

		return image.getDstBitmap();
	}
}
