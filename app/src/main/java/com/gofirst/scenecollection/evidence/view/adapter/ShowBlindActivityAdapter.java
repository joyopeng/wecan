package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bm.library.PhotoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/11.
 */
public class ShowBlindActivityAdapter extends PagerAdapter {


    private List<File> fileList;
    private List<ImageView> imageViewList = new ArrayList<>();

    public ShowBlindActivityAdapter(List<File> imageList, Context context) {
        PhotoView imageView;
        this.fileList = imageList;
        for (File file : fileList){
            imageView = new PhotoView(context);
            imageView.enable();
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.animaFrom(imageView.getInfo());
            imageViewList.add(imageView);
        }
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(imageViewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(imageViewList.get(position));
       /* Bitmap bit=BitmapFactory.decodeFile(fileList.get(position).getPath());
        Bitmap afterBitmap = Bitmap.createBitmap(bit.getWidth(),
                bit.getHeight(), bit.getConfig());
        Canvas canvas = new Canvas(afterBitmap);
        Matrix matrix = new Matrix();
        // 根据原图的中心位置旋转
        matrix.setRotate(180, bit.getWidth() / 2,
                bit.getHeight() / 2);
        canvas.drawBitmap(bit, matrix, null);
*/
        //imageViewList.get(position).setImageBitmap(BitmapFactory.decodeFile(AppPathUtil.getDataPath() + "/" + fileList.get(position).getPath()));
        imageViewList.get(position).setImageBitmap(BitmapFactory.decodeFile(fileList.get(position).getPath()));
        return imageViewList.get(position);
    }



    /**
     2      * 图片旋转
     3      */

}
