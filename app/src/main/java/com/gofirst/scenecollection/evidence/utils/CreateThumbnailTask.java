package com.gofirst.scenecollection.evidence.utils;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.gofirst.scenecollection.evidence.model.RecordFileInfo;

/**
 * 异步线程池创建视频缩略图
 * @author maxiran
 */
public class CreateThumbnailTask extends AsyncTask<RecordFileInfo,Integer,Bitmap>{

    private ImageView imageView;
    private String obb;
    public CreateThumbnailTask(ImageView imageView, String obb) {
        this.imageView = imageView;
        this.obb = obb;
    }

    @Override
    protected Bitmap doInBackground(RecordFileInfo... params) {
        return getVideoThumb(obb,params[0],55,55);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        imageView.setImageBitmap(bitmap);
    }

    public static Bitmap getVideoThumb(String obb,RecordFileInfo videoPath, int width, int height){
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(obb + "/" +videoPath.getFilePath(), MediaStore.Images.Thumbnails.MINI_KIND);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap,4*width, 4*height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}
