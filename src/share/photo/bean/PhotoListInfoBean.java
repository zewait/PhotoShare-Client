package share.photo.bean;



public class PhotoListInfoBean
{
	private int code;
	private String message;
	private DataBean<PhotoBean> data;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public DataBean<PhotoBean> getData() {
		return data;
	}
	public void setData(DataBean<PhotoBean> data) {
		this.data = data;
	}
	
	
	

}
