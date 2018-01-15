package com.gofirst.scenecollection.evidence.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsh on 2016/8/17.
 */
public class OrgNode {
    //当前节点id
    private String id;
    //当前节点父节点id
    private String pId;
    //当前节点名称
    private String name;
    //当前节点是单位还是部门
    private String type;
    //等级
    private int level;
    //显示的图片
    private int icon;
    //是否是展开状态
    private boolean isExpand = false;
    //当前节点的父节点
    private OrgNode parentNode;
    //当前节点的子节点
    private List<OrgNode> childs = new ArrayList<OrgNode>();

    public OrgNode(String id,String pId,String name,String type) {
        super();
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLevel() {
        //return level;
        return parentNode == null ? 0 : parentNode.getLevel() + 1;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
        if(!isExpand){
            for (OrgNode node : childs){
                node.setExpand(isExpand);
            }
        }
    }

    public OrgNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(OrgNode parentNode) {
        this.parentNode = parentNode;
    }

    public List<OrgNode> getChilds() {
        return childs;
    }

    public void setChilds(List<OrgNode> childs) {
        this.childs = childs;
    }

    //是否是叶节点
    public boolean isLeaf(){
        return childs.size() == 0;
    }

    //父节点是否是展开状态
    public boolean isParentExpand(){
        if(parentNode == null){
            return false;
        }
        return parentNode.isExpand();
    }

    //是否是根节点
    public boolean isRootNode(){
        return parentNode == null;
    }
}
