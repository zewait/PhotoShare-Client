package share.photo.utility;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 分享类(分享类型如:微博,微信等)
 *
 */
public class ShareUtilty
{
	public static void share(String subject, String content, Drawable drawable ,Activity activity)
	{
		Bitmap bitmap = null;
		if(null!=drawable)
		{
			BitmapDrawable bd = (BitmapDrawable) drawable;

			bitmap = bd.getBitmap();
		}
		share(subject, content, bitmap, activity);
	}
	public static void share(String subject, String content, String file,Activity activity)
	{
		File f = null;
		if(null==file || 0==file.length())
			f = new File("");
		else
			f = new File(file);
		share(subject, content, f, activity);
	}
	public static void share(String subject, String content, File file,Activity activity)
	{
		Uri uri = null;
		if(null!=file&&file.exists())
			uri = Uri.fromFile(file);
		share(subject, content, uri, activity);
	}
	public static void share(String subject, String content, Bitmap bitmap,
			Activity activity)
	{
		Uri uri = null;
		if (null != bitmap)
		{
			uri = Uri.parse(MediaStore.Images.Media.insertImage(
					activity.getContentResolver(), bitmap, null, null));
		}
		share(subject, content, uri, activity);
	}
	
	public static void share(String subject, String content, Uri uri,
			Activity activity)
	{
		if(null == subject) subject = "";
		if(null == content) content = "";
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*"); // 分享的数据类型
		intent.putExtra(Intent.EXTRA_SUBJECT, subject); // 主题
		intent.putExtra(Intent.EXTRA_TEXT, content); // 内容
		if (null != uri)
		{
			intent.putExtra(Intent.EXTRA_STREAM, uri);
		}
		
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(Intent.createChooser(intent, "分享到"));
	}
}
