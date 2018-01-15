
package com.gofirst.scenecollection.evidence.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.User;

import java.util.List;

/**
 * @author maxiran
 */

public class UserAdapter extends BaseAdapter {

    private List<User> list;
    private OnUserListener listener;
    public UserAdapter(List<User> userList,OnUserListener listener) {
        list = userList;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent,false);
            viewHolder.name = (TextView)convertView.findViewById(R.id.name);
            viewHolder.remove = (ImageView) convertView.findViewById(R.id.remove);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(list.get(position).getUserName());
        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnRemoveUser(list.get(position).getUserName(),list.size() == 1);
                    list.remove(position);
                    notifyDataSetChanged();
                }
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    User user = list.get(position);
                    listener.OnUserClick(user.getUserName(),user.getPassword());
                }
            }
        });
        return convertView;
    }

    private class ViewHolder{
        private TextView name;
        private ImageView remove;
    }

    public interface OnUserListener{
        void OnRemoveUser(String userName,boolean isLast);
        void OnUserClick(String userName,String pass);
    }
}

