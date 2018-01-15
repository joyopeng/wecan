
package com.gofirst.scenecollection.evidence.view.customview;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;

public class YesNoSingleLevel {

    private PopupWindow popupWindow;

    public YesNoSingleLevel(Context context, String name, final TextView click2Input) {
        initView(context, name, click2Input);
    }


    private void initView(Context context, String name, final TextView click2Input) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_level_list_pop, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final TextView popName = (TextView) view.findViewById(R.id.name);
        popName.setText(name);
        final ListView listView = (ListView) view.findViewById(R.id.single_list);
        final SingleYesNoAdapter singleYesNoAdapter = new SingleYesNoAdapter();
        listView.setAdapter(singleYesNoAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String text = singleYesNoAdapter.names[position];
                    String ids = singleYesNoAdapter.ids[position];
                    click2Input.setTag(ids);
                    click2Input.setText(text);
                popupWindow.dismiss();
            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
            }
        });
        view.findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.setAnimationStyle(R.style.tabpopstyle);
        popupWindow.setFocusable(true);
        if (!popupWindow.isShowing())
            popupWindow.showAtLocation(click2Input, Gravity.BOTTOM, 0, 0);
    }

}
class SingleYesNoAdapter extends BaseAdapter {

    public String[] ids = {"1","0"};
    public String[] names = {"是","否"};
    @Override
    public int getCount() {
        return ids.length;
    }

    @Override
    public Object getItem(int position) {
        return ids[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        ((TextView) convertView).setText(names[position]);
        return convertView;
    }

}

