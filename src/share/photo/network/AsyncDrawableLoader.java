package share.photo.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import share.photo.listener.INetworkListener;
import share.photo.mydebug.MyDebugUtil;
import share.photo.utility.FileUtility;
import share.photo.view.PorterDuffView;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.widget.ImageView;

/**
 * 图片加载类(Drawable类型)
 * 
 * @author 黄世凡
 * 
 */
public class AsyncDrawableLoader
{
	private static AsyncDrawableLoader mLoader = new AsyncDrawableLoader();
	// private static ImageCacheManager imageCacheManager;
	private Map<String, SoftReference<Drawable>> mCaches = new HashMap<String, SoftReference<Drawable>>();

	private static List<String> mTaskContainer = new ArrayList<String>();

	private Handler mHandler = new Handler();

	/**
	 * 缓存文件夹
	 */
	private static final String Cache_Path = "SharePhotoCache";
	/**
	 * 本地缓存路径
	 */
	private static String path;

	// /**
	// * 必须先初始化ImageCacheManager才可以使用此类
	// *
	// * @param context
	// */
	// public static void intiImageCacheManager(Context context)
	// {
	// /* FMU :FMU算法，在存储器中固定一定大小的存储空间，超过固定空间后 将缓存中占用最大尺寸的图片删除。 */
	// imageCacheManager = ImageCacheManager.getImageCacheService(context,
	// ImageCacheManager.MODE_FIXED_MEMORY_USED, "memory");
	// imageCacheManager.setMax_Memory(1024 * 1024);
	//
	// /* FTU :FTU算法，固定每张图片的缓存时限，以最后一次使用算起，超过时限后删除。 */
	// // imageCacheManager = ImageCacheManager.getImageCacheService(this,
	// // ImageCacheManager.MODE_FIXED_TIMED_USED, "time");
	// // imageCacheManager.setDelay_millisecond(3 * 60 * 1000);
	//
	// /* LRU :LRU算法，固定缓存图片数量(max_num)，当图片数量超出max_num时，将缓存中最近用的最少的图片删除。 */
	// // imageCacheManager = ImageCacheManager.getImageCacheService(this,
	// // ImageCacheManager.MODE_LEAST_RECENTLY_USED, "num");
	// // imageCacheManager.setMax_num(5);
	// /* 不使用缓存 */
	// // imageCacheManager = ImageCacheManager.getImageCacheService(this,
	// // ImageCacheManager.MODE_NO_CACHE_USED, "nocache");
	// }

	public void removeCache(String url)
	{
		mCaches.remove(url);
	}

	public static void init(Context context)
	{

		if (FileUtility.isSDCardExist())
		{
			path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
					+ File.separator + Cache_Path + File.separator;
		} else
		{
			path = context.getFilesDir() + File.separator + Cache_Path
					+ File.separator;
		}
		File filePath = new File(path);
		if (!filePath.exists())
		{
			filePath.mkdirs();
		}
	}

	private AsyncDrawableLoader()
	{
	}

	public void clear()
	{
		mCaches.clear();
	}

	public static AsyncDrawableLoader getInstance()
	{
		return mLoader;
	}

	/**
	 * 加载缩略图
	 * 
	 * @param smallImgUrl
	 * @param bigImgUrl
	 * @param img
	 * @param listener
	 * @return 返回大图的Drawable
	 */
	public Drawable loadZoomDrawable(String smallImgUrl,
			final String bigImgUrl, final ImageView img,
			final INetworkListener listener)
	{
		Drawable drawable = null;
		File f = getLocalFile(bigImgUrl);
		if (null != f)
		{
			drawable = file2Draw(f);
			if (null != drawable)
			{
				img.setScaleType(ImageView.ScaleType.CENTER_CROP);
				return drawable;
			}
		}
		img.setScaleType(ImageView.ScaleType.FIT_XY);
		drawable = loadSmallDrawable(smallImgUrl, new INetworkListener()
		{
			@Override
			public void onReslut(Object result)
			{
				img.setImageDrawable((Drawable) result);
				listener.onReslut((Drawable) result);
				Drawable drawable = loadBigDrawable(bigImgUrl, listener);
				if (null != drawable)
				{
					listener.onReslut(drawable);
				}
			}

			@Override
			public void onError(SharePhotoException e)
			{
			}
		});
		if (null != drawable)
		{
			img.setImageDrawable(drawable);
			drawable = loadBigDrawable(bigImgUrl, listener);
			if (null != drawable)
			{
				return drawable;
			}
		}
		return drawable;
	}

