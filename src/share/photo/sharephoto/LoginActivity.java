package share.photo.sharephoto;

import share.photo.bean.UserInfoBean;
import share.photo.listener.INetworkListener;
import share.photo.network.SharePhotoException;
import share.photo.network.Token;
import share.photo.network.UserSend;
import share.photo.utility.Constant;
import share.photo.utility.Notice;
import share.photo.utility.PhoneDisplayAdapter;
import share.photo.view.LoadingDialog;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class LoginActivity extends Activity implements INetworkListener
{
	// 界面控件
	private EditText etLoginName;
	private EditText etLoginPwd;

	// 初始化用户名密码
	private String name = null;
	private String pwd = null;
	private LoadingDialog mDialogLoading;
	private Handler mHandler = new Handler();
	private UserInfoBean userInfoBean;

	private SharedPreferences mSharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(PhoneDisplayAdapter.computeLayout(this,
				R.layout.activity_login));
		mSharedPreferences = getSharedPreferences(Constant.USER_CONFIG_FILE,
				MODE_PRIVATE);

		mDialogLoading = new LoadingDialog(this, "登陆中...");
		getFindByViewId();

		if (!isSHFileNull())
		{
			fillData();
			if (checkNamePwd())
				login();
		}
	}

	@Override
	protected void onDestroy()
	{
		mDialogLoading.dimiss();
		super.onDestroy();
	}

	/**
	 * 从SharedPreferences配置文件里取出数据填充用户名和密码
	 */
	private void fillData()
	{
		etLoginName.setText(mSharedPreferences.getString(
				Constant.KEY_USER_NAME, null));
		etLoginPwd.setText(mSharedPreferences.getString(
				Constant.KEY_USER_PASSWORD, null));
	}

	/**
	 * 判断SharedPreferences配置文件是否为空
	 * 
	 * @return true-空,false-不为空
	 */
	private boolean isSHFileNull()
	{
		boolean result = mSharedPreferences.contains(Constant.KEY_USER_NAME);
		result = result ? true : mSharedPreferences
				.contains(Constant.KEY_USER_PASSWORD);
		return !result;
	}

	/**
	 * 获取空间
	 */
	private void getFindByViewId()
	{
		etLoginName = (EditText) findViewById(R.id.login_edittext_name);
		etLoginPwd = (EditText) findViewById(R.id.login_edittext_password);
	}

	// 确定登录按钮
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.login_btn_login:
			if (checkNamePwd())
			{

				login();
			}
			break;

		case R.id.login_btn_register:
			Intent intent = new Intent(LoginActivity.this,
					RegisterActivity.class);
			startActivity(intent);
			break;
		}
	}

	/**
	 * 判断用户名和密码是否为空and少于6个字符
	 **/
	private boolean checkNamePwd()
	{
		boolean flag = true;

		name = etLoginName.getText().toString().trim();
		if (null == name || name.length() == 0)
		{
			etLoginName.setError("用户名不能为空");
			flag = false;
		}

		pwd = etLoginPwd.getText().toString().trim();
		if (null == pwd || pwd.length() < 6)
		{
			etLoginPwd.setError("密码不少于6位");
			flag = false;
		}

		return flag;
	}

	/**
	 * 用户登录
	 **/
	private void login()
	{
		mDialogLoading.show();
		new UserSend().login(name, pwd, LoginActivity.this);
	}

	@Override
	public void onReslut(final Object result)
	{
		mHandler.post(new Runnable()
		{
			public void run()
			{
				if (null != result)
				{
					Gson gson = new Gson();
					try
					{
						userInfoBean = gson.fromJson(result.toString(),
								UserInfoBean.class);
						Token token = Token.getInstance();
						token.setUserName(userInfoBean.getData().getName());
						token.setUserUid(userInfoBean.getData().getId());
						token.setUserHeadPicSrc(userInfoBean.getData()
								.getHeadPicSrc());
						SharePhotoApplication.getInstance().setHasLogin(true);
						SharedPreferences.Editor editor = mSharedPreferences
								.edit();
						editor.putString(Constant.KEY_USER_NAME, name);
						editor.putString(Constant.KEY_USER_PASSWORD, pwd);
						editor.commit();
					} catch (JsonSyntaxException e)
					{
						Notice.d(e.toString());
						e.printStackTrace();
					}
					mDialogLoading.hide();
					showToast("登录成功");
					int userID = userInfoBean.getData().getId();
					Bundle data = new Bundle();
					data.putInt("userID", userID);
					Intent intent = new Intent(LoginActivity.this,
							TabActivity.class);
					intent.putExtras(data);
					startActivity(intent);
					finish();

				}
			}
		});

	}

	@Override
	public void onError(final SharePhotoException e)
	{
		mHandler.post(new Runnable()
		{
			public void run()
			{
				showToast(e.toString());
				Notice.d(e.toString());
				mDialogLoading.hide();
			}
		});
	}

	private void showToast(CharSequence text)
	{
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

}
