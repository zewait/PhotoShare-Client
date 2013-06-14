package share.photo.utility;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import org.json.JSONException;
import org.json.JSONObject;

import share.photo.mydebug.MyDebugUtil;
import share.photo.network.SharePhotoException;
import android.text.TextUtils;

/**
 * 网络管理
 * 
 */
public final class NetManager
{

	public static final String URL_API = "http://192.168.0.102:8080/Photo_Share_Server/";

	// private static final String UrlApi =
	// "htp://192.168.10.185:8080/Photo_Share_Server/";
	/*
	 * 以下是全部网络api的链接
	 */
	/**
	 * 用户注册
	 */
	public static final String USER_REGISTER_URL = URL_API
			+ "servlet/UserRegister?";
	/**
	 * 用户登录
	 */
	public static final String USER_LOGIN_URL = URL_API + "servlet/UserLogin?";
	/**
	 * 上传图片
	 */
	public static final String PHOTO_UPLOAD_URL = URL_API
			+ "servlet/PhotoUpload?";// 上传图片
	/**
	 * 获取用户图片
	 */
	public static final String PHOTO_LIST_4_USER_ID_GET_URL = URL_API
			+ "servlet/PhotoList4UserIdGet?";
	/**
	 * 获取用户图片数量
	 */
	public static final String PHOTO_COUNT_GET_URL = URL_API
			+ "servlet/PhotoCountGet?";
	/**
	 * 获取用户订阅用户的数量
	 */
	public static final String SUBSCRIPTION_RELATION_COUNT_GET_URL = URL_API
			+ "servlet/SubscriptionRelationCountGet?";
	/**
	 * 根据用户获取粉丝量
	 */
	public static final String FAN_RELATION_COUNT_GET_URL = URL_API
			+ "servlet/FanRelationCountGet?";
	/**
	 * 上传头像
	 */
	public static final String USRE_HEAD_PIC_UPLOAD_URL = URL_API
			+ "servlet/UserHeadPicUpload?";
	/**
	 * 获取用户订阅的用户列表
	 */
	public static final String SUBSCRIPTION_RELATION_LIST_GET_URL = URL_API
			+ "servlet/SubscriptionRelationListGet?";
	/**
	 * 获取用户的粉丝列表
	 */
	public static final String FAN_RELATION_LIST_GET_URL = URL_API
			+ "servlet/FanRelationListGet?";
	/**
	 * 获取用户资料
	 */
	public static final String USER_GET_URL = URL_API + "servlet/UserGet?";
	/**
	 * 获取用户订阅的图片列表
	 */
	public static final String SUBSCRIPTION_PHOTO_LIST_GET_URL = URL_API
			+ "servlet/SubscriptionPhotoListGet?";
	/**
	 * 获取图片评论列表
	 */
	public static final String PHOTO_COMMENT_LIST_GET_URL = URL_API
			+ "servlet/PhotoCommentListGet?";
	/**
	 * 添加评论
	 */
	public static final String PHOTO_COMMENT_ADD_URL = URL_API
			+ "servlet/PhotoCommentAdd?";
	/**
	 * 获取附近用户图片
	 */
	public static final String PHOTO_NEAR_BY_LIST_GET_URL = URL_API
			+ "servlet/PhotoNearbyListGet?";
	/**
	 * 获取订阅与否
	 */
	public static final String SUBSCRIPTION_RELATION_WHETHER_GET_URL = URL_API
			+ "servlet/SubscriptionRelationWhether?";
	/**
	 * 添加订阅
	 */
	public static final String SUBSCRIPTION_RELATION_ADD_URL = URL_API
			+ "servlet/SubscriptionRelationAdd?";
	/**
	 * 取消订阅
	 */
	public static final String SUBSCRIPTION_RELATION_CANCEL_URL = URL_API
			+ "servlet/SubscriptionRelationDelete?";
	/**
	 * 随机获取吹图片
	 */
	public static final String PHOTO_BLOW_RANDOM_GET_URL = URL_API
			+ "servlet/PhotoBlowRandomGet?";
	/**
	 * 获取相片评论总数
	 */
	public static final String PHOTO_COMMENT_COUNT_GET = URL_API
			+ "servlet/PhotoCommentCountGet?";
	/**
	 * 搜索附近用户
	 */
	public static final String USER_SEARCH_BY_NAME_URL = URL_API
			+ "servlet/UserSearchByName?";
	/**
	 * 用户添加喜欢的相片
	 */
	public static final String USER_LIKE_PHOTO_ADD_URL = URL_API
			+ "servlet/UserLikePhotoAdd?";
	/**
	 * 用户删除喜欢的相片
	 */
	public static final String USER_LIKE_PHOTO_DETELE_URL = URL_API
			+ "servlet/UserLikePhotoDelete?";
	/**
	 * 获取用户是否喜欢此相片
	 */
	public static final String USER_LIKE_PHOTO_WHETHER_URL = URL_API
			+ "servlet/UserLikePhotoWhetherGet?";

