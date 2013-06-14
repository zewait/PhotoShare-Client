package share.photo.network;

import java.util.HashMap;
import java.util.Map;

import share.photo.bean.FormFileBean;
import share.photo.listener.INetworkListener;
import share.photo.utility.MD5Utility;
import share.photo.utility.NetManager;


public class CommentSend extends AbsSend
{
	/**
	 * 添加评论
	 * @param photoId
	 * @param ownerId
	 * @param toId 为0时为无回复者
	 * @param content
	 * @param listener
	 */
	public void add(int photoId, int ownerId, int toId, String content,
			INetworkListener listener)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("photoId", Integer.toString(photoId));
		params.put("ownerId", Integer.toString(ownerId));
		if (0 != toId)
			params.put("toId", Integer.toString(toId));
		params.put("content", content);
		requestData(NetManager.PHOTO_COMMENT_ADD_URL, params, listener);
	}

}
