package share.photo.sharephoto;

import share.photo.listener.LocationCallBackListener;
import share.photo.mydebug.MyDebugUtil;
import share.photo.network.AsyncDrawableLoader;
import share.photo.network.Token;
import share.photo.utility.LocationUtility;
import android.app.Application;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;


public class SharePhotoApplication extends Application implements BDLocationListener
{
	private static final String TAG = "SharePhotoApplication";
	private static SharePhotoApplication mInstance = null;
	public boolean m_bKeyRight = true;
	private BMapManager mBMapManager = null;
	
	/*话筒设备-begin*/
	/**
	 * 设备的话筒表达的Hertz
	 */
	private int mSampleRateInHz;
	private int mBufferSizeInBytes = 100;
	private AudioRecord mAudioRecord;
	/*话筒设备-end*/
	private boolean hasLogin = false;

	public static final String strKey = "3191EA9AE5F2BA625B97AD16F8DFE0988AE0CB50";
	
	private LocationCallBackListener mCallBack;
	

	
	@Override
	public void onCreate()
	{
		super.onCreate();
		mInstance = this;
		AsyncDrawableLoader.init(this);
		initEngineManager(this);
	}
	
	public void initSampleRateInHz()
	{
		while (true)
		{
			try
			{
				mBufferSizeInBytes = AudioRecord.getMinBufferSize(
						mSampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO,
						AudioFormat.ENCODING_PCM_16BIT);

				if (mBufferSizeInBytes != AudioRecord.ERROR_BAD_VALUE)
				{
					// Seems like we have ourself the optimum AudioRecord
					// parameter for this device.
					mAudioRecord = new AudioRecord(
							MediaRecorder.AudioSource.MIC, mSampleRateInHz,
							AudioFormat.CHANNEL_CONFIGURATION_MONO,
							AudioFormat.ENCODING_PCM_16BIT, mBufferSizeInBytes);
					// Test if an AudioRecord instance can be initialized with
					// the given parameters.
					if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED)
					{
						String configResume = "initRecorderParameters(sRates) has found recorder settings supported by the device:"
								+ "\nSource   = MICROPHONE"
								+ "\nsRate    = "
								+ mSampleRateInHz
								+ "Hz"
								+ "\nChannel  = MONO"
								+ "\nEncoding = 16BIT";
						Log.i(TAG, configResume);
						mAudioRecord.release();
						// +++Release temporary recorder resources and leave.
						return;
					}
					mAudioRecord.release();
				} else
				{
					Log.w(TAG,
							"Incorrect buffer size. Continue sweeping Sampling Rate...");
				}
			} catch (IllegalArgumentException e)
			{
				Log.e(TAG, "The " + mBufferSizeInBytes
						+ "Hz Sampling Rate is not supported on this device");
			}
			mSampleRateInHz++;
		}
	}
	
	/**
	 * 获取位置
	 * @param callBack 回调接口
	 */
	public void getLoction(LocationCallBackListener callBack)
	{
		mCallBack = callBack;
		LocationUtility.startLocation(this, this);
	}
	
	/**
	 * 建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
	 */
	@Override
	public void onTerminate()
	{
		if (mBMapManager != null)
		{
			mBMapManager.destroy();
			mBMapManager = null;
		}
		super.onTerminate();
	}

	public void initEngineManager(Context context)
	{
		if (mBMapManager == null)
		{
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(strKey, new MyGeneralListener()))
		{
			Toast.makeText(
					SharePhotoApplication.getInstance().getApplicationContext(),
					"BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
		}
	}

	public static SharePhotoApplication getInstance()
	{
		return mInstance;
	}
	/**
	 *  常用事件监听，用来处理通常的网络错误，授权验证错误等
	 */
	static class MyGeneralListener implements MKGeneralListener
	{

		@Override
		public void onGetNetworkState(int iError)
		{
			if (iError == MKEvent.ERROR_NETWORK_CONNECT)
			{
				Toast.makeText(
						SharePhotoApplication.getInstance()
								.getApplicationContext(), "您的网络出错啦！",
						Toast.LENGTH_LONG).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA)
			{
				Toast.makeText(
						SharePhotoApplication.getInstance()
								.getApplicationContext(), "输入正确的检索条件！",
						Toast.LENGTH_LONG).show();
			}
			// ...
		}

		@Override
		public void onGetPermissionState(int iError)
		{
			if (iError == MKEvent.ERROR_PERMISSION_DENIED)
			{
				// 授权Key错误：
				Toast.makeText(
						SharePhotoApplication.getInstance()
								.getApplicationContext(),
						"请在Application文件输入正确的授权Key！", Toast.LENGTH_LONG).show();
				SharePhotoApplication.getInstance().m_bKeyRight = false;
			}
		}
	}

	public boolean hasLogin()
	{
		if (-1 == Token.getInstance().getUserUid())
			return false;
		return hasLogin;
	}

	public void setHasLogin(boolean hasLogin)
	{
		this.hasLogin = hasLogin;
	}

	public BMapManager getBMapManager()
	{
		return mBMapManager;
	}
	
	@Override
	public void onReceiveLocation(BDLocation location)
	{
		if (location == null)
			return;
		mCallBack.callBack(location.getLongitude(), location.getLatitude());
		LocationUtility.stopLocation();
	}

	@Override
	public void onReceivePoi(BDLocation arg0)
	{
		// TODO Auto-generated method stub
		
	}

	public int getSampleRateInHz()
	{
		return mSampleRateInHz;
	}


}
