package com.gofirst.scenecollection.evidence.view.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.activity.EvidenceDetail;
import com.gofirst.scenecollection.evidence.view.activity.EvidencePhotoExplorer;

/**
 * Created by Administrator on 2016/3/14.
 */
public class EvidenceListAdapter extends BaseAdapter {


    private Activity activity;
    public EvidenceListAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return 5;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.evidence_item,null);
        view.findViewById(R.id.imageLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, EvidencePhotoExplorer.class));
            }
        });

        view.findViewById(R.id.evidence_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity,EvidenceDetail.class));
            }
        });
        return view;
    }
}
