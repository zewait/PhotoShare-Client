package share.photo.network;

import java.util.HashMap;
import java.util.Map;

import share.photo.bean.PhotoListInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.utility.NetManager;
import share.photo.utility.Notice;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 *  获取用户订阅的图片列表
 */
public class SubscriptionPhotoListGet extends AbsGet
{
	private INetworkListener mListener;

	/**
	 * 获取用户订阅的图片列表
	 * 
	 * @param userUid
	 * @param pageIndex
	 * @param pageSize
	 * @param listener
	 */
	public void getSubscriptionPhotoList(int userUid, int pageIndex, int pageSize,
			INetworkListener listener)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", Integer.toString(userUid));
		params.put("pageIndex", Integer.toString(pageIndex));
		params.put("pageSize", Integer.toString(pageSize));
		mListener = listener;
		requestData(NetManager.SUBSCRIPTION_PHOTO_LIST_GET_URL, params);
	}

	protected void onBackground(String jsonData)
	{
		Gson gson = new Gson();
		try
		{
			mListener
					.onReslut(gson.fromJson(jsonData, PhotoListInfoBean.class));
		} catch (JsonSyntaxException e)
		{
			mListener.onError(new SharePhotoException(e));
		}
	}

	protected void onError(SharePhotoException error)
	{
		Notice.d("error : " + error);
	}

}
