package share.photo.network;

import java.util.HashMap;
import java.util.Map;

import share.photo.bean.PhotoListInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.utility.NetManager;
import share.photo.utility.Notice;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class UserLikePhotoPhotoListGet extends AbsGet
{
	private INetworkListener mListener;
	/**
	 * 获取用户喜欢的相片列表
	 * @param userId
	 * @param pageIndex
	 * @param pageSize
	 * @param listener
	 */
	public void getUserLikePhotos(int userId, int pageIndex, int pageSize, INetworkListener listener)
	{
		mListener = listener;
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", Integer.toString(userId));
		params.put("pageIndex", Integer.toString(pageIndex));
		params.put("pageSize", Integer.toString(pageSize));
		requestData(NetManager.USER_LIKE_PHOTO_PHOTO_LIST_GET, params);
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
