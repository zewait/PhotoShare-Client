package share.photo.network;

import java.util.HashMap;
import java.util.Map;

import share.photo.bean.UserLikePhotoWhetherInfoBean;
import share.photo.bean.UserListInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.utility.NetManager;
import share.photo.utility.Notice;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class UserLikePhotoWhetherGet extends AbsGet
{
	private INetworkListener mListener;

	public void getUserLikePhotoWhetherInfo(int userId, int photoId,
			INetworkListener listener)
	{
		mListener = listener;
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", Integer.toString(userId));
		params.put("photoId", Integer.toString(photoId));
		requestData(NetManager.USER_LIKE_PHOTO_WHETHER_URL, params);
	}

	@Override
	protected void onBackground(String jsonData)
	{
		Gson gson = new Gson();
		try
		{
			mListener.onReslut(gson.fromJson(jsonData, UserLikePhotoWhetherInfoBean.class));
		} catch (JsonSyntaxException e)
		{
			mListener.onError(new SharePhotoException(e));
		}
	}

	@Override
	protected void onError(SharePhotoException error)
	{
		Notice.d("error " + error);
	}

}
