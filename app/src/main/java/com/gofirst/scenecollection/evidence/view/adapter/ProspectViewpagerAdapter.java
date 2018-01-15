package com.gofirst.scenecollection.evidence.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gofirst.scenecollection.evidence.view.easytagdragview.bean.SimpleTitleTip;
import com.gofirst.scenecollection.evidence.view.easytagdragview.bean.Tip;

import java.util.List;

public class ProspectViewpagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> FragmentList;
    private List<Tip> titles;

    public ProspectViewpagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        FragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int arg0) {
        return FragmentList.get(arg0);

    }

    @Override
    public int getCount() {
        if (FragmentList == null){
            return 0;
        }
        return FragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ((SimpleTitleTip) titles.get(position)).getTip();
    }

    public void setTitles(List<Tip> titles) {
        this.titles = titles;
    }
}
