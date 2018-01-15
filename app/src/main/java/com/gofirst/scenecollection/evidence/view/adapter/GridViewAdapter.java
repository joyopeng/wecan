package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.view.activity.ShowAllPicturesActivity;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/9.
 */
public class GridViewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<HashMap<String, Object>> mList;
    private String pictureType[]={"","1","2","3","4"};
    private int listPosition;
    private String caseId,mode,templateId,father;

    private String[] typePicture={"","1","2","3","4"};
    private String type_Picture="";
    private ListView mListView;
    private ArrayList<ArrayList<HashMap<String,Object>>> mArrayList;
    private boolean isAddRec;

    public void setAddRec(boolean addRec) {
        isAddRec = addRec;
    }

    public GridViewAdapter(String father, String mode, String caseId, String templateId, int listPosition, Context mContext, ArrayList<HashMap<String, Object>> mList) {
        super();
        this.mContext = mContext;
        this.mList = mList;
        this.listPosition=listPosition;
        this.caseId=caseId;
        this.mode=mode;
        this.templateId = templateId;
        this.father=father;
    }

    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        } else {
            return this.mList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mList == null) {
            return null;
        } else {
            return this.mList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from
                    (this.mContext).inflate(R.layout.gridview_item, null, false);
            holder.Img = (ImageView) convertView.findViewById(R.id.gridview_item_button);
            holder.anchor_flage_picture= (ImageView) convertView.findViewById(R.id.anchor_flage_picture);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (this.mList != null) {
            HashMap<String, Object> hashMap = this.mList.get(position);
            if (holder.Img != null) {
                //for(int i=0;i<pictureType.length;i++){
                // List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                //        "belongTo = '"+ pictureType[i] +"'");//"belongTo = '" + belongTo + "' and caseId = '" + caseId + "'"

                if(mList.size()>0) {
                    //mArrayList=new ArrayList<ArrayList<HashMap<String,Object>>>();
                    HashMap<String, Object> has = null;
                    ArrayList<HashMap<String, Object>> arrayListForEveryGridView;
                    getTypePicture(listPosition);
                    //for (int i = 0; i < typePicture.length; i++) {
                    arrayListForEveryGridView = new ArrayList<HashMap<String, Object>>();
                    List<RecordFileInfo> list = EvidenceApplication.db.
                            findAllByWhere(RecordFileInfo.class, "photoType = '" + type_Picture + "' and caseId = '" + caseId + "'and fileType = 'png' and father = '" + father + "'");
                   // for (int j = 0; j < list.size(); j++) {
                    if(position>=list.size()){

                    }else {
                        if (list.get(position).getContractionsFilePath().equals(hashMap.get("content").toString())) {
                            if (list.get(position).getIsMarked() != null) {
                                if (list.get(position).getIsMarked().equals("yes")) {
                                    holder.anchor_flage_picture.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }

                   // }
                    //  mArrayList.add(arrayListForEveryGridView);
                    //  }

                }

                //               holder.button.setText(hashMap.get("content").toString());
//                holder.button.setImageBitmap(BitmapFactory.decodeFile(list.get(0).getFilePath()));


                holder.Img.setImageBitmap(BitmapFactory.decodeFile(AppPathUtil.getDataPath()+"/"+hashMap.get("content").toString()));
                holder.Img.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(mContext, "第" + (listPosition) + "list", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(mContext, "第" + (position) + "个", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(v.getContext(), ShowAllPicturesActivity.class)
                                .putExtra("position", position)
                                .putExtra("listPosition",listPosition)
                                .putExtra("caseId",caseId)
                                .putExtra("mode",mode)
                                .putExtra("father",father)
                                .putExtra("templateId", templateId)
                                .putExtra(BaseView.ADDREC,isAddRec);
                        v.getContext().startActivity(intent);

                    }
                });
                // }
               /* List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "belongTo = 'unclass'");
                //               holder.button.setText(hashMap.get("content").toString());
//                holder.button.setImageBitmap(BitmapFactory.decodeFile(list.get(0).getFilePath()));
                holder.Img.setImageBitmap(BitmapFactory.decodeFile(hashMap.get("content").toString()));
                holder.Img.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "第" + (position + 1) + "个", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(v.getContext(), ShowAllPicturesActivity.class)
                                .putExtra("position", position)
                                .putExtra("mode","edit");
                        v.getContext().startActivity(intent);

                    }
                });*/
            }
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView Img;
        ImageView anchor_flage_picture;
    }


    private void getTypePicture(int listPosition){
        switch (listPosition){
            case 0:
                type_Picture="";
                break;
            case 1:
                type_Picture="1";
                break;
            case 2:
                type_Picture="2";
                break;
            case 3:
                type_Picture="3";
                break;
            case 4:
                type_Picture="4";
                break;
        }
    }
}