package com.gofirst.scenecollection.evidence.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.view.adapter.NvestigationQueryAdapter;
import com.gofirst.scenecollection.evidence.view.fragment.AllQueryFragment;
import com.gofirst.scenecollection.evidence.view.fragment.ProspectCompleteFragment;
import com.gofirst.scenecollection.evidence.view.fragment.ProspectingFragment;
import com.gofirst.scenecollection.evidence.view.fragment.UnpoliceRecordsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/6.
 */
public class NvestigationQuery extends FragmentActivity {

    private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private ImageView ivBottomLine,findData;
    private TextView allQuery, unpoliceRecords,prospecting,prospectComplete,findText;

    private int currIndex = 0;
    private int bottomLineWidth;
    private int offset = 0;
    private int position_one;
    public final static int num = 3 ;
    Fragment allQueryFragment,unpoliceRecordsFragment,prospectingFragment,prospectCompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nvestigation_query);
        InitWidth();
        InitTextView();
        InitViewPager();
        TranslateAnimation animation = new TranslateAnimation(position_one, offset, 0, 0);
//		AllTab.setTextColor(resources.getColor(R.color.milky_white));
        animation.setFillAfter(true);
        animation.setDuration(300);
        ivBottomLine.startAnimation(animation);
    }

    private void InitTextView( ) {
        allQuery = (TextView) findViewById(R.id.all_query);
        unpoliceRecords = (TextView) findViewById(R.id.unpolice_records);
        prospecting = (TextView) findViewById(R.id.prospecting);
        prospectComplete = (TextView) findViewById(R.id.prospect_complete);

        allQuery.setOnClickListener(new MyOnClickListener(0));
        unpoliceRecords.setOnClickListener(new MyOnClickListener(1));
        prospecting.setOnClickListener(new MyOnClickListener(2));
        prospectComplete.setOnClickListener(new MyOnClickListener(3));

        allQuery.setBackgroundResource(R.color.white);
        unpoliceRecords.setBackgroundResource(R.color.gray_blue);
        prospecting.setBackgroundResource(R.color.gray_blue);
        prospectComplete.setBackgroundResource(R.color.gray_blue);

        findText=(TextView)findViewById(R.id.find_textview);
        findData=(ImageView)findViewById(R.id.finddata);
        findData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(findText.getText().toString().equals("")){
                    Toast.makeText(NvestigationQuery.this,"情输入查询条件",Toast.LENGTH_SHORT).show();
                }else{
                    List<CsSceneCases> list=EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                            "id like '%" + findText.getText().toString() + "'");

                    Log.d("list",list.get(0).getExposureProcess()+"");

                }
            }
        });

    }

    private void InitViewPager( ) {
        mPager = (ViewPager) findViewById(R.id.vPager);
        fragmentsList = new ArrayList<Fragment>();

        allQueryFragment = new AllQueryFragment();
        unpoliceRecordsFragment = new UnpoliceRecordsFragment();
        prospectingFragment = new ProspectingFragment();
       prospectCompleteFragment= new ProspectCompleteFragment();

        fragmentsList.add(allQueryFragment);
        fragmentsList.add(unpoliceRecordsFragment);
        fragmentsList.add(prospectingFragment);
        fragmentsList.add(prospectCompleteFragment);

        mPager.setAdapter(new NvestigationQueryAdapter(getSupportFragmentManager(), fragmentsList));
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mPager.setCurrentItem(0);

    }

    private void InitWidth( ) {
        ivBottomLine = (ImageView) findViewById(R.id.iv_bottom_line);
        bottomLineWidth = ivBottomLine.getLayoutParams().width;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (int) ((screenW / num - bottomLineWidth) / 4);
        int avg = (int) (screenW / num);
        position_one = avg + offset;
    }


    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {

            index = i;
        }

        @Override
        public void onClick(View v) {

            mPager.setCurrentItem(index);
            Log.d("index", "" + index);
        }
    };

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
           /* switch (arg0) {

                case 0:
                    if (currIndex == 3) {
                        animation = new TranslateAnimation(position_one, offset, 0, 0);

                    }
                    break;
                case 1:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, position_one, 0, 0);

                    }
                    break;
                case 2:
                    if (currIndex == 1) {
                        animation = new TranslateAnimation(offset, position_one, 0, 0);

                    }
//					CompleteTab.setTextColor(resources.getColor(R.color.white));
                    break;
                case 3:
                    if (currIndex == 2) {
                        animation = new TranslateAnimation(offset, position_one, 0, 0);

                    }
//					CompleteTab.setTextColor(resources.getColor(R.color.white));
                    break;
            }*/
            currIndex = arg0;

            Log.d("currIndex",""+currIndex);
            switch (currIndex) {

                case 0:

                    allQuery.setBackgroundResource(R.color.white);
                    unpoliceRecords.setBackgroundResource(R.color.gray_blue);
                    prospecting.setBackgroundResource(R.color.gray_blue);
                    prospectComplete.setBackgroundResource(R.color.gray_blue);

                    break;
                case 1:
                    allQuery.setBackgroundResource(R.color.gray_blue);
                    unpoliceRecords.setBackgroundResource(R.color.white);
                    prospecting.setBackgroundResource(R.color.gray_blue);
                    prospectComplete.setBackgroundResource(R.color.gray_blue);
                    break;
                case 2:
                    allQuery.setBackgroundResource(R.color.gray_blue);
                    unpoliceRecords.setBackgroundResource(R.color.gray_blue);
                    prospecting.setBackgroundResource(R.color.white);
                    prospectComplete.setBackgroundResource(R.color.gray_blue);
                    break;
                case 3:
                    allQuery.setBackgroundResource(R.color.gray_blue);
                    unpoliceRecords.setBackgroundResource(R.color.gray_blue);
                    prospecting.setBackgroundResource(R.color.gray_blue);
                    prospectComplete.setBackgroundResource(R.color.white);
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
    public int getCurrentPagerIdx() {
        return currIndex;
    }

}
