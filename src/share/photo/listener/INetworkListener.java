package share.photo.listener;

import share.photo.network.SharePhotoException;

/**
 * 一个监听接口
 *
 * 
 */
public interface INetworkListener {
	void onReslut(Object result);

	void onError(SharePhotoException e);
}
