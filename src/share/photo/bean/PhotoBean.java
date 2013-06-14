package share.photo.bean;

import java.io.Serializable;

public class PhotoBean implements Serializable
{
	private int id;
	private int userId;
	// 经度
	private double longitude;
	// 纬度
	private double latitude;
	private String content;
	private boolean isBlow = false;
	private String createTime;
	private String src;
	private String zoomSrc;
	private UserBean user;
	private int distance;
	// 喜欢的人数
	private long likeCount;
	// 是否被该用户喜欢
	private boolean isLiked;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getUserId()
	{
		return userId;
	}

	public void setUserId(int userId)
	{
		this.userId = userId;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public boolean isBlow()
	{
		return isBlow;
	}

	public void setBlow(boolean isBlow)
	{
		this.isBlow = isBlow;
	}

	public String getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(String createTime)
	{
		this.createTime = createTime;
	}

	public String getSrc()
	{
		return src;
	}

	public void setSrc(String src)
	{
		this.src = src;
	}

	public String getZoomSrc()
	{
		return zoomSrc;
	}

	public void setZoomSrc(String zoomSrc)
	{
		this.zoomSrc = zoomSrc;
	}

	public UserBean getUser()
	{
		return user;
	}

	public void setUser(UserBean user)
	{
		this.user = user;
	}

	public int getDistance()
	{
		return distance;
	}

	public void setDistance(int distance)
	{
		this.distance = distance;
	}

	public long getLikeCount()
	{
		return likeCount;
	}

	public void setLikeCount(long likeCount)
	{
		this.likeCount = likeCount;
	}

	public boolean getIsLiked()
	{
		return isLiked;
	}

	public void setIsLiked(boolean isLiked)
	{
		this.isLiked = isLiked;
	}

}
