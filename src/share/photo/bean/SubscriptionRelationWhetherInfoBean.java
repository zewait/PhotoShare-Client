package share.photo.bean;

/**
 * 订阅与否
 * @author 黄世凡
 *
 */
public class SubscriptionRelationWhetherInfoBean
{
	private int code;
	private String message;
	private boolean data;
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
	public boolean getData()
	{
		return data;
	}
	public void setData(boolean data)
	{
		this.data = data;
	}
}
