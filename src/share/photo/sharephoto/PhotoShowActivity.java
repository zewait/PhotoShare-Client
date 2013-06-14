package share.photo.sharephoto;

import java.util.ArrayList;
import java.util.List;

import share.photo.bean.CommentBean;
import share.photo.bean.CommentCountInfoBean;
import share.photo.bean.CommentListInfoBean;
import share.photo.bean.PhotoBean;
import share.photo.bean.UserLikePhotoUserCountInfoBean;
import share.photo.bean.UserLikePhotoWhetherInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.mydebug.MyDebugUtil;
import share.photo.network.AsyncDrawableLoader;
import share.photo.network.CommentSend;
import share.photo.network.PhotoCommentCountGet;
import share.photo.network.PhotoCommentListGet;
import share.photo.network.SharePhotoException;
import share.photo.network.Token;
import share.photo.network.UserLikePhotoPhotoCountGet;
import share.photo.network.UserLikePhotoUserCountGet;
import share.photo.network.UserLikePhotoWhetherGet;
import share.photo.network.UserSend;
import share.photo.utility.Constant;
import share.photo.utility.ImageUtility;
import share.photo.utility.NetManager;
import share.photo.utility.Notice;
import share.photo.utility.PhoneDisplayAdapter;
import share.photo.utility.ShareUtilty;
import share.photo.view.PorterDuffView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoShowActivity extends Activity implements INetworkListener,
		OnClickListener
{
	private static int loadCommentSize = Constant.SHOW_SHOW_LOAD_COMMENT_SIZE;// 得到的评论数
	private int mCurrentPage = 1;
	private int mCountPage;
	private TextView txUserName, txCreateTime;
	// 相片内容
	private TextView mTxtComent;
	private ImageView imgHeadPic;// 用户头像
	private ImageView mImgMyHeadPic;// 我的头像
	private PorterDuffView imgSrc;
	private EditText mEditTextComment;
	private View mPhotoBody;
	private TextView mTxtCommentNum;
	private TextView mTxtLikeNum;

	private ListView mLv;
	private PhotoShowAdapter mAdapter;

	private AsyncDrawableLoader mAsyncDrawableLoader = AsyncDrawableLoader
			.getInstance();
	private Drawable mShareDrawable;
	private PhotoBean mPhoto;
	private List<CommentBean> mCommentData = new ArrayList<CommentBean>();
	private InputMethodManager mInputMethodManager;
	private boolean mIsLike = false;
	private ImageView mImgLike;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(PhoneDisplayAdapter.computeLayout(this,
				R.layout.activity_photo_show));
		initComponent();
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		mPhoto = (PhotoBean) data.getSerializable("photo");
		mTxtComent.setText(mPhoto.getContent());
		txCreateTime.setText(mPhoto.getCreateTime());
		txUserName.setText(mPhoto.getUser().getName());
		// 设置相片用户的头像
		if (null != mPhoto.getUser().getHeadPicSrc()
				&& mPhoto.getUser().getHeadPicSrc().length() > 0)
		{
			mAsyncDrawableLoader.loadSmallDrawable(NetManager.URL_API
					+ mPhoto.getUser().getHeadPicSrc(), imgHeadPic);
		}

		// 设置我的头像
		String myHeadPicSrc = Token.getInstance().getUserHeadPicSrc();
		if (null != myHeadPicSrc && myHeadPicSrc.length() > 0)
		{
			mAsyncDrawableLoader.loadSmallDrawable(NetManager.URL_API
					+ myHeadPicSrc, mImgMyHeadPic);
		}

		if (null != mPhoto.getSrc() && mPhoto.getSrc().length() > 0
				&& !imgSrc.isLoading())
		{
			imgSrc.setPorterDuffMode(true);
			imgSrc.setLoading(true);
			imgSrc.setProgress(0);
			mShareDrawable = imgSrc.setImgUrl(
					NetManager.URL_API + mPhoto.getSrc(), this);
			if (null != mShareDrawable)
				imgSrc.finish(mShareDrawable);
			imgSrc.invalidate();
		}

		/* ListView操作-Begin */
		mLv.addHeaderView(mPhotoBody);
		mAdapter = new PhotoShowAdapter();
		mLv.setAdapter(mAdapter);
		mLv.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				if (scrollState != OnScrollListener.SCROLL_STATE_IDLE)
					return;
				if (view.getLastVisiblePosition() == view.getCount() - 1)
				{
					if (mCurrentPage < mCountPage)
					{
						mCurrentPage++;
						new PhotoCommentListGet().getPhotoList(mPhoto.getId(),
								mCurrentPage, loadCommentSize,
								PhotoShowActivity.this);
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
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id)
			{
				if (0==position || position>mCommentData.size())
					return;
				CommentBean cb = mCommentData.get(position-1);
				if (null == cb)
					return;
				Intent intent = new Intent(PhotoShowActivity.this,
						OneseltActivity.class);
				intent.putExtra("userId", cb.getOwner().getId());
				intent.putExtra("userName", cb.getOwner().getName());
				intent.putExtra("userHeadPicSrc", cb.getOwner().getHeadPicSrc());
				intent.putExtra("isTab", false);
				startActivity(intent);
			}
		});
		/* ListView操作-End */

		//向服务端请求数据
		new PhotoCommentListGet().getPhotoList(mPhoto.getId(), mCurrentPage,
				loadCommentSize, PhotoShowActivity.this);
		new PhotoCommentCountGet().getPhotoCommentCount(mPhoto.getId(), this);
		new UserLikePhotoWhetherGet().getUserLikePhotoWhetherInfo(Token
				.getInstance().getUserUid(), mPhoto.getId(), this);
		new UserLikePhotoUserCountGet()
		.getUserLikePhotoUserCount(mPhoto.getId(),
				this);
	}

	// 界面控件ID
	private void initComponent()
	{
		mPhotoBody = PhoneDisplayAdapter.computeLayout(this,
				R.layout.activity_photo_show_head);
		txUserName = (TextView) mPhotoBody
				.findViewById(R.id.photo_show_textView_user_name);
		txCreateTime = (TextView) mPhotoBody
				.findViewById(R.id.photo_show_txt_create_time_value);
		mTxtComent = (TextView) mPhotoBody
				.findViewById(R.id.photo_show_txt_photo_coment);
		imgHeadPic = (ImageView) mPhotoBody
				.findViewById(R.id.photo_show_img_user_src);
		imgSrc = (PorterDuffView) mPhotoBody
				.findViewById(R.id.photo_show_img_src);
		mTxtCommentNum = (TextView) mPhotoBody
				.findViewById(R.id.photo_show_comment_num_value);
		mTxtLikeNum = (TextView) mPhotoBody
				.findViewById(R.id.photo_show_head_txt_photo_like_num);

		mImgLike = (ImageView) mPhotoBody
				.findViewById(R.id.photo_show_head_img_photo_like);
		mImgLike.setOnClickListener(this);

		mEditTextComment = (EditText) findViewById(R.id.photo_show_edit);
		mImgMyHeadPic = (ImageView) findViewById(R.id.photo_show_my_head_pic);
		mLv = (ListView) findViewById(R.id.photo_show_lv_comment_container);

		mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	/**
	 * 
	 * @return 没内容返回true,否则false
	 */
	private boolean checkCommentContent()
	{
		return null == mEditTextComment.getText()
				|| 0 == mEditTextComment.getText().toString().length();
	}

	@Override
	public void onClick(View v)
	{
		Intent intent = null;
		switch (v.getId())
		{
		case R.id.photo_show_img_src:
			ImageUtility.showPicByUrl(this, mPhoto.getSrc());
			break;
		case R.id.photo_show_img_user_src:
			intent = new Intent(this, OneseltActivity.class);
			intent.putExtra("userId", mPhoto.getUser().getId());
			intent.putExtra("userName", mPhoto.getUser().getName());
			intent.putExtra("userHeadPicSrc", mPhoto.getUser().getHeadPicSrc());
			intent.putExtra("isTab", false);
			break;
		case R.id.photo_show_btn_back:
			finish();
			break;
		case R.id.photo_show_img_commit:
			if (checkCommentContent())
			{
				mEditTextComment.setError("请输入评论内容");
			} else
			{
				new CommentSend().add(mPhoto.getId(), Token.getInstance()
						.getUserUid(), 0,
						mEditTextComment.getText().toString(), this);
				mEditTextComment.setText("");
				// 如果输入法打开
				if (mInputMethodManager.isActive())
				{
					mInputMethodManager.hideSoftInputFromWindow(this
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
			break;
		case R.id.photo_show_btn_share:
			ShareUtilty.share("分享-云图", mPhoto.getContent(), mShareDrawable,
					this);
			break;
		case R.id.photo_show_head_img_photo_like:
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (mIsLike)
					{// 已经是喜欢的图片情况
						new UserSend().deleteUserLikePhoto(Token.getInstance()
								.getUserUid(), mPhoto.getId(),
								PhotoShowActivity.this);
					} else
					{// 还不是喜欢的图片情况
						new UserSend().addUserLikePhoto(Token.getInstance()
								.getUserUid(), mPhoto.getId(),
								PhotoShowActivity.this);

					}
				}
			});
			break;
		default:
			break;
		}
		if (null != intent)
			startActivity(intent);
	}

	private void showToast(String text)
	{
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
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
					if (result instanceof Drawable)
					{
						mShareDrawable = (Drawable) result;
						imgSrc.finish(mShareDrawable);
					} else if (result instanceof CommentListInfoBean)
					{
						CommentListInfoBean slib = (CommentListInfoBean) result;
						mCountPage = slib.getData().getPageCount();
						List<CommentBean> list = slib.getData().getList();
						for (CommentBean cb : list)
						{
							if (null == cb)
								continue;
							mCommentData.add(cb);
						}
						mAdapter.notifyDataSetChanged();
					} else if (result instanceof String)
					{
						showToast("操作成功");
						mCurrentPage = 1;
						mCommentData.clear();

						// 发送数据更新
						new PhotoCommentListGet().getPhotoList(mPhoto.getId(),
								mCurrentPage, loadCommentSize,
								PhotoShowActivity.this);
						new PhotoCommentCountGet().getPhotoCommentCount(
								mPhoto.getId(), PhotoShowActivity.this);
						new UserLikePhotoWhetherGet()
								.getUserLikePhotoWhetherInfo(Token
										.getInstance().getUserUid(), mPhoto
										.getId(), PhotoShowActivity.this);
						new UserLikePhotoUserCountGet()
								.getUserLikePhotoUserCount(mPhoto.getId(),
										PhotoShowActivity.this);
					} else if (result instanceof CommentCountInfoBean)
					{
						long num = ((CommentCountInfoBean) result).getData();
						mTxtCommentNum.setText(Long.toString(num));
					} else if (result instanceof UserLikePhotoWhetherInfoBean)
					{
						UserLikePhotoWhetherInfoBean bean = (UserLikePhotoWhetherInfoBean) result;
						mIsLike = bean.getData();
						if (mIsLike)
						{
							mTxtLikeNum.setTextColor(Color.WHITE);
							mImgLike.setImageResource(R.drawable.public_img_liked);
						} else
						{
							mTxtLikeNum.setTextColor(Color.BLACK);
							mImgLike.setImageResource(R.drawable.public_img_like);
						}
					}
					else if(result instanceof UserLikePhotoUserCountInfoBean)
					{
						long data = ((UserLikePhotoUserCountInfoBean)result).getData();
						mTxtLikeNum.setText(Long.toString(data));
					}
				}
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
				showToast("操作失败");
				Notice.i("error:" + e.toString());
			}
		});
	}

	private class PhotoShowAdapter extends BaseAdapter
	{
		@Override
		public int getCount()
		{
			return mCommentData.size();
		}

		@Override
		public Object getItem(int arg0)
		{
			if (mCommentData.size() == 0)
				return null;
			return mCommentData.get(arg0);
		}

		@Override
		public long getItemId(int id)
		{
			return id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (0 != mCommentData.size() && mCommentData.size() >= position)
			{
				Holder holder;
				if (null == convertView)
				{
					convertView = PhoneDisplayAdapter.computeLayout(
							PhotoShowActivity.this, R.layout.list_iten_comment);
					holder = new Holder();
					holder.headPic = (ImageView) convertView
							.findViewById(R.id.list_iten_comment_img_head_pic);
					holder.userName = (TextView) convertView
							.findViewById(R.id.list_iten_comment_txt_user_name);
					holder.userComment = (TextView) convertView
							.findViewById(R.id.list_iten_comment_txt_user_comment);
					holder.commentCreateTime = (TextView) convertView
							.findViewById(R.id.list_iten_comment_txt_comment_createtime);
					convertView.setTag(holder);
				} else
				{
					holder = (Holder) convertView.getTag();
				}

				CommentBean cb = mCommentData.get(position);
				String headPicSrc = cb.getOwner().getHeadPicSrc();
				if (null != headPicSrc && headPicSrc.length() > 0)
				{
					mAsyncDrawableLoader.loadSmallDrawable(NetManager.URL_API
							+ headPicSrc, holder.headPic);
				}
				holder.userName.setText(cb.getOwner().getName() + ":");
				holder.userComment.setText(cb.getContent());
				holder.commentCreateTime.setText(cb.getCreateTime());
			}
			return convertView;
		}

		private class Holder
		{
			ImageView headPic;
			TextView userName;
			TextView userComment;
			TextView commentCreateTime;
		}
	}

}
