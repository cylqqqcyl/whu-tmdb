package drz.tmdb.map;

import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
//import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapException;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.provider.Settings.Secure;

import drz.tmdb.R;


public class MapActivity extends Activity implements LocationSource,
        AMapLocationListener {

    private Polyline polyline;
    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private TextView mLocationErrText;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = false;
    private Marker mLocMarker;
    private SensorEventHelper mSensorHelper;
    private Circle mCircle;
    public static final String LOCATION_MARKER_FLAG = "mylocation";

    ArrayList<TrajectoryPoint> trajectory = new ArrayList<>();

    private int[] colorIcon = new int[]{R.drawable.tpoint1, R.drawable.tpoint2, R.drawable.tpoint3,
            R.drawable.tpoint4,R.drawable.tpoint5, R.drawable.tpoint6, R.drawable.tpoint7,
            R.drawable.tpoint8, R.drawable.tpoint9, R.drawable.tpoint10, R.drawable.tpoint11 };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 询问隐私政策
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this,true);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
        setContentView(R.layout.activity_map);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();

        // 在地图上绘制历史轨迹数据
        drawTrace();

        // 退出地图按钮
        Button clean_button = findViewById(R.id.back_button);
        //join轨迹
        Button join_button=findViewById(R.id.join);
        clean_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showExitDialog(v);
            }
        });
        join_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    // 在地图上绘制历史轨迹数据
    // 1号设备产生的轨迹使用红色点
    // 2号设备产生的轨迹使用蓝色点
    private void drawTrace(){
        // 读取历史轨迹数据
        ArrayList<ArrayList<TrajectoryPoint>> trajectories = TrajectoryUtils.load();
        if(trajectories == null || trajectories.size() == 0)
            return;
        int counter = -1;
        for(ArrayList<TrajectoryPoint> trajectory : trajectories){
            counter = (counter + 1) % 11;
            List<LatLng> trace = new ArrayList<LatLng>();
            // 绘制每条轨迹
            for(TrajectoryPoint point : trajectory){
                // 绘制每个点
                LatLng latLng = new LatLng(point.latitude, point.longitude);
                aMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(colorIcon[counter])));
                trace.add(latLng);
            }
            Bitmap newBm = BitmapDescriptorFactory.fromResource(colorIcon[counter]).getBitmap();
            PolylineOptions options = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                options = new PolylineOptions().addAll(trace).width(8)
                        .color(newBm.getColor(newBm.getWidth()/2,newBm.getHeight()/2).toArgb());
            }
            else{
                options = new PolylineOptions().addAll(trace).width(8)
                        .color(Color.argb(255,255-255*counter/11,255*counter/11,255*counter/11));
            }
            aMap.addPolyline(options);
        }
    }


    /**
     * 初始化
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        mSensorHelper = new SensorEventHelper(this);
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
        mLocationErrText = (TextView)findViewById(R.id.location_errInfo_text);
        mLocationErrText.setVisibility(View.GONE);

        test_trace();
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        mapView.onPause();
        deactivate();
        mFirstFix = false;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mLocationErrText.setVisibility(View.GONE);
                LatLng location = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                if (!mFirstFix) {
                    mFirstFix = true;
                    addCircle(location, amapLocation.getAccuracy());//添加定位精度圆
                    addMarker(location);//添加定位图标
                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18));
                } else {
                    mCircle.setCenter(location);
                    mCircle.setRadius(amapLocation.getAccuracy());
                    mLocMarker.setPosition(location);
                }



                double latitude = amapLocation.getLatitude(); // 获取纬度
                double longitude = amapLocation.getLongitude(); // 获取经度
                Date date = new Date(amapLocation.getTime()); // 获取定位时间
                String userID = Secure.getString(getContentResolver(), Secure.ANDROID_ID); // 获取uid


                List<LatLng> trace = new ArrayList<LatLng>();
                int szT = trajectory.size();
                if(szT>0) trace.add(new LatLng(trajectory.get(szT-1).latitude,trajectory.get(szT-1).longitude));
                // 将定位点加入轨迹集合
                trajectory.add(new TrajectoryPoint(longitude, latitude, date, userID));
                trace.add(new LatLng(trajectory.get(szT).latitude,trajectory.get(szT).longitude));
                PolylineOptions options = new PolylineOptions().addAll(trace).width(2).color(Color.argb(255, 0, 0, 255));
                if(szT>0){
                    polyline = aMap.addPolyline(options);
                    int a=1;//cut
                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
                mLocationErrText.setVisibility(View.VISIBLE);
                mLocationErrText.setText(errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            try {
                mlocationClient = new AMapLocationClient(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(2000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.navi_map_gps_locked);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);

//		BitmapDescriptor des = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
        mLocMarker.setTitle(LOCATION_MARKER_FLAG);
    }


    // 点击返回按钮
    public void showExitDialog(View v){
        //定义一个新对话框对象
        AlertDialog.Builder exit_dialog = new AlertDialog.Builder(this);
        //设置对话框提示内容
        exit_dialog.setMessage("Do you want to save the trajectory before exiting the map?");
        //定义对话框两个按钮及接受事件
        exit_dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 持久化保存轨迹
                TrajectoryUtils.save(trajectory);
                // 返回上一个界面
                finish();
            }
        });
        exit_dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 返回上一个界面
                finish();
            }
        });
        //创建并显示对话框
        AlertDialog exit_dialog0 = exit_dialog.create();
        exit_dialog0.show();
    }

    private List<LatLng> readLatLngs(){
        List<LatLng> trace = new ArrayList<LatLng>();

        double [][] whu_position = {
                {30.53576927684723,114.36409111149217},{30.53583396296305,114.3639891875496},{30.53583396296338,114.3638443482628},
                {30.535935612486835,114.36383898384477},{30.536009539346175,114.36376924641038},{30.536111188685922,114.36371560223009},
                {30.53617587457394,114.36362440712358},{30.536198976666398,114.36353321201707},{30.536198976666398,114.36338837273027},
                {30.536111188685947,114.36330790645982},{30.536078845725743,114.3631416095009},{30.53612042952972,114.36300749905016},
                {30.53613429079378,114.36289484627153},{30.536162013315884,114.36276610023882},{30.53616663373543,114.36262126095201},
                {30.536194356248355,114.36252470142747},{30.536152772476132,114.36243350632097},{30.5360187801999,114.36240668423082},
                {30.535898649036454,114.36237986214067},{30.53577389728637,114.36233694679643},{30.535792379037247,114.36220283634569},
                {30.535820101656935,114.36207409031297},{30.535870926439234,114.36186487800981},{30.535898649036554,114.36170394546892},
                {30.53592175119494,114.3615215552559},{30.535981816781018,114.3612855208626},{30.536037261904365,114.36104948646928},
                {30.53608346614972,114.36084563858415},{30.536189735830515,114.36065251953508},{30.536277523739976,114.36052377350236},
                {30.53646234013212,114.36043257839586},{30.536647156172595,114.36020190842058},{30.5367903883621,114.35992295868303},
                {30.53695672225258,114.35974056847002},{30.537183120701275,114.35949380524065},{30.53749730542921,114.35920949108507},
                {30.537959339947687,114.35885007507707},{30.53819497670574,114.35872669346239},{30.538347447244725,114.3585121167412},
                {30.538268901845598,114.35820098049547},{30.538213457996278,114.35786302215959},{30.538153393790385,114.35756261474992},
                {30.538324345663572,114.35744459755327}
        };

        for (int i = 0; i < whu_position.length; i++) {
            trace.add(new LatLng(whu_position[i][0], whu_position[i][1]));
        }
        for (int i = whu_position.length-1; i >=0; i--) {
            trace.add(new LatLng(whu_position[i][0], whu_position[i][1]));
        }

        PolylineOptions options = new PolylineOptions().addAll(trace).width(5).color(Color.argb(255, 0, 255, 0));

        polyline = aMap.addPolyline(options);
        return trace;
    }
    private void test_trace(){
        List<LatLng> points = readLatLngs();
        LatLngBounds bounds = null;
        try {
            bounds = new LatLngBounds(points.get(0), points.get(points.size() - 2));
        } catch (AMapException e) {
            throw new RuntimeException(e);
        }
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

//        SmoothMoveMarker smoothMarker = new SmoothMoveMarker(aMap);
//        // 设置滑动的图标
//        smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.position));
//
//        LatLng drivePoint = points.get(0);
//        Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint);
//        points.set(pair.first, drivePoint);
//        List<LatLng> subList = points.subList(pair.first, points.size());
//
//        // 设置滑动的轨迹左边点
//        smoothMarker.setPoints(subList);
//        // 设置滑动的总时间
//        smoothMarker.setTotalDuration(40);
//        // 开始滑动
//        smoothMarker.startSmoothMove();
    }

}