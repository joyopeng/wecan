package com.gofirst.scenecollection.evidence.view.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.PicturesData;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.model.SysAppParamSetting;
import com.gofirst.scenecollection.evidence.utils.BitmapUtils;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.Utils;
import com.gofirst.scenecollection.evidence.view.adapter.AddMapCaptrueAdapter;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author maxiran
 */
public class SceneDirectionFragment extends Fragment implements Animation.AnimationListener, LocationSource, AMapLocationListener,

        OfflineMapManager.OfflineMapDownloadListener, BitmapUtils.OnSaveSuccessListener, AMap.OnMapClickListener {

    private static int DESTINATION_IMG_WIDTH = 1754;
    private static int DESTINATION_IMG_HEIGHT = 1240;

    private static int[][] GRIDVIEW_X_Y = {
            {1244, 964, 1244, 1072},
            {1323, 964, 1323, 1072},
            {1611, 964, 1611, 1072},
            {1244, 964, 1611, 964},
            {1244, 1000, 1611, 1000},
            {1244, 1036, 1611, 1036},
            {1244, 1072, 1611, 1072}
    };

    private static int[][] GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y = {
            {1244, 930, 1244, 1072},
            {1323, 930, 1323, 1072},
            {1611, 930, 1611, 1072},
            {1244, 930, 1611, 930},
            {1244, 1000, 1611, 1000},
            {1244, 1036, 1611, 1036},
            {1244, 1072, 1611, 1072}
    };

    private MapView mapView;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private OnLocationChangedListener mListener;
    private GridView mapGrid;
    private Button mBtnSaveMap;
    private AddMapCaptrueAdapter addMapCaptrueAdapter;
    private String lastRefreshCityCode = "";
    private List<RecordFileInfo> recordFileInfos = new ArrayList<>();
    private AMap aMap;
    private UiSettings mUiSettings;
    private Marker lastMaker;
    private String mode, templateId, father;
    private double mLat = 0;
    private double mLong = 0;

    private SharePre mShare;
    private String mCaseId = "";
    private ImageView centerImageView;
    private boolean isFirst = true;
    private AMapLocation amapLocation;
    private CsSceneCases caseInfo;
    private List<BaseView> viewList = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationManager locationManager
                = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("GPS提示");
            builder.setMessage("打开GPS开关以获得更为精确的定位");
            builder.setPositiveButton("打开", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(settingsIntent);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mShare = new SharePre(getActivity(), "user_info",
                Context.MODE_PRIVATE);
        mCaseId = getArguments().getString("caseId");
        templateId = getArguments().getString("templateId");
        father = getArguments().getString("father");
        caseInfo = ViewUtil.getCsSceneCasesByCaseId(mCaseId);
        View view = null;
        try {
            File file = new File(getContext().getObbDir() + "/amp/");
            if (!file.exists())
                file.mkdirs();
            MapsInitializer.sdcardDir = file.toString();
            mode = getArguments().getString("mode");
            view = inflater.inflate(R.layout.scene_direction_layout, null);
            mapView = (MapView) view.findViewById(R.id.map);
            mapGrid = (GridView) view.findViewById(R.id.map_grid);
            mBtnSaveMap = (Button) view.findViewById(R.id.btn_save_map);
            if (mode != null && BaseView.VIEW.equals(mode)) {
                mBtnSaveMap.setVisibility(View.GONE);
                //mapView.setVisibility(View.GONE);
                //view.findViewById(R.id.mark).setVisibility(View.GONE);
                view.findViewById(R.id.map_view_layout).setVisibility(View.GONE);
            }

            mBtnSaveMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    captureMapScreen();
                }
            });
            mapView.onCreate(savedInstanceState);
            if (aMap == null) {
                aMap = mapView.getMap();
                mUiSettings = aMap.getUiSettings();
            }
            CameraPosition cp = aMap.getCameraPosition();
            List<SysAppParamSetting> paramSettings = EvidenceApplication.db.findAllByWhere(SysAppParamSetting.class, "key = 'default_location_coordinate'");
            if (paramSettings.size() > 0) {
                SysAppParamSetting paramSetting = paramSettings.get(0);
                String value = paramSettings.get(0).getValue();
                if (value != null && !"".equals(value)) {
                    try {
                        JSONObject object = new JSONObject(paramSetting.getValue());
                        double longitude = Double.valueOf(object.getString("long"));
                        double latitude = Double.valueOf(object.getString("lat"));
                        CameraPosition cpNew = CameraPosition.fromLatLngZoom(new LatLng(latitude, longitude), cp.zoom);
                        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cpNew);
                        aMap.moveCamera(cu);
                    } catch (Exception e) {
                        Log.i("SceneDirectionFragment", "onCreateView exception", e);
                        //cpNew = CameraPosition.fromLatLngZoom(new LatLng(39.92, 116.46), cp.zoom);   //北京坐标点
                        //CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cpNew);
                        //aMap.moveCamera(cu);
                    }
                }
            }

            //aMap.setOnMapClickListener(this);
            aMap.setLocationSource(this);// 设置定位监听
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
            aMap.getUiSettings().setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);//是否可触发定位并显示定位层
            aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                    .fromResource(R.drawable.lucency));// 设置小蓝点的图标
            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
            myLocationStyle.strokeWidth(0f);// 设置圆形的边框粗细
            aMap.setMyLocationStyle(myLocationStyle);
            //aMap.setMyLocationRotateAngle(180);

            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
            //mUiSettings.setTiltGesturesEnabled(false);// 设置地图是否可以倾斜
            mUiSettings.setScaleControlsEnabled(true);

            mapGrid.setAdapter(addMapCaptrueAdapter = new AddMapCaptrueAdapter(recordFileInfos = getMapFiles(), new AddMapCaptrueAdapter.takeMapCapture() {
                @Override
                public void takeCapture() {
                    aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
                        @Override
                        public void onMapScreenShot(final Bitmap bitmap) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
//                                    BitmapUtils.saveBitmap("map", getActivity(), reCanvasBitmap(bitmap), System.currentTimeMillis() + "", getArguments().getString("father"), System.currentTimeMillis() + "", SceneDirectionFragment.this, getArguments().getString("caseId"));
                                    BitmapUtils.saveBitmap("map", getActivity(), reCanvasBitmap(bitmap), System.currentTimeMillis() + "", getArguments().getString("father"), System.currentTimeMillis() + "", SceneDirectionFragment.this, getArguments().getString("caseId"));
                                }
                            }).start();
                        }

                        @Override
                        public void onMapScreenShot(Bitmap bitmap, int i) {

                        }
                    });
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }

        addMapCaptrueAdapter.setMode(mode);

        return view;
    }

    private void captureMapScreen() {
        aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
            @Override
            public void onMapScreenShot(final Bitmap bitmap) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //reCanvasBitmap(Utils.convertToBlackWhite(bitmap));
                        BitmapUtils.saveBitmap("map", getActivity(), reCanvasBitmap(Utils.convertToBlackWhite(bitmap)), System.currentTimeMillis() + "",
                                getArguments().getString("father"), System.currentTimeMillis() + "", SceneDirectionFragment.this, getArguments().getString("caseId"));
                    }
                }).start();
            }

            @Override
            public void onMapScreenShot(Bitmap bitmap, int i) {

            }
        });
    }

    private Bitmap[] reCanvasBitmap(Bitmap bp) {
        Bitmap[] resultset = new Bitmap[2];
        int width = bp.getWidth();
        int height = bp.getHeight();
        Bitmap result = bp;
        Bitmap loc = BitmapFactory.decodeResource(getResources(), R.drawable.scene_direction_loc);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(loc, (width - loc.getWidth()) / 2, (height - loc.getHeight()) / 2, new Paint());
        loc.recycle();
        Bitmap compass = BitmapFactory.decodeResource(getResources(), R.drawable.compass);
        Bitmap location_sample = BitmapFactory.decodeResource(getResources(), R.drawable.location_sample);
        String orgName = mShare.getString("organizationCname", "");
        String userName = mShare.getString("user_name", "");
        Calendar cal = Calendar.getInstance();
        String time = cal.get(Calendar.YEAR) + "年" + (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DATE) + "日";
        Date occurrenceDateFrom = caseInfo.getOccurrenceDateFrom();
        StringBuffer title = new StringBuffer();
        if (occurrenceDateFrom != null) {
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy年MM月dd日");
            title.append(dateFormater.format(occurrenceDateFrom) + "    ");
        }
        title.append(caseInfo.getSceneDetail());
        title.append(caseInfo.getAlarmCategoryName());
        resultset[0]= result;
        resultset[1] = drawNewBitmap(canvas, result, compass, location_sample, title.toString(), 36, "宋体", new String[]{orgName, userName, time}, 16);
        return resultset;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recordFileInfos.size() != getMapFiles().size()) {
            mapGrid.setAdapter(addMapCaptrueAdapter = new AddMapCaptrueAdapter(recordFileInfos = getMapFiles(), new AddMapCaptrueAdapter.takeMapCapture() {
                @Override
                public void takeCapture() {
                    aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
                        @Override
                        public void onMapScreenShot(final Bitmap bitmap) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    BitmapUtils.saveBitmap("map", getActivity(), reCanvasBitmap(bitmap), System.currentTimeMillis() + "", getArguments().getString("father"), System.currentTimeMillis() + "", SceneDirectionFragment.this, getArguments().getString("caseId"));
                                    //BitmapUtils.saveBitmap("map", getActivity(), bitmap, System.currentTimeMillis() + "", getArguments().getString("father"), System.currentTimeMillis() + "", SceneDirectionFragment.this, getArguments().getString("caseId"));
                                }
                            }).start();
                        }

                        @Override
                        public void onMapScreenShot(Bitmap bitmap, int i) {

                        }
                    });
                }
            }));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        save();
    }

    @Override
    public void onAnimationStart(Animation animation) {
        centerImageView.setImageResource(R.drawable.scene_direction_loc);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        centerImageView.setImageResource(R.drawable.scene_direction_loc);
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getActivity().getApplicationContext());
            mLocationOption = new AMapLocationClientOption();
            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            this.amapLocation = amapLocation;
            if (amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                if (!lastRefreshCityCode.equals(amapLocation.getCityCode())) {
                    OfflineMapManager offlineMapManager = new OfflineMapManager(getActivity(), this);
                    try {
                        offlineMapManager.downloadByCityCode(amapLocation.getCityCode());
                    } catch (AMapException e) {
                        e.printStackTrace();
                    }
                    lastRefreshCityCode = amapLocation.getCityCode();
                }

                List<RecordFileInfo> listRecordFileInfo = getMapFiles();
                if (listRecordFileInfo == null || listRecordFileInfo.size() == 0) {
                    if (mLat == 0
                            || Math.abs(mLat - amapLocation.getLatitude()) > 0.0001
                            || Math.abs(mLong - amapLocation.getLongitude()) > 0.0001) {
                        mLat = amapLocation.getLatitude();
                        mLong = amapLocation.getLongitude();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                captureMapScreen();
                            }
                        }, 1000);
                    }
                }
                Log.e("location", "loc : " + amapLocation.toStr());
                SharePre userInfo = new SharePre(getActivity(), "user_info", Context.MODE_PRIVATE);
                String address = amapLocation.getAddress();
                userInfo.put("address", address);
                userInfo.commit();
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);

            }

        }
    }


    private List<RecordFileInfo> getMapFiles() {
        return EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "father = '" + getArguments().getString("father") + "' and caseId = \"" + getArguments().getString("caseId") + "\"", "fileDate desc");
    }

    @Override
    public void onDownload(int i, int i1, String s) {
        Log.d("downloading.....", i + i1 + s);
    }

    @Override
    public void onCheckUpdate(boolean b, String s) {

    }

    @Override
    public void onRemove(boolean b, String s, String s1) {

    }

    @Override
    public void onSuccess(RecordFileInfo recordFileInfo) {
        recordFileInfos.add(0, recordFileInfo);
        Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                addMapCaptrueAdapter.notifyDataSetChanged();
                return false;
            }
        });
        handler.obtainMessage().sendToTarget();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (lastMaker != null)
            lastMaker.remove();
