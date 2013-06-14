package share.photo.sharephoto;

import java.util.ArrayList;
import java.util.List;

import share.photo.bean.CommentBean;
import share.photo.bean.UserBean;
import share.photo.bean.UserListInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.mydebug.MyDebugUtil;
import share.photo.network.AsyncDrawableLoader;
import share.photo.network.SharePhotoException;
import share.photo.network.UserSearchByNameListGet;
import share.photo.utility.NetManager;
import share.photo.utility.PhoneDisplayAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UserSearchByNameActivity extends Activity implements
		INetworkListener, OnClickListener
{
	// 界面控件用户名
	private EditText etUserName;
	private ListView userListView;

	private String userName;
	private List<UserBean> data = new ArrayList<UserBean>();
	private UserSearchByNameAdapter mUserSearchByNameAdapter;
	private AsyncDrawableLoader mAsyncDrawableLoader = AsyncDrawableLoader
			.getInstance();
	private InputMethodManager mInputMethodManager;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(PhoneDisplayAdapter.computeLayout(this,
				R.layout.activity_user_search_by_name));
		etUserName = (EditText) findViewById(R.id.user_search_by_name_edittext_username);
		userListView = (ListView) findViewById(R.id.user_search_by_name_listview_list);
		mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mUserSearchByNameAdapter = new UserSearchByNameAdapter(this);
		userListView.setAdapter(mUserSearchByNameAdapter);

		userListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id)
			{
				Intent intent = new Intent(UserSearchByNameActivity.this,
						OneseltActivity.class);
				UserBean ub = data.get(position);
				intent.putExtra("userId", ub.getId());
				intent.putExtra("userName", ub.getName());
				intent.putExtra("userHeadPicSrc", ub.getHeadPicSrc());
				intent.putExtra("isTab", false);
				startActivity(intent);
			}
		});

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.user_search_by_name_btn_back:
			finish();
			break;

		// 清空用户名
		case R.id.user_search_by_name_img_search_clear:
			etUserName.setText("");
			break;

		case R.id.user_search_by_name_img_search:
			userName = etUserName.getText().toString().trim();
			new UserSearchByNameListGet().getSearchByNameList(userName, 0, 0,
					UserSearchByNameActivity.this);
			etUserName.setText("");
			if (mInputMethodManager.isActive())
			{
				mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), mInputMethodManager.HIDE_NOT_ALWAYS);
			}
			break;
		}
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
					if (result instanceof UserListInfoBean)
					{
						data = ((UserListInfoBean) result).getData().getList();
						UserBean bean = ((UserListInfoBean) result).getData()
								.getList().get(0);
						if (bean != null)
						{
							mUserSearchByNameAdapter.setData(data);
						} else
						{
							showToast("用户不存在！");
							data.clear();
							mUserSearchByNameAdapter.setData(data);
						}

					}
				}

			}
		});
	}

	@Override
	public void onError(SharePhotoException e)
	{
		MyDebugUtil.i("error:" + e);
	}

	private class UserSearchByNameAdapter extends BaseAdapter implements
			INetworkListener
	{
		private Context mContext;
		private List<UserBean> mUserBeans;

		public UserSearchByNameAdapter(Context context)
		{
			this.mContext = context;
			this.mUserBeans = new ArrayList<UserBean>();
		}

		public void setData(List<UserBean> data)
		{
			this.mUserBeans = data;
			notifyDataSetChanged();
		}

		@Override
		public int getCount()
		{
			if (null != this.mUserBeans)
			{
				return this.mUserBeans.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position)
		{
			if (null != this.mUserBeans)
			{
				return this.mUserBeans.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int arg0)
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (null != this.mUserBeans || this.mUserBeans.size() > position)
			{
				Holder holder;
				UserBean userBean = this.mUserBeans.get(position);
				if (null == convertView)
				{
					holder = new Holder();
					convertView = PhoneDisplayAdapter.computeLayout(mContext,
							R.layout.user_search_by_name_list_listview);
					holder.imgHeadPicSrc = (ImageView) convertView
							.findViewById(R.id.user_search_by_name_list_img_usersrc);
					holder.txUserNmae = (TextView) convertView
							.findViewById(R.id.user_search_by_name_list_textView__username);
					convertView.setTag(holder);
				} else
				{
					holder = (Holder) convertView.getTag();
				}

				String headPicSrc = userBean.getHeadPicSrc();

				if (null != headPicSrc && headPicSrc.length() > 0
						&& !"null".equals(headPicSrc))
				{
					Drawable drawable = mAsyncDrawableLoader.loadSmallDrawable(
							NetManager.URL_API + headPicSrc, this);
					if (null != drawable)
					{
						holder.imgHeadPicSrc.setImageDrawable(drawable);
					} else
					{
						holder.imgHeadPicSrc
								.setImageResource(R.drawable.public_head_pic);
					}
				} else
				{
					holder.imgHeadPicSrc
							.setImageResource(R.drawable.public_head_pic);
				}
				holder.txUserNmae.setText(userBean.getName());

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
		private ImageView imgHeadPicSrc;
		private TextView txUserNmae;
	}

	private void showToast(String text)
	{
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

}
