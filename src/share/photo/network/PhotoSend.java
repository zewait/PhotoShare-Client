package share.photo.network;

import java.util.HashMap;
import java.util.Map;

import share.photo.bean.FormFileBean;
import share.photo.listener.INetworkListener;
import share.photo.utility.NetManager;

public class PhotoSend extends AbsSend
{
	/**
	 * 上传图片
	 * 
	 * @param userUid
	 *            用户id
	 * @param contents
	 *            内容
	 * @param longitude
	 *            经度
	 * @param latitude
	 *            纬度
	 * @param address
	 *            地址
	 * @param imageLink
	 *            图片文件
	 * @param listener
	 *            监听器
	 */
	public void upload(int userUid, String contents, double longitude,
			double latitude, String address, boolean isBlow,
			FormFileBean imageLink, INetworkListener listener)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", Integer.toString(userUid));
		params.put("content", contents);
		params.put("longitude", Double.toString(longitude));
		params.put("latitude", Double.toString(latitude));
		params.put("address", address);
		params.put("isBlow", Boolean.toString(isBlow));
		new HttpUpload().uploadData(NetManager.PHOTO_UPLOAD_URL, params,
				imageLink, listener);
	}
}
