package com.gofirst.scenecollection.evidence.view.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.customview.TabTextView;

import java.util.ArrayList;
import java.util.List;

public class TabWindowAdapter extends BaseAdapter {
    private List<String> list;
    private TabTextView lastTab;
    private List<View> tabTextViews = new ArrayList<>();
    private OnTabListener onTabListener;

    public TabWindowAdapter(List<String> list, GridView gridView) {
        this.list = list;
        for (String s : list) {
            tabTextViews.add(LayoutInflater.from(gridView.getContext()).inflate(R.layout.tab_window_item, gridView, false));
        }

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = tabTextViews.get(position);
        TabTextView tabTextView = (TabTextView) view.findViewById(R.id.type);
        String name = list.get(position);
        tabTextView.setText(name.length() < 5 ? name : name.substring(0, 4));
        tabTextView.setOnNextClickListener(new TabTextView.OnNextClickListener() {
            @Override
            public void OnClick(View view, boolean isSelect) {
                changeState((TabTextView) view);
                if (onTabListener != null)
                    onTabListener.onTab(position);
            }
        });
        return view;
    }

    public void setDefaultItem(int position) {
        if (position < tabTextViews.size()) {
            TabTextView tabTextView = (TabTextView) tabTextViews.get(position).findViewById(R.id.type);
            if (tabTextView != null)
                changeState(tabTextView);
        }
    }

    private void changeState(TabTextView tabTextView) {
        if (lastTab != null)
            lastTab.setIsSelect(false);
        tabTextView.setIsSelect(true);
        lastTab = tabTextView;
    }

    public interface OnTabListener {
        void onTab(int position);
    }

    public void setOnTabListener(OnTabListener onTabListener) {
        this.onTabListener = onTabListener;
    }
}
