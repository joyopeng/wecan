package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.NodeData;
import com.gofirst.scenecollection.evidence.model.OrgNode;

import java.util.List;

/**
 * Created by zsh on 2016/8/17.
 */
public class OrgSpecialAdapter extends OrgListViewAdapter {
    /**
     * 回调函数结束
     *
     * @param lv
     * @param context
     * @param datas
     * @param defaultLevel
     */
    public OrgSpecialAdapter(ListView lv, Context context, List<NodeData> datas, int defaultLevel) throws IllegalArgumentException, IllegalAccessException {
        super(lv, context, datas, defaultLevel);
    }

    @Override
    protected View getConvertView(final int position, View converView, ViewGroup parent, OrgNode node) {
        OrgHodler hodler = new OrgHodler();
        converView = mInflater.inflate(R.layout.org_listview_item,parent,false);
        hodler.mImag = (ImageView) converView.findViewById(R.id.org_item_img);
        hodler.mImag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("zhangsh","mImag onClick position = " + position);
                expandOrFold(position);
            }
        });
        if(node.getIcon() != -1) {
            hodler.mImag.setVisibility(View.VISIBLE);
            hodler.mImag.setBackgroundResource(node.getIcon());
        }else {
            hodler.mImag.setVisibility(View.INVISIBLE);
        }
        hodler.mText = (TextView) converView.findViewById(R.id.org_item_tv);
        hodler.mText.setText(node.getName());

        return converView;
    }

    private class OrgHodler{
        public ImageView mImag;
        public TextView mText;
    }
}
