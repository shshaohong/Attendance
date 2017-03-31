package com.edu.chenshaohong.attendance.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.edu.chenshaohong.attendance.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by shaohong on 2017-3-21.
 * 地图界面
 */

public class MapViewActivity extends Activity {

    private static LatLng mCurrentLocation;
    @BindView(R.id.bmapView)
    MapView mBmapView;
    private Unbinder mBind;
    private String mMachineLocation;
    private LocationClient mLocationClient = null;
    private BaiduMap mBaiduMap;
    private MapView mMapView;
    private static String mLocationAddr;
    private LatLng mPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mapview);
        mBind = ButterKnife.bind(this);
        init();
        // 初始化百度地图
        initMapView();
        //添加标注物
        initMarker();
        // 初始化定位相关
        initLocation();
    }
    private void initMarker() {

        String[] split = mMachineLocation.split(":");
        mPoint = new LatLng(Double.parseDouble(split[1]), Double.parseDouble(split[0]));
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.map);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions options = new MarkerOptions()
                .position(mPoint)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBmapView.getMap().addOverlay(options);

        MapStatus mMapStatus = new MapStatus.Builder()
                .target(mPoint)
                .rotate(0)
                .overlook(0)
                .zoom(18)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBmapView.getMap().setMapStatus(mMapStatusUpdate);
    }

    private void init() {
        Intent intent = getIntent();
        mMachineLocation = intent.getStringExtra("location");
        mLocationClient = new LocationClient(getApplicationContext());
    }

    private void initMapView() {
        // 设置地图状态
        MapStatus mapStatus = new MapStatus.Builder()
                .target(mPoint)
                .zoom(16)// 3--21：默认的是12
                .overlook(0)// 俯仰的角度
                .rotate(0)// 旋转的角度
                .build();

        // 设置百度地图的设置信息
        BaiduMapOptions options = new BaiduMapOptions()
                .mapStatus(mapStatus)
                .compassEnabled(true)// 是否显示指南针
                .zoomGesturesEnabled(true)// 是否允许缩放手势
                .scaleControlEnabled(false)// 不显示比例尺
//                .zoomControlsEnabled(false)// 不显示缩放的控件
                ;

        // 创建
        mMapView = new MapView(this, options);

        // 拿到地图的操作类(控制器：操作地图等都是使用这个)
        mBaiduMap = mMapView.getMap();

    }

    // 初始化定位相关
    private void initLocation() {
        //激活定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 初始化LocationClient类:LocationClient类必须在主线程中声明，需要Context类型的参数。
        mLocationClient = new LocationClient(this.getApplicationContext());
        // 配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开GPS
        option.setCoorType("bd09ll");// 设置百度坐标类型，默认gcj02，会有偏差，bd9ll百度地图坐标类型，将无偏差的展示到地图上
        option.setIsNeedAddress(true);// 需要地址信息
        mLocationClient.setLocOption(option);
        // 实现BDLocationListener接口
        mLocationClient.registerLocationListener(mBDLocationListener);
        // 开始定位
        mLocationClient.start();

    }
    // 定位监听
    private BDLocationListener mBDLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // 如果没有拿到结果，重新请求：部分机型会失败
            if (bdLocation == null) {
                mLocationClient.requestLocation();
                return;
            }

            // 定位结果的经纬度
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();

            // 定位的经纬度的类
            mCurrentLocation = new LatLng(latitude, longitude);
            mLocationAddr = bdLocation.getAddrStr();

            // 设置定位图层展示的数据
            MyLocationData data = new MyLocationData.Builder()
                    // 定位数据展示的经纬度
                    .latitude(latitude)
                    .longitude(longitude)
                    .accuracy(100f)// 定位精度的大小
                    .build();
            Log.e("longitude", longitude + "=====" + latitude);
            // 定位数据展示到地图上
            mBaiduMap.setMyLocationData(data);

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mBmapView.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mBmapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBmapView.onDestroy();
        mBind.unbind();
    }

    @OnClick(R.id.back)
    public void setback() {
        finish();
    }

}
