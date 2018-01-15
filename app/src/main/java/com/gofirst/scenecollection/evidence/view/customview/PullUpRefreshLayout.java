package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;

import com.gofirst.scenecollection.evidence.R;

public class PullUpRefreshLayout extends SwipeRefreshLayout {

	private int mTouchslop;
	private View mListViewFooter;
	private ListView mListView;
	private int mYdown;
	private int mLastY;
	private boolean isLoading = false;
	private onLoadListener listener;

	public PullUpRefreshLayout(Context context) {
		super(context);
	}

	@SuppressWarnings({ "static-access", "deprecation" })
	public PullUpRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTouchslop = ViewConfiguration.get(context).getTouchSlop();
		mListViewFooter = LayoutInflater.from(context).inflate(
				R.layout.pull_to_load_footer, null);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
							int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mListView == null) {
			getListView();
		}
	}

	private void getListView() {
		int childs = getChildCount();
		if (childs > 0) {
			View childView = getChildAt(0);
			if (childView instanceof ListView) {
				mListView = (ListView) childView;
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:

				mYdown = (int) ev.getRawY();

				break;

			case MotionEvent.ACTION_MOVE:
				mLastY = (int) ev.getRawY();
				break;

			case MotionEvent.ACTION_UP:

				if (canLoad()) {
					loadData();

				}

				break;
			default:
				break;
		}
		return super.dispatchTouchEvent(ev);
	}

	public static interface onLoadListener {
		public void onLoad();
	}

	private boolean canLoad() {
		return isBottom() && !isLoading && isPullUp();
	}

	private boolean isPullUp() {
		Log.d("是否在上啦", "" + ((mYdown - mLastY) >= mTouchslop));
		return ((mYdown - mLastY) >= mTouchslop);

	}

	private boolean isBottom() {
		if (mListView != null && mListView.getAdapter() != null && mListView.getAdapter().getCount() != 0) {
			return mListView.getLastVisiblePosition() == (mListView
					.getAdapter().getCount() - 1);
		}
		return false;
	}

	private void loadData() {
		if (listener != null) {
			setLoading(true);
			listener.onLoad();
		} else {

			throw new NullPointerException("lister is not should be null");
		}
	}

	// 加载完成后setLoading(false);
	public void setLoading(boolean loading) {
		isLoading = loading;
		if (mListViewFooter != null && mListView != null) {
			if (isLoading) {
				mListView.addFooterView(mListViewFooter);

			} else {
				mListView.removeFooterView(mListViewFooter);
				mYdown = 0;
				mLastY = 0;
			}
		}

	}

	public void setOnLoadListener(onLoadListener listener) {
		this.listener = listener;
	}

}
