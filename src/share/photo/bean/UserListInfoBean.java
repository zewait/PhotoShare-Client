package share.photo.bean;

import java.io.Serializable;

/**
 * 由于服务端没有完成，之后需要根据实际的json修改bean类
 * 
 */
public class UserListInfoBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int code;
	private String message;
	private DataBean<UserBean> data;
	
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
	public DataBean<UserBean> getData() {
		return data;
	}
	public void setData(DataBean<UserBean> data) {
		this.data = data;
	}


}
