package share.photo.sharephoto;

import java.util.ArrayList;
import java.util.List;

import share.photo.bean.CommentBean;
import share.photo.bean.CommentListInfoBean;
import share.photo.bean.PhotoBean;
import share.photo.listener.INetworkListener;
import share.photo.mydebug.MyDebugUtil;
import share.photo.network.CommentSend;
import share.photo.network.AsyncDrawableLoader;
import share.photo.network.PhotoCommentListGet;
import share.photo.network.SharePhotoException;
import share.photo.network.Token;
import share.photo.utility.Constant;
import share.photo.utility.NetManager;
import share.photo.utility.PhoneDisplayAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoCommentListActivity extends Activity implements
		INetworkListener
{
	// 每次获取评论的数量
	private static int mPageSize = Constant.PhotoCommentList_LOAD_COMMENT_SIZE;
	private int mCurrentPage = 1;
	private int mCountPage;
	List<CommentBean> mData = new ArrayList<CommentBean>();

	private ListView mListView;
	private PhotoCommentAdapter photoCommentAdapter;
	private int mPhotoId;
	private EditText etComment;// 评论内容
	private ImageView mImgMyHeadPic;
	private AsyncDrawableLoader mAsyncDrawableLoader = AsyncDrawableLoader
			.getInstance();
	private InputMethodManager mInputMethodManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(PhoneDisplayAdapter.computeLayout(this,
				R.layout.activity_photo_comment_list));
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		mPhotoId = (int) data.getInt("photoId");
		mListView = (ListView) findViewById(R.id.photo_comment_list_listview_comment);
		etComment = (EditText) findViewById(R.id.photo_comment_list_edit);
		mImgMyHeadPic = (ImageView) findViewById(R.id.photo_comment_list_my_head_pic);
		String myHeadPic = Token.getInstance().getUserHeadPicSrc();
		if (null != myHeadPic && myHeadPic.length() > 0)
			mAsyncDrawableLoader.loadSmallDrawable(NetManager.URL_API
					+ myHeadPic, mImgMyHeadPic);
		mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		photoCommentAdapter = new PhotoCommentAdapter(this);
		mListView.setAdapter(photoCommentAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> adapterview, View view,
					int position, long l)
			{
				Intent intent = new Intent(PhotoCommentListActivity.this,
						OneseltActivity.class);
				CommentBean cb = mData.get(position);
				intent.putExtra("userId", cb.getOwner().getId());
				intent.putExtra("userName", cb.getOwner().getName());
				intent.putExtra("userHeadPicSrc", cb.getOwner().getHeadPicSrc());
				intent.putExtra("isTab", false);
				startActivity(intent);
			}
		});
		mListView.setOnScrollListener(new OnScrollListener()
		{

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				if (OnScrollListener.SCROLL_STATE_IDLE != scrollState)
					return;
				if (view.getLastVisiblePosition() == view.getCount() - 1)
				{
					mCurrentPage++;
					if (mCurrentPage <= mCountPage)
					{
						new PhotoCommentListGet().getPhotoList(mPhotoId,
								mCurrentPage, mPageSize,
								PhotoCommentListActivity.this);
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount)
			{

			}
		});

		refresh();// 获取从GetPhotoCommentListActivity传过来的Photo
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.photo_comment_list_img_commit:
			String connent = etComment.getText().toString();
			if (!check(connent))
			{
				new CommentSend().add(mPhotoId, Token.getInstance()
						.getUserUid(), 0, connent,
						PhotoCommentListActivity.this);
				etComment.setText("");
				if (mInputMethodManager.isActive())
				{
					mInputMethodManager.hideSoftInputFromWindow(
							getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			} else
			{
				etComment.setError("请输入内容");
			}
			break;
		case R.id.photo_comment_list_btn_refresh:
			refresh();
			break;
		case R.id.photo_comment_list_img_back:
			finish();
			break;
		}
	}

	/**
	 * 
	 * @return 为空是返回true,否则false
	 */
	private boolean check(String str)
	{
		return null == str || 0 == str.length();
	}

	private void refresh()
	{
		mCurrentPage = 1;
		mData.clear();
		photoCommentAdapter.notifyDataSetInvalidated();
		new PhotoCommentListGet().getPhotoList(mPhotoId, mCurrentPage,
				mPageSize, PhotoCommentListActivity.this);

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
					if (result instanceof CommentListInfoBean)
					{
						CommentListInfoBean clib = (CommentListInfoBean) result;
						mCountPage = clib.getData().getPageCount();
						List<CommentBean> data = clib.getData().getList();
						for (CommentBean commentBean : data)
						{
							if (null == commentBean)
								return;
							mData.add(commentBean);
						}
						photoCommentAdapter.notifyDataSetChanged();
					} else if (result instanceof String)
					{
						refresh();
						Toast.makeText(PhotoCommentListActivity.this, "操作成功",
								Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}

	@Override
	public void onError(SharePhotoException e)
	{
		MyDebugUtil.i("error:" + e.toString());
		Toast.makeText(PhotoCommentListActivity.this, "操作失败", Toast.LENGTH_LONG)
				.show();
	}

	private class PhotoCommentAdapter extends BaseAdapter
	{
		private Context mContext;

		public PhotoCommentAdapter(Context context)
		{
			this.mContext = context;
		}

		@Override
		public int getCount()
		{
			if (null == mData)
			{
				return 0;
			}
			return mData.size();
		}

		@Override
		public Object getItem(int position)
		{
			if (null == mData)
			{
				return null;
			}
			return mData.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			if (null == mData)
			{
				return 0;
			}
			return mData.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{

			if (null != mData || mData.size() >= position)
			{
				CommentBean mCommentBean = mData.get(position);
				Holder holder;
				if (convertView == null)
				{
					convertView = PhoneDisplayAdapter.computeLayout(mContext,
							R.layout.list_iten_comment);
					holder = new Holder();
					holder.txUsername = (TextView) convertView
							.findViewById(R.id.list_iten_comment_txt_user_name);
					holder.txCommentContent = (TextView) convertView
							.findViewById(R.id.list_iten_comment_txt_user_comment);
					holder.txTime = (TextView) convertView
							.findViewById(R.id.list_iten_comment_txt_comment_createtime);
					holder.imgUsersrc = (ImageView) convertView
							.findViewById(R.id.list_iten_comment_img_head_pic);
					convertView.setTag(holder);
				} else
				{
					holder = (Holder) convertView.getTag();

				}
				if (null != mCommentBean.getOwner().getHeadPicSrc())
				{
					mAsyncDrawableLoader.loadSmallDrawable(NetManager.URL_API
							+ mCommentBean.getOwner().getHeadPicSrc(),
							holder.imgUsersrc);
				}
				holder.txUsername.setText(mCommentBean.getOwner().getName()
						+ ":");
				holder.txCommentContent.setText(mCommentBean.getContent());
				holder.txTime.setText(mCommentBean.getCreateTime());

			}
			return convertView;

		}
	}

	private class Holder
	{
		private ImageView imgUsersrc;
		private TextView txUsername, txCommentContent, txTime;
	}

}
