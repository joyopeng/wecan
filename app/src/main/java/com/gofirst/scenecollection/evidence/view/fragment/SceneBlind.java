package com.gofirst.scenecollection.evidence.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.PublicMsg;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.adapter.SceneBlindAdapter;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/29.
 */
public class SceneBlind extends Fragment implements View.OnClickListener{
    private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private int currIndex;
    private Fragment unClassPictures,aLLPictures;
    private TextView general_tab,key_tab;
    private String caseId;
    private String father;
    private String belongTo;
    private String mode;
    private boolean isAddRec;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
//		System.out.println("OneFragment  onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.scene_blind, container, false);
        caseId = getArguments().getString("caseId");
        father = getArguments().getString("father");
        belongTo= getArguments().getString("belongTo");
        mode= getArguments().getString("mode");
        isAddRec = getArguments().getBoolean(BaseView.ADDREC);
        Log.d("taofacaseId",caseId);
        InitText(view);
        InitViewPager(view);
        return view;
    }

    private void InitText(View view){
        general_tab=(TextView)view.findViewById(R.id.general_tab);
        general_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));
        key_tab=(TextView)view.findViewById(R.id.key_tab);
        general_tab.setOnClickListener(new MyOnClickListener(0));
        key_tab.setOnClickListener(new MyOnClickListener(1));


    }

    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
            Log.d("index",""+index);
        }
    };
    private void InitViewPager(View parentView) {
        mPager = (ViewPager) parentView.findViewById(R.id.vPager);
        fragmentsList = new ArrayList<Fragment>();
        unClassPictures = new UnClassPictures();
        //unClassPicturesNew=new UnClassPictureNew();
        aLLPictures = new ALLPictures();
        Bundle bundle=new Bundle();
        bundle.putString("caseId",caseId);
        bundle.putString("father",father);
        bundle.putString("belongTo",belongTo);
        bundle.putString("mode",isAddRec ? BaseView.EDIT : mode);
        bundle.putBoolean(BaseView.ADDREC,isAddRec);
        bundle.putString("templateId",getArguments().getString("templateId"));
        unClassPictures.setArguments(bundle);
        aLLPictures.setArguments(bundle);
        //unClassPicturesNew.setArguments(bundle);
        if(mode != null && mode.equals(BaseView.VIEW) && !isAddRec){
            general_tab.setVisibility(View.GONE);
            key_tab.setVisibility(View.GONE);
            fragmentsList.add(aLLPictures);
        }else{
            fragmentsList.add(unClassPictures);
            fragmentsList.add(aLLPictures);
        }
        //fragmentsList.add(unClassPictures);
        //fragmentsList.add(unClassPicturesNew);
        mPager.setAdapter(new SceneBlindAdapter(getChildFragmentManager(), fragmentsList));
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mPager.setCurrentItem(0);
        mPager.setOffscreenPageLimit(1);

    }

    @Override
    public void onClick(View v) {
//        generalPicture,keyPicture,detailPicture,otherPicture;
//        all_tab,general_tab,key_tab,detail_tab,other_tab
        switch (v.getId()) {

            //上面的tab
            case R.id.general_tab:
                general_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));
                key_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                break;

            case R.id.key_tab:
                general_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                key_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));
                PublicMsg.belongTo = "all";
                break;
        }

        }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            Log.d("arg0", "" + arg0);
            currIndex = arg0;
            Log.d("currIndex",""+currIndex);
            switch (currIndex) {
                case 0:
                    general_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    key_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                    break;
                case 1:
                    general_tab.setBackgroundColor(Color.parseColor("#D0D8DF"));
                    key_tab.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
}
