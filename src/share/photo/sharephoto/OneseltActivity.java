package share.photo.sharephoto;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import share.photo.bean.FanRelationCountInfoBean;
import share.photo.bean.FormFileBean;
import share.photo.bean.PhotoBean;
import share.photo.bean.PhotoListInfoBean;
import share.photo.bean.SubscriptionRelationCountInfoBean;
import share.photo.bean.SubscriptionRelationWhetherInfoBean;
import share.photo.bean.UserHeadPicInfoBean;
import share.photo.bean.UserLikePhotoPhotoCountInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.mydebug.MyDebugUtil;
import share.photo.network.AsyncDrawableLoader;
import share.photo.network.FanRelationCountGet;
import share.photo.network.SharePhotoException;
import share.photo.network.SubscriptionRelationCountGet;
import share.photo.network.SubscriptionRelationSend;
import share.photo.network.SubscriptionRelationWhetherGet;
import share.photo.network.Token;
import share.photo.network.UserLikePhotoPhotoCountGet;
import share.photo.network.UserPhotoListGet;
import share.photo.network.UserSend;
import share.photo.utility.Constant;
import share.photo.utility.ImageUtility;
import share.photo.utility.NetManager;
import share.photo.utility.PhoneDisplayAdapter;
import share.photo.view.FlowTag;
import share.photo.view.FlowView;
import share.photo.view.LazyScrollView;
import share.photo.view.LazyScrollView.OnScrollListener;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class OneseltActivity extends Activity implements INetworkListener
{
	private static final int ROUND_PX = 100;
	// 请求打开图库
	private static final int REQUEST_OPEN_IMAGE_GALLERY = 0;
	private LazyScrollView mWaterfallScroll;
	private LinearLayout mWaterfallContainer;
	private ArrayList<LinearLayout> mWaterfallItems;
	private Display mDisplay;
	private AssetManager mAssetManager;
	private List<PhotoBean> mPhotoList = new ArrayList<PhotoBean>();
	private Handler mHandler;
	private int mItemWidth;

	private PhotoListInfoBean mPhotoListInfoBean;

	private UserPhotoListGet mUserPhotoListGet = new UserPhotoListGet();

	private int mColumnCount = Constant.COLUMNS;// 显示列数
	private static int mPageSize = Constant.PHOTO_ONSELT_LOAD_SIZE;// 每次加载15张图片

	private int mCurrentPage = 1;// 当前页数
	private int mCountPage;// 总页数

	private int[] mTopIndex;
	private int[] mBottomIndex;
	private int[] mLineIndex;
	private int[] mColumnHeight;// 每列的高度

	private HashMap<Integer, String> mPins;

	private int mLoadedCount = 0;// 已加载数量

	private HashMap<Integer, Integer>[] mPinMark = null;

	private Context mContext;

	private HashMap<Integer, FlowView> mIviews;
	private int mScrollHeight;

	private int mUserId;
	private String mUserName;
	private String mHeadPicSrc;
	private boolean mIsFromTab;

	/* 喜欢 关注 粉丝 - begin */
	private TextView mTxtLike;
	private TextView mTxtSubscription;
	private TextView mTxtFan;
	/* 喜欢 关注 粉丝 - end */
	private ImageView mImgHeadPic;
	private ImageView mLayoutHeadPic;
	private ProgressBar mProgressBar;
	private ImageView mBtnRefresh;
	private RelativeLayout mBtnBack;
	private Button mBtnSubscription;

	/**
	 * 订阅关系
	 */
	private boolean mIsSubscriptionRelation;

	/**
	 * 是否头像操作
	 */
	private boolean mIsHeadPicOperate = false;

	private AsyncDrawableLoader mAsyncDrawableLoader = AsyncDrawableLoader
			.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(PhoneDisplayAdapter.computeLayout(this,
				R.layout.activity_oneselt));

		Intent intent = getIntent();
		mUserId = intent.getIntExtra("userId", -1);
		mUserName = intent.getStringExtra("userName");
		mHeadPicSrc = intent.getStringExtra("userHeadPicSrc");
		mIsFromTab = intent.getBooleanExtra("isTab", false);

		mProgressBar = (ProgressBar) findViewById(R.id.oneselt_progressbar);
		mTxtLike = (TextView) findViewById(R.id.oneselt_txt_like_value);
		mTxtSubscription = (TextView) findViewById(R.id.oneselt_txt_subscription_value);
		mTxtFan = (TextView) findViewById(R.id.oneselt_txt_fan_value);
		mImgHeadPic = (ImageView) findViewById(R.id.oneselt_img_head_pic);
		mImgHeadPic.setImageBitmap(ImageUtility.getRoundedCornerBitmap(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.public_head_pic), ROUND_PX));
		if (null != mHeadPicSrc)
		{
			Drawable drawable = mAsyncDrawableLoader.loadSmallDrawable(
					NetManager.URL_API + mHeadPicSrc, this);
			if (null != drawable)
			{
				Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
				mImgHeadPic.setImageBitmap(ImageUtility.getRoundedCornerBitmap(
						bitmap, ROUND_PX));
			}
		}
		// mProgressBar = (ProgressBar) findViewById(R.id.oneselt_progress_bar);
		mLayoutHeadPic = (ImageView) findViewById(R.id.oneselt_img_head_pic);
		mBtnBack = (RelativeLayout) findViewById(R.id.oneselt_layout_back);
		mBtnRefresh = (ImageView) findViewById(R.id.oneselt_btn_refresh);
		mBtnSubscription = (Button) findViewById(R.id.oneselt_btn_subscription);
		if (mIsFromTab)
		{

			mBtnBack.setEnabled(false);
			mBtnBack.setVisibility(View.GONE);
			mBtnSubscription.setText("注销");

		} else
		{
			mBtnRefresh.setEnabled(false);
			mBtnRefresh.setVisibility(View.GONE);
			mLayoutHeadPic.setEnabled(false);

			if (mUserId == Token.getInstance().getUserUid())
			{
				mBtnSubscription.setEnabled(false);
				mBtnSubscription.setVisibility(View.GONE);
			} else
			{
				new SubscriptionRelationWhetherGet()
						.getSubscriptionRelationWhether(Token.getInstance()
								.getUserUid(), mUserId, this);
			}
		}

		mDisplay = this.getWindowManager().getDefaultDisplay();
		mItemWidth = mDisplay.getWidth() / mColumnCount;// 根据屏幕大小计算每列大小
		mAssetManager = this.getAssets();

		mColumnHeight = new int[mColumnCount];
		mContext = this;
		mIviews = new HashMap<Integer, FlowView>();
		mPins = new HashMap<Integer, String>();
		mPinMark = new HashMap[mColumnCount];

		this.mLineIndex = new int[mColumnCount];
		this.mBottomIndex = new int[mColumnCount];
		this.mTopIndex = new int[mColumnCount];

		for (int i = 0; i < mColumnCount; i++)
		{
			mLineIndex[i] = -1;
			mBottomIndex[i] = -1;
			mPinMark[i] = new HashMap();
		}

		initLayout();
		refresh();
	}

	/**
	 * 打开图库
	 */
	private void openImageGallery()
	{
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, REQUEST_OPEN_IMAGE_GALLERY);
	}

	/**
	 * 刷新
	 */
	private void refresh()
	{
		mCurrentPage = 1;
		mLoadedCount = 0;
		mWaterfallContainer.removeAllViewsInLayout();
		mPhotoList.clear();
		mIviews.clear();
		for (int i = 0; i < mPinMark.length; i++)
			mPinMark[i].clear();
		if (null == mWaterfallItems)
			mWaterfallItems = new ArrayList<LinearLayout>();
		else
			mWaterfallItems.clear();
		for (int i = 0; i < mColumnCount; i++)
		{
			// 清楚列数据
			mPinMark[i].clear();

			LinearLayout itemLayout = new LinearLayout(OneseltActivity.this);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
					mItemWidth, LayoutParams.WRAP_CONTENT);

			itemLayout.setPadding(2, 2, 2, 2);
			itemLayout.setOrientation(LinearLayout.VERTICAL);

			itemLayout.setLayoutParams(itemParam);
			mWaterfallItems.add(itemLayout);
			mWaterfallContainer.addView(itemLayout);
		}

		// 向服务端发送数据更新
		mUserPhotoListGet.getPhotoList(mUserId, mCurrentPage, mPageSize,
				OneseltActivity.this);
		new SubscriptionRelationCountGet().getSubscriptionRelationCount(
				mUserId, this);
		new FanRelationCountGet().getSubscriptionRelationCount(mUserId, this);
		new UserLikePhotoPhotoCountGet().getUserLikePhotoPhotoCount(mUserId,
				this);
	}

	private void initLayout()
	{
		mWaterfallScroll = (LazyScrollView) findViewById(R.id.oneselt_waterfall_scroll);

		mWaterfallScroll.getView();
		mWaterfallScroll.setOnScrollListener(new OnScrollListener()
		{

			@Override
			public void onTop()
			{
				// 滚动到最顶端
				Log.d("LazyScroll", "Scroll to top");
				// refresh();
			}

			@Override
			public void onScroll()
			{

			}

			@Override
			public void onBottom()
			{
				// TODO
				// 滚动到最低端
				if (mCurrentPage < mCountPage)
				{
					++mCurrentPage;
					mUserPhotoListGet.getPhotoList(mUserId, mCurrentPage,
							mPageSize, OneseltActivity.this);
				}
			}

			@Override
			public void onAutoScroll(int l, int t, int oldl, int oldt)
			{

				mScrollHeight = mWaterfallScroll.getMeasuredHeight();

				if (t > oldt)
				{// 向下滚动
					if (t > 2 * mScrollHeight)
					{// 超过两屏幕后

						for (int k = 0; k < mColumnCount; k++)
						{

							LinearLayout localLinearLayout = mWaterfallItems
									.get(k);

							if (null != mPinMark[k].get(Math.min(
									mBottomIndex[k] + 1, mLineIndex[k]))
									&& mPinMark[k].get(Math.min(
											mBottomIndex[k] + 1, mLineIndex[k])) <= t
											+ 3 * mScrollHeight)
							{// 最底部的图片位置小于当前t+3*屏幕高度

								FlowView flowView = (FlowView) mWaterfallItems
										.get(k).getChildAt(
												Math.min(1 + mBottomIndex[k],
														mLineIndex[k]));
								if (null != flowView)
									flowView.reload();

								mBottomIndex[k] = Math.min(1 + mBottomIndex[k],
										mLineIndex[k]);

							}
							if (null != mPinMark[k].get(mTopIndex[k])
									&& mPinMark[k].get(mTopIndex[k]) < t - 2
											* mScrollHeight)
							{// 未回收图片的最高位置<t-两倍屏幕高度

								int i1 = mTopIndex[k];
								mTopIndex[k]++;
								FlowView flowView = (FlowView) localLinearLayout
										.getChildAt(i1);
								if (null != flowView)
									flowView.recycle();

							}
						}

					}
				} else
				{// 向上滚动
					if (t > 2 * mScrollHeight)
					{// 超过两屏幕后
						for (int k = 0; k < mColumnCount; k++)
						{
							LinearLayout localLinearLayout = mWaterfallItems
									.get(k);
							if (null != mPinMark[k].get(mBottomIndex[k])
									&& mPinMark[k].get(mBottomIndex[k]) > t + 3
											* mScrollHeight)
							{
								FlowView flowView = (FlowView) localLinearLayout
										.getChildAt(mBottomIndex[k]);
								if (null != flowView)
									flowView.recycle();

								mBottomIndex[k]--;
							}

							if (null != mPinMark[k].get(Math.max(
									mTopIndex[k] - 1, 0))
									&& mPinMark[k].get(Math.max(
											mTopIndex[k] - 1, 0)) >= t - 2
											* mScrollHeight)
							{
								FlowView flowView = (FlowView) localLinearLayout
										.getChildAt(Math.max(-1 + mTopIndex[k],
												0));
								if (null != flowView)
									flowView.reload();
								mTopIndex[k] = Math.max(mTopIndex[k] - 1, 0);
							}
						}
					}

				}

			}
		});

		mWaterfallContainer = (LinearLayout) this
				.findViewById(R.id.oneselt_waterfall_container);
		mHandler = new Handler()
		{

			@Override
			public void dispatchMessage(Message msg)
			{

				super.dispatchMessage(msg);
			}

			@Override
			public void handleMessage(Message msg)
			{

				// super.handleMessage(msg);

				switch (msg.what)
				{
				case 1:

					FlowView v = (FlowView) msg.obj;
					int w = msg.arg1;
					int h = msg.arg2;
					// Log.d("MainActivity",
					// String.format(
					// "获取实际View高度:%d,ID：%d,columnIndex:%d,rowIndex:%d,filename:%s",
					// v.getHeight(), v.getId(), v
					// .getColumnIndex(), v.getRowIndex(),
					// v.getFlowTag().getFileName()));
					String f = v.getFlowTag().getFileName();

					// 此处计算列值
					int columnIndex = GetMinValue(mColumnHeight);

					v.setColumnIndex(columnIndex);

					mColumnHeight[columnIndex] += h;

					mPins.put(v.getId(), f);
					mIviews.put(v.getId(), v);
					mWaterfallItems.get(columnIndex).addView(v);

					mLineIndex[columnIndex]++;

					mPinMark[columnIndex].put(mLineIndex[columnIndex],
							mColumnHeight[columnIndex]);
					mBottomIndex[columnIndex] = mLineIndex[columnIndex];
					break;
				}

			}

			@Override
			public boolean sendMessageAtTime(Message msg, long uptimeMillis)
			{
				return super.sendMessageAtTime(msg, uptimeMillis);
			}
		};
	}

	private void addItemToContainer(int pageindex, int pageSize)
	{
		int currentIndex = (pageindex - 1) * pageSize;

		int imagecount = mPhotoList.size();// image_filenames.size();
		if (currentIndex > imagecount)
			return;
		for (int i = currentIndex; i < pageSize * (pageindex + 1)
				&& i < imagecount; i++)
		{
			mLoadedCount++;
			PhotoBean photo = mPhotoList.get(i);
			String url = NetManager.URL_API + photo.getZoomSrc();
			addImage(url,
					(int) Math.ceil(mLoadedCount / (double) mColumnCount),
					mLoadedCount, photo);
		}

	}

	private void addImage(String filename, int rowIndex, int id, PhotoBean photo)
	{

		FlowView item = new FlowView(mContext);

		item.setRowIndex(rowIndex);
		item.setId(id);
		item.setmPhoto(photo);
		item.setViewHandler(this.mHandler);
		// 多线程参数
		FlowTag param = new FlowTag();
		param.setFlowId(id);
		param.setAssetManager(mAssetManager);
		param.setFileName(filename);
		param.setItemWidth(mItemWidth);

		item.setFlowTag(param);
		item.LoadImage();
	}

	private int GetMinValue(int[] array)
	{
		int m = 0;
		int length = array.length;
		for (int i = 0; i < length; ++i)
		{

			if (array[i] < array[m])
			{
				m = i;
			}
		}
		return m;
	}

	@Override
	public void onReslut(final Object result)
	{
		// TODO
		if (null == result)
			return;
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{

				if (result instanceof PhotoListInfoBean)
				{
					mPhotoListInfoBean = (PhotoListInfoBean) result;
					mCountPage = mPhotoListInfoBean.getData().getPageCount();
					List<PhotoBean> list = mPhotoListInfoBean.getData()
							.getList();
					if (null == list)
						return;
					for (PhotoBean p : list)
					{
						if (null != p)
							mPhotoList.add(p);
					}

					addItemToContainer(mCurrentPage, mPageSize);

				} else if (result instanceof SubscriptionRelationCountInfoBean)
				{
					long count = ((SubscriptionRelationCountInfoBean) result)
							.getData();
					mTxtSubscription.setText(Long.toString(count));
				} else if (result instanceof FanRelationCountInfoBean)
				{
					long count = ((FanRelationCountInfoBean) result).getData();
					mTxtFan.setText(Long.toString(count));
				} else if (result instanceof String)
				{
					MyDebugUtil.i(result);
					if (mIsHeadPicOperate)
					{
						// 头像操作
						Gson gson = new Gson();
						try
						{
							UserHeadPicInfoBean headPic = gson.fromJson(
									(String) result, UserHeadPicInfoBean.class);

							mAsyncDrawableLoader.removeCache(NetManager.URL_API
									+ headPic.getData());

							Drawable drawable = mAsyncDrawableLoader
									.loadSmallDrawable(NetManager.URL_API
											+ headPic.getData(),
											OneseltActivity.this);
							if (null != drawable)
							{
								Bitmap bitmap = ((BitmapDrawable) drawable)
										.getBitmap();
								mImgHeadPic.setImageBitmap(ImageUtility
										.getRoundedCornerBitmap(bitmap,
												ROUND_PX));
							}
							Token.getInstance().setUserHeadPicSrc(
									headPic.getData());
							mProgressBar.setVisibility(View.GONE);
						} catch (JsonSyntaxException e)
						{
							e.printStackTrace();
						}
					} else
					{
						// 订阅/取消成功
						mIsSubscriptionRelation = !mIsSubscriptionRelation;
						changeSubscriptionRelation();
						showToast("操作成功");
					}

				} else if (result instanceof SubscriptionRelationWhetherInfoBean)
				{
					SubscriptionRelationWhetherInfoBean srwib = (SubscriptionRelationWhetherInfoBean) result;
					mIsSubscriptionRelation = srwib.getData();
					changeSubscriptionRelation();
				} else if (result instanceof Drawable)
				{
					Drawable drawable = (Drawable) result;
					Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
					mImgHeadPic.setImageBitmap(ImageUtility
							.getRoundedCornerBitmap(bitmap, ROUND_PX));
				} else if (result instanceof UserLikePhotoPhotoCountInfoBean)
				{
					UserLikePhotoPhotoCountInfoBean bean = (UserLikePhotoPhotoCountInfoBean) result;
					long data = bean.getData();
					mTxtLike.setText(Long.toString(data));
				}
			}
		});
	}

	@Override
	public void onError(SharePhotoException e)
	{
		showToast("操作失败");
		MyDebugUtil.i("error:" + e.toString());
		if (mIsHeadPicOperate)
			mIsHeadPicOperate = false;
	}

	public void onClick(View v)
	{
		Intent intent = null;
		switch (v.getId())
		{
		case R.id.oneselt_btn_refresh:
			refresh();
			break;
		case R.id.oneselt_img_head_pic:
			openImageGallery();
			break;
		case R.id.oneselt_view_subscription:
			intent = new Intent(this, UserListActivity.class);
			intent.putExtra("type", UserListActivity.TYPE_SUBSCRIPTION);
			intent.putExtra("userId", mUserId);
			break;
		case R.id.oneselt_view_fan:
			intent = new Intent(this, UserListActivity.class);
			intent.putExtra("type", UserListActivity.TYPE_FAN);
			intent.putExtra("userId", mUserId);
			break;
		case R.id.oneselt_img_back:
			finish();
			break;
		case R.id.oneselt_btn_subscription:
			if (mIsFromTab)
			{
				SharedPreferences.Editor editor = getSharedPreferences(
						Constant.USER_CONFIG_FILE, MODE_PRIVATE).edit();
				editor.remove(Constant.KEY_USER_NAME);
				editor.remove(Constant.KEY_USER_PASSWORD);
				editor.commit();
				Intent inte = new Intent(this, LoginActivity.class);
				startActivity(inte);
				finish();
			} else
			{
				if (mIsSubscriptionRelation)
				{
					new SubscriptionRelationSend().cancel(Token.getInstance()
							.getUserUid(), mUserId, this);
				} else
				{
					new SubscriptionRelationSend().add(Token.getInstance()
							.getUserUid(), mUserId, this);
				}
			}
			break;
		case R.id.oneselt_view_like:
			intent = new Intent(this, UserLikePhotosActivity.class);
			intent.putExtra("userId", mUserId);
			break;
		default:
			break;
		}
		if (null != intent)
			startActivity(intent);
	}

	private void changeSubscriptionRelation()
	{
		if (mIsSubscriptionRelation)
		{
			mBtnSubscription.setText("已关注");
		} else
		{
			mBtnSubscription.setText("关注");
		}
	}

	private void showToast(final String text)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				Toast.makeText(OneseltActivity.this, text, Toast.LENGTH_LONG)
						.show();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (RESULT_OK == resultCode
				&& REQUEST_OPEN_IMAGE_GALLERY == requestCode)
		{
			mProgressBar.setVisibility(View.VISIBLE);
			Uri uri = data.getData();
			ContentResolver cr = this.getContentResolver();
			{
				try
				{
					Bitmap bitmap = ImageUtility
							.zoomBitmapFitXY(BitmapFactory.decodeStream(cr
									.openInputStream(uri)), 100, 100);
					// 上传头像
					byte[] byteArray = ImageUtility.bitmap2Byte(bitmap,
							CompressFormat.JPEG);
					FormFileBean fileBean = new FormFileBean("headPic.jpg",
							"headPic", byteArray, "image/jpeg");
					mIsHeadPicOperate = true;
					new UserSend().uploadHeadPic(Token.getInstance()
							.getUserUid(), fileBean, this);
				} catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
			}
			;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
