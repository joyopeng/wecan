package com.gofirst.scenecollection.evidence.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;


public class NewestStateFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentsList;

    public NewestStateFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public NewestStateFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragmentsList = fragments;
    }

    @Override
    public int getCount() {

        return fragmentsList.size();
    }

    @Override
    public Fragment getItem(int arg0) {

        return fragmentsList.get(arg0);
    }

    @Override
    public int getItemPosition(Object object) {

        return super.getItemPosition(object);
    }

}
