package share.photo.network;

/**
 * SharePhoto的一个异常类
 * 
 * 
 */
public class SharePhotoException extends Exception {
	private static final long serialVersionUID = 1L;

	public SharePhotoException(String msg, int code) {
		super("错误代号:" + code + ",错误消息:" + msg);
	}

	public SharePhotoException(Throwable throwable) {
		super(throwable);
	}

}
