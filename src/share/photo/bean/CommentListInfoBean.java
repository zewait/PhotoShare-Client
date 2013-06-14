package share.photo.bean;

import java.io.Serializable;


public class CommentListInfoBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int code;
	private String message;
	private DataBean<CommentBean> data;
	
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
	public DataBean<CommentBean> getData() {
		return data;
	}
	public void setData(DataBean<CommentBean> data) {
		this.data = data;
	}
	
	
	

}
