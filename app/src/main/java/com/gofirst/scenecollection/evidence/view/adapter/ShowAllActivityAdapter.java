package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bm.library.PhotoView;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/4.
 */
public class ShowAllActivityAdapter extends PagerAdapter {


    private List<Map<String,File>> fileList;
    private List<ImageView> imageViewList = new ArrayList<>();

    public ShowAllActivityAdapter(List<Map<String,File>> imageList, Context context) {
        PhotoView imageView;
        this.fileList = imageList;
        //for (File file : fileList){
        for (int i=0;i<fileList.size();i++){
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
*/      String test=fileList.get(position).values().toString();
        String test1=test.substring(1, test.length() - 1);
        imageViewList.get(position).setImageBitmap(BitmapFactory.decodeFile(AppPathUtil.getDataPath()+"/"+test1));
        return imageViewList.get(position);
    }



    /**
     2      * 图片旋转
     3      */

}
