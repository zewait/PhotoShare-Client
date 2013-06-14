package share.photo.network;

import java.util.Map;

import share.photo.listener.INetworkListener;
import share.photo.mydebug.MyDebugUtil;
import share.photo.utility.NetManager;
import share.photo.utility.NetManager.HttpMethod;
import share.photo.utility.WorkerThread;

/**
 * 发送数据基类 ，注意和传输数据没有关系
 * 
 * 
 */
public abstract class AbsSend
{
	public AbsSend()
	{
	}

	/**
	 * 向网络上发送数据
	 * 
	 * @param url
	 *            网址
	 * @param params
	 *            参数
	 */
	public final void requestData(final String url,
			final Map<String, String> params, final INetworkListener listener)
	{
		WorkerThread.execute(new Runnable()
		{
			public void run()
			{
				try
				{
					listener.onReslut(NetManager.request(HttpMethod.Post, url,
							params));
				} catch (SharePhotoException e)
				{
					listener.onError(e);
				}
			}
		});
	}
}
