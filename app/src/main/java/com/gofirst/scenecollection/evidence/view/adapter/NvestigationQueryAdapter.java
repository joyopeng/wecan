package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/6.
 */
public class NvestigationQueryAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> list;
     ImageView iv;
    Context mContext;

    public NvestigationQueryAdapter(FragmentManager fm) {
        super(fm);
    }



    public NvestigationQueryAdapter(FragmentManager fm,
                           ArrayList<Fragment> list) {
        super(fm);
        this.list = list;

    }



    @Override
    public Fragment getItem(int arg0) {
        // TODO Auto-generated method stub
        return list.get(arg0);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


}