package share.photo.network;

/**
 * 钥匙是一个单例，永远都只有一个
 * 
 * 
 */
public class Token {
	private final static Token mToken = new Token("null", -1);

	private String mUserName;
	private int mUserUid;
	private String mUserHeadPicSrc;

	public static final Token getInstance() {
		return mToken;
	}

	private Token(String accessToken, int userUid) {
		mUserName = accessToken;
		mUserUid = userUid;
	}

	public String getUserName() {
		return mUserName;
	}

	public int getUserUid() {
		return mUserUid;
	}

	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public void setUserUid(int mUserUid) {
		this.mUserUid = mUserUid;
	}

	public void clear() {
		mUserName = "token";
		mUserUid = -1;
	}

	public String getUserHeadPicSrc()
	{
		return mUserHeadPicSrc;
	}

	public void setUserHeadPicSrc(String mUserHeadPicSrc)
	{
		this.mUserHeadPicSrc = mUserHeadPicSrc;
	}


}
