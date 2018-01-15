package com.gofirst.scenecollection.evidence.view.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;

import com.gofirst.scenecollection.evidence.R;

import io.saeid.fabloading.LoadingView;

/**
 * 一款炫酷的加载页面
 *
 * @author maxiran
 */
public class UniversalLoadingView {

    private Dialog loadingDialog;
    private LoadingView loadingView;

    public UniversalLoadingView(Context context) {
        loadingDialog = new Dialog(context, R.style.LoadingDialog);
        loadingDialog.setContentView(R.layout.universal_loading_layout);
        loadingView = (LoadingView) loadingDialog.findViewById(R.id.loading_view);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingView.setRepeat(Integer.MAX_VALUE);
        loadingView.setDuration(500);
       /* boolean isLollipop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        final int marvel_1 = isLollipop ? R.drawable.loading : R.drawable.marvel_1;
        final int marvel_2 = isLollipop ? R.drawable.loading : R.drawable.marvel_2;
        final int marvel_3 = isLollipop ? R.drawable.loading : R.drawable.marvel_3;
        final int marvel_4 = isLollipop ? R.drawable.loading : R.drawable.marvel_4;*/
        final int marvel_1 = R.drawable.loading;
        final int marvel_2 = R.drawable.loading;
        final int marvel_3 = R.drawable.loading;
        final int marvel_4 = R.drawable.loading;

       /* loadingView.addAnimation(Color.parseColor("#FFD200"),marvel_1,
                LoadingView.FROM_LEFT);*/
        loadingView.addAnimation(Color.parseColor("#FFFFFF"), marvel_1,
                LoadingView.FROM_LEFT);
       /* loadingView.addAnimation(Color.parseColor("#2F5DA9"),marvel_2,
                LoadingView.FROM_TOP);*/
        loadingView.addAnimation(Color.parseColor("#C9BEBE"), marvel_2,
                LoadingView.FROM_TOP);
        /*loadingView.addAnimation(Color.parseColor("#FF4218"),marvel_3,
                LoadingView.FROM_RIGHT);*/
        loadingView.addAnimation(Color.parseColor("#9D9F9A"), marvel_3,
                LoadingView.FROM_RIGHT);
        /*loadingView.addAnimation(Color.parseColor("#C7E7FB"), marvel_4,
                LoadingView.FROM_BOTTOM);*/
        loadingView.addAnimation(Color.parseColor("#9FA8B5"), marvel_4,
                LoadingView.FROM_BOTTOM);
        loadingView.addListener(new LoadingView.LoadingListener() {
            @Override
            public void onAnimationStart(int currentItemPosition) {

            }

            @Override
            public void onAnimationRepeat(int nextItemPosition) {
                /*loadingView.addAnimation(Color.parseColor("#FFD200"),marvel_1,
                        LoadingView.FROM_LEFT);
                loadingView.addAnimation(Color.parseColor("#2F5DA9"),marvel_2,
                        LoadingView.FROM_TOP);
                loadingView.addAnimation(Color.parseColor("#FF4218"),marvel_3,
                        LoadingView.FROM_RIGHT);
                loadingView.addAnimation(Color.parseColor("#C7E7FB"), marvel_4,
                        LoadingView.FROM_BOTTOM);*/
                loadingView.addAnimation(Color.parseColor("#FFFFFF"), marvel_1,
                        LoadingView.FROM_LEFT);
       /* loadingView.addAnimation(Color.parseColor("#2F5DA9"),marvel_2,
                LoadingView.FROM_TOP);*/
                loadingView.addAnimation(Color.parseColor("#C9BEBE"), marvel_2,
                        LoadingView.FROM_TOP);
        /*loadingView.addAnimation(Color.parseColor("#FF4218"),marvel_3,
                LoadingView.FROM_RIGHT);*/
                loadingView.addAnimation(Color.parseColor("#9D9F9A"), marvel_3,
                        LoadingView.FROM_RIGHT);
        /*loadingView.addAnimation(Color.parseColor("#C7E7FB"), marvel_4,
                LoadingView.FROM_BOTTOM);*/
                loadingView.addAnimation(Color.parseColor("#9FA8B5"), marvel_4,
                        LoadingView.FROM_BOTTOM);
            }

            @Override
            public void onAnimationEnd(int nextItemPosition) {

            }
        });
    }

    public void startLoading() {
        loadingDialog.show();
        loadingView.startAnimation();

    }

    public void stopLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}
