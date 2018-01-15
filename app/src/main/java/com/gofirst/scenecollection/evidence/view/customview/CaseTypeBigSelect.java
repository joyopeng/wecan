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

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsDicts;

import java.util.List;


public class CaseTypeBigSelect {

    private PopupWindow popupWindow;
    private int lastFirstVisiblePosition = 0;
    private List<CsDicts> list;
    private onResultListener listener;

    public CaseTypeBigSelect(Context context, String name, String rootKey, final TextView click2Input) {
        initView(context, name, rootKey, click2Input, null);
    }

    public CaseTypeBigSelect(Context context, String name, View click2Input, List<CsDicts> otherList) {
        initView(context, name, null, click2Input, otherList);
    }

    private void initView(Context context, String name, String rootKey, final View click2Input, List<CsDicts> otherList) {
        list = rootKey != null ? EvidenceApplication.db.findAllByWhere(CsDicts.class, "rootKey = 'GASSLAJ'" + " and dictLevel = '1'") : otherList;
        View view = LayoutInflater.from(context).inflate(R.layout.single_level_list_pop, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final TextView popName = (TextView) view.findViewById(R.id.name);
        popName.setText(name);
        final ListView listView = (ListView) view.findViewById(R.id.single_list);
        listView.setAdapter(new SingleAdapter(list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*if (click2Input instanceof TextView) {
                    String text = list.get(position).getDictValue1();
                    ((TextView) click2Input).setText(TextUtils.isEmpty(text) ? "点击输入" : text);
                }*/
                if (listener != null)
                    listener.onResult(list.get(position).getDictKey());
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
                    /*String text = ((SingleAdapter) listView.getAdapter()).;
                    click2Input.setText(TextUtils.isEmpty(text) ? "点击输入" : text);*/
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


class CaseTypeBigSelectAdapter extends BaseAdapter {
    private List<CsDicts> list;

    public CaseTypeBigSelectAdapter(List<CsDicts> list) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        ((TextView) convertView).setText(list.get(position).getDictValue1());
        return convertView;
    }

}


