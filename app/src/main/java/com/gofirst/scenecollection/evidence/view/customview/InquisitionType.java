package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/15.
 */
public class InquisitionType {
    private PopupWindow popupWindow;
    private int lastFirstVisiblePosition = 0;
    private List<String> list;
    private onResultListener listener;

    public InquisitionType(Context context, String name, String rootKey, final TextView click2Input) {
        initView(context, name, rootKey, click2Input, null);
    }

    public InquisitionType(Context context, String name, TextView click2Input, List<String> otherList) {
        initView(context, name, null, click2Input, otherList);
    }

    private void initView(Context context, String name, String organizationId, final TextView click2Input, List<String> otherList) {
        //list = organizationId != null ? EvidenceApplication.db.findAllByWhere(HyEmployees.class, "organizationId = '" + organizationId + "'" ) : otherList;
        list =new ArrayList<String>();
        list.add("简勘");
        list.add("非简勘");

        View view = LayoutInflater.from(context).inflate(R.layout.single_level_list_pop, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final TextView popName = (TextView) view.findViewById(R.id.name);
        popName.setText(name);
        final ListView listView = (ListView) view.findViewById(R.id.single_list);
        listView.setAdapter(new InquisitionTypeAdapter(list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (click2Input instanceof TextView) {
                    String text = list.get(position).toString();
                    ((TextView) click2Input).setText(TextUtils.isEmpty(text) ? "点击输入" : text);
                    if (list.get(position).toString().equals("简勘")) {
                        ((TextView) click2Input).setTag("1");
                    } else {
                        ((TextView) click2Input).setTag("0");
                    }
                    // ((TextView) click2Input).setTag(list.get(position).getEmployeeNo());
                }
                if (listener != null)
                    listener.onResult(list.get(position).toString());
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
                    //String text = ((SingleAdapter) listView.getAdapter()).;
                    //click2Input.setText(TextUtils.isEmpty(text) ? "点击输入" : text);
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.setAnimationStyle(R.style.tabpopstyle);
        popupWindow.setFocusable(true);
        if (!popupWindow.isShowing())
            popupWindow.showAtLocation(click2Input, Gravity.BOTTOM, 0, 0);
    }

    public void setListener(onResultListener listener) {
        this.listener = listener;
    }

    public interface onResultListener {
        void onResult(String templateId);
    }
}


class InquisitionTypeAdapter extends BaseAdapter {
    private List<String> list;

    public InquisitionTypeAdapter(List<String> list) {
        this.list = list;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inquisition_single_list_item, parent, false);
        ((TextView) convertView).setText(list.get(position).toString());
        return convertView;
    }




}
