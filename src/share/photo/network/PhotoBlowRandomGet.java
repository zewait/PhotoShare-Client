package share.photo.network;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import share.photo.bean.PhotoInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.utility.NetManager;
import share.photo.utility.Notice;

public class PhotoBlowRandomGet extends AbsGet
{
	private INetworkListener mListener;
	public void getRandomBlowPhoto(INetworkListener listener)
	{
		mListener = listener;
		Map<String, String> params = new HashMap<String, String>();
		requestData(NetManager.PHOTO_BLOW_RANDOM_GET_URL, params);
	}
	
	@Override
	protected void onBackground(String jsonData)
	{
		Gson gson = new Gson();
		try
		{
			mListener.onReslut(gson.fromJson(jsonData, PhotoInfoBean.class));
		}
		catch (JsonSyntaxException e) {
			mListener.onError(new SharePhotoException(e));
		}
	}

	@Override
	protected void onError(SharePhotoException error)
	{
		Notice.d("error : " + error);
	}

}
