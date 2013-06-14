package share.photo.network;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import share.photo.bean.SubscriptionRelationWhetherInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.utility.NetManager;

public class SubscriptionRelationWhetherGet extends AbsGet
{
	private INetworkListener mListener;
	public void getSubscriptionRelationWhether(int userId, int subscriptionId, INetworkListener listener)
	{
		mListener = listener;
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", Integer.toString(userId));
		params.put("subscriptionId", Integer.toString(subscriptionId));
		requestData(NetManager.SUBSCRIPTION_RELATION_WHETHER_GET_URL, params);
	}
	
	@Override
	protected void onBackground(String jsonData)
	{
		Gson gson = new Gson();
		try
		{
			mListener.onReslut(gson.fromJson(jsonData, SubscriptionRelationWhetherInfoBean.class));
		}
		catch (JsonSyntaxException e) {
			mListener.onError(new SharePhotoException(e));
		}
	}

	@Override
	protected void onError(SharePhotoException error)
	{
		// TODO Auto-generated method stub

	}

}
