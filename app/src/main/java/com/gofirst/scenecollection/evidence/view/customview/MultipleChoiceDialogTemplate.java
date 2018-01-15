package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.easytagdragview.bean.SimpleTitleTip;
import com.gofirst.scenecollection.evidence.view.easytagdragview.bean.Tip;

import java.util.List;

public class MultipleChoiceDialogTemplate {

    private PopupWindow popupWindow;
    private int lastFirstVisiblePosition = 0;
    private List<Tip> list;
    private onResultListener listener;
    private MultiChoiceListAdpater listAdpater;

    public MultipleChoiceDialogTemplate(Context context, String name, String rootKey, final TextView click2Input) {
        initView(context, name, rootKey, click2Input, null);
    }

    public MultipleChoiceDialogTemplate(Context context, String name, TextView click2Input, List<Tip> otherList) {
        initView(context, name, null, click2Input, otherList);
    }

    private void initView(Context context, String name, String organizationId, final TextView click2Input, List<Tip> otherList) {
        list = otherList;
        View view = LayoutInflater.from(context).inflate(R.layout.single_level_list_pop, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final TextView popName = (TextView) view.findViewById(R.id.name);
        popName.setText(name);
        final ListView listView = (ListView) view.findViewById(R.id.single_list);
        listAdpater = new MultiChoiceListAdpater(list);
        listView.setAdapter(listAdpater);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
                checkBox.setChecked(!checkBox.isChecked());
                if (checkBox.isChecked()) {
                    listAdpater.saveChecked(position);
                } else {
                    listAdpater.removeChecked(position);
                }
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
                listener.onResult(listAdpater.getSelectData());
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

    public void setListener(onResultListener listener) {
        this.listener = listener;
    }

    public interface onResultListener {
        void onResult(SparseArray<String> fatherKeys);
    }
}


class MultiChoiceListAdpater extends BaseAdapter {
    private List<Tip> list;
    private SparseArray<String> selectData = new SparseArray<String>();

    public MultiChoiceListAdpater(List<Tip> list) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.multi_choicl_list_pop_item, parent, false);

        TextView nameView = (TextView) convertView.findViewById(R.id.tv_device_name);
        nameView.setText(((SimpleTitleTip)list.get(position)).getTip());
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
        if (selectData.indexOfKey(position) != -1) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
        return convertView;
    }

    public void saveChecked(int position) {
        selectData.put(position, ((SimpleTitleTip)list.get(position)).getFieldId());
    }

    public void removeChecked(int position) {
        selectData.remove(position);
    }

    public SparseArray<String> getSelectData() {
        return selectData;
    }
}

