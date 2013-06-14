package share.photo.network;

import java.util.HashMap;
import java.util.Map;

import share.photo.bean.PhotoListInfoBean;
import share.photo.bean.UserListInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.utility.NetManager;
import share.photo.utility.Notice;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 *  获取用户图片列表
 */
public class UserSearchByNameListGet extends AbsGet
{
	private INetworkListener mListener;

	/**
	 * 获取用户图片列表
	 * 
	 * @param userUid
	 * @param pageIndex
	 * @param pageSize
	 * @param listener
	 */
	public void getSearchByNameList(String userName,int pageIndex,int pageSize,INetworkListener listener)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("name",userName);
		params.put("pageIndex", Integer.toString(pageIndex));
		params.put("pageSize", Integer.toString(pageSize));
		System.out.println("==1==");
		mListener = listener;
		requestData(NetManager.USER_SEARCH_BY_NAME_URL, params);
	}

	protected void onBackground(String jsonData)
	{
		Gson gson = new Gson();
		try
		{
			mListener
					.onReslut(gson.fromJson(jsonData, UserListInfoBean.class));
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
