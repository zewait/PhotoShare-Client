package share.photo.network;

import java.util.HashMap;
import java.util.Map;

import share.photo.listener.INetworkListener;
import share.photo.utility.NetManager;

public class SubscriptionRelationSend extends AbsSend
{
	
	public void add(int userId, int subscriptionId, INetworkListener listener)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", Integer.toString(userId));
		params.put("subscriptionId", Integer.toString(subscriptionId));
		requestData(NetManager.SUBSCRIPTION_RELATION_ADD_URL, params, listener);
	}
	
	public void cancel(int userId, int subscriptionId, INetworkListener listener)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", Integer.toString(userId));
		params.put("subscriptionId", Integer.toString(subscriptionId));
		requestData(NetManager.SUBSCRIPTION_RELATION_CANCEL_URL, params, listener);
	}
}
