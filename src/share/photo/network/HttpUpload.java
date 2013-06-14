package share.photo.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import share.photo.bean.FormFileBean;
import share.photo.listener.INetworkListener;
import share.photo.utility.NetUtility;
import share.photo.utility.WorkerThread;

import android.text.TextUtils;


/**
 * http协议post上传文件
 * 
 * @author 黄世凡
 * 
 */
public class HttpUpload {
	private static final String LANGAGE = "zh_hans";

	private static final String BOUNDARY = "---------------------------7da2137580612";
	private static final String NEXT_LINE = "\r\n";
	private static final String DATA_SPLIT_LINE = "--" + BOUNDARY + NEXT_LINE;
	private static final String END_LINE = "--" + BOUNDARY + "--" + NEXT_LINE;

	public final void uploadData(final String url,
			final Map<String, String> params, final FormFileBean formFile,
			final INetworkListener listener) {
		uploadData(url, params, new FormFileBean[] { formFile }, listener);
	}

	public final void uploadData(final String url,
			final Map<String, String> params, final FormFileBean[] formFiles,
			final INetworkListener listener) {
	//	params.put("lang", LANGAGE);
		
		WorkerThread.execute(new Runnable() {
			public void run() {
				try {
					listener.onReslut(postUpload(url, params, formFiles));

				} catch (SharePhotoException e) {
					listener.onError(e);
				}
			}
		});
	}

	public String postUpload(String path, Map<String, String> params,
			FormFileBean[] formFiles) throws SharePhotoException {
		BufferedReader br = null;
		OutputStream os = null;
		InputStream is = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(path);

			Proxy proxy = getProxy();

			if (proxy != null)
				conn = (HttpURLConnection) url.openConnection(proxy);
			else
				conn = (HttpURLConnection) url.openConnection();

			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);

			conn.setUseCaches(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			os = conn.getOutputStream();
			if (null != params && params.size() > 0) {

				for (Map.Entry<String, String> entry : params.entrySet()) {
					// 得到文本数据
					StringBuilder textEntry = new StringBuilder();
					textEntry.append(DATA_SPLIT_LINE);
					textEntry.append("Content-Disposition: form-data; ");
					textEntry.append("name=\"" + entry.getKey() + "\";");
					textEntry.append(NEXT_LINE + NEXT_LINE);
					textEntry.append(entry.getValue());
					textEntry.append(NEXT_LINE);

					// 发送文本数据
					os.write(textEntry.toString().getBytes());
					// System.out.print(textEntry);

				}
			}
			if (null != formFiles && formFiles.length > 0) {
				for (FormFileBean formFile : formFiles) {
					// 得到文件数据
					StringBuilder fileExplain = new StringBuilder();
					fileExplain = new StringBuilder();
					fileExplain.append(DATA_SPLIT_LINE);
					fileExplain.append("Content-Disposition: form-data; ");
					fileExplain.append("name=\"" + formFile.getParameterName()
							+ "\"; ");
					fileExplain.append("filename=\"" + formFile.getFileName()
							+ "\";");
					fileExplain.append(NEXT_LINE);
					fileExplain.append("Content-Type: "
							+ formFile.getContentType());
					fileExplain.append(NEXT_LINE + NEXT_LINE);

					// 发送文件数据
					os.write(fileExplain.toString().getBytes());
					// System.out.print(fileExplain.toString());
					if (null == formFile.getInputStream()) {

						os.write(formFile.getData());
						// System.out.println(new String(formFile.getData()));
					} else {
						byte[] buff = new byte[4 * 1024];
						int n = -1;
						while (-1 != (n = formFile.getInputStream().read(buff,
								0, buff.length))) {

							os.write(buff, 0, n);
							// System.out.println(new String(buff));
						}
					}
					os.write(NEXT_LINE.getBytes());
					// System.out.println(NEXT_LINE);
				}
			}
			// 发送完毕,发送结束行
			os.write(END_LINE.getBytes());
			// System.out.println(END_LINE);
			os.flush();

			return response(conn);
		} catch (IOException e) {
			throw new SharePhotoException(e);
		} finally {
			try {
				if (null != os) {
					os.close();
				}

				if (null != is) {
					is.close();
				}
				if (null != br) {
					br.close();
				}
				if (null != conn) {
					conn.disconnect();
				}
			} catch (IOException e) {
				throw new SharePhotoException(e);
			}
		}
	}

	public String postUpload(String path, Map<String, String> params,
			FormFileBean formFile) throws SharePhotoException {
		return postUpload(path, params, new FormFileBean[] { formFile });
	}

	/**
	 * vpn
	 * 
	 * @return 代理
	 */
	private static Proxy getProxy() {
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
			throws SharePhotoException {
		String result = null;
		try {
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new ConnectException("检查网络连接");
			} else {
				result = readResult(httpURLConnection);
				if (result != null) {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.getInt("code");
					if (code != 0) {
						String message;
						message = jsonObject.getString("message");
						throw new SharePhotoException(message, code);
					}
				}
			}
		} catch (JSONException e) {
			throw new SharePhotoException(e);
		} catch (ConnectException e) {
			throw new SharePhotoException(e);
		} catch (IOException e) {
			throw new SharePhotoException(e);
		}
		return result;
	}

	/**
	 * 读取返回结果
	 * 
	 * @param urlConnection
	 * @return
	 * @throws IOException
	 */
	private static String readResult(HttpURLConnection urlConnection)
			throws IOException {
		InputStream is = null;
		BufferedReader buffer = null;
		try {
			is = urlConnection.getInputStream();
			String content_encode = urlConnection.getContentEncoding();
			if (null != content_encode && !"".equals(content_encode)
					&& content_encode.equals("gzip")) {
				is = new GZIPInputStream(is);
			}
			buffer = new BufferedReader(new InputStreamReader(is));
			StringBuilder strBuilder = new StringBuilder();
			String line;
			while ((line = buffer.readLine()) != null) {
				strBuilder.append(line);
			}
			return strBuilder.toString();
		} finally {
			NetUtility.closeSilently(is);
			NetUtility.closeSilently(buffer);
			urlConnection.disconnect();
		}

	}
}
