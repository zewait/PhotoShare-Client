package share.photo.view;

import share.photo.bean.PhotoBean;
import share.photo.listener.INetworkListener;
import share.photo.mydebug.MyDebugUtil;
import share.photo.network.AsyncDrawableLoader;
import share.photo.network.SharePhotoException;
import share.photo.sharephoto.PhotoShowActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class FlowView extends ImageView implements View.OnClickListener,
		INetworkListener
{

	// private AnimationDrawable loadingAnimation;
	private FlowTag flowTag;
	private Context context;
	public Drawable drawable;
	private int columnIndex;// 图片属于第几列
	private int rowIndex;// 图片属于第几行
	private Handler viewHandler;
	private PhotoBean mPhoto;

	public FlowView(Context c, AttributeSet attrs, int defStyle)
	{
		super(c, attrs, defStyle);
		this.context = c;
		init();
	}

	public FlowView(Context c, AttributeSet attrs)
	{
		super(c, attrs);
		this.context = c;
		init();
	}

	public FlowView(Context c)
	{
		super(c);
		this.context = c;
		init();
	}

	public FlowView(Context c, PhotoBean photo)
	{
		super(c);
		this.context = c;
		this.mPhoto = photo;
		init();
	}

	private void init()
	{
		setBackgroundColor(Color.WHITE);
		setPadding(2, 4, 4, 6);
		setOnClickListener(this);
		// this.setOnLongClickListener(this);
		setAdjustViewBounds(true);

	}

	// @Override
	// public boolean onLongClick(View v)
	// {
	// Log.d("FlowView", "LongClick");
	// Bundle data=new Bundle();
	// data.putInt("", this.flowTag.getFlowId());
	// context.startActivity(new Intent(context, PhotoShowActivity.class));
	// Toast.makeText(context, "长按：" + this.flowTag.getFlowId(),
	// Toast.LENGTH_SHORT).show();
	// return true;
	// }

	public void recycle()
	{
		Bitmap bitmap = this.getDrawingCache();
		if (null == bitmap || bitmap.isRecycled())
			return;
		bitmap.recycle();
	}

	@Override
	public void onClick(View v)
	{
		Bundle data = new Bundle();
		data.putSerializable("photo", mPhoto);
		Intent intent = new Intent(context, PhotoShowActivity.class);
		intent.putExtras(data);
		context.startActivity(intent);
	}

	/**
	 * 加载图片
	 */
	public void LoadImage()
	{
		if (getFlowTag() != null)
		{
			// new LoadImageThread().start();
			drawable = AsyncDrawableLoader.getInstance().loadSmallDrawable(
					flowTag.getFileName(), (INetworkListener) this);
			if (drawable != null)
			{
				imageSet();
			}
		}
	}

	private void imageSet()
	{
		((Activity) context).runOnUiThread(new Runnable()
		{
			public void run()
			{
				if (drawable != null)
				{// 此处在线程过多时可能为null
					int width = drawable.getIntrinsicWidth();// 获取真实宽高
					int height = drawable.getIntrinsicHeight();


					int layoutHeight = (height * flowTag.getItemWidth())
							/ width;// 调整高度
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							flowTag.getItemWidth(), layoutHeight);
					// 设置间距
					lp.setMargins(4, 4, 4, 8);
					setLayoutParams(lp);

					setImageDrawable(drawable);
					Handler h = getViewHandler();
					Message m = h.obtainMessage(flowTag.what, width,
							layoutHeight, FlowView.this);
					h.sendMessage(m);
				}
			}
		});

	}

	/**
	 * 重新加载图片
	 */
	public void reload()
	{
		if (this.drawable == null && getFlowTag() != null)
		{

			((Activity) context).runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					setImageDrawable(drawable);
				}
			});
		}
	}

	public FlowTag getFlowTag()
	{
		return flowTag;
	}

	public void setFlowTag(FlowTag flowTag)
	{
		this.flowTag = flowTag;
	}

	public int getColumnIndex()
	{
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex)
	{
		this.columnIndex = columnIndex;
	}

	public int getRowIndex()
	{
		return rowIndex;
	}

	public void setRowIndex(int rowIndex)
	{
		this.rowIndex = rowIndex;
	}

	public Handler getViewHandler()
	{
		return viewHandler;
	}

	public FlowView setViewHandler(Handler viewHandler)
	{
		this.viewHandler = viewHandler;
		return this;
	}

	public PhotoBean getmPhoto()
	{
		return mPhoto;
	}

	public void setmPhoto(PhotoBean mPhoto)
	{
		this.mPhoto = mPhoto;
	}

	@Override
	public void onReslut(Object result)
	{
		drawable = (Drawable) result;
		imageSet();

	}

	@Override
	public void onError(SharePhotoException e)
	{
		MyDebugUtil.i("error:" + e.toString());
	}

}
