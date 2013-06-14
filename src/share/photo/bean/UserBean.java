package share.photo.bean;

import java.io.Serializable;

public class UserBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String headPicSrc;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getHeadPicSrc()
	{
		return headPicSrc;
	}

	public void setHeadPicSrc(String headPicSrc)
	{
		this.headPicSrc = headPicSrc;
	}
}
