package share.photo.view;

import share.photo.sharephoto.R;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;


/**
 * 等待、加载半透明界面
 * 
 * @author 黄世凡
 * 
 */
public class LoadingDialog extends AbsDialog
{
	private TextView mTextView;

	public LoadingDialog(Context context, String text)
	{
		super(context, R.style.dialog_loading, R.layout.dialog_loading);
		mTextView = (TextView) mDialog
				.findViewById(R.id.dialog_loading_textview);
		mTextView.setText(text);
		mDialog.getWindow().setBackgroundDrawable(new BitmapDrawable());
		mDialog.setOnKeyListener(new OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event)
			{
				if (KeyEvent.KEYCODE_BACK == keyCode)
					return true;
				return false;
			}
		});

	}

	public void setText(String text)
	{
		mTextView.setText(text);
	}

	@Override
	public void onClick(View v)
	{
	}

}
