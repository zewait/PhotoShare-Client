package share.photo.network;

import java.util.HashMap;
import java.util.Map;

import share.photo.bean.CommentCountInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.utility.NetManager;
import share.photo.utility.Notice;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class PhotoCommentCountGet extends AbsGet
{
	private INetworkListener mListener;

	public void getPhotoCommentCount(int photoId, INetworkListener listener)
	{
		mListener = listener;
		Map<String, String> params = new HashMap<String, String>();
		params.put("photoId", Integer.toString(photoId));
		requestData(NetManager.PHOTO_COMMENT_COUNT_GET, params);
	}

	@Override
	protected void onBackground(String jsonData)
	{
		Gson gson = new Gson();
		try
		{
			mListener.onReslut(gson.fromJson(jsonData,
					CommentCountInfoBean.class));
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
