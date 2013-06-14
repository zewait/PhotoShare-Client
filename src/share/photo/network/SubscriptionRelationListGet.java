package share.photo.network;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import share.photo.bean.SubscriptionRelationListInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.utility.NetManager;
import share.photo.utility.Notice;

public class SubscriptionRelationListGet extends AbsGet
{
	private INetworkListener mListener;

	public void getSubscriptionRelationList(int userId, int pageIndex,
			int pageSize, INetworkListener listener)
	{
		mListener = listener;
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", Integer.toString(userId));
		params.put("pageIndex", Integer.toString(pageIndex));
		params.put("pageSize", Integer.toString(pageSize));
		requestData(NetManager.SUBSCRIPTION_RELATION_LIST_GET_URL, params);

	}

	@Override
	protected void onBackground(String jsonData)
	{
		Gson gson = new Gson();
		try
		{
			mListener.onReslut(gson.fromJson(jsonData, SubscriptionRelationListInfoBean.class));
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
