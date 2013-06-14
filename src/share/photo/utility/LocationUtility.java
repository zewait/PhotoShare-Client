package share.photo.utility;

import share.photo.mydebug.MyDebugUtil;
import android.content.Context;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class LocationUtility
{

	private static LocationClient mLocationClient = null;

	/**
	 * 定位
	 * 
	 * @param context
	 * @param listener
	 */
	public static void startLocation(Context context,
			BDLocationListener listener)
	{
		if (mLocationClient == null || !mLocationClient.isStarted())
		{
			mLocationClient = new LocationClient(context);
			mLocationClient.registerLocationListener(listener);
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);
			option.setAddrType("all");// 返回的定位结果包含地址信息
			option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
			option.setScanSpan(60000);// 设置发起定位请求的间隔时间为60000ms
			option.disableCache(true);// 禁止启用缓存定位
			option.setPoiNumber(5); // 最多返回POI个数
			option.setPoiDistance(1000); // poi查询距离
			option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
			mLocationClient.setLocOption(option);
			mLocationClient.start();
		}
	}
	
	/**
	 * 将经纬度转换成地址
	 * @param longitude
	 * @param latitude
	 * @param listener 回调的监听器
	 */
	public static void GeoPointTransformPlace(double longitude, double latitude, BMapManager bMapManager, MKSearchListener listener)
	{
		//将点的经纬度转换成int类型
		int intLongitude = (int)(1000000 * longitude);
		int intLatitude = (int)(1000000 * latitude);
		MKSearch mkSearch = new MKSearch();
		mkSearch.init(bMapManager, listener);
		mkSearch.reverseGeocode(new GeoPoint(intLatitude, intLongitude));
	}

	public static void stopLocation()
	{
		if (mLocationClient != null && mLocationClient.isStarted())
		{
			mLocationClient.stop();// 退出时停止定位
			// System.exit(0);
		}
	}

}
