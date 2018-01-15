package com.gofirst.scenecollection.evidence.view.adapter;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.ContactInformation;
import com.gofirst.scenecollection.evidence.utils.CustomDialog;

import java.util.ArrayList;

/**
 * Created by maxiran on 2016/5/4.
 */
public class ContactAdapter extends BaseExpandableListAdapter{

    private ArrayList<String> groupNameLists;
    private ArrayList<ArrayList<ContactInformation>> childLists;

    public ContactAdapter(ArrayList<String> groupNameLists,ArrayList<ArrayList<ContactInformation>> childLists){
            this.childLists = childLists;
            this.groupNameLists = groupNameLists;
    }
    @Override
    public int getGroupCount() {
        return groupNameLists.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childLists.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupNameLists.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childLists.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        int hei = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1,parent.getResources().getDisplayMetrics());
        LinearLayout ll = new LinearLayout(parent.getContext());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        TextView textView = new TextView(parent.getContext());
        textView.setPadding(hei*10, 9*hei, 0, 9*hei);
        textView.setText(groupNameLists.get(groupPosition));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        ll.addView(textView);
        return ll;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        childHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_child_item,parent,false);
            holder = new childHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.contact_icon);
            holder.name = (TextView) convertView.findViewById(R.id.contact_name);
            holder.remove = (ImageView) convertView.findViewById(R.id.remove);
            convertView.setTag(holder);
        }else {
            holder = (childHolder) convertView.getTag();
        }
        ContactInformation contactInformation = childLists.get(groupPosition).get(childPosition);
        holder.icon.setImageResource(contactInformation.getAvatar());
        holder.name.setText(contactInformation.getName());
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomDialog dialog = new CustomDialog(v.getContext());
                dialog.setContentAndListener("确定要将" + childLists.get(groupPosition).get(childPosition).getName() + "移除常用联系人？",v,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                childLists.get(groupPosition).remove(childPosition);
                                notifyDataSetChanged();
                                dialog.dialog.dismiss();
                            }
                        },

                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dialog.dismiss();
                            }
                        });
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class childHolder{
        private ImageView icon;
        private TextView name;
        private ImageView remove;
    }
}
