package share.photo.filter;

import android.graphics.Bitmap;

public class ImageSoftGlowFilter implements ImageFilterInterface
{
	/**
	 * 高亮对比度特效
	 */
	ImageBrightContrastFilter contrastFx;

	ImageGaussianBlurFilter gaussianBlurFx;
	private ImageData image = null;

	public ImageSoftGlowFilter(Bitmap bmp)
	{
		image = new ImageData(bmp);
	}

	public ImageSoftGlowFilter(Bitmap bmp, int nSigma, float nBrightness,
			float nContrast)
	{
		gaussianBlurFx = new ImageGaussianBlurFilter(bmp);
		gaussianBlurFx.Sigma = nSigma;
		Bitmap bitmap = gaussianBlurFx.imageProcess(); // 柔化处理
		contrastFx = new ImageBrightContrastFilter(bitmap);
		contrastFx.BrightnessFactor = nBrightness;
		contrastFx.ContrastFactor = nContrast;

		bitmap = contrastFx.imageProcess(); // 高亮对比度
	}

	public Bitmap imageProcess()
	{
		int width = image.getWidth();
		int height = image.getHeight();
		ImageData clone = image.clone();
		int old_r, old_g, old_b, r, g, b;
		for (int x = 0; x < (width - 1); x++)
		{
			for (int y = 0; y < (height - 1); y++)
			{
				old_r = clone.getRComponent(x, y);
				old_g = clone.getGComponent(x, y);
				old_b = clone.getBComponent(x, y);

				r = 255 - (255 - old_r) * (255 - image.getRComponent(x, y))
						/ 255;
				g = 255 - (255 - old_g) * (255 - image.getGComponent(x, y))
						/ 255;
				b = 255 - (255 - old_b) * (255 - image.getBComponent(x, y))
						/ 255;
				image.setPixelColor(x, y, r, g, b);
			}
		}
		return image.getDstBitmap();
	}
}