	/**
	 * 获取用户喜欢相片的总数
	 */
	public static final String USER_LIKE_PHOTO_PHOTO_COUNT_GET_URL = URL_API
			+ "servlet/UserLikePhotoPhotoCountGet?";
	/**
	 * 获取用户喜欢的相片列表
	 */
	public static final String USER_LIKE_PHOTO_PHOTO_LIST_GET = URL_API
			+ "servlet/UserLikePhotoPhotoListGet?";
	/**
	 * 获取图片喜欢的人数
	 */
	public static final String USER_LIKE_PHOTO_USER_COUNT_GET_URL = URL_API
			+ "servlet/UserLikePhotoUsersCountGet?";

	
	
	
	/**
	 * 网络的超时时间设定
	 * */
	private static final int CONNECT_TIMEOUT = 10 * 1000;
	private static final int READ_TIMEOUT = 10 * 1000;

	/**
	 * Http的通信方式
	 * */
	public enum HttpMethod
	{
		Post, Get, Get_AVATAR_File, Get_PICTURE_FILE
	}

	/**
	 * 网络请求
	 * 
	 * @param httpMethod
	 *            Post、Get两种网络访问方式
	 * @param url
	 *            网址
	 * @param param
	 *            参数
	 * @return 服务器端的数据
	 * @throws SharePhotoException
	 *             针对项目社交图片分享的异常类
	 */
	public static String request(HttpMethod httpMethod, String url,
			Map<String, String> param) throws SharePhotoException
	{
		switch (httpMethod)
		{
		case Post:
			return doPost(url, param);
		case Get:
			return doGet(url, param);
		default:
			break;
		}
		return "";
	}

	/**
	 * Get方式网络访问
	 * 
	 * @param urlAddress
	 *            网络地址
	 * @param param
	 *            参数
	 * @return 结果
	 * @throws SharePhotoException
	 *             社交图片分享异常
	 */
	private static String doGet(String urlAddress, Map<String, String> param)
			throws SharePhotoException
	{
		HttpURLConnection urlConnection = null;
		try
		{
			StringBuilder urlBuilder = new StringBuilder(urlAddress);
			urlBuilder.append("?").append(NetUtility.encodeUrl(param));
			URL url = new URL(urlBuilder.toString());
			Proxy proxy = getProxy();
			if (proxy != null)
				urlConnection = (HttpURLConnection) url.openConnection(proxy);
			else
				urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(false);
			urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
			urlConnection.setReadTimeout(READ_TIMEOUT);
			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setRequestProperty("Charset", "UTF-8");
			urlConnection
					.setRequestProperty("Accept-Encoding", "gzip, deflate");
			urlConnection.connect();
			return response(urlConnection);
		} catch (IOException e)
		{
			throw new SharePhotoException("检查网络", 0);
		} finally
		{
			if (urlConnection != null)
			{
				urlConnection.disconnect();
			}
		}

	}

