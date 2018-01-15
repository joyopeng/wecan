package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.view.customview.PhotoGridview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/6/29.
 */
public class AllPicturesAdapter extends BaseExpandableListAdapter implements
        AdapterView.OnItemClickListener
{
    public static final int ItemHeight = 60;// 每项的高度
    public static final int PaddingLeft = 36;// 每项的高度
    private int myPaddingLeft = 0;

    private PhotoGridview toolbarGrid;
    private String belongTo="unclass";

    private String menu_toolbar_name_array[] = { "存储卡", "我的下载", "图书导入", "系统备份",
            "系统恢复", "清除全部", "在线升级", "快速入门", "关于开卷", "退出系统", "在线升级", "快速入门",
            "关于开卷", "退出系统", "关于开卷", "退出系统", "关于开卷", "退出系统", "关于开卷", "退出系统" };
    private int menu_toolbar_image_array[] = { R.mipmap.app_icon,
            R.mipmap.app_icon, R.mipmap.app_icon,
            R.mipmap.app_icon, R.mipmap.app_icon,
            R.mipmap.app_icon, R.mipmap.app_icon,
            R.mipmap.app_icon, R.mipmap.app_icon,
            R.mipmap.app_icon, R.mipmap.app_icon,
            R.mipmap.app_icon, R.mipmap.app_icon,
            R.mipmap.app_icon, R.mipmap.app_icon,
            R.mipmap.app_icon, R.mipmap.app_icon,
            R.mipmap.app_icon, R.mipmap.app_icon,
            R.mipmap.app_icon };

    private List<TreeNode> treeNodes = new ArrayList<TreeNode>();

    private Context parentContext;

    private LayoutInflater layoutInflater;
    private List<Bitmap> listpicture=new ArrayList<>();

    static public class TreeNode
    {
        public Object parent;
        public List<Object> childs = new ArrayList<Object>();
    }

    public AllPicturesAdapter(Context view, int myPaddingLeft)
    {
        parentContext = view;
        this.myPaddingLeft = myPaddingLeft;
    }

    public List<TreeNode> GetTreeNode()
    {
        return treeNodes;
    }

    public void UpdateTreeNode(List<TreeNode> nodes)
    {
        treeNodes = nodes;
    }

    public void RemoveAll()
    {
        treeNodes.clear();
    }

    public Object getChild(int groupPosition, int childPosition)
    {
        return treeNodes.get(groupPosition).childs.get(childPosition);
    }

    public int getChildrenCount(int groupPosition)
    {
        return treeNodes.get(groupPosition).childs.size();
    }

    static public TextView getTextView(Context context)
    {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, ItemHeight);
        TextView textView = new TextView(context);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        return textView;
    }

    /**
     * 可自定义ExpandableListView
     */
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            layoutInflater = (LayoutInflater) parentContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.all_photo_gridview, null);

            toolbarGrid = (PhotoGridview) convertView
                    .findViewById(R.id.GridView_toolbar);
            /*toolbarGrid.setNumColumns(3);// 设置每行列数
            toolbarGrid.setGravity(Gravity.CENTER);// 位置居中
            toolbarGrid.setHorizontalSpacing(10);// 水平间隔
            toolbarGrid.setAdapter(getMenuAdapter(*//*menu_toolbar_name_array,*//*
                    menu_toolbar_image_array));// 设置菜单Adapter
            toolbarGrid.setOnItemClickListener(this);*/
        }
        return convertView;
    }

    /**
     * 可自定义list
     */
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent)
    {
        TextView textView = getTextView(this.parentContext);
        textView.setText(getGroup(groupPosition).toString());
        textView.setPadding(myPaddingLeft + PaddingLeft, 10, 10, 10);
        return textView;
    }

    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    public Object getGroup(int groupPosition)
    {
        return treeNodes.get(groupPosition).parent;
    }

    public int getGroupCount()
    {
        return treeNodes.size();
    }

    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

    public boolean hasStableIds()
    {
        return true;
    }

    /**
     * 构造菜单Adapter
     *
     * //@param menuNameArray
     *            名称
     * @param imageResourceArray
     *            图片
     * @return SimpleAdapter
     */


    private SimpleAdapter getMenuAdapter(/*String[] menuNameArray,*/
                                         int[] imageResourceArray)
    {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        List<RecordFileInfo> list=EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"belongTo = '" + belongTo + "'");
        for (int i = 0; i < 1; i++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();

            Log.d("filepath_taofa", "" + list.get(0).getFilePath());
            map.put("itemImage", imageResourceArray[i]);
 //           map.put("itemImage", BitmapFactory.decodeFile(list.get(0).getFilePath()));

            data.add(map);
        }
        SimpleAdapter simperAdapter = new SimpleAdapter(parentContext, data,
                R.layout.item_menu, new String[] { "itemImage"/*, "itemText"*/ },
                new int[] { R.id.item_image/*, R.id.item_text */});
        return simperAdapter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id)
    {
        Toast.makeText(parentContext, "当前选中的是:" + position, Toast.LENGTH_SHORT)
                .show();


    }


   /* protected class MyGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return titles.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = View.inflate(MainActivity.this, R.layout.list_item_main, null);
            TextView title =  (TextView) view.findViewById(R.id.tv_title);
            title.setText(titles[position]);

            return view;
        }
*/


}
