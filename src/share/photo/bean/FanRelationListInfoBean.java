package share.photo.bean;

public class FanRelationListInfoBean
{
	private int code;
	private String message;
	private DataBean<FanRelationBean> data;
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
	public DataBean<FanRelationBean> getData()
	{
		return data;
	}
	public void setData(DataBean<FanRelationBean> data)
	{
		this.data = data;
	}
}
