package share.photo.sharephoto;

import share.photo.mydebug.MyDebugUtil;
import share.photo.network.Token;
import share.photo.utility.PhoneDisplayAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TabHost;

public class TabActivity extends android.app.TabActivity
{
	private static final String TAB_SUBSCRIPTION_PHOTOS = "tab_subscription_photos";
	private static final String TAB_NEARBY_PHOTOS = "tab_nearby_photos";
	private static final String TAB_BLOW_PHOTO = "tab_blow_photo";
	private static final String TAB_ONESELF = "tab_oneself";

	private RadioGroup mRadioGroup;
	private TabHost mTabHost;
	private ImageView mCursor;
	private int mCursorLeft;

	private RadioButton mRBSubscriptionPhoto;
	private RadioButton mRBNearbyPhotos;
	private RadioButton mRBBlow;
	private RadioButton mRBOneselt;

	private RelativeLayout mRMenuButtom;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// LayoutInflater.from(this).inflate(R.layout.activity_tab, mTabHost,
		// true);
		setContentView(R.layout.activity_tab);

		mCursor = (ImageView) findViewById(R.id.tab_menu_cursor);
		mCursorLeft = mCursor.getLeft();
		mRBSubscriptionPhoto = (RadioButton) findViewById(R.id.tab_radio_subscription_photos);
		mRBNearbyPhotos = (RadioButton) findViewById(R.id.tab_radio_nearby_photos);
		mRBBlow = (RadioButton) findViewById(R.id.tab_radio_blow);
		mRBOneselt = (RadioButton) findViewById(R.id.tab_radio_oneselt);
		mRMenuButtom = (RelativeLayout) findViewById(R.id.tab_layout_menu_buttom);
		mTabHost = getTabHost();

		mTabHost.addTab(mTabHost.newTabSpec(TAB_SUBSCRIPTION_PHOTOS)
				.setIndicator(TAB_SUBSCRIPTION_PHOTOS)
				.setContent(new Intent(this, SubscriptionPhotosActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec(TAB_NEARBY_PHOTOS)
				.setIndicator(TAB_NEARBY_PHOTOS)
				.setContent(new Intent(this, PhotosNearbyActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec(TAB_BLOW_PHOTO)
				.setIndicator(TAB_BLOW_PHOTO)
				.setContent(new Intent(this, PhotoBlowActivity.class)));
		Intent oneselfInent = new Intent(this, OneseltActivity.class);
		oneselfInent.putExtra("userId", Token.getInstance().getUserUid());
		oneselfInent.putExtra("userName", Token.getInstance().getUserName());
		oneselfInent.putExtra("userHeadPicSrc", Token.getInstance()
				.getUserHeadPicSrc());
		oneselfInent.putExtra("isTab", true);
		mTabHost.addTab(mTabHost.newTabSpec(TAB_ONESELF)
				.setIndicator(TAB_ONESELF).setContent(oneselfInent));

		mRadioGroup = (RadioGroup) findViewById(R.id.tab_radio_group);

		PhoneDisplayAdapter.computeLayout(mRMenuButtom);
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				switch (checkedId)
				{
				case R.id.tab_radio_subscription_photos:
					cursorMove(mRBSubscriptionPhoto);
					mTabHost.setCurrentTabByTag(TAB_SUBSCRIPTION_PHOTOS);
					break;
				case R.id.tab_radio_nearby_photos:
					cursorMove(mRBNearbyPhotos);
					mTabHost.setCurrentTabByTag(TAB_NEARBY_PHOTOS);
					break;
				case R.id.tab_radio_blow:
					cursorMove(mRBBlow);
					mTabHost.setCurrentTabByTag(TAB_BLOW_PHOTO);
					break;
				case R.id.tab_radio_oneselt:
					cursorMove(mRBOneselt);
					mTabHost.setCurrentTabByTag(TAB_ONESELF);
					break;

				}
			}
		});
	}

	/**
	 * Cursor move 移动游标
	 */
	private void cursorMove(final View target)
	{
		/*
		 * 温馨提示: TranslateAnimation动画类只执行移动画面并不会移动控件， 并且动画过程中部分设备会看到一瞬间的残留；
		 * 处理方法:设置初始移动位置和目标移动位置， 需要移动的图像在边缘留一像素的空白。
		 */
		// mAnimation = null;

		int mTargetLeft = target.getLeft();
		TranslateAnimation mAnimation = new TranslateAnimation(mCursorLeft,
				mTargetLeft, 0, 0);
		mCursorLeft = mTargetLeft;

		mAnimation.setDuration(250);
		// 游标图像移动后保持位置
		mAnimation.setFillAfter(true);
		mAnimation.setInterpolator(new LinearInterpolator());

		mCursor.startAnimation(mAnimation);
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.tab_btn_camera:
			startActivity(new Intent(this, UploadUserPhotoActivity.class));
			break;
		}
	}
}
