package share.photo.bean;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
public class DataBean<T> {
	@SerializedName(value = "items")
	private List<T> mList = new ArrayList<T>();
	private int pageIndex;
	private int pageSize;
	private int pageCount;

	public List<T> getList() {
		return mList;
	}

	public void setList(List<T> mList) {
		this.mList = mList;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	
}