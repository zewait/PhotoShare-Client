package share.photo.filter;

import share.photo.utility.ImageUtility;
import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * 连环画特效滤镜
 * 
 * @author 黄世凡
 * 
 */
public class ImageComicFilter implements ImageFilterInterface
{

	private ImageData image = null;

	public ImageComicFilter(Bitmap bmp)
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

				// R = |g – b + g + r| * r / 256;
				pixel = G - B + G + R;
				if (pixel < 0)
					pixel = -pixel;
				pixel = pixel * R / 256;
				if (pixel > 255)
					pixel = 255;
				R = pixel;

				// G = |b – g + b + r| * r / 256;
				pixel = B - G + B + R;
				if (pixel < 0)
					pixel = -pixel;
				pixel = pixel * R / 256;
				if (pixel > 255)
					pixel = 255;
				G = pixel;

				// B = |b – g + b + r| * g / 256;
				pixel = B - G + B + R;
				if (pixel < 0)
					pixel = -pixel;
				pixel = pixel * G / 256;
				if (pixel > 255)
					pixel = 255;
				B = pixel;
				image.setPixelColor(x, y, R, G, B);
			}
		}
		Bitmap bitmap = image.getDstBitmap();
		bitmap = ImageUtility.toGrayscale(bitmap); // 图片灰度化处理
		image = new ImageData(bitmap);
		return image.getDstBitmap();
	}

}
