package share.photo.network;

import java.util.HashMap;
import java.util.Map;

import share.photo.bean.FormFileBean;
import share.photo.listener.INetworkListener;
import share.photo.utility.MD5Utility;
import share.photo.utility.NetManager;


/**
 * 用户模块发送请求
 * 
 * 
 */
public class UserSend extends AbsSend
{
	
	/**
	 * 用户注册
	 * 
	 * @param pwd
	 *            密码，6+位
	 * @param name
	 *            　　用户真实姓名
	 * @param listener
	 */
	public void add(String name,String pwd, 
			INetworkListener listener)
	{
		Map<String, String> params = new HashMap<String, String>();
		//MD5加密
		pwd = MD5Utility.encrypt(pwd);
		params.put("password", pwd);
		params.put("name", name);
		requestData(NetManager.USER_REGISTER_URL, params, listener);
	}

	
	/**
	 * 用户登陆
	 * 
	 * @param email
	 *            账号，邮箱格式
	 * @param pwd
	 *            密码，6+位
	 * @param listener
	 */
	public void login(String name, String pwd, INetworkListener listener)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", name);
		pwd = MD5Utility.encrypt(pwd);
		params.put("password", pwd);
		requestData(NetManager.USER_LOGIN_URL, params, listener);
	}
	
	
	public void uploadHeadPic(int userId, FormFileBean ffb, INetworkListener listener)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", Integer.toString(userId));
		new HttpUpload().uploadData(NetManager.USRE_HEAD_PIC_UPLOAD_URL, params, ffb, listener);
	}
	
	public void addUserLikePhoto(int userId, int photoId, INetworkListener listener)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", Integer.toString(userId));
		params.put("photoId", Integer.toString(photoId));
		requestData(NetManager.USER_LIKE_PHOTO_ADD_URL, params, listener);
	}
	
	public void deleteUserLikePhoto(int userId, int photoId, INetworkListener listener)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", Integer.toString(userId));
		params.put("photoId", Integer.toString(photoId));
		requestData(NetManager.USER_LIKE_PHOTO_DETELE_URL, params, listener);
	}
}
