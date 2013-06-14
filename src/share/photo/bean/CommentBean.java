package share.photo.bean;

import java.io.Serializable;

public class CommentBean implements Serializable
{
	private int id;
	private String content;
	private String createTime;
	private UserBean owner;
	private int toId;
	private int photoId;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(String createTime)
	{
		this.createTime = createTime;
	}

	public int getToId()
	{
		return toId;
	}

	public void setToId(int toId)
	{
		this.toId = toId;
	}

	public int getPhotoId()
	{
		return photoId;
	}

	public void setPhotoId(int photoId)
	{
		this.photoId = photoId;
	}

	public UserBean getOwner()
	{
		return owner;
	}

	public void setOwner(UserBean owner)
	{
		this.owner = owner;
	}

}
