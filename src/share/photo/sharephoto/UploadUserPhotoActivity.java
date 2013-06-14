package share.photo.sharephoto;

import java.io.FileNotFoundException;

import share.photo.bean.FormFileBean;
import share.photo.filter.ImageBlackWhiteFilter;
import share.photo.filter.ImageComicFilter;
import share.photo.filter.ImageDesaltFilter;
import share.photo.filter.ImageFeatherFilter;
import share.photo.filter.ImageFilterInterface;
import share.photo.filter.ImageIceFilter;
import share.photo.filter.ImageSoftGlowFilter;
import share.photo.listener.INetworkListener;
import share.photo.mydebug.MyDebugUtil;
import share.photo.network.PhotoSend;
import share.photo.network.SharePhotoException;
import share.photo.network.Token;
import share.photo.utility.ImageUtility;
import share.photo.utility.LocationUtility;
import share.photo.utility.Notice;
import share.photo.utility.PhoneDisplayAdapter;
import share.photo.utility.WorkerThread;
import share.photo.view.AbsDialog;
import share.photo.view.LoadingDialog;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;

public class UploadUserPhotoActivity extends Activity implements
		INetworkListener, BDLocationListener, MKSearchListener
{
	private static final int REQUEST_OPEN_CAMERA = 0;
	private static final int REQUEST_OPEN_PHOTO_GALLERY = 1;

	private static ImageFilterInterface[] FILTERS;

	private Bitmap[] mBitmapFilters;

	private static Bitmap mBitmap;

	private Gallery mGallery;

	/**
	 * 图片旋转的角度
	 */
	private static final float DEGREES = 90f;

	// 界面控件
	private ImageView mImgUserPhoto;

	private EditText mEtContent;

	// 经度
	private double mLongitude = 0.0;
	// 纬度
	private double mLatitude = 0.0;
	private LoadingDialog mDialogLoading;

	private Button mBtnLocation;

	private BMapManager mBMapManager;

	private String mPlace = "";

	private boolean mIsLocation = true;

	private AbsDialog mDialogSelect;

	private CheckBox mCheckBoxIsBlow;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(PhoneDisplayAdapter.computeLayout(this,
				R.layout.activity_upload_user_photo));
		mDialogLoading = new LoadingDialog(this, "正在上传数据,请稍后...");

		mGallery = (Gallery) findViewById(R.id.upload_user_photo_gallery);

		mImgUserPhoto = (ImageView) findViewById(R.id.upload_user_img_photo);

		mEtContent = (EditText) findViewById(R.id.upload_user_photo_content);

		mBtnLocation = (Button) findViewById(R.id.upload_user_photo_btn_location);

		mCheckBoxIsBlow = (CheckBox) findViewById(R.id.upload_user_photo_checkbox_is_blow);

		mDialogSelect = new DialogSelect(this);

		mBMapManager = SharePhotoApplication.getInstance().getBMapManager();

		if (mIsLocation)
		{
			mBtnLocation.setText("定位中...");
			mBMapManager.start();
			LocationUtility.startLocation(this, this);
		}

	}

	@Override
	protected void onDestroy()
	{

		if (mIsLocation)
		{
			LocationUtility.stopLocation();
			mBMapManager.stop();
		}
		mDialogSelect.dimiss();
		mDialogLoading.dimiss();
		super.onDestroy();
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		// 打开拍照界面
		case R.id.upload_user_photo_btn_camera:
			mDialogSelect.show();
			break;
		// 上传信息
		case R.id.upload_user_photo_btn_upload:
			if (null == mBitmap)
			{
				showToast("请拍照上传");
			} else
			{
				uploadImformation();
			}
			break;
		case R.id.upload_user_photo_btn_location:
			location();
			break;
		case R.id.upload_user_photo_img_rotate:
			if (null != mBitmap)
			{
				mBitmap = ImageUtility.rotate(mBitmap, DEGREES);
				mImgUserPhoto.setImageBitmap(mBitmap);
			}
			break;
		case R.id.upload_user_photo_btn_back:
			finish();
			break;
		}
	}

	/**
	 * 根据mIsLocation判断是开启定位还是关闭
	 */
	private void location()
	{
		if (mIsLocation)
		{
			mIsLocation = false;
			mBtnLocation.setText("定位");
			mPlace = "";
			LocationUtility.stopLocation();
			mBMapManager.stop();
		} else
		{
			mBtnLocation.setText("定位中...");
			mIsLocation = true;
			mBMapManager.start();
			LocationUtility.startLocation(this, this);
		}
	}

	/**
	 * 用户图片信息上传
	 * */
	private void uploadImformation()
	{
		mDialogLoading.show();
		WorkerThread.execute(new Runnable()
		{
			public void run()
			{
				byte[] data = ImageUtility.bitmap2Byte(mBitmap,
						CompressFormat.PNG);

				FormFileBean imge = new FormFileBean("image_link.png",
						"image_link", data, "image/png");
				// TODO
				Boolean isBlow = mCheckBoxIsBlow.isChecked();
				MyDebugUtil.i("isBlow: " + isBlow);
				new PhotoSend().upload(Token.getInstance().getUserUid(),
						mEtContent.getText().toString(), mLongitude, mLatitude,
						mPlace.toString(), isBlow, imge,
						UploadUserPhotoActivity.this);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode)
		{
			// 销毁Bitmap
			if (null != mBitmap && !mBitmap.isRecycled())
			{
				mBitmap.recycle();
				mBitmap = null;
			}

			switch (requestCode)
			{
			case REQUEST_OPEN_CAMERA:
				Bundle bundle = data.getExtras();
				// 缩放图片
				mBitmap = ImageUtility.zoomBitmapByFitCenter(
						(Bitmap) bundle.get("data"), 640, 640);
				break;
			case REQUEST_OPEN_PHOTO_GALLERY:
				Uri uri = data.getData();
				ContentResolver cr = this.getContentResolver();
				try
				{
					// 压缩图片
					mBitmap = ImageUtility
							.zoomBitmapByFitCenter(BitmapFactory
									.decodeStream(cr.openInputStream(uri)),
									640, 640);

				} catch (FileNotFoundException e)
				{
					Notice.d(e.toString());
					e.printStackTrace();
				}
				break;
			default:
				break;
			}

			if (null != mBitmap)
			{

				mImgUserPhoto.setImageBitmap(mBitmap);
				initFilters(mBitmap);
				mBitmapFilters = new Bitmap[FILTERS.length + 1];
				mBitmapFilters[0] = mBitmap;
				for (int i = 1; i < mBitmapFilters.length; i++)
				{
					mBitmapFilters[i] = FILTERS[i - 1].imageProcess();
				}
				GalleryAdapter adapter = new GalleryAdapter();
				mGallery.setAdapter(adapter);
				mGallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
				{
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int postion, long arg3)
					{
						mBitmap = mBitmapFilters[postion
								% mBitmapFilters.length];
						mImgUserPhoto.setImageBitmap(mBitmap);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0)
					{
					}

				});
			}
		}

	}

	private void initFilters(Bitmap bmp)
	{
		FILTERS = new ImageFilterInterface[] {
				// new ImageIceFilter(bmp),
				new ImageComicFilter(bmp), new ImageSoftGlowFilter(bmp),
				new ImageFeatherFilter(bmp), new ImageBlackWhiteFilter(bmp),
				new ImageDesaltFilter(bmp) };
	}

	@Override
	public void onReslut(Object result)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				mDialogLoading.hide();
				showToast("上传成功!");
			}
		});
	}

	@Override
	public void onError(final SharePhotoException e)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				mDialogLoading.hide();
				showToast(e.toString());
				Notice.d(e.toString());
			}
		});
	}

	private void showToast(CharSequence text)
	{
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 获取坐标位置时回调的函数
	 */
	@Override
	public void onReceiveLocation(BDLocation location)
	{
		if (location == null)
			return;

		mLongitude = location.getLongitude();
		mLatitude = location.getLatitude();
		LocationUtility.GeoPointTransformPlace(mLongitude, mLatitude,
				mBMapManager, this);
	}

	@Override
	public void onReceivePoi(BDLocation arg0)
	{
	}

	/**
	 * 将定位的位置转换成地址
	 */
	@Override
	public void onGetAddrResult(MKAddrInfo result, int error)
	{
		if (null == result)
		{
			return;
		}
		mPlace = result.strAddr;
		MyDebugUtil.i(mPlace);
		mBtnLocation.setText(mPlace);
	}

	@Override
	public void onGetBusDetailResult(MKBusLineResult arg0, int arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetPoiDetailSearchResult(int arg0, int arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1)
	{
		// TODO Auto-generated method stub

	}

	private class DialogSelect extends AbsDialog
	{
		private Context mContex;

		public DialogSelect(Context context)
		{
			super(context, R.style.dialog, R.layout.dialog_select);
			mContex = context;

			Button takePhoto = (Button) mDialog
					.findViewById(R.id.dialog_select_btn_take_photo);
			takePhoto.setOnClickListener(this);
			Button selectPhoto = (Button) mDialog
					.findViewById(R.id.dialog_select_btn_select_photo);
			selectPhoto.setOnClickListener(this);

		}

		@Override
		public void onClick(View v)
		{
			hide();
			switch (v.getId())
			{
			case R.id.dialog_select_btn_take_photo:
				openCamera();
				break;
			case R.id.dialog_select_btn_select_photo:
				openPhotoGallery();
				break;
			}
		}

		/**
		 * 打开相机
		 * */
		private void openCamera()
		{
			Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intentCamera, REQUEST_OPEN_CAMERA);
		}

		private void openPhotoGallery()
		{
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, REQUEST_OPEN_PHOTO_GALLERY);
		}
	}

	private class GalleryAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return mBitmapFilters.length;
		}

		@Override
		public Object getItem(int position)
		{
			return position;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ImageView img = new ImageView(UploadUserPhotoActivity.this);
			img.setImageBitmap(mBitmapFilters[position % mBitmapFilters.length]);
			img.setScaleType(ImageView.ScaleType.FIT_XY);
			img.setLayoutParams(new Gallery.LayoutParams(100, 100));
			TypedArray typeedArray = obtainStyledAttributes(R.styleable.Gallery);
			img.setBackgroundResource(typeedArray.getResourceId(
					R.styleable.Gallery_android_galleryItemBackground, 0));
			return img;
		}
	}
}
