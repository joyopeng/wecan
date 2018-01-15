package com.gofirst.scenecollection.evidence.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.User;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.Utils;
import com.gofirst.scenecollection.evidence.view.activity.DataSelectActivity;
import com.gofirst.scenecollection.evidence.view.activity.SelectUploadCase;
import com.gofirst.scenecollection.evidence.view.activity.UploadedCaseList;
import com.gofirst.scenecollection.evidence.view.activity.UploadingCaseListActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/9.
 */
public class SyncDataUploadFragment extends Fragment implements View.OnClickListener{
    private final String TAG = "SyncDataUploadFragment";
    private SharePre mShare;

    private TextView mAutoUploadRemarks;
    private View mAutoUploadLayout;
    private View mDoUploadLayout;
    private View mDoUploadingLayout;
    private View mDoUploadedListLayout;

    private final String SHARE_UPLOAD_TILE_SLOT = "sync_data_auto_upload_time_slot";
    private boolean mIsSupportAutoLoad = false;
    private String mAutoUploadSlot;
    private String mAutoUploadStartTime;
    private String mAutoUploadEndTime;

    private ForegroundColorSpan mRemarkColorSpan = null;
    private final int AUTO_UPLOAD_SLOT_TIME_DIF = 3;
    private final String AUTO_UPLOAD_REMARK_FIRST_STATE = "( 未设置 )";
    private final String AUTO_UPLOAD_REMARK_SECOND_STATE = "( 当前设定 : ";
    private final String AUTO_UPLOAD_REMARK_THIRD_STATE = "不设置上传";
    private DataSelectActivity mPopSelect;
    private DateSelectAdapter mDateSelectAdapter;
    List<String> dateList = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sync_data_upload_layout,null);
        mShare = new SharePre(getActivity(), "user_info", Context.MODE_PRIVATE);
        mIsSupportAutoLoad = mShare.getBoolean(Utils.SHARE_AUTO_UPLOAD_SUPPORT,false);
        mAutoUploadSlot = mShare.getString(Utils.SHARE_UPLOAD_TILE_SLOT_TIME,"");
        mRemarkColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.text_common_blue_color));
        initView(view);
        mPopSelect = new DataSelectActivity(getActivity());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sync_data_auto_upload_layout:
                if(mDateSelectAdapter == null) {
                    initDateList();
                    mDateSelectAdapter = new DateSelectAdapter(getActivity(), dateList);
                    mPopSelect.setmListener(mDateSelectAdapter);
                }
                mPopSelect.setItemClick(mDateSelectItemClick);
                mPopSelect.showAtLocation(getActivity().findViewById(R.id.upload_layout_root_view), Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.sync_data_upload_layout:
                startActivity(new Intent(getActivity(), SelectUploadCase.class));
                break;
            case R.id.sync_data_uploading_layout:
                startActivity(new Intent(getActivity(), UploadingCaseListActivity.class));
                break;
            case R.id.sync_data_uploaded_list_layout:
                startActivity(new Intent(getActivity(),UploadedCaseList.class));
                break;
            default:
                break;
        }
    }

    private void initView(View root){
        mAutoUploadLayout = root.findViewById(R.id.sync_data_auto_upload_layout);
        mAutoUploadLayout.setOnClickListener(this);
        mAutoUploadRemarks = (TextView)root.findViewById(R.id.sync_data_auto_upload_remark);
        if("".equals(mAutoUploadSlot)){
            mAutoUploadRemarks.setText(AUTO_UPLOAD_REMARK_FIRST_STATE);
        }else{
            int length = AUTO_UPLOAD_REMARK_SECOND_STATE.length();
            SpannableStringBuilder builder = new SpannableStringBuilder(AUTO_UPLOAD_REMARK_SECOND_STATE + mAutoUploadSlot + " )");
            builder.setSpan(mRemarkColorSpan,length ,length + mAutoUploadSlot.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mAutoUploadRemarks.setText(builder);
        }
        mDoUploadLayout = root.findViewById(R.id.sync_data_upload_layout);
        mDoUploadingLayout = root.findViewById(R.id.sync_data_uploading_layout);
        mDoUploadedListLayout = root.findViewById(R.id.sync_data_uploaded_list_layout);
        mDoUploadLayout.setOnClickListener(this);
        mDoUploadingLayout.setOnClickListener(this);
        mDoUploadedListLayout.setOnClickListener(this);
    }

    private void saveAutoUploadTime(String time){
        mShare.put(Utils.SHARE_UPLOAD_TILE_SLOT_TIME,time);
        mShare.commit();
        User user = EvidenceApplication.db.findAllByWhere(User.class,"userId = \"" + mShare.getString("userId","") + "\"").get(0);
        user.setAutoUploadSoltTime(time);
        EvidenceApplication.db.update(user);
    }

    private void saveSupportAutoUploadTime(boolean support){
        mShare.put(Utils.SHARE_AUTO_UPLOAD_SUPPORT,support);
        mShare.commit();
        User user = EvidenceApplication.db.findAllByWhere(User.class,"userId = \"" + mShare.getString("userId","") + "\"").get(0);
        user.setSupportAutoUpload(support);
        EvidenceApplication.db.update(user);
    }

    private class DateSelectAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> mDates = new ArrayList<>();

        public DateSelectAdapter(Context context,List<String> datas) {
            super();
            this.mContext = context;
            this.mDates = datas;
        }

        @Override
        public int getCount() {
            return mDates.size();
        }

        @Override
        public Object getItem(int position) {
            return mDates.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.spinner_item_tv,null);
            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setText(mDates.get(position));
            return convertView;
        }
    }

    private AdapterView.OnItemClickListener mDateSelectItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(TAG,"onItemClick date = " + dateList.get(position));
            /*if(position%2 == 0) {
                saveAutoUploadTime(position/2 + ":00");
            }else{
                saveAutoUploadTime(position/2 + ":30");
            }*/
            String time = dateList.get(position) + "-" + (position + 3)%24 + ":00";
            int length = AUTO_UPLOAD_REMARK_SECOND_STATE.length();
            SpannableStringBuilder builder = new SpannableStringBuilder(AUTO_UPLOAD_REMARK_SECOND_STATE + time + " )");
            builder.setSpan(mRemarkColorSpan,length ,length + time.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            saveAutoUploadTime(TextUtils.equals(dateList.get(position),AUTO_UPLOAD_REMARK_THIRD_STATE) ? "" : time);
            mAutoUploadRemarks.setText(TextUtils.equals(dateList.get(position),AUTO_UPLOAD_REMARK_THIRD_STATE) ? AUTO_UPLOAD_REMARK_FIRST_STATE : builder);
            saveSupportAutoUploadTime(!TextUtils.equals(dateList.get(position),AUTO_UPLOAD_REMARK_THIRD_STATE));
            mPopSelect.dismiss();
        }
    };

    private void initDateList(){
        dateList.add(AUTO_UPLOAD_REMARK_THIRD_STATE);
        for (int i = 0;i < 48;i++) {
            if(i%2 == 0) {
                dateList.add(i/2 + ":00");
            }else{
                //dateList.add(i/2 + ":30");
            }
        }
    }
}
