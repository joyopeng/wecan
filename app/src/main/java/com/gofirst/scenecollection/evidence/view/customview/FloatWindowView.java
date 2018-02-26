package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;

import static android.content.Context.SENSOR_SERVICE;


public class FloatWindowView extends LinearLayout implements SensorEventListener {
    /**
     * 记录悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录悬浮窗的高度
     */
    public static int viewHeight;

    /**
     * 用于更新悬浮窗的位置
     */
    private WindowManager windowManager;

    /**
     * 悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;

    // 定义真机的Sensor管理器
    private SensorManager mSensorManager;

    private Context context;

    // 记录指南针图片转过的角度
    private float currentDegree = 0f;
    public static String direction = "正东";
    private String directionTemp = "";
    private String showContext = "";
    public static String rotationtemp = "clockwise";
    private TextView show;

    private OrientationEventListener mOrEventListener; // 设备方向监听器
    public static Boolean mCurrentOrientation = true; // 当前设备方向 横屏竖屏true,竖屏false

    public FloatWindowView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        View rootview = LayoutInflater.from(context).inflate(R.layout.floatwindow_layout, this);
        show = (TextView) rootview.findViewById(R.id.percent);
        viewWidth = 600;
        viewHeight = 180;
        this.context = context;
    }


    /**
     * 将悬浮窗的参数传入，用于更新悬浮窗的位置。
     *
     * @param params 悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mSensorManager.registerListener(FloatWindowView.this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        startOrientationChangeListener();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mSensorManager.unregisterListener(FloatWindowView.this);
        if (mOrEventListener != null) {
            mOrEventListener.disable();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 如果真机上触发event的传感器类型为水平传感器类型
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            // 获取绕Z轴转过的角度
            float degree = event.values[0];
            currentDegree = -degree;
            String s = Float.toString(degree);
            s = s.substring(0, s.indexOf('.'));
            int i = Integer.parseInt(s);
            if (rotationtemp.equals("counterclockwise")) {
                i = i + 90;
                if (i >= 360) {
                    i = i - 360;
                }
            } else if (rotationtemp.equals("clockwise")) {
                i = i - 90;
                if (i < 0) {
                    i = Math.abs(i);
                }
            }

            if ((i >= 338 && i <= 360) || (i >= 0 && i < 23)) {
                direction = "由南向北";
                show.setText("由南向北");
            } else if (i >= 23 && i < 68) {
                direction = "由西南向东北";
                show.setText("由西南向东北");
            } else if (i >= 68 && i < 113) {
                direction = "由西向东";
                show.setText("由西向东");
            } else if (i >= 113 && i < 158) {
                direction = "由西北向东南";
                show.setText("由西北向东南");
            } else if (i >= 158 && i < 203) {
                direction = "由北向南";
                show.setText("由北向南");
            } else if (i >= 203 && i < 248) {
                direction = "由东北向西南";
                show.setText("由东北向西南");
            } else if (i >= 248 && i < 293) {
                direction = "由东向西";
                show.setText("由东向西");
            } else if (i >= 293 && i < 338) {
                direction = "由东南向西北";
                show.setText("由东南向西北");
            }
            //           show.setText(s);

        }
    }


    private final void startOrientationChangeListener() {
        mOrEventListener = new OrientationEventListener(context) {
            @Override
            public void onOrientationChanged(int rotation) {

                if (((rotation >= 0) && (rotation <= 45)) || (rotation >= 315)
                        || ((rotation >= 135) && (rotation <= 225))) {// portrait
                    mCurrentOrientation = false;
                    rotationtemp = "1";
                } else if (((rotation > 45) && (rotation < 135))
                        || ((rotation > 225) && (rotation < 315))) {// landscape rotationtemp
                    Log.d("rotation", "" + rotation);
                    mCurrentOrientation = true;
                    if (((rotation > 45) && (rotation < 135))) {
                        rotationtemp = "clockwise";
                    } else if (((rotation > 225) && (rotation < 315))) {
                        rotationtemp = "counterclockwise";
                    }
                }
            }
        };
        mOrEventListener.enable();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v("aaaa","view key");
        return true;

    }
}