	/**
	 * 获取Drawable(压入内存缓存)
	 * 
	 * @param url
	 * @param img
	 */
	public void loadSmallDrawable(String url, final ImageView img)
	{
		if (null == url)
			return;
		if (mTaskContainer.contains(url))
			return;
		if (null != mCaches.get(url))
		{
			final Drawable drawable = mCaches.get(url).get();

			if (null != drawable)
			{
				mHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						img.setImageDrawable(drawable);
					}
				});
				return;
			}
		}
		mCaches.remove(url);
		mTaskContainer.add(url);
		new LoadSmallImageAsyncTask(img, null).execute(url);
	}

	public Drawable loadSmallDrawable(String url, INetworkListener listener)
	{
		Drawable drawable = null;
		if (mTaskContainer.contains(url))
			return drawable;
		if (null != mCaches.get(url))
		{
			drawable = mCaches.get(url).get();
			if (null != drawable)
				return drawable;
		}
		mCaches.remove(url);
		mTaskContainer.add(url);
		new LoadSmallImageAsyncTask(null, listener).execute(url);
		return drawable;
	}

	/**
	 * 获取Drawable(写入文件缓存)
	 * 
	 * @param url
	 * @param img
	 */
	public void loadBigDrawable(final String url, final ImageView img)
	{
		if (null == url)
			return;
		File f = getLocalFile(url);
		if (null != f)
		{
			final Drawable drawable = file2Draw(f);
			if (null != drawable)
			{
				mHandler.post(new Runnable()
				{
					public void run()
					{
						img.setImageDrawable(drawable);
					}
				});
				return;
			}
		}
		if (mTaskContainer.contains(url))
			return;
		mTaskContainer.add(url);
		new LoadBigImageAsyncTask(img, null, null).execute(url);
	}

	public Drawable loadBigDrawable(final String url,
			final INetworkListener listener)
	{

		Drawable drawable = null;
		if (null == url)
			return drawable;
		File f = getLocalFile(url);
		if (null != f)
		{
			drawable = file2Draw(f);
			if (null != drawable)
			{
				return drawable;
			}
		}
		if (mTaskContainer.contains(url))
			return drawable;
		mTaskContainer.add(url);
		new LoadBigImageAsyncTask(null, listener, null).execute(url);
		return drawable;
	}

	/**
	 * 有显示加载的load图
	 * 
	 * @param url
	 * @param pdView
	 */
	public void loadBigDrawable(final String url, final PorterDuffView pdView)
	{

		Drawable drawable = null;
		if (null == url || 0 == url.length())
			return;
		File f = getLocalFile(url);
		if (null != f)
		{
			drawable = file2Draw(f);
			if (null != drawable)
			{
				pdView.finish(drawable);
			}
		}
		if (mTaskContainer.contains(url))
			return;
		mTaskContainer.add(url);
		new LoadBigImageAsyncTask(null, null, pdView).execute(url);
	}

	public Drawable loadBigDrawable(final String url,
			INetworkListener listener, final PorterDuffView pdView)
	{

		Drawable drawable = null;
		if (null == url || 0 == url.length())
			return drawable;
		File f = getLocalFile(url);
		if (null != f)
		{
			drawable = file2Draw(f);
			if (null != drawable)
			{
				return drawable;
			}
		}
		if (mTaskContainer.contains(url))
			return drawable;
		mTaskContainer.add(url);
		new LoadBigImageAsyncTask(null, listener, pdView).execute(url);
		return drawable;
	}

	private class LoadSmallImageAsyncTask extends
			AsyncTask<String, Integer, Drawable>
	{
		private ImageView img;
		private String url;
		private INetworkListener listener;

		public LoadSmallImageAsyncTask(ImageView img, INetworkListener listener)
		{
			this.img = img;
			this.listener = listener;
		}

		@Override
		protected Drawable doInBackground(String... params)
		{
			url = params[0];
			InputStream is = null;
			try
			{
				is = (InputStream) new URL(url).getContent();
				Drawable drawable = Drawable.createFromStream(is, null);
				mCaches.put(url, new SoftReference<Drawable>(drawable));
				return drawable;
			} catch (IOException e)
			{
				mTaskContainer.remove(url);
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Drawable result)
		{
			if (null != url)
				mTaskContainer.remove(url);
			// 下载完成后的操作，可以与ui交互

			if (null != listener)
				listener.onReslut(result);
			else
			{
				img.setImageDrawable(result);
			}

			super.onPostExecute(result);
		}
	}

	private class LoadBigImageAsyncTask extends
			AsyncTask<String, Float, Drawable>
	{
		private ImageView img;
		private String url;
		private INetworkListener listener;
		private PorterDuffView pdView;

		/**
		 * 大图加载异步构造器
		 * 
		 * @param img
		 *            显示图片的组件,可以为null
		 * @param listener
		 *            监听器,可以为null
		 * @param PorterDuffView
		 *            有无加载,可以为null
		 */
		public LoadBigImageAsyncTask(ImageView img, INetworkListener listener,
				PorterDuffView pdView)
		{
			this.listener = listener;
			this.img = img;
			this.pdView = pdView;
		}

		@Override
		protected Drawable doInBackground(String... params)
		{
			url = params[0];
			InputStream is = null;
			OutputStream os = null;
			try
			{
				URL url = new URL(params[0]);
				URLConnection connection = url.openConnection();
				connection.connect();
				int len = connection.getContentLength();
				is = connection.getInputStream();
				File f = new File(path + FileUtility.getFileName(params[0]));
				f.createNewFile();
				os = new FileOutputStream(f);
				byte[] buffer = new byte[1024];
				int n = -1;
				int current = 0;
				while (-1 != (n = is.read(buffer)))
				{
					os.write(buffer, 0, n);
					os.flush();
					if (null != pdView)
					{
						current += n;
						publishProgress(current * 1.0f / len);
					}

				}
				Drawable drawable = Drawable.createFromPath(f.getPath());
				return drawable;
			} catch (IOException e)
			{
				mTaskContainer.remove(url);
				if (null != listener)
					listener.onError(new SharePhotoException(e));
				e.printStackTrace();
			} finally
			{
				try
				{
					if (null != os)
					{
						os.close();
					}
					if (null != is)
					{
						is.close();
					}
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(final Drawable result)
		{
			if (null != url)
				mTaskContainer.remove(url);

			if (null != listener)
				listener.onReslut(result);
			else
			{
				if (null == img)
				{
					pdView.finish(result);
				} else
				{
					img.setImageDrawable(result);
				}
			}

		}

		@Override
		protected void onProgressUpdate(Float... values)
		{
			pdView.setProgress(values[0]);
		}

	}

	/**
	 * 根据File得到Drawable
	 * 
	 * @param f
	 * @return
	 */
	public Drawable file2Draw(File f)
	{
		return Drawable.createFromPath(f.getPath());
	}

	/**
	 * 通过网络地址得到本地文件
	 * 
	 * @param url
	 *            图片网络地址
	 * @return 存在返回File,否则返回null
	 */
	public File getLocalFile(String url)
	{
		File f = new File(path + FileUtility.getFileName(url));
		if (f.exists())
			return f;
		return null;
	}


	public String getLocalImagePath(String url)
	{
		return url == null ? null : path + FileUtility.getFileName(url);
	}
}
