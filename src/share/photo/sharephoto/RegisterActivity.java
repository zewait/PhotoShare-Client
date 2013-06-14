package share.photo.sharephoto;

import com.baidu.platform.comapi.map.p;

import share.photo.listener.INetworkListener;
import share.photo.network.SharePhotoException;
import share.photo.network.UserSend;
import share.photo.utility.PhoneDisplayAdapter;
import share.photo.view.LoadingDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.app.Activity;

public class RegisterActivity extends Activity implements INetworkListener
{
	// 界面控件
	private EditText etRegName;
	private EditText etRegPwd;
	private EditText etRegRePwd;
	private ProgressBar mProgressBar;

	// 初始化用户名密码
	private String name = null;
	private String pwd = null;
	private String rePwd=null;
	private LoadingDialog mDialogLoading;
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(PhoneDisplayAdapter.computeLayout(this,
				R.layout.activity_register));
		mDialogLoading = new LoadingDialog(this, "登陆中...");
		getFindByViewId();
	}

	// 界面控件ID

	private void getFindByViewId()
	{
		etRegName = (EditText) findViewById(R.id.register_edittext_name);
		etRegPwd = (EditText) findViewById(R.id.register_edittext_password);
		etRegRePwd=(EditText) findViewById(R.id.register_edittext_repassword);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
	}

	// 确定注册按钮
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.register_btn_register:
			if (checkNamePwd())
			{
				mProgressBar.setVisibility(View.VISIBLE);
				register();
			}
			break;
		case R.id.register_btn_back:
			finish();
			break;

		}
	}

	/**
	 * 判断用户名和密码是否为空and少于6个字符
	 **/
	private boolean checkNamePwd()
	{
		boolean flag = true;

		name = etRegName.getText().toString().trim();
		if (null == name || name.length() == 0)
		{
			etRegName.setError("用户名不能为空");
			flag = false;
		}

		pwd = etRegPwd.getText().toString().trim();
		if (null == pwd || pwd.length() < 6)
		{
			etRegPwd.setError("密码不能少于六个字符");
			flag = false;
		}
		
		rePwd=etRegRePwd.getText().toString().trim();
		if(null==rePwd||!rePwd.equals(pwd)){
			etRegRePwd.setError("密码不一致");
			flag = false;
		}

		return flag;
	}

	/**
	 * 用户注册
	 * */
	private void register()
	{
		System.out.println(name + " " + pwd);
		new UserSend().add(name, pwd, RegisterActivity.this);
	}

	@Override
	public void onReslut(final Object result)
	{
		mHandler.post(new Runnable()
		{
			public void run()
			{
				mProgressBar.setVisibility(View.GONE);
				showToast("注册成功,请登录!");
				RegisterActivity.this.finish();

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
				mProgressBar.setVisibility(View.GONE);
				showToast(e.toString());
			}
		});

	}

	private void showToast(CharSequence text)
	{
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

}
