package share.photo.utility;

/**
 * Log打印
 * 
 * 
 */
public final class Notice {
	/**
	 * 根据项目而修改
	 */
	protected static final String TAG = "SharePhoto";

	private Notice() {
	}

	public static void v(String msg) {
		android.util.Log.v(TAG, TAG + buildMessage(msg));
	}

	public static void v(String msg, Throwable thr) {
		android.util.Log.v(TAG, TAG + buildMessage(msg), thr);
	}

	public static void d(String msg) {
		android.util.Log.d(TAG, TAG + buildMessage(msg));
	}

	public static void d(String msg, Throwable thr) {
		android.util.Log.d(TAG, TAG + buildMessage(msg), thr);
	}

	public static void i(String msg) {
		android.util.Log.i(TAG, TAG + buildMessage(msg));
	}

	public static void i(String msg, Throwable thr) {
		android.util.Log.i(TAG, TAG + buildMessage(msg), thr);
	}

	public static void e(String msg) {
		android.util.Log.e(TAG, TAG + buildMessage(msg));
	}

	public static void w(String msg) {
		android.util.Log.w(TAG, TAG + buildMessage(msg));
	}

	public static void w(String msg, Throwable thr) {
		android.util.Log.w(TAG, TAG + buildMessage(msg), thr);
	}

	public static void w(Throwable thr) {
		android.util.Log.w(TAG, TAG + buildMessage(""), thr);
	}

	public static void e(String msg, Throwable thr) {
		android.util.Log.e(TAG, TAG + buildMessage(msg), thr);
	}

	protected static String buildMessage(String msg) {
		StackTraceElement caller = new Throwable().fillInStackTrace()
				.getStackTrace()[2];

		return new StringBuilder().append(caller.getClassName()).append(".")
				.append(caller.getMethodName()).append("(): ").append(msg)
				.toString();
	}
}
