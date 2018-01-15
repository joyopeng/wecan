package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.EvidenceExtra;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.OSUtil;
import com.gofirst.scenecollection.evidence.view.activity.AddEvidence;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

/**
 * @author maxiran
 */
public class ExtractEvidenceFragmentAdapter extends BaseAdapter {

    private List<EvidenceExtra> list;
    private String templateId;
    private String mode;
    private boolean addRec;

    public ExtractEvidenceFragmentAdapter(String templateId, List<EvidenceExtra> list, String mode,boolean addRec) {
        this.templateId = templateId;
        this.list = list;
        this.mode = mode;
        this.addRec = addRec;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public EvidenceExtra getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int arg0, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.extract_evidence_list_item, null);
            viewHolder.evidenceId = (TextView) view.findViewById(R.id.evidence_id);
            viewHolder.evidenceName = (TextView) view.findViewById(R.id.evidence_name);
            viewHolder.extractDate = (TextView) view.findViewById(R.id.extract_date);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        JSONObject jsonObject = getExtraJson(arg0);
        String code = getExtraCode(jsonObject);
        viewHolder.evidenceId.setText(code);
        viewHolder.evidenceName.setText(safeGetJsonValue(jsonObject, "NAME"));
        viewHolder.extractDate.setText(safeGetJsonValue(jsonObject, "COLLECTED_DATE"));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddEvidence.class);
                intent.putExtra("isShow", true);
                intent.putExtra("id", getItem(arg0).getId());
                intent.putExtra("templateId", templateId);
                intent.putExtra("caseId", getItem(arg0).getCaseId());
                intent.putExtra("father", getItem(arg0).getFather());
                intent.putExtra("mode", mode);
                intent.putExtra(BaseView.ADDREC, addRec);
                v.getContext().startActivity(intent);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Context context = v.getContext();
                View view = LayoutInflater.from(context).inflate(R.layout.delete_pop, null);
                int wid = OSUtil.dip2px(context, 300);
                int hei = OSUtil.dip2px(context, 45);
                final PopupWindow popupWindow = new PopupWindow(view, wid,
                        hei);
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                TextView textView = (TextView) view.findViewById(R.id.delete);
                textView.setText("删除此物证");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean deleteFileSuccess = deleteExtra(list.get(arg0));
                        Toast.makeText(context, deleteFileSuccess ? "删除成功" : "删除失败", Toast.LENGTH_SHORT).show();
                        if (deleteFileSuccess) {
                            list.remove(arg0);
                            notifyDataSetChanged();
                        }

                        popupWindow.dismiss();
                    }
                });
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                return false;
            }
        });
        return view;

    }

    private class ViewHolder {
        private ImageView evidencePhoto;
        private TextView evidenceId;
        private TextView evidenceName;
        private TextView extractDate;
    }

    private String getExtraImage(int extraPosition) {
        EvidenceExtra evidenceExtra = list.get(extraPosition);
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "section = '" + evidenceExtra.getSection() + "' and fileType = 'png'", "fileDate asc");
        return list.size() != 0 ? list.get(0).getFilePath() : "";
    }

    private JSONObject getExtraJson(int extraPosition) {
        EvidenceExtra evidenceExtra = list.get(extraPosition);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(evidenceExtra.getJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private String getExtraCode(JSONObject jsonObject) {
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            try {
                if (key.equals("BAR_CODE")) {
                    return jsonObject.getString(key);
                } else if (key.equals("QR_CODE")) {
                    return jsonObject.getString(key);
                } else if (key.equals("RFID_CODE")) {
                    return jsonObject.getString(key);
                }
                if (key.equals("MATERIAL_NO")) {
                    return jsonObject.getString(key);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "未扫描";
    }

    private String safeGetJsonValue(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "未录入";
    }

    @Override
    public boolean isEmpty() {
        return list.size() == 0;
    }

    private boolean deleteExtra(EvidenceExtra evidenceExtra) {
        EvidenceApplication.db.deleteById(EvidenceExtra.class, evidenceExtra.getId());
        List<DataTemp> dataTemps = EvidenceApplication.db.findAllByWhere(DataTemp.class, "father = '" + evidenceExtra.getId() + "'");
        for (DataTemp dataTemp : dataTemps)
            EvidenceApplication.db.delete(dataTemp);
        List<RecordFileInfo> recordFileInfos = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '"
                + evidenceExtra.getCaseId() + "' and father = '" + evidenceExtra.getFather() + "'");
        for (RecordFileInfo recordFileInfo : recordFileInfos)
            EvidenceApplication.db.delete(recordFileInfo);
        return true;
    }
}

