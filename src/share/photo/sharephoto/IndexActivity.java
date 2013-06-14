package share.photo.sharephoto;

import share.photo.utility.PhoneDisplayAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Window;

/**
 * 应用完成时间 2013.04.09-2013.04.24
 * @author Administrator
 *
 */
public class IndexActivity extends Activity
{
	private int what = 0x0002;
	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			if (what == msg.what)
			{
				startActivity(new Intent(IndexActivity.this, LoginActivity.class));
				finish();
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Display display = getWindowManager().getDefaultDisplay();
		PhoneDisplayAdapter.init(display.getWidth(), display.getHeight());
		setContentView(PhoneDisplayAdapter.computeLayout(this,
				R.layout.activity_index));

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				SharePhotoApplication.getInstance().initSampleRateInHz();
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(what);
			}
		}).start();

	}

	
}
