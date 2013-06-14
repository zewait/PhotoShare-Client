package share.photo.sharephoto;

import java.util.ArrayList;
import java.util.List;

import share.photo.bean.DataBean;
import share.photo.bean.PhotoBean;
import share.photo.bean.PhotoNearbyListInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.listener.LocationCallBackListener;
import share.photo.mydebug.MyDebugUtil;
import share.photo.network.AsyncDrawableLoader;
import share.photo.network.PhotosNearbyListGet;
import share.photo.network.SharePhotoException;
import share.photo.network.Token;
import share.photo.utility.NetManager;
import share.photo.utility.PhoneDisplayAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PhotosNearbyActivity extends Activity implements INetworkListener,
		LocationCallBackListener, OnClickListener
{
	// activity_photos_nearby中的控件
	private GridView mGridView;
	private ProgressBar progressBar;
	private ImageView imgRefresh;

	private AsyncDrawableLoader mDrawableLoader;
	private PhotosNearbyListGet mPhotosNearbyListGet = new PhotosNearbyListGet();
	private GridviewAdapter mGridviewAdapter;
	private double mLongitude = 0.0;// 经度
	private double mLatitude = 0.0;// 纬度

	private List<PhotoBean> mData = new ArrayList<PhotoBean>();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(PhoneDisplayAdapter.computeLayout(this,
				R.layout.activity_photos_nearby));
		SharePhotoApplication.getInstance().getLoction(this);
		mDrawableLoader = AsyncDrawableLoader.getInstance();
		getGridView();

	}

	private void getGridView()
	{
		mGridView = (GridView) findViewById(R.id.nearby_gridview_gridview);
		progressBar = (ProgressBar) findViewById(R.id.nearby_progress_bar);
		imgRefresh = (ImageView) findViewById(R.id.nearby_btn_refresh);
		mGridviewAdapter = new GridviewAdapter(this);
		mGridView.setAdapter(mGridviewAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapterview, View view,
					int position, long l)
			{
				PhotoBean photo = mData.get(position);
				Bundle bundle = new Bundle();
				bundle.putSerializable("photo", photo);
				Intent intent = new Intent(PhotosNearbyActivity.this,
						PhotoShowActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	@Override
	public void callBack(double longitude, double latitude)
	{

		mLongitude = longitude;
		mLatitude = latitude;

		mPhotosNearbyListGet
				.getPhotoNearbyList(Token.getInstance().getUserUid(),
						mLongitude, mLatitude, PhotosNearbyActivity.this);
	}

	@Override
	public void onReslut(final Object result)
	{
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				if (null != result)
				{
					if (result instanceof PhotoNearbyListInfoBean)
					{
						PhotoNearbyListInfoBean photoNearbyListInfoBean = (PhotoNearbyListInfoBean) result;
						if (null != photoNearbyListInfoBean)
						{
							mData = photoNearbyListInfoBean.getData().getList();

							if (null != mData && mData.size() > 0
									&& null != mData.get(0))
							{
								mGridviewAdapter.setData(mData);
							}
						}
					}
					progressBar.setVisibility(View.INVISIBLE);
					imgRefresh.setVisibility(View.VISIBLE);

				}
			}
		});

	}

	@Override
	public void onError(SharePhotoException e)
	{
		MyDebugUtil.i("error:" + e.toString());
	}

	private class GridviewAdapter extends BaseAdapter implements
			INetworkListener
	{
		private Context mContext;
		private List<PhotoBean> mPhotoList;

		public GridviewAdapter(Context context)
		{
			this.mContext = context;
			this.mPhotoList = new ArrayList<PhotoBean>();
		}

		public void setData(List<PhotoBean> data)
		{
			mPhotoList = data;
			notifyDataSetChanged();
		}

		@Override
		public int getCount()
		{
			if (null == mPhotoList)
			{
				return 0;
			}
			return mPhotoList.size();
		}

		@Override
		public Object getItem(int position)
		{
			if (null == mPhotoList || 0 == mPhotoList.size())
			{
				return null;
			}
			return mPhotoList.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (null != this.mPhotoList && this.mPhotoList.size() > 0
					&& this.mPhotoList.size() >= position)
			{
				Holder holder;
				PhotoBean photoBean = this.mPhotoList.get(position);
				if (null == photoBean)
					return convertView;
				if (null == convertView)
				{
					holder = new Holder();
					convertView = PhoneDisplayAdapter.computeLayout(mContext,
							R.layout.gridview_photos_nearby);
					holder.imgImg = (ImageView) convertView
							.findViewById(R.id.nearby_gridview_img);
					holder.imgImg.setScaleType(ScaleType.FIT_XY);
					holder.txtDistance = (TextView) convertView
							.findViewById(R.id.nearby_gridview_text_juli);
					holder.imgLiked = (ImageView) convertView
							.findViewById(R.id.gridview_photos_nearby_liked);
					holder.txtLikeNum = (TextView) convertView
							.findViewById(R.id.gridview_photos_nearby_like_num);
					convertView.setTag(holder);
				} else
				{
					holder = (Holder) convertView.getTag();
				}
				// 设置图片
				String zoomSrc = photoBean.getZoomSrc();
				if (null != zoomSrc && zoomSrc.length() > 0)
				{
					Drawable drawable = mDrawableLoader.loadSmallDrawable(
							NetManager.URL_API + photoBean.getZoomSrc(), this);
					if (null != drawable)
					{
						holder.imgImg.setImageDrawable(drawable);
					} else
					{
						holder.imgImg
								.setImageResource(R.drawable.public_img_bg);
					}
				} else
				{
					holder.imgImg.setImageResource(R.drawable.public_img_bg);
				}

				holder.txtDistance.setText(photoBean.getDistance() + "m");
				if (photoBean.getIsLiked())
				{
					holder.imgLiked
							.setImageResource(R.drawable.photo_nearby_liked);
				} else
				{
					holder.imgLiked
							.setImageResource(R.drawable.photo_nearby_like);
				}
				holder.txtLikeNum.setText("" + photoBean.getLikeCount());
			}
			return convertView;
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

	private class Holder
	{
		private ImageView imgImg;
		private TextView txtDistance;// 距离
		private ImageView imgLiked;
		private TextView txtLikeNum;
	}

	@Override
	public void onClick(View v)
	{

		switch (v.getId())
		{
		case R.id.nearby_btn_refresh:
			SharePhotoApplication.getInstance().getLoction(this);
			break;
		case R.id.nearby_btn_search:
			Intent intent = new Intent(PhotosNearbyActivity.this,
					UserSearchByNameActivity.class);
			startActivity(intent);
			break;
		}
	}

}
