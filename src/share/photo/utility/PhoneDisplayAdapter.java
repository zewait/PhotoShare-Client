package share.photo.utility;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 手机分辨率自适应
 * 
 * 
 */
public final class PhoneDisplayAdapter {

	private static float screenRatioOfWidth = 0; // 屏幕的宽
	private static float screenRatioOfHeight = 0; // 屏幕的高

	/**
	 * 程序初始化的时候必须使用，否则你就肯定扯到蛋
	 * 
	 * @param width
	 *            宽
	 * @param height
	 *            高
	 */
	public static void init(float width, float height) {
		if (getScreenWidth() == 0 || getScreenHeight() == 0) {
			screenRatioOfWidth = width / 480;
			screenRatioOfHeight = height / 800;
		}
	}

	/**
	 * 计算Layout
	 * 
	 * @param context
	 * @param layoutResID
	 *            Layou资源ID
	 * @return View
	 */
	public static View computeLayout(Context context, int layoutResID) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(layoutResID, null);
		computeLayout(view);
		return view;
	}

	/**
	 * 计算Layout
	 * 
	 * @param context
	 * @param parent
	 *            父容器
	 * @param layoutResID
	 *            Layou资源ID
	 * @return
	 */
	public static View computeLayout(Context context, ViewGroup parent,
			int layoutResID) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(layoutResID, parent, false);
		computeLayout(view);
		return view;
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @return
	 */
	public static float getScreenWidth() {
		return screenRatioOfWidth;
	}

	/**
	 * 设置屏幕宽度
	 * 
	 * @param width
	 */
	public static void setScreenWidth(float width) {
		screenRatioOfWidth = width / 480;
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @return
	 */
	public static float getScreenHeight() {
		return screenRatioOfHeight;
	}

	/**
	 * 设置屏幕高度
	 * 
	 * @param width
	 */
	public static void setScreenHeight(float height) {
		screenRatioOfHeight = height / 800;
	}

	/**
	 * 计算Layout
	 * 
	 * @param view
	 */
	public static void computeLayout(View view) {
		if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
			setRelativeLayout(view);
		} else if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
			setLinearLayout(view);
		} else if (view.getLayoutParams() instanceof FrameLayout.LayoutParams) {
			setFrameLayout(view);
		}

		if (view instanceof ViewGroup) {
			computeLayoutOfViewGroup((ViewGroup) view);
		} else if (view instanceof TextView) {
			computeLayoutOfText((TextView) view);
		} else {

			// view.setPadding((int) (view.getPaddingLeft() *
			// screenRatioOfWidth),
			// (int) (view.getPaddingTop() * screenRatioOfHeight),
			// (int) (view.getPaddingRight() * screenRatioOfWidth),
			// (int) (view.getPaddingBottom() * screenRatioOfHeight));
			view.setPadding((int) (view.getPaddingLeft() * screenRatioOfWidth),
					(int) (view.getPaddingTop() * screenRatioOfWidth),
					(int) (view.getPaddingRight() * screenRatioOfWidth),
					(int) (view.getPaddingBottom() * screenRatioOfWidth));
		}
	}

	/**
	 * 设置相对布局
	 * 
	 * @param view
	 */
	private static void setRelativeLayout(View view) {
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
				.getLayoutParams();

		RelativeLayout.LayoutParams newLayoutParams = new RelativeLayout.LayoutParams(
				(layoutParams.width == RelativeLayout.LayoutParams.MATCH_PARENT)
						|| (layoutParams.width == RelativeLayout.LayoutParams.WRAP_CONTENT) ? layoutParams.width
						: (int) (layoutParams.width * screenRatioOfWidth),
				(layoutParams.height == RelativeLayout.LayoutParams.MATCH_PARENT)
						|| (layoutParams.height == RelativeLayout.LayoutParams.WRAP_CONTENT) ? layoutParams.height
						: (int) (layoutParams.height * screenRatioOfWidth));

		for (int verb = 0; verb < layoutParams.getRules().length; verb++) {
			int anchor = layoutParams.getRules()[verb];
			newLayoutParams.addRule(verb, anchor);
		}

		newLayoutParams.setMargins(
				(int) (layoutParams.leftMargin * screenRatioOfWidth),
				(int) (layoutParams.topMargin * screenRatioOfWidth),
				(int) (layoutParams.rightMargin * screenRatioOfWidth),
				(int) (layoutParams.bottomMargin * screenRatioOfWidth));
		// newLayoutParams.setMargins(
		// (int) (layoutParams.leftMargin * screenRatioOfWidth),
		// (int) (layoutParams.topMargin * screenRatioOfHeight),
		// (int) (layoutParams.rightMargin * screenRatioOfWidth),
		// (int) (layoutParams.bottomMargin * screenRatioOfHeight));

		view.setLayoutParams(newLayoutParams);
	}

	/**
	 * 设置线�?布局
	 * 
	 * @param view
	 */
	private static void setLinearLayout(View view) {
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view
				.getLayoutParams();

		LinearLayout.LayoutParams newLayoutParams = new LinearLayout.LayoutParams(
				(layoutParams.width == LinearLayout.LayoutParams.MATCH_PARENT)
						|| (layoutParams.width == LinearLayout.LayoutParams.WRAP_CONTENT) ? layoutParams.width
						: (int) (layoutParams.width * screenRatioOfWidth),
				(layoutParams.height == LinearLayout.LayoutParams.MATCH_PARENT)
						|| (layoutParams.height == LinearLayout.LayoutParams.WRAP_CONTENT) ? layoutParams.height
						: (int) (layoutParams.height * screenRatioOfWidth));

		newLayoutParams.gravity = layoutParams.gravity;

		newLayoutParams.setMargins(
				(int) (layoutParams.leftMargin * screenRatioOfWidth),
				(int) (layoutParams.topMargin * screenRatioOfWidth),
				(int) (layoutParams.rightMargin * screenRatioOfWidth),
				(int) (layoutParams.bottomMargin * screenRatioOfWidth));
		// newLayoutParams.setMargins(
		// (int) (layoutParams.leftMargin * screenRatioOfWidth),
		// (int) (layoutParams.topMargin * screenRatioOfHeight),
		// (int) (layoutParams.rightMargin * screenRatioOfWidth),
		// (int) (layoutParams.bottomMargin * screenRatioOfHeight));
		view.setLayoutParams(newLayoutParams);
	}

	/**
	 * 设置绝对布局
	 * 
	 * @param view
	 */
	private static void setFrameLayout(View view) {
		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view
				.getLayoutParams();

		FrameLayout.LayoutParams newLayoutParams = new FrameLayout.LayoutParams(
				(layoutParams.width == FrameLayout.LayoutParams.MATCH_PARENT)
						|| (layoutParams.width == FrameLayout.LayoutParams.WRAP_CONTENT) ? layoutParams.width
						: (int) (layoutParams.width * screenRatioOfWidth),
				(layoutParams.height == FrameLayout.LayoutParams.MATCH_PARENT)
						|| (layoutParams.height == FrameLayout.LayoutParams.WRAP_CONTENT) ? layoutParams.height
						: (int) (layoutParams.height * screenRatioOfWidth));

		newLayoutParams.gravity = layoutParams.gravity;

		newLayoutParams.setMargins(
				(int) (layoutParams.leftMargin * screenRatioOfWidth),
				(int) (layoutParams.topMargin * screenRatioOfWidth),
				(int) (layoutParams.rightMargin * screenRatioOfWidth),
				(int) (layoutParams.bottomMargin * screenRatioOfWidth));
		// newLayoutParams.setMargins(
		// (int) (layoutParams.leftMargin * screenRatioOfWidth),
		// (int) (layoutParams.topMargin * screenRatioOfHeight),
		// (int) (layoutParams.rightMargin * screenRatioOfWidth),
		// (int) (layoutParams.bottomMargin * screenRatioOfHeight));

		view.setLayoutParams(newLayoutParams);
	}

	/**
	 * 计算ViewGroup
	 * 
	 * @param viewGroup
	 */
	private static void computeLayoutOfViewGroup(ViewGroup viewGroup) {
		// viewGroup.setPadding(
		// (int) (viewGroup.getPaddingLeft() * screenRatioOfWidth),
		// (int) (viewGroup.getPaddingTop() * screenRatioOfHeight),
		// (int) (viewGroup.getPaddingRight() * screenRatioOfWidth),
		// (int) (viewGroup.getPaddingBottom() * screenRatioOfHeight));
		viewGroup.setPadding(
				(int) (viewGroup.getPaddingLeft() * screenRatioOfWidth),
				(int) (viewGroup.getPaddingTop() * screenRatioOfWidth),
				(int) (viewGroup.getPaddingRight() * screenRatioOfWidth),
				(int) (viewGroup.getPaddingBottom() * screenRatioOfWidth));

		if (viewGroup instanceof ListView) {
			((ListView) viewGroup)
					.setDividerHeight((int) (((ListView) viewGroup)
							.getDividerHeight() * screenRatioOfWidth));
		}

		for (int i = 0, count = viewGroup.getChildCount(); i < count; i++) {
			View chileView = viewGroup.getChildAt(i);
			computeLayout(chileView);
		}
	}

	/**
	 * 计算文本字体大小
	 * 
	 * @param view
	 */
	private static void computeLayoutOfText(TextView view) {
		/*
		 * TypedValue.COMPLEX_UNIT_PX : Pixels TypedValue.COMPLEX_UNIT_SP :
		 * Scaled Pixels TypedValue.COMPLEX_UNIT_DIP : Device Independent Pixels
		 */
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, view.getTextSize()
				* screenRatioOfWidth);

		view.setPadding((int) (view.getPaddingLeft() * screenRatioOfWidth),
				(int) (view.getPaddingTop() * screenRatioOfWidth),
				(int) (view.getPaddingRight() * screenRatioOfWidth),
				(int) (view.getPaddingBottom() * screenRatioOfWidth));
	}

}
