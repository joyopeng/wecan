package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.gofirst.scenecollection.evidence.model.NodeData;
import com.gofirst.scenecollection.evidence.model.OrgNode;
import com.gofirst.scenecollection.evidence.utils.NodeUtils;
import com.gofirst.scenecollection.evidence.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsh on 2016/8/17.
 */
public abstract class OrgListViewAdapter extends BaseAdapter{
    private ListView mListView;
    private Context mContext;
    protected LayoutInflater mInflater;

    private List<OrgNode> mFilterNodes = new ArrayList<>();
    private List<OrgNode> mAllNodes =new ArrayList<>();

    /**
     * 回调函数
     * */
    protected onNodeClickListener nodeClickListener;

    public interface onNodeClickListener{
        void onNodeClick(OrgNode node,int position);
    }

    public void setNodeClickListener(onNodeClickListener listener){
        this.nodeClickListener = listener;
    }

    public AdapterView.OnItemClickListener getOnItemClickLintener(){
        return  listViewListener;
    }
    /**
     * 回调函数结束
     * */

    public OrgListViewAdapter(ListView lv, Context context, List<NodeData> datas, int defaultLevel)
            throws IllegalArgumentException,IllegalAccessException{

        mContext = context;
        mAllNodes = NodeUtils.getTransNodes(datas,defaultLevel);
        mFilterNodes = NodeUtils.getFilterVisible(mAllNodes);
        mInflater = LayoutInflater.from(context);

        lv.setOnItemClickListener(listViewListener);
    }

    @Override
    public int getCount() {
        return mFilterNodes.size();
    }

    @Override
    public Object getItem(int position) {
        return mFilterNodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrgNode node = mFilterNodes.get(position);
        convertView = getConvertView(position,convertView,parent,node);

        convertView.setPadding(node.getLevel()*Utils.dp2Px(mContext,20),0,0,0);
        return convertView;
    }

    //ListView item click listener
    private AdapterView.OnItemClickListener listViewListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            OrgNode node = mFilterNodes.get(position);
            //展开or收起列表
            //expandOrFold(position);
            //if(nodeClickListener != null && node.isLeaf()){
            if(nodeClickListener != null){
                nodeClickListener.onNodeClick(node,position);
            }
        }
    };

    /**
     * 展开或收起列表
     * */
    protected void expandOrFold(int position){
        OrgNode node = mFilterNodes.get(position);
        if(node != null && !node.isLeaf()){
            node.setExpand(!node.isExpand());
            mFilterNodes = NodeUtils.getFilterVisible(mAllNodes);
            notifyDataSetChanged();
        }
    }

    protected abstract View getConvertView(int position,View converView,ViewGroup parent,OrgNode node);
}
