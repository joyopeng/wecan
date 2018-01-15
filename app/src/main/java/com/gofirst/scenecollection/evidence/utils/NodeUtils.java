package com.gofirst.scenecollection.evidence.utils;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.NodeData;
import com.gofirst.scenecollection.evidence.model.OrgNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsh on 2016/8/17.
 */
public class NodeUtils {

    /**
     * 挂载node
     * */
    public static void addNode(List<OrgNode> nodes,OrgNode node,int defaultExpandLevel,int currentLevel){
        nodes.add(node);
        //if(defaultExpandLevel >= currentLevel){
        //    node.setExpand(true);
        //}
        if(node.isRootNode()){
            node.setExpand(true);
        }
        if(node.isLeaf())
            return;
        int length = node.getChilds().size();
        for (int i = 0;i < length;i++){
            addNode(nodes,node.getChilds().get(i),defaultExpandLevel,currentLevel + 1);
        }
    }

    /**
     * 设置节点图片
     * */
    public static void setNodeIcon(OrgNode node){
        if(node.getChilds().size() > 0 && node.isExpand()){
            //设置图片展开状态
            node.setIcon(R.drawable.contact_org_down);
        }else if(node.getChilds().size() > 0 && !node.isExpand()){
            //设置图片收起状态
            node.setIcon(R.drawable.contact_org_up);
        }else{
            node.setIcon(-1);
        }
    }

    /**
     * 获得Root node
     * */
    public static List<OrgNode> getRootNode(List<OrgNode> nodes){
        List<OrgNode> results = new ArrayList<OrgNode>();
        for(OrgNode node : nodes){
            if(node.isRootNode()){
                results.add(node);
            }
        }
        return results;
    }

    /**
     * 查找过滤可见的node
     * */
    public static List<OrgNode> getFilterVisible(List<OrgNode> nodes){
        List<OrgNode> results = new ArrayList<OrgNode>();
        for (OrgNode node:nodes){
            if(node.isParentExpand() || node.isRootNode()){
                setNodeIcon(node);
                results.add(node);
            }
        }
        return results;
    }

    /**
     * 将加载的data转换成相应的node
     * */
    public static List<OrgNode> getTransNodes(List<NodeData> datas, int defaultExpandLevel)
        throws  IllegalArgumentException,IllegalAccessException{
        List<OrgNode> results = new ArrayList<OrgNode>();
        //将用户数据转换成nodes组
        List<OrgNode> nodes = transData2Node(datas);
        //得到根节点
        List<OrgNode> rootNodes = getRootNode(nodes);

        //排序及设置node间关系
        for(OrgNode node : rootNodes){
            addNode(results,node,defaultExpandLevel,1);
        }
        return results;
    }

    /**
     * 用户数据转换成Node数组
     * */
    public static List<OrgNode> transData2Node(List<NodeData> datas){
        List<OrgNode> results = new ArrayList<OrgNode>();
        OrgNode node = null;
        for(NodeData data : datas){
            node = new OrgNode(data.getId(),data.getpId(),data.getName(),data.getType());
            results.add(node);
        }

        //循环比较两个节点，设置父子关系
        int length = results.size();
        for(int i = 0; i < length;i++){
            OrgNode n = results.get(i);
            for(int j = i + 1;j < length;j++){
                OrgNode m = results.get(j);
                if(n.getpId().equals(m.getId())){
                    m.getChilds().add(n);
                    n.setParentNode(m);
                }else if(n.getId().equals(m.getpId())){
                    n.getChilds().add(m);
                    m.setParentNode(n);
                }
            }
        }
        //初始化icon
        for (OrgNode node1 : results){
            setNodeIcon(node1);
        }
        return results;
    }
}
