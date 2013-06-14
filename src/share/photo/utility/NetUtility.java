package share.photo.utility;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import android.os.Bundle;
import android.text.TextUtils;

/**
 * 网络工具
 * 
 * 
 */
public final class NetUtility {

	/**
	 * 拼接参数
	 * 
	 * @param param
	 *            参数
	 * @return
	 */
	public static String encodeUrl(Map<String, String> param) {
		if (param == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Set<String> keys = param.keySet();
		boolean first = true;
		for (String key : keys) {
			String value = param.get(key);
			if (!TextUtils.isEmpty(value)) {
				if (first)
					first = false;
				else
					sb.append("&");
				try {
					sb.append(URLEncoder.encode(key, "UTF-8")).append("=")
							.append(URLEncoder.encode(param.get(key), "UTF-8"));
				} catch (UnsupportedEncodingException e) {

				}
			}
		}
		return sb.toString();
	}

	/**
	 * 将网址转成UTF-8编码
	 * 
	 * @param s
	 * @return
	 */
	public static Bundle decodeUrl(String s) {
		Bundle params = new Bundle();
		if (s != null) {
			String array[] = s.split("&");
			for (String parameter : array) {
				String v[] = parameter.split("=");
				try {
					params.putString(URLDecoder.decode(v[0], "UTF-8"),
							URLDecoder.decode(v[1], "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();

				}
			}
		}
		return params;
	}

	/**
	 * 计算网址参数长度
	 * 
	 * @param paramString
	 * @return
	 */
	public static int length(String paramString) {
		int i = 0;
		for (int j = 0; j < paramString.length(); j++) {
			if (paramString.substring(j, j + 1).matches("[Α-￥]"))
				i += 2;
			else
				i++;
		}
		if (i % 2 > 0) {
			i = 1 + i / 2;
		} else {
			i = i / 2;
		}

		return i;
	}

	public static void closeSilently(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