//        addMarkersToMap(latLng);
    }


    private void addMarkersToMap(LatLng latLng) {
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        markerOption.perspective(true);
        Marker marker = aMap.addMarker(markerOption);
        marker.showInfoWindow();
        lastMaker = marker;
    }


    private void save() {
        for (RecordFileInfo recordFileInfo : getMapFiles()) {
            PicturesData picturesData = getPictureData(recordFileInfo.getAttachmentId(), recordFileInfo.getRefKeyId(),
                    recordFileInfo.getPhotoType(),//现场照片类型
                    recordFileInfo.getPhotoTypeName(),//场照片类型名称
                    recordFileInfo.getPhotoId(),
                    recordFileInfo.getPhotoName(),
                    recordFileInfo.getSceneType(),
                    recordFileInfo.getContractionsFilePath(),
                    recordFileInfo.getTwoHundredFilePath(),
                    recordFileInfo.getFilePath(),
                    recordFileInfo.getDescription());
            String picDataJson = JSON.toJSONString(picturesData);
            try {
                JSONObject jsonObject1 = new JSONObject(picDataJson);

                jsonObject1.put("INVESTIGATION_ID", getArguments().getString("caseId"));
                jsonObject1.put("PHOTO_NAME", recordFileInfo.getPhotoName());
                jsonObject1.put("PICTURE_TYPE", "1080");
                jsonObject1.put("PICTURE_TYPE_NAME", "方位示意图");
                jsonObject1.put("PICTURE_ID", recordFileInfo.getAttachmentId());
                jsonObject1.put("DESCRIPTION", "");
                jsonObject1.put("DELETE_FLAG", "");
                jsonObject1.put("CREATE_USER_NAME", "");
                jsonObject1.put("CREATE_DATETIME", "");
                jsonObject1.put("filePath", recordFileInfo.getFilePath());
                jsonObject1.put("twoHundredFilePath", recordFileInfo.getFilePath());
                jsonObject1.put("contractionsFilePath", recordFileInfo.getFilePath());
                DataTemp picDataTemp = SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getAttachmentId() + "picData");
                picDataTemp.setDataType("scene_investigation_data");
                // picDataTemp.setData(picDataJson);
                picDataTemp.setData(jsonObject1.toString());
                EvidenceApplication.db.update(picDataTemp);

                JSONObject jsonObject = new JSONObject(JSON.toJSONString(recordFileInfo));
                jsonObject.put("filePath", recordFileInfo.getFilePath());
                jsonObject.put("twoHundredFilePath", recordFileInfo.getFilePath());
                jsonObject.put("contractionsFilePath", recordFileInfo.getFilePath());
                jsonObject.remove("ID");
                DataTemp recDataTemp = SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getAttachmentId() + "recData");
                recDataTemp.setDataType("common_attachment");

                recDataTemp.setData(jsonObject.toString());
                EvidenceApplication.db.update(recDataTemp);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            //产生附件DATA
            String timeStamp = new SimpleDateFormat("yyyyMMdd")
                    .format(new Date());
            String recJson = JSON.toJSONString(recordFileInfo);
        }
    }

    private PicturesData getPictureData(String attachId, String refKeyId, String photoType,
                                        String photoTypeName, String photoId, String photoName, String sceneType,
                                        String contractionsFilePath,
                                        String twoHundredFilePath,
                                        String filePath,
                                        String description) {
        List<PicturesData> list = EvidenceApplication.db.findAllByWhere(PicturesData.class,
                "ATTACHMENT_ID = '" + attachId + "'");
        Log.d("TAGXX1", list.size() + "");
        if (list == null || list.size() == 0) {
            // if(photoType.equals("blind")) {
            PicturesData picturesData = new PicturesData();
            picturesData.setId(refKeyId);
            picturesData.setATTACHMENT_ID(attachId);
            Log.d("TAGXX1", picturesData.getId() + "");

            picturesData.setSCENE_TYPE(sceneType);

            picturesData.setType("0");//0：图片；1：视频；2：音频）
            picturesData.setPhotoTypeName(photoTypeName);
            picturesData.setPhotoType(photoType);
            picturesData.setPhotoId(attachId);
            picturesData.setPhotoName(photoName);
            picturesData.setDescription(description);

            List<PicturesData> listrefKeyId = EvidenceApplication.db.findAllByWhere(PicturesData.class,
                    "id = '" + refKeyId + "'");
            if (listrefKeyId.size() > 0) {
                EvidenceApplication.db.update(picturesData);
            } else {
                EvidenceApplication.db.save(picturesData);
            }
            // }
        }
        return EvidenceApplication.db.findAllByWhere(PicturesData.class,
                "ATTACHMENT_ID = '" + attachId + "'").get(0);
    }

    /**
     * 获取字符串宽度,高度
     */
    public static int[] getStringWidth(String s, String textStyle, int textSize) {
        int[] widthandheight = new int[2];
        TextPaint font = new TextPaint();
        font.setTextSize(36);
        font.setTypeface(Typeface.DEFAULT_BOLD);
        Paint.FontMetrics fm = font.getFontMetrics();
        widthandheight[0] = (int) font.measureText(s);
        widthandheight[1] = (int) (fm.bottom - fm.top);
        return widthandheight;
    }


    /****
     *
     * @param graphics
     * @param srcbitmap
     * @param compassImg
     * @param locatSampleImage
     * @param title
     * @param titleSize
     * @param stringStyle
     * @param gridContext
     * @param gridContextSize
     * @return
     */
    public static Bitmap drawNewBitmap(Canvas graphics, Bitmap srcbitmap, Bitmap compassImg, Bitmap locatSampleImage, String title, int titleSize, String stringStyle, String[] gridContext, int gridContextSize) {
        int height = 841; // 851
        Bitmap result = Bitmap.createBitmap(DESTINATION_IMG_WIDTH, DESTINATION_IMG_HEIGHT, Bitmap.Config.RGB_565);
        graphics.setBitmap(result);
        Paint mypaint = new Paint();
        mypaint.setColor(Color.WHITE);
        mypaint.setStyle(Paint.Style.FILL);
        graphics.drawRect(0, 0, DESTINATION_IMG_WIDTH, DESTINATION_IMG_HEIGHT, mypaint);
        mypaint.setColor(Color.BLACK);
        graphics.drawRect(129, 141, 1640, 1123, mypaint);
        mypaint.setColor(Color.WHITE);
        graphics.drawRect(138, 150, 1631, 1114, mypaint);
        mypaint.setColor(Color.BLACK);
        int[] title_rec = getStringWidth(title, stringStyle, titleSize);
        mypaint.setTextSize(titleSize);
        mypaint.setTypeface(Typeface.DEFAULT);

        if (title_rec[0] >= 2800) {

        } else if (title_rec[0] >= 1400) {
            int length = title.length();
            String title1 = title.substring(0, (int) (0.7 * length));
            String title2 = title.substring(title1.length(), length);
            graphics.drawText(title1, (DESTINATION_IMG_WIDTH - getStringWidth(title1, stringStyle, titleSize)[0]) / 2, 221, mypaint);
            graphics.drawText(title2, (DESTINATION_IMG_WIDTH - getStringWidth(title2, stringStyle, titleSize)[0]) / 2, 277, mypaint);
        } else {
            graphics.drawText(title, (DESTINATION_IMG_WIDTH - title_rec[0]) / 2, 193, mypaint);
        }
        graphics.drawBitmap(srcbitmap, null, new Rect(176, 333, 1222, 1076), mypaint);
        graphics.drawBitmap(compassImg, null, new Rect(1393, 349, 1477, 475), mypaint);

        String markOrg = gridContext[0];
        int[] markOrgWidth = getStringWidth(markOrg, stringStyle, gridContextSize);
        if (markOrgWidth[0] <= 280) {
            graphics.drawBitmap(locatSampleImage, null, new Rect(1244, height, 1244 + locatSampleImage.getWidth(), height + locatSampleImage.getHeight()), null);
            //draw gridview
            graphics.drawLine(GRIDVIEW_X_Y[0][0], GRIDVIEW_X_Y[0][1], GRIDVIEW_X_Y[0][2], GRIDVIEW_X_Y[0][3], mypaint);
            graphics.drawLine(GRIDVIEW_X_Y[1][0], GRIDVIEW_X_Y[1][1], GRIDVIEW_X_Y[1][2], GRIDVIEW_X_Y[1][3], mypaint);
            graphics.drawLine(GRIDVIEW_X_Y[2][0], GRIDVIEW_X_Y[2][1], GRIDVIEW_X_Y[2][2], GRIDVIEW_X_Y[2][3], mypaint);
            graphics.drawLine(GRIDVIEW_X_Y[3][0], GRIDVIEW_X_Y[3][1], GRIDVIEW_X_Y[3][2], GRIDVIEW_X_Y[3][3], mypaint);
            graphics.drawLine(GRIDVIEW_X_Y[4][0], GRIDVIEW_X_Y[4][1], GRIDVIEW_X_Y[4][2], GRIDVIEW_X_Y[4][3], mypaint);
            graphics.drawLine(GRIDVIEW_X_Y[5][0], GRIDVIEW_X_Y[5][1], GRIDVIEW_X_Y[5][2], GRIDVIEW_X_Y[5][3], mypaint);
            graphics.drawLine(GRIDVIEW_X_Y[6][0], GRIDVIEW_X_Y[6][1], GRIDVIEW_X_Y[6][2], GRIDVIEW_X_Y[6][3], mypaint);

            //draw gridview text
            mypaint.setTypeface(Typeface.DEFAULT);
            mypaint.setTextSize(gridContextSize);
            graphics.drawText("制图单位", 1254, 988, mypaint);
            graphics.drawText("制图人", 1254, 1024, mypaint);
            graphics.drawText("制图时间", 1254, 1060, mypaint);
            graphics.drawText(markOrg, 1333, 988, mypaint);
            graphics.drawText(gridContext[1], 1333, 1024, mypaint);
            graphics.drawText(gridContext[2], 1333, 1060, mypaint);
        } else {
            graphics.drawBitmap(locatSampleImage, null, new Rect(1244, height, 1244 + locatSampleImage.getWidth(), height + locatSampleImage.getHeight()), mypaint);
            //draw gridview
            graphics.drawLine(GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[0][0], GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[0][1],
                    GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[0][2], GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[0][3], mypaint);
            graphics.drawLine(GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[1][0], GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[1][1],
                    GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[1][2], GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[1][3], mypaint);
            graphics.drawLine(GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[2][0], GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[2][1],
                    GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[2][2], GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[2][3], mypaint);
            graphics.drawLine(GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[3][0], GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[3][1],
                    GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[3][2], GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[3][3], mypaint);
            graphics.drawLine(GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[4][0], GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[4][1],
                    GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[4][2], GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[4][3], mypaint);
            graphics.drawLine(GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[5][0], GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[5][1],
                    GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[5][2], GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[5][3], mypaint);
            graphics.drawLine(GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[6][0], GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[6][1],
                    GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[6][2], GRIDVIEW_ORG_DOUBLE_HEIGHT_X_Y[6][3], mypaint);

            //draw gridview text
            mypaint.setTypeface(Typeface.DEFAULT);
            mypaint.setTextSize(gridContextSize);
            graphics.drawText("制图单位", 1254, 971, mypaint);
            graphics.drawText("制图人", 1254, 1024, mypaint);
            graphics.drawText("制图时间", 1254, 1060, mypaint);
            int index = (int) ((280 * 1.0f / markOrgWidth[0]) * markOrg.length());
            graphics.drawText(markOrg.substring(0, index), 1333, 954, mypaint);
            graphics.drawText(markOrg.substring(index, markOrg.length()), 1333, 988, mypaint);
            graphics.drawText(gridContext[1], 1333, 1024, mypaint);
            graphics.drawText(gridContext[2], 1333, 1060, mypaint);
        }
        return result;
    }

}

