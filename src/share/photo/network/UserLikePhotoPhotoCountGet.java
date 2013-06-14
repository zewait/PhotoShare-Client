package share.photo.network;

import java.util.HashMap;
import java.util.Map;

import share.photo.bean.UserLikePhotoPhotoCountInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.utility.NetManager;
import share.photo.utility.Notice;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class UserLikePhotoPhotoCountGet extends AbsGet
{
	private INetworkListener mListener;
	public void getUserLikePhotoPhotoCount(int userId, INetworkListener listener)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", Integer.toString(userId));
		mListener = listener;
		requestData(NetManager.USER_LIKE_PHOTO_PHOTO_COUNT_GET_URL, params);
	}
	protected void onBackground(String jsonData)
	{
		Gson gson = new Gson();
		try
		{
			mListener
					.onReslut(gson.fromJson(jsonData, UserLikePhotoPhotoCountInfoBean.class));
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
