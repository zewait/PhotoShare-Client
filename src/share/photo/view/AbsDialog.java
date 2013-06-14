package share.photo.view;

import share.photo.utility.PhoneDisplayAdapter;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Dialog抽象类(要用到的按键事件时绑定自身，重写onClcik方法)
 * 
 * @author 黄世凡
 * 
 */
public abstract class AbsDialog implements OnClickListener
{
	protected Dialog mDialog;
	protected Context mContext;

	protected AbsDialog(Context context, int theme, int layout)
	{
		mContext = context;
		mDialog = new Dialog(mContext, theme);
		mDialog.setContentView(PhoneDisplayAdapter.computeLayout(mContext,
				layout));
	}

	public void show()
	{
		mDialog.show();
	}

	public void hide()
	{
		mDialog.hide();
	}

	public void dimiss()
	{
		mDialog.dismiss();
	}

	public abstract void onClick(View v);
}
