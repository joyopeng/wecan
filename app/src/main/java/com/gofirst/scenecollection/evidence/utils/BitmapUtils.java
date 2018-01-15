package com.gofirst.scenecollection.evidence.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BitmapUtils {
    public static int max = 0;

    public static ArrayList<ImageItem> tempSelectBitmap = new ArrayList<ImageItem>();   //选择的图片的临时列表
    public static HashMap<String, Boolean> mHashMap = new HashMap<String, Boolean>();
    public static ArrayList<String> drr = new ArrayList<String>();

    public static Bitmap revitionImageSize(String path) {
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(
                    new File(path)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        int i = 0;
        Bitmap bitmap = null;
        while (true) {
            if ((options.outWidth >> i <= 1000)
                    && (options.outHeight >> i <= 1000)) {
                try {
                    in = new BufferedInputStream(
                            new FileInputStream(new File(path)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                options.inSampleSize = (int) Math.pow(2.0D, i);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(in, null, options);
                break;
            }
            i += 1;
        }
        return bitmap;
    }

    public static void saveBitmap(String belong, Context context, Bitmap bm, String name, String father, String child, OnSaveSuccessListener listener, String caseId) {
        //File f = new File(context.getObbDir() + "/note");
        String timeStamp = new SimpleDateFormat("yyyyMMdd")
                .format(new Date());
        String pathTemp="";
        File dir = new File(AppPathUtil.getDataPath() + "/"+timeStamp+"/" + caseId);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File f = new File(AppPathUtil.getDataPath() + "/"+timeStamp+ "/" + caseId + "/note");
        pathTemp=timeStamp+ "/" + caseId + "/note"+"/";
        File filepath;
        if (!f.exists()) {
            f.mkdirs();
        }
        try {
            FileOutputStream out = new FileOutputStream(filepath = new File(f, name + ".png"));
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            RecordFileInfo saveNote = getRecordFileInfo(caseId,child);
            saveNote.setFather(father);
            saveNote.setPictureType("map");
            saveNote.setPhotoType(belong);
            saveNote.setFilePath(pathTemp + name + ".png");
            saveNote.setTwoHundredFilePath(pathTemp+name+".png");
            saveNote.setFileType("png");
            saveNote.setFileDate(new Date());
            saveNote.setType("0");
            saveNote.setPhotoName(name+".png");
            saveNote.setSceneType(father);
            String photoId="";
            photoId=ViewUtil.getUUid();
            saveNote.setPhotoId(photoId);
            saveNote.setRefKeyId(photoId);

            EvidenceApplication.db.update(saveNote);
            if (listener != null) {
                listener.onSuccess(saveNote);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public interface OnSaveSuccessListener {
        void onSuccess(RecordFileInfo recordFileInfo);
    }

    public static RecordFileInfo getRecordFileInfo(String caseId, String child) {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" + caseId + "' and child = '" + child + "'");
        if (list == null || list.size() == 0) {
            RecordFileInfo saveNote = new RecordFileInfo();
            String id=ViewUtil.getUUid();
            saveNote.setId(id);
            saveNote.setCaseId(caseId);
            saveNote.setChild(child);
            saveNote.setAttachmentId(id);
            EvidenceApplication.db.save(saveNote);
        }
        return EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" + caseId + "' and child = '" + child + "'").get(0);
    }

    //图片按比例大小压缩方法（根据Bitmap图片压缩）
    //flag 0 200K图片设置(2400 * 1600)，flag 1 缩络图片设置8k(200 * 150)，
    public static Bitmap comp(Bitmap image, int flag) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = 4;//设置缩放比例
       /* newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收*/

        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap, 300);//压缩好比例大小后再进行质量压缩
    }

    /**
     * @param bitmap     原图
     * @param edgeLength 希望得到的正方形部分的边长
     * @return 缩放截取正中部分后的位图。
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
        if (null == bitmap || edgeLength <= 0) {
            return null;
        }

        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if (widthOrg > edgeLength && heightOrg > edgeLength) {
            //压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = (int) (edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;

            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            } catch (Exception e) {
                return null;
            }

            //从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;

            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
                scaledBitmap.recycle();
            } catch (Exception e) {
                return null;
            }
        }

        return compressImage(result,8);
    }

    public static Bitmap compressImage(Bitmap image, int length) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 >= length) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//
            if (options < 1)
                break;
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 1;//每次都减少1
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        return  BitmapFactory.decodeStream(isBm, null, null);
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
}
