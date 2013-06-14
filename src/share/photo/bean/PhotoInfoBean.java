package share.photo.bean;

public class PhotoInfoBean
{
	private int code;
	private String message;
	private PhotoBean data;
	public int getCode()
	{
		return code;
	}
	public void setCode(int code)
	{
		this.code = code;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
	public PhotoBean getData()
	{
		return data;
	}
	public void setData(PhotoBean data)
	{
		this.data = data;
	}
}
