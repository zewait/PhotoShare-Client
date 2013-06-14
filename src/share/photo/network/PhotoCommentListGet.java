package share.photo.network;

import java.util.HashMap;
import java.util.Map;

import share.photo.bean.CommentListInfoBean;
import share.photo.bean.PhotoListInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.utility.NetManager;
import share.photo.utility.Notice;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 *  获取用户图片列表
 */
public class PhotoCommentListGet extends AbsGet
{
	private INetworkListener mListener;

	/**
	 * 
	 * /获取图片评论列表
	 * 
	 * @param userUid
	 * @param pageIndex
	 * @param pageSize
	 * @param listener
	 */
	public void getPhotoList(int photoId, int pageIndex, int pageSize,
			INetworkListener listener)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("photoId", Integer.toString(photoId));
		params.put("pageIndex", Integer.toString(pageIndex));
		params.put("pageSize", Integer.toString(pageSize));
		mListener = listener;
		requestData(NetManager.PHOTO_COMMENT_LIST_GET_URL, params);
	}

	protected void onBackground(String jsonData)
	{
		Gson gson = new Gson();
		try
		{
			mListener
					.onReslut(gson.fromJson(jsonData, CommentListInfoBean.class));
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
