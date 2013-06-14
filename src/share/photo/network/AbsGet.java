package share.photo.network;

import java.util.Map;

import share.photo.utility.NetManager;
import share.photo.utility.NetManager.HttpMethod;
import share.photo.utility.WorkerThread;

/**
 * 网络数据解析的基类
 * 
 * 
 */
public abstract class AbsGet
{
	private static Token mToken = Token.getInstance(); // 这个是秘要
	private static String mLangage = "zh_hans";

	public AbsGet()
	{
	}

	abstract protected void onBackground(String jsonData);

	abstract protected void onError(SharePhotoException error);

	/**
	 * 从网络上获取数据
	 * 
	 * @param url
	 *            网址
	 * @param params
	 *            参数
	 */
	public final void requestData(final String url,
			final Map<String, String> params)
	{
		WorkerThread.execute(new Runnable()
		{
			public void run()
			{
				try
				{
					String jsonData = NetManager.request(HttpMethod.Post, url,
							params);
					onBackground(jsonData);
				} catch (SharePhotoException e)
				{
					onError(e);
				}
			}
		});
	}

}
