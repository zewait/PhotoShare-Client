package share.photo.mydebug;

import android.util.Log;

public class MyDebugUtil
{
	private static final boolean DEBUG = true;
	public static final String TAG = "ze.debug"; 
	public static void i(Object msg)
	{
		if(DEBUG)
			Log.i(TAG, msg.toString());
	}
	
	public static void e(Object msg)
	{
		if(DEBUG)
			Log.e(TAG, "error: " + msg.toString());
	}
}
