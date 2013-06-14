package share.photo.sharephoto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import share.photo.bean.PhotoBean;
import share.photo.bean.PhotoListInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.mydebug.MyDebugUtil;
import share.photo.network.SharePhotoException;
import share.photo.network.SubscriptionPhotoListGet;
import share.photo.network.Token;
import share.photo.utility.Constant;
import share.photo.utility.NetManager;
import share.photo.utility.PhoneDisplayAdapter;
import share.photo.view.FlowTag;
import share.photo.view.FlowView;
import share.photo.view.LazyScrollView;
import share.photo.view.LazyScrollView.OnScrollListener;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SubscriptionPhotosActivity extends Activity implements
		INetworkListener
{
	private LazyScrollView mWaterfallscroll;
	private LinearLayout mWaterfallContainer;
	private ArrayList<LinearLayout> mWaterfallItems;
	private Display mDisplay;
	private List<PhotoBean> mPhotoList = new ArrayList<PhotoBean>();
	private Handler mHandler;
	private int mItemWidth;

	private PhotoListInfoBean mPhotoListInfoBean;

	private SubscriptionPhotoListGet mSubscriptionPhotoListGet = new SubscriptionPhotoListGet();

	private int mColumnCount = Constant.COLUMNS;// 显示列数
	private static int mPageSize = Constant.SUBSCRIPTION_LOAD_SIZE;// 每次加载15张图片

	private int mCurrentPage = 1;// 当前页数
	private int mCountPage;

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

	private ImageView mImgRefresh;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(PhoneDisplayAdapter.computeLayout(this,
				R.layout.activity_subscription_photos));

		mImgRefresh = (ImageView) findViewById(R.id.subscription_photos_btn_refresh);
		mDisplay = this.getWindowManager().getDefaultDisplay();
		mItemWidth = mDisplay.getWidth() / mColumnCount;// 根据屏幕大小计算每列大小
		// asset_manager = this.getAssets();

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
		mImgRefresh.setVisibility(View.GONE);
		refresh();
	}

	/**
	 * 刷新
	 */
	private void refresh()
	{
		mCurrentPage = 1;
		mLoadedCount = 0;
		mPhotoList.clear();
		mIviews.clear();
		for (int i = 0; i < mPinMark.length; i++)
			mPinMark[i].clear();
		mWaterfallContainer.removeAllViewsInLayout();
		if (null == mWaterfallItems)
			mWaterfallItems = new ArrayList<LinearLayout>();
		else
			mWaterfallItems.clear();
		for (int i = 0; i < mColumnCount; i++)
		{
			LinearLayout itemLayout = new LinearLayout(
					SubscriptionPhotosActivity.this);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
					mItemWidth, LayoutParams.WRAP_CONTENT);

			itemLayout.setPadding(2, 2, 2, 2);
			itemLayout.setOrientation(LinearLayout.VERTICAL);

			itemLayout.setLayoutParams(itemParam);
			mWaterfallItems.add(itemLayout);
			mWaterfallContainer.addView(itemLayout);
		}
		mSubscriptionPhotoListGet.getSubscriptionPhotoList(Token.getInstance()
				.getUserUid(), mCurrentPage, mPageSize,
				SubscriptionPhotosActivity.this);
	}

	private void initLayout()
	{
		mWaterfallscroll = (LazyScrollView) findViewById(R.id.subcription_photos_waterfall_scroll);

		mWaterfallscroll.getView();
		mWaterfallscroll.setOnScrollListener(new OnScrollListener()
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
					mImgRefresh.setVisibility(View.GONE);
					++mCurrentPage;
					mSubscriptionPhotoListGet.getSubscriptionPhotoList(Token
							.getInstance().getUserUid(), mCurrentPage,
							mPageSize, SubscriptionPhotosActivity.this);
				}
			}

			@Override
			public void onAutoScroll(int l, int t, int oldl, int oldt)
			{

				mScrollHeight = mWaterfallscroll.getMeasuredHeight();

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
							System.out.println("mPinMark[k]:" + mPinMark[k]);
							System.out.println("mTopIndex[k]:" + mTopIndex[k]);
							System.out.println("mPinMark[k].get(mTopIndex[k]):"
									+ mPinMark[k].get(mTopIndex[k]));
							if (null != mPinMark[k].get(mTopIndex[k])
									&& mPinMark[k].get(mTopIndex[k]) < t - 2
											* mScrollHeight)
							{// 未回收图片的最高位置<t-两倍屏幕高度

								int i1 = mTopIndex[k];
								mTopIndex[k]++;
								((FlowView) localLinearLayout.getChildAt(i1))
										.recycle();

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
								((FlowView) localLinearLayout.getChildAt(Math
										.max(-1 + mTopIndex[k], 0))).reload();
								mTopIndex[k] = Math.max(mTopIndex[k] - 1, 0);
							}
						}
					}

				}

			}
		});

		mWaterfallContainer = (LinearLayout) this
				.findViewById(R.id.subcription_photos_waterfall_container);
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
			String url = NetManager.URL_API + mPhotoList.get(i).getZoomSrc();
			PhotoBean photo = mPhotoList.get(i);
			addImage(url,
					(int) Math.ceil(mLoadedCount / (double) mColumnCount),
					mLoadedCount, photo);
		}
	}

	private void addImage(String filename, int rowIndex, int id,
			PhotoBean mPhoto)
	{

		FlowView item = new FlowView(mContext);

		item.setRowIndex(rowIndex);
		item.setId(id);
		item.setmPhoto(mPhoto);
		item.setViewHandler(this.mHandler);
		// 多线程参数
		FlowTag param = new FlowTag();
		param.setFlowId(id);
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
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				mImgRefresh.setVisibility(View.VISIBLE);
				if (null == result)
					return;
				if (result instanceof PhotoListInfoBean)
				{
					mPhotoListInfoBean = (PhotoListInfoBean) result;
					if (null == mPhotoListInfoBean.getData())
						return;
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
				}
			}
		});
	}

	@Override
	public void onError(SharePhotoException e)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				mImgRefresh.setVisibility(View.VISIBLE);
			}
		});
		MyDebugUtil.i("error:" + e.toString());
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.subscription_photos_btn_refresh:
			mImgRefresh.setVisibility(View.GONE);
			refresh();
			break;

		default:
			break;
		}
	}
}
