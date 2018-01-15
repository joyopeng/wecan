package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.gofirst.scenecollection.evidence.R;


public class EvidenceDetail extends Activity {

    private ListView photoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.evidence_detail);
        photoListView = (ListView) findViewById(R.id.evidence_photo_listview);
        photoListView.addHeaderView(getLayoutInflater().inflate(R.layout.evidence_detail_head, null));
        photoListView.setAdapter(new adapter());
    }

    private class adapter extends BaseAdapter {


        @Override
        public int getCount() {
            return 3;
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
            View view = getLayoutInflater().inflate(R.layout.evidence_detail_item, null);
            return view;
        }
    }
}
