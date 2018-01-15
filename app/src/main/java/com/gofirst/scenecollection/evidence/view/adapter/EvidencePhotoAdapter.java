package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bm.library.PhotoView;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author maxiran
 */
public class EvidencePhotoAdapter extends PagerAdapter {


    private List<RecordFileInfo> fileList;
    private List<ImageView> list = new ArrayList<>();

    public EvidencePhotoAdapter(List<RecordFileInfo> imageList, Context context) {
        this.fileList = imageList;
        for (int i = 0; i < imageList.size(); i ++){
            PhotoView imageView = new PhotoView(context);
            imageView.enable();
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.animaFrom(imageView.getInfo());
            list.add(imageView);
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
        container.removeView(list.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(list.get(position));
        list.get(position).setImageBitmap(BitmapFactory.decodeFile(AppPathUtil.getDataPath() + "/" + fileList.get(position).getFilePath()));
        return list.get(position);
    }
}
