package share.photo.sharephoto;

import java.util.ArrayList;
import java.util.List;

import share.photo.bean.FanRelationBean;
import share.photo.bean.FanRelationListInfoBean;
import share.photo.bean.SubscriptionRelationBean;
import share.photo.bean.SubscriptionRelationListInfoBean;
import share.photo.bean.UserBean;
import share.photo.bean.UserInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.mydebug.MyDebugUtil;
import share.photo.network.AsyncDrawableLoader;
import share.photo.network.FanRelationListGet;
import share.photo.network.SharePhotoException;
import share.photo.network.SubscriptionRelationListGet;
import share.photo.network.UserGet;
import share.photo.utility.Constant;
import share.photo.utility.NetManager;
import share.photo.utility.PhoneDisplayAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UserListActivity extends Activity implements INetworkListener
{
	public static final int TYPE_SUBSCRIPTION = 0;
	public static final int TYPE_FAN = 1;

	private TextView mTxtTitle;
	private ListView mLv;
	private UserListAdapter mAdapter;
	private List<UserBean> mData = new ArrayList<UserBean>();
	private List<Drawable> mDrawList = new ArrayList<Drawable>();
	private int mType;
	private int mUserId;
	private int mCurrentPage = 1;
	private int mCountPage;
	private static int mLoadSize = Constant.SUBSCRIPTION_LOAD_SIZE;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(PhoneDisplayAdapter.computeLayout(this,
				R.layout.activity_user_list));
		mTxtTitle = (TextView) findViewById(R.id.user_list_textview_title);
		mLv = (ListView) findViewById(R.id.user_list_lv);
		mAdapter = new UserListAdapter();
		mLv.setAdapter(mAdapter);
		mLv.setOnScrollListener(new OnScrollListener()
		{

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				if (OnScrollListener.SCROLL_STATE_IDLE != scrollState)
					return;
				if (view.getLastVisiblePosition() == (view.getCount() - 1))
				{
					mCurrentPage++;
					if (mCurrentPage > mCountPage)
						return;
					switch (mType)
					{
					case TYPE_SUBSCRIPTION:
						new SubscriptionRelationListGet()
								.getSubscriptionRelationList(mUserId,
										mCurrentPage, mLoadSize,
										UserListActivity.this);
						break;
					case TYPE_FAN:
						new FanRelationListGet().getFanRelationList(mUserId,
								mCurrentPage, mLoadSize, UserListActivity.this);
						break;
					default:
						break;
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount)
			{
			}
		});

		mLv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3)
			{
				Intent intent = new Intent(UserListActivity.this,
						OneseltActivity.class);
				UserBean ub = mData.get(position);
				intent.putExtra("userId", ub.getId());
				intent.putExtra("userName", ub.getName());
				intent.putExtra("userHeadPicSrc", ub.getHeadPicSrc());
				intent.putExtra("isTab", false);
				startActivity(intent);
			}
		});

		Intent intent = getIntent();
		mType = intent.getIntExtra("type", -1);
		mUserId = intent.getIntExtra("userId", -1);
		switch (mType)
		{
		case TYPE_SUBSCRIPTION:
			mTxtTitle.setText("关注");
			new SubscriptionRelationListGet().getSubscriptionRelationList(
					mUserId, mCurrentPage, mLoadSize, this);
			break;
		case TYPE_FAN:
			mTxtTitle.setText("粉丝");
			new FanRelationListGet().getFanRelationList(mUserId, mCurrentPage,
					mLoadSize, UserListActivity.this);
			break;
		default:
			break;
		}

	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.user_list_btn_back:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onReslut(Object result)
	{
		if (null == result)
			return;
		if (result instanceof SubscriptionRelationListInfoBean)
		{
			SubscriptionRelationListInfoBean srlb = (SubscriptionRelationListInfoBean) result;
			mCountPage = srlb.getData().getPageCount();
			List<SubscriptionRelationBean> list = srlb.getData().getList();
			for (SubscriptionRelationBean srb : list)
			{
				if (null == srb)
					continue;
				getUser(srb.getSubscriptionId());
			}
		} else if (result instanceof FanRelationListInfoBean)
		{
			FanRelationListInfoBean flib = (FanRelationListInfoBean) result;
			mCountPage = flib.getData().getPageCount();
			List<FanRelationBean> list = flib.getData().getList();
			for (FanRelationBean frb : list)
			{
				if (null == frb)
					continue;
				getUser(frb.getFanId());
			}
		}
	}

	private void getUser(int id)
	{
		new UserGet().getUser(id, new INetworkListener()
		{
			@Override
			public void onReslut(final Object userBean)
			{
				if (null == userBean || !(userBean instanceof UserInfoBean))
					return;
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						UserInfoBean ub = (UserInfoBean) userBean;
						mData.add(ub.getData());
						mAdapter.notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onError(SharePhotoException e)
			{
			}
		});
	}

	@Override
	public void onError(SharePhotoException e)
	{
		// TODO Auto-generated method stub

	}

	private class UserListAdapter extends BaseAdapter implements
			INetworkListener
	{
		private AsyncDrawableLoader mLoader = AsyncDrawableLoader.getInstance();

		@Override
		public int getCount()
		{
			return mData.size();
		}

		@Override
		public Object getItem(int position)
		{
			return mData.get(position);
		}

		@Override
		public long getItemId(int id)
		{
			return id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (mData.size() >= position)
			{
				Holder holder;
				if (null == convertView)
				{
					convertView = PhoneDisplayAdapter
							.computeLayout(UserListActivity.this,
									R.layout.list_iten_user_list);
					holder = new Holder();
					holder.headPic = (ImageView) convertView
							.findViewById(R.id.list_iten_user_list_img_head_pic);
					holder.name = (TextView) convertView
							.findViewById(R.id.list_iten_user_list_txt_name);
					convertView.setTag(holder);
				} else
				{
					holder = (Holder) convertView.getTag();
				}
				UserBean ub = mData.get(position);
				String headPic = ub.getHeadPicSrc();
				if (null != headPic)
				{

					Drawable drawable = mLoader.loadSmallDrawable(
							NetManager.URL_API + ub.getHeadPicSrc(), this);
					if (null == drawable)
					{
						holder.headPic
								.setImageResource(R.drawable.public_head_pic);
					} else
					{
						holder.headPic.setImageDrawable(drawable);
					}
				} else
				{
					holder.headPic.setImageResource(R.drawable.public_head_pic);
				}
				holder.name.setText(ub.getName());
			}
			return convertView;
		}

		private class Holder
		{
			ImageView headPic;
			TextView name;
		}

		@Override
		public void onReslut(Object result)
		{
			notifyDataSetChanged();
		}

		@Override
		public void onError(SharePhotoException e)
		{
		}

	}
}
