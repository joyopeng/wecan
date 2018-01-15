package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.R;

/**
 * Created by Administrator on 2016/6/14.
 */
public class MultipleCheckBox extends LinearLayout {
    private CheckBox checkbox;
    private String isCheckdValue;
    private String[] name;
    private int positions;

    public MultipleCheckBox(Context context) {
        this(context, null);
        initLayout(context);
    }

    public MultipleCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.base_checkbox, this, true);

    }

    private void AddCheckBox(int count, View view, String[] name) {
        for (int index = 0; index < count; index++) {
            checkbox = (CheckBox) view.findViewById(index);
            if (checkbox != null) {
                checkbox.setChecked(true);
            }
            checkbox.setText(name[index]);

        }
    }

    class CheckBoxAdapter extends BaseAdapter {
        Context context;

        public CheckBoxAdapter(Context context) {
            this.context = context;
        }

        public int getCount() {
            return name.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            checkbox = new CheckBox(context);
            checkbox.setText(name[position]);
            checkbox.setId(position);
            positions = position;
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Toast.makeText(getContext(), "你点击的是第" + position + "张", Toast.LENGTH_LONG).show();
                }

            });
            return checkbox;
        }
    }


}

