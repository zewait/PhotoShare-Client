package share.photo.network;

import java.util.HashMap;
import java.util.Map;

import share.photo.bean.UserLikePhotoUserCountInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.utility.NetManager;
import share.photo.utility.Notice;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * 获取图片喜欢人数
 * @author 黄世凡
 *
 */
public class UserLikePhotoUserCountGet extends AbsGet
{
	private INetworkListener mListener;
	public void getUserLikePhotoUserCount(int photoId, INetworkListener listener)
	{
		mListener = listener;
		Map<String, String> params = new HashMap<String, String>();
		params.put("photoId", Integer.toString(photoId));
		requestData(NetManager.USER_LIKE_PHOTO_USER_COUNT_GET_URL, params);
	}
	
	@Override
	protected void onBackground(String jsonData)
	{
		Gson gson = new Gson();
		try
		{
			mListener.onReslut(gson.fromJson(jsonData, UserLikePhotoUserCountInfoBean.class));
		} catch (JsonSyntaxException e)
		{
			mListener.onError(new SharePhotoException(e));
		}
	}

	@Override
	protected void onError(SharePhotoException error)
	{
		Notice.d("error : " + error);
	}

}
