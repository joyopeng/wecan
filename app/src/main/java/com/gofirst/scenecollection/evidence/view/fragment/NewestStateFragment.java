package com.gofirst.scenecollection.evidence.view.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.adapter.NewestStateFragmentPagerAdapter;

import java.util.ArrayList;

public class NewestStateFragment extends Fragment {

    Resources resources;
    private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private ImageView ivBottomLine;
    private TextView AllTab, MyTab,CompleteTab;

    private int currIndex = 0;
    private int bottomLineWidth;
    private int offset = 0;
    private int position_one;
    public final static int num = 3 ;
    Fragment AllNewestStateFragment,MyNewestStateFragment,CompleteFragment;

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
        View view = inflater.inflate(R.layout.neweststatefragment, container, false);
        resources = getResources();
        InitWidth(view);
        InitTextView(view);
        InitViewPager(view);
        TranslateAnimation animation = new TranslateAnimation(position_one, offset, 0, 0);
//		AllTab.setTextColor(resources.getColor(R.color.milky_white));
        animation.setFillAfter(true);
        animation.setDuration(300);
        ivBottomLine.startAnimation(animation);

        return view;
    }


    private void InitTextView(View parentView) {
        AllTab = (TextView) parentView.findViewById(R.id.alltab);
        MyTab = (TextView) parentView.findViewById(R.id.mytab);
        CompleteTab = (TextView) parentView.findViewById(R.id.completetab);

        AllTab.setOnClickListener(new MyOnClickListener(0));
        MyTab.setOnClickListener(new MyOnClickListener(1));
        CompleteTab.setOnClickListener(new MyOnClickListener(2));
        AllTab.setBackgroundResource(R.color.white);
        MyTab.setBackgroundResource(R.color.gray_blue);
        CompleteTab.setBackgroundResource(R.color.gray_blue);
    }

    private void InitViewPager(View parentView) {
        mPager = (ViewPager) parentView.findViewById(R.id.vPager);
        fragmentsList = new ArrayList<Fragment>();

        AllNewestStateFragment = new AllNewestFragment();
        MyNewestStateFragment = new MyNewestStateFragment();
        CompleteFragment = new CompleteNewestStateFragment();


        fragmentsList.add(AllNewestStateFragment);
        fragmentsList.add(MyNewestStateFragment);
        fragmentsList.add(CompleteFragment);

        mPager.setAdapter(new NewestStateFragmentPagerAdapter(getChildFragmentManager(), fragmentsList));
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mPager.setCurrentItem(0);

    }

    private void InitWidth(View parentView) {
        ivBottomLine = (ImageView) parentView.findViewById(R.id.iv_bottom_line);
        bottomLineWidth = ivBottomLine.getLayoutParams().width;
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (int) ((screenW / num - bottomLineWidth) / 3);
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
            Log.d("index",""+index);
        }
    };

    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            Log.d("arg0",""+arg0);
            switch (arg0) {

                /*case 0:
                    if (currIndex == 2) {
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
                    break;*/
            }
            currIndex = arg0;
            Log.d("currIndex",""+currIndex);
            switch (currIndex) {
                case 0:
                    AllTab.setBackgroundResource(R.color.white);
                    MyTab.setBackgroundResource(R.color.gray_blue);
                    CompleteTab.setBackgroundResource(R.color.gray_blue);
                    break;
                case 1:
                    AllTab.setBackgroundResource(R.color.gray_blue);
                    MyTab.setBackgroundResource(R.color.white);
                    CompleteTab.setBackgroundResource(R.color.gray_blue);
                    break;
                case 2:
                    AllTab.setBackgroundResource(R.color.gray_blue);
                    MyTab.setBackgroundResource(R.color.gray_blue);
                    CompleteTab.setBackgroundResource(R.color.white);
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

