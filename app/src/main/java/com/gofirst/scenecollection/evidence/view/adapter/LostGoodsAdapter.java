package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.LostGood;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.BitmapUtils;
import com.gofirst.scenecollection.evidence.utils.OSUtil;
import com.gofirst.scenecollection.evidence.view.activity.LostGoodDetail;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.fragment.SceneInfoFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public class LostGoodsAdapter extends BaseAdapter {
    private List<LostGood> lostGoods;
    private String caseId;
    private String father;
    private String templateId;
    private String mode, name;
    private boolean addsRec;

    public LostGoodsAdapter(String caseId, List<LostGood> lostGoods, String father, String templateId,boolean addsRec) {
        this.caseId = caseId;
        this.lostGoods = lostGoods;
        this.father = father;
        this.templateId = templateId;
        this.addsRec = addsRec;
    }

    public void setMode(String mode, String name) {
        this.mode = mode;
        this.name = name;
    }

    @Override
    public int getCount() {
        return lostGoods.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("ALL")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (position == lostGoods.size()) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_new_lost_goods, parent, false);
            if (mode != null && mode.equals(BaseView.VIEW))
                convertView.setVisibility(View.GONE);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), LostGoodDetail.class);
                    intent.putExtra("caseId", caseId);
                    intent.putExtra("father", father);
                    intent.putExtra("templateId", templateId);
                    intent.putExtra("mode", mode);
                    intent.putExtra("title", name);
                    intent.putExtra(BaseView.ADDREC,addsRec);
                    v.getContext().startActivity(intent);
                }
            });
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lost_good_item, parent, false);
            TextView textView = (TextView) convertView.findViewById(R.id.goods_desc);
            TextView sign = (TextView) convertView.findViewById(R.id.start_pop);
            final LostGood lostGood = lostGoods.get(position);
            sign.setBackground(new BitmapDrawable(getSignBitmap(lostGood.getId())));
            textView.setText(getJsonValue(lostGood));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), LostGoodDetail.class);
                    intent.putExtra("id", lostGood.getId());
                    intent.putExtra("caseId", caseId);
                    intent.putExtra("father", father);
                    intent.putExtra("mode", mode);
                    intent.putExtra("title", name);
                    intent.putExtra("templateId", templateId);
                    intent.putExtra(BaseView.ADDREC,addsRec);
                    v.getContext().startActivity(intent);
                }
            });
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    View view = LayoutInflater.from(v.getContext()).inflate(R.layout.delete_pop, null);
                    int wid = OSUtil.dip2px(v.getContext(), 300);
                    int hei = OSUtil.dip2px(v.getContext(), 45);
                    final PopupWindow popupWindow = new PopupWindow(view, wid,
                            hei);
                    popupWindow.setFocusable(true);
                    popupWindow.setOutsideTouchable(true);
                    ((TextView) view.findViewById(R.id.delete)).setText(!father.equals("SCENE_LOST_GOODS") ? "刪除此人" : "刪除此物");
                    view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EvidenceApplication.db.delete(lostGood);
                            DataTemp dataTemp = SceneInfoFragment.getDataTemp(caseId, lostGood.getId());
                            EvidenceApplication.db.delete(dataTemp);
                            lostGoods.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(v.getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                            popupWindow.dismiss();
                        }
                    });
                    popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                    return false;
                }
            });
        }
        return convertView;
    }


    private String getJsonValue(LostGood lostGood) {
        try {
            JSONObject jsonObject = new JSONObject(lostGood.getJson());
            String text = "";
            if (!lostGood.getFather().equals("SCENE_LOST_GOODS")) {
                if (!jsonObject.isNull("NAME")) {
                    text += jsonObject.getString("NAME");
                }
                if (!jsonObject.isNull("SEX_NAME")) {
                    text += "，" + jsonObject.getString("SEX_NAME");
                }
                if (!jsonObject.isNull("AGE")) {
                    text += "，" + jsonObject.getString("AGE");
                }
            } else {
                if (!jsonObject.isNull("NAME")) {
                    text += jsonObject.getString("NAME");
                }
                if (!jsonObject.isNull("AMOUNT")) {
                    text += "，数量" + jsonObject.getString("AMOUNT");
                }
                if (!jsonObject.isNull("VALUE")) {
                    text += "，价值" + jsonObject.getString("VALUE") + "元";
                }
            }
            return text;
        } catch (JSONException e) {
            e.printStackTrace();
            return "信息未录入完整";
        }
    }

    private Bitmap getSignBitmap(String section) {
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "section = '" + section + "' and child = 'note'");
        return list.size() != 0 ? BitmapUtils.revitionImageSize(AppPathUtil.getDataPath() + "/" + list.get(0).getFilePath()) : null;
    }

}