	/**
	 * Post方式的访问网络
	 * 
	 * @param urlAddress
	 *            网络地址
	 * @param param
	 *            参数
	 * @return 结果
	 * @throws SharePhotoException
	 *             社交图片分享
	 */
	private static String doPost(String urlAddress, Map<String, String> param)
			throws SharePhotoException
	{
		Notice.d(urlAddress + NetUtility.encodeUrl(param));
		HttpURLConnection urlConnection = null;
		try
		{
			URL url = new URL(urlAddress);
			Proxy proxy = getProxy();
			if (proxy != null)
				urlConnection = (HttpURLConnection) url.openConnection(proxy);
			else
				urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setUseCaches(false);
			urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
			urlConnection.setReadTimeout(READ_TIMEOUT);
			urlConnection.setInstanceFollowRedirects(false);
			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setRequestProperty("Charset", "UTF-8");
			urlConnection
					.setRequestProperty("Accept-Encoding", "gzip, deflate");
			urlConnection.connect();
			DataOutputStream out = new DataOutputStream(
					urlConnection.getOutputStream());
			out.write(NetUtility.encodeUrl(param).getBytes());
			out.flush();
			out.close();
			return response(urlConnection);
		} catch (IOException e)
		{
			throw new SharePhotoException(e);
		} finally
		{
			if (urlConnection != null)
			{
				urlConnection.disconnect();
			}
		}
	}

	/**
	 * 读取返回结果
	 * 
	 * @param urlConnection
	 * @return
	 * @throws IOException
	 */
	private static String readResult(HttpURLConnection urlConnection)
			throws IOException
	{
		InputStream is = null;
		BufferedReader buffer = null;
		try
		{
			is = urlConnection.getInputStream();
			String content_encode = urlConnection.getContentEncoding();
			if (null != content_encode && !"".equals(content_encode)
					&& content_encode.equals("gzip"))
			{
				is = new GZIPInputStream(is);
			}
			buffer = new BufferedReader(new InputStreamReader(is));
			StringBuilder strBuilder = new StringBuilder();
			String line;
			while ((line = buffer.readLine()) != null)
			{
				strBuilder.append(line);
			}
			return strBuilder.toString();
		} finally
		{
			NetUtility.closeSilently(is);
			NetUtility.closeSilently(buffer);
			urlConnection.disconnect();
		}

	}

	/**
	 * 带宽测试
	 * 
	 * @return 时间
	 */
	public static String getBoundry()
	{
		StringBuffer _sb = new StringBuffer();
		for (int t = 1; t < 12; t++)
		{
			long time = System.currentTimeMillis() + t;
			if (time % 3 == 0)
			{
				_sb.append((char) time % 9);
			} else if (time % 3 == 1)
			{
				_sb.append((char) (65 + time % 26));
			} else
			{
				_sb.append((char) (97 + time % 26));
			}
		}
		return _sb.toString();
	}

	/**
	 * vpn
	 * 
	 * @return 代理
	 */
	private static Proxy getProxy()
	{
		String proxyHost = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");
		if (!TextUtils.isEmpty(proxyHost) && !TextUtils.isEmpty(proxyPort))
			return new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(
					proxyHost, Integer.valueOf(proxyPort)));
		else
			return null;
	}

	/**
	 * 接受反馈信息
	 * 
	 * @param httpURLConnection
	 * @return 网络结果
	 * @throws SharePhotoException
	 *             社交图片分享异常
	 */
	private static String response(HttpURLConnection httpURLConnection)
			throws SharePhotoException
	{
		String result = null;
		try
		{
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
			{
				throw new ConnectException("检查网络连接");
			} else
			{
				result = readResult(httpURLConnection);
				if (result != null)
				{
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.getInt("code");
					if (code != 0)
					{
						String message;
						message = jsonObject.getString("message");
						throw new SharePhotoException(message, code);
					}
				}
			}
		} catch (JSONException e)
		{
			throw new SharePhotoException(e);
		} catch (ConnectException e)
		{
			throw new SharePhotoException(e);
		} catch (IOException e)
		{
			throw new SharePhotoException(e);
		}
		return result;
	}
}
