package share.photo.sharephoto;

import share.photo.bean.CommentCountInfoBean;
import share.photo.bean.PhotoBean;
import share.photo.bean.PhotoInfoBean;
import share.photo.bean.UserBean;
import share.photo.listener.INetworkListener;
import share.photo.mydebug.MyDebugUtil;
import share.photo.network.AsyncDrawableLoader;
import share.photo.network.PhotoBlowRandomGet;
import share.photo.network.PhotoCommentCountGet;
import share.photo.network.SharePhotoException;
import share.photo.utility.NetManager;
import share.photo.utility.PhoneDisplayAdapter;
import share.photo.view.BlowView;
import share.photo.view.PorterDuffView;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoBlowActivity extends Activity implements INetworkListener,
		SensorEventListener
{
	private PhotoBean mPhotoBean;

	private AudioRecord mAudioRecord;
	private int mBufferSizeInBytes;
	private int mSampleRateInHz = SharePhotoApplication.getInstance()
			.getSampleRateInHz();
	private boolean mIsBlow;
	private boolean mIsAudioRecordStop = true;
	private AsyncDrawableLoader mAsyncDrawableLoader = AsyncDrawableLoader
			.getInstance();

	private SensorManager mSensorManager;
	private Vibrator mVibrator;

	/* ListView head的组件-begin */
	private TextView mTxtPhotoUserName;
	private ImageView mImgPhotoUserHeadPic;
	private TextView mTxtPhotoComment;
	private TextView mTxtPhotoCreatedTime;
	private TextView mTxtCommentNum;
	private BlowView mBlowView;
	private PorterDuffView mPorterDuffView;
	private Drawable mDrawable;
	/* ListView head的组件-end */
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		setContentView(PhoneDisplayAdapter.computeLayout(this,
				R.layout.activity_photo_blow));
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		/* ListView head-Begin */
		mTxtPhotoUserName = (TextView) findViewById(R.id.photo_blow_head_txt_user_name);
		mImgPhotoUserHeadPic = (ImageView) findViewById(R.id.photo_blow_head_img_user_src);
		mTxtPhotoComment = (TextView) findViewById(R.id.photo_blow_head_txt_photo_coment);
		mTxtPhotoCreatedTime = (TextView) findViewById(R.id.photo_blow_head_txt_create_time_value);
		mTxtCommentNum = (TextView) findViewById(R.id.photo_blow_head_txt_comment_num_value);
		mBlowView = (BlowView) findViewById(R.id.photo_blow_head_blowview_shade);
		mPorterDuffView = (PorterDuffView) findViewById(R.id.photo_blow_head_porterduff_download);

		mBlowView.init();
		mBlowView.setScale(10);
		/* ListView head-End */

		initAudioRecord();
		refresh();
		super.onCreate(savedInstanceState);
	}

	private void refresh()
	{
		new PhotoBlowRandomGet().getRandomBlowPhoto(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		/* 注册传感器 */
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		/* 开启麦克的线程 */
		if (mIsAudioRecordStop)
		{
			mIsBlow = true;
			mIsAudioRecordStop = false;
			new Thread(new RecordThread()).start();
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		mSensorManager.unregisterListener(this);
		releaseAudioRecord();
	}

	@Override
	protected void onDestroy()
	{
		mAudioRecord.release();
		super.onDestroy();
	}

	private void initAudioRecord()
	{
		mBufferSizeInBytes = AudioRecord.getMinBufferSize(mSampleRateInHz,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
				mSampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, mBufferSizeInBytes);
	}

	private void releaseAudioRecord()
	{
		mIsBlow = false;
	}

	private class RecordThread implements Runnable
	{

		@Override
		public void run()
		{
			try
			{
				mAudioRecord.startRecording();

				int min;
				int max;
				// 用于读取的 buffer
				byte[] buffer = new byte[mBufferSizeInBytes];

				// 初始化min和max
				int r = mAudioRecord.read(buffer, 0, mBufferSizeInBytes);
				int v = 0;
				for (int i = 0; i < buffer.length; i++)
				{
					v += (buffer[i] * buffer[i]);
				}
				//平方和除以数据总长度，得到音量大小。可以获取白噪声值，然后对实际采样进行标准化。
				int value = Integer.valueOf(v / (int) r);
				//value 的 值 控制 为 0 到 100 之间 0为最小 》= 100为最大！！
//				int value = (int) (Math.abs((int)(v /(float)r)/10000) >> 1);
				min = max = value;

				int first = mSampleRateInHz;

				while (first > 10)
				{
					first /= 10;
				}
				double broadValue = mSampleRateInHz - mSampleRateInHz / first
						+ 100;
				while (mIsBlow)
				{
					Thread.sleep(100);
					// 计算阔值
					// broadValue = (min + max) / 2 + 500;
					r = mAudioRecord.read(buffer, 0, mBufferSizeInBytes);
					v = 0;
					for (int i = 0; i < buffer.length; i++)
					{
						v += (buffer[i] * buffer[i]);
					}
					value = Integer.valueOf(v / (int) r);
//					value = (int) (Math.abs((int)(v /(float)r)/1000) >> 1);
					if (value > max)
						max = value;
					if (value < min)
						min = value;
					MyDebugUtil.i("mSampleRateInHz:" + mSampleRateInHz);
					MyDebugUtil.i("mBufferSizeInBytes:" + mBufferSizeInBytes);
					MyDebugUtil.i("阔值:" + value);
					MyDebugUtil.i("value:" + value);

					if (value > broadValue)
					{
						if (null != mDrawable)
						{
							runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									mBlowView.up();
								}
							});
						}
					}
				}
				mAudioRecord.stop();
				mIsAudioRecordStop = true;
			} catch (Exception e)
			{
				e.printStackTrace();
				mIsAudioRecordStop = true;
			}

		}

	}

	@Override
	public void onReslut(final Object result)
	{
		if (null != result)
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (result instanceof PhotoInfoBean)
					{
						PhotoInfoBean pib = (PhotoInfoBean) result;
						mPhotoBean = pib.getData();
						mPorterDuffView.setVisibility(View.VISIBLE);

						mDrawable = mAsyncDrawableLoader.loadBigDrawable(
								NetManager.URL_API + mPhotoBean.getSrc(),
								PhotoBlowActivity.this, mPorterDuffView);
						if (null != mDrawable)
						{
							mBlowView.setBackgroundDrawable(mDrawable);
							mBlowView.setForeground(BitmapFactory
									.decodeResource(getResources(),
											R.drawable.blow_foreground));
							mBlowView.reset();
							mPorterDuffView.setVisibility(View.GONE);
						}

						mTxtPhotoComment.setText(mPhotoBean.getContent());
						mTxtPhotoCreatedTime
								.setText(mPhotoBean.getCreateTime());

						UserBean user = mPhotoBean.getUser();
						if (null != user.getHeadPicSrc()
								&& user.getHeadPicSrc().length() > 0)
						{
							mAsyncDrawableLoader.loadSmallDrawable(
									NetManager.URL_API + user.getHeadPicSrc(),
									mImgPhotoUserHeadPic);
						}

						mTxtPhotoUserName.setText(user.getName());

						// 向服务器发送请求
						new PhotoCommentCountGet().getPhotoCommentCount(
								mPhotoBean.getId(), PhotoBlowActivity.this);
					} else if (result instanceof Drawable)
					{
						mDrawable = (Drawable) result;

						mBlowView.setBackgroundDrawable(mDrawable);
						mBlowView.setForeground(BitmapFactory.decodeResource(
								getResources(), R.drawable.blow_foreground));
						mBlowView.reset();
						mPorterDuffView.setVisibility(View.GONE);
					} else if (result instanceof CommentCountInfoBean)
					{
						long num = ((CommentCountInfoBean) result).getData();
						mTxtCommentNum.setText(Long.toString(num));
					}
				}
			});
		}
	}

	@Override
	public void onError(SharePhotoException e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(final SensorEvent event)
	{
		int sensorType = event.sensor.getType();
		float[] values = event.values;
		if (sensorType == Sensor.TYPE_ACCELEROMETER)
		{
			if (Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14
					|| Math.abs(values[2]) > 14)
			{
				new PhotoBlowRandomGet()
						.getRandomBlowPhoto(PhotoBlowActivity.this);
				mVibrator.vibrate(100);
				refresh();
			}
		}
	}

	public void onClick(View v)
	{
		Intent intent = null;
		switch (v.getId())
		{
		case R.id.photo_blow_head_txt_see_comment:
			if (null == mPhotoBean)
			{
				Toast.makeText(this, "正在努力获取到图片,请稍后再试", Toast.LENGTH_LONG)
						.show();
				return;
			}
			intent = new Intent(this, PhotoCommentListActivity.class);
			intent.putExtra("photoId", mPhotoBean.getId());

			break;
		case R.id.photo_blow_head_img_user_src:
			intent = new Intent(this, OneseltActivity.class);
			if (null == mPhotoBean)
				return;
			UserBean u = mPhotoBean.getUser();
			intent.putExtra("userId", u.getId());
			intent.putExtra("userName", u.getName());
			intent.putExtra("userHeadPicSrc", u.getHeadPicSrc());
			intent.putExtra("isTab", false);
			break;
		default:
			break;
		}
		if (null != intent)
			startActivity(intent);
	}
}
