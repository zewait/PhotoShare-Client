package share.photo.network;

import java.util.HashMap;
import java.util.Map;

import share.photo.bean.PhotoListInfoBean;
import share.photo.bean.PhotoNearbyListInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.utility.NetManager;
import share.photo.utility.Notice;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * 获取附近用户图片列表
 */
public class PhotosNearbyListGet extends AbsGet
{
	private INetworkListener mListener;

	/**
	 * 获取附近用户图片
	 * @param userId
	 * @param longitude
	 * @param latitude
	 * @param listener
	 */
	public void getPhotoNearbyList(int userId, double longitude,
			double latitude, INetworkListener listener)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", Integer.toString(userId));
		params.put("longitude", Double.toString(longitude));
		params.put("latitude", Double.toString(latitude));
		mListener = listener;
		requestData(NetManager.PHOTO_NEAR_BY_LIST_GET_URL, params);
	}

	protected void onBackground(String jsonData)
	{
		Gson gson = new Gson();
		try
		{

			mListener.onReslut(gson.fromJson(jsonData,
					PhotoNearbyListInfoBean.class));
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
