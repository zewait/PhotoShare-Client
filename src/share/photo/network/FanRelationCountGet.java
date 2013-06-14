package share.photo.network;

import java.util.HashMap;
import java.util.Map;

import share.photo.bean.FanRelationCountInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.utility.NetManager;
import share.photo.utility.Notice;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class FanRelationCountGet extends AbsGet
{
	private INetworkListener mListener;
	
	public void getSubscriptionRelationCount(int userId, INetworkListener listener)
	{
		mListener = listener;
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", Integer.toString(userId));
		requestData(NetManager.FAN_RELATION_COUNT_GET_URL, params);
	}

	@Override
	protected void onBackground(String jsonData)
	{
		Gson gson = new Gson();
		try
		{
			mListener.onReslut(gson.fromJson(jsonData,
					FanRelationCountInfoBean.class));
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
