package share.photo.utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import share.photo.network.SharePhotoException;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;


/**
 * 机身文件访问工具
 * 
 * 
 */
public final class FileUtility
{

	/**
	 * 检测时候具有sdcard
	 * 
	 * @return true 就是有，否者就是没有
	 */
	public static boolean isSDCardExist()
	{
		return Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取sd卡路径
	 * 
	 * @return 路径
	 * @throws IOException
	 */
	public static String getSDPath() throws IOException
	{
		return Environment.getExternalStorageDirectory().getCanonicalPath();
	}

	/**
	 * 获取缓存文件
	 * 
	 * @param imageUrl
	 *            网址
	 * @param defaultPath
	 *            默认路径
	 * @return 已打开的文件
	 */
	public static File openCacheFile(String imageUrl, String defaultPath)
	{
		File cacheFile = null;
		String fileName = getUrlFileName(imageUrl);
		File dir = new File(defaultPath);
		if (!dir.exists())
		{
			dir.mkdirs();
		}
		
		
		cacheFile = new File(dir, fileName);
		return cacheFile;
	}

	/**
	 * 获取文件名字
	 * 
	 * @param path
	 *            完整路径
	 * @return 名字
	 */
	public static String getFileName(String path)
	{
		if(null == path) return null;
		int index = path.lastIndexOf("/");
		return path.substring(index + 1);
	}

	/**
	 * 获取网络文件名字
	 * 
	 * @param imageUrl
	 *            网址
	 * @return
	 */
	public static String getUrlFileName(String imageUrl)
	{
		final int index = imageUrl.indexOf("//");
		return imageUrl.substring(index + 2).toUpperCase()
				.replaceAll("[//_:]", "");
	}

	/**
	 * 保存文件
	 * 
	 * @param context
	 * @param bitmap
	 * @param filename
	 */
	public static void openFileGetBitmap(Context context, Bitmap bitmap,
			String filename)
	{
		BufferedOutputStream outputStream = null;
		try
		{
			outputStream = new BufferedOutputStream(context.openFileOutput(
					filename, Context.MODE_PRIVATE));
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 保存图片
	 * 
	 * @param picName
	 *            图片名字
	 * @param bitmap
	 * @return
	 * @throws SharePhotoException
	 */
	public boolean saveBitmapToSDCard(String picName, Bitmap bitmap)
			throws SharePhotoException
	{
		boolean nowbol = false;
		try
		{
			File saveFile = new File(FileUtility.getSDPath() + picName + ".png");
			if (!saveFile.exists())
			{
				saveFile.createNewFile();
			}
			FileOutputStream saveFileOutputStream = new FileOutputStream(
					saveFile);
			nowbol = bitmap.compress(Bitmap.CompressFormat.PNG, 100,
					saveFileOutputStream);
			saveFileOutputStream.close();
		} catch (Exception e)
		{
			throw new SharePhotoException(e);
		}
		return nowbol;
	}

	/**
	 * 将文件写入缓存系统中
	 * 
	 * @param filename
	 * @param is
	 * @return
	 */
	public static String savePNGFile(Context context, String fileName,
			InputStream is)
	{
		BufferedInputStream inputStream = null;
		BufferedOutputStream outputStream = null;
		try
		{
			inputStream = new BufferedInputStream(is);
			outputStream = new BufferedOutputStream(context.openFileOutput(
					fileName, Context.MODE_PRIVATE));
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) != -1)
			{
				outputStream.write(buffer, 0, length);
			}
		} catch (Exception e)
		{
		} finally
		{
			if (inputStream != null)
			{
				try
				{
					inputStream.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if (outputStream != null)
			{
				try
				{
					outputStream.flush();
					outputStream.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return context.getFilesDir() + "/" + fileName + ".png";
	}

}
