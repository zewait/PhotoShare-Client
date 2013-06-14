package share.photo.bean;

public class SubscriptionRelationListInfoBean
{
	private int code;
	private String message;
	private DataBean<SubscriptionRelationBean> data;
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
	public DataBean<SubscriptionRelationBean> getData()
	{
		return data;
	}
	public void setData(DataBean<SubscriptionRelationBean> data)
	{
		this.data = data;
	}
}
