package com.gofirst.scenecollection.evidence.view.activity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {


    private Camera camera = null;
    private SurfaceHolder surfaceHolder = null;
    private int cameraCount,maxZoom;

    public MySurfaceView(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public MySurfaceView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            camera.cancelAutoFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

        //实现自动对焦
      /*  camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if(success){*/
        //根本没有可处理的SurfaceView
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        //先停止Camera的预览
        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //这里可以做一些我们要做的变换。
        // 如果相机资源并不为空
        if (camera != null) {
            // 获得相机参数对象
            Camera.Parameters parameters = camera.getParameters();
            // 获取最合适的参数，为了做到拍摄的时候所见即所得，我让previewSize和pictureSize相等
            Camera.Size previewSize = getOptimalPreviewSize(parameters
                    .getSupportedPreviewSizes(), 2656, 1494);
            Camera.Size pictureSize = getOptimalPictureSize(parameters
                    .getSupportedPictureSizes(), 5120, 3840);
            System.out
                    .println("---------------------------------------------------------------");
            System.out.println("previewSize: " + previewSize.width + ", "
                    + previewSize.height);
            System.out.println("pictureSize: " + pictureSize.width + ", "
                    + pictureSize.height);
            // 设置照片格式
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            // 设置图片保存时候的分辨率大小
            maxZoom = parameters.getMaxZoom();//60
            //parameters.setZoom(10);
            parameters.setPictureFormat(PixelFormat.JPEG);

            // 设置预览大小
            try {
                parameters.setPreviewSize(previewSize.width,
                        previewSize.height);
                parameters.setPictureSize(pictureSize.width,
                        pictureSize.height);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            } catch (Exception e) {
                e.printStackTrace();

            }
            // 设置自动对焦，先进行判断
            // 给相机对象设置刚才设置的参数
            camera.setParameters(parameters);
            // 开始预览
            camera.startPreview();
            //camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上

        }
        //camera.autoFocus();//只有加上了这一句，才会自动对焦。
    }
          /*  }

        });
    }*/

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        /*if (camera==null)
            return;
        holder.removeCallback(this);
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.lock();
        camera.release();
        camera = null;*/

    }


   /* *//**
     * 得到最合适的PictureSize
     *
     * @param sizes
     * @param w
     * @param h
     * @return
     *//*
    public static Camera.Size getOptimalPictureSize(List<Camera.Size> sizes) {
        Camera.Size defSize = null;
        //for (int i = sizes.size() - 1; i >= 0; i--) {
        for (int i = sizes.size() - 1; i >=sizes.size() - 1; i--) {
            Log.d("sizes.size", "" + sizes.size());
            Camera.Size size = sizes.get(i);
            int w = size.width;
            int h = size.height;
            Log.d("w",""+w);
            Log.d("h",""+h);
            if (w >= 2656 && w <= 3264) {
                if (h >= 1494 && h <= 1840) {
                    return size;
                }
            }*//*if (w >= 5312 && w <= 5312) {
                if (h >= 2988 && h <= 2988) {
                    return size;
                }
            }*//*

            *//*if (w >= 1600 && w <= 2656) {
                if (h >= 1200 && h <= 1494) {
                    return size;
                }
            }else{
                size.width=1494;
                size.height=2656;
                return size;
            }*//*

            defSize = size;
        }
        return defSize;

    }*/



    /////

    /**
     * 得到最适合的预览大小
     * @param sizes
     * @param w
     * @param h
     * @return
     */
    public static Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            Log.d("PictureSize","w = " + size.width +" h = " + size.height + " radio = " + + (float)size.width/(float) size.height);
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


    /**
     * 得到最合适的PictureSize
     * @param sizes
     * @param w
     * @param h
     * @return
     */
    public static Camera.Size getOptimalPictureSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            Log.d("PictureSize","w = " + size.width +" h = " + size.height + " radio = " + (float)size.width/(float) size.height);
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }







}
