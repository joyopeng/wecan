package com.gofirst.scenecollection.evidence.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.User;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.utils.Utils;
import com.gofirst.scenecollection.evidence.view.customview.ListViewInScrollView;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/9.
 */
public class SyncDataDownloadFragment extends Fragment implements View.OnClickListener{

    private SharePre mShare;

    private TextView mBaseDataContext;
    private ImageView mBaseDataImg;
    private CheckBox mBaseDataCheck;
    private TextView mContactContext;
    private ImageView mContactImg;
    private CheckBox mContactCheck;
    private TextView mScheduleContext;
    private ImageView mScheduleImg;
    private CheckBox mScheduleCheck;
    private View mInquestLayout;
    private TextView mInquestContext;
    private ImageView mInquestImg;
    private ListViewInScrollView mInquestList;
    private InquestListAdapter mInquestListAdapter;
    private CheckBox mNeedClearData;
    private Button mStartDownload;
    private boolean mIsNeedClear = false;
    //show inquest list
    private boolean mIsInquestListShow = false;

    private boolean iscoredataTime = false;
    private boolean isaddresslistTime = false;
    private boolean isscheduleTime = false;

    private final int DOWNLOAD_BASE_SELECTED = 0;
    private final int DOWNLOAD_CONTACT_SELECTED = 1;
    private final int DOWNLOAD_SCHEDULE_SELECTED = 2;
    private final int DOWNLOAD_INQUEST_FIRSTMONTH = 3;
    private final int DOWNLOAD_INQUEST_SECONDMONTH = 4;
    private final int DOWNLOAD_INQUEST_THIRDMONTH = 5;
    private final int DOWNLOAD_INQUEST_FOURTHMONTH = 6;
    private final int DOWNLOAD_INQUEST_FIFTHMONTH = 7;
    private final int DOWNLOAD_INQUEST_SIXTHMONTH = 8;
    private int mCurrentDownloadSeelected = DOWNLOAD_BASE_SELECTED;

    private final String SHARE_DOWNLOAD_SELECTED = "sync_data_download_selected";
    private final String SHARE_DOWNLOAD_NEED_CLEAR = "sync_data_download_need_clear";

    private final String DOWNLOAD_DATA_URL = "/baseDataMulti";
    private final int INQUEST_LIST_SHOW_COUNT = 1;
    private List<InquestListItemData> mInquestListDatas = new ArrayList<>();
    private String mStartTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShare = new SharePre(getActivity(), "user_info", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sync_data_donwload_layout,null);
        mCurrentDownloadSeelected = mShare.getInt(SHARE_DOWNLOAD_SELECTED,DOWNLOAD_BASE_SELECTED);
        mIsNeedClear = mShare.getBoolean(SHARE_DOWNLOAD_NEED_CLEAR,false);
        initIquestListData();
        intiView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initSyncDataState();
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
            case R.id.sync_data_inquest_layout:
                if(mIsInquestListShow){
                    mInquestList.setVisibility(View.GONE);
                    mIsInquestListShow = false;
                    mInquestImg.setBackgroundResource(R.drawable.selection_screen_group_up);
                }else{
                    mInquestList.setVisibility(View.VISIBLE);
                    mIsInquestListShow = true;
                    mInquestImg.setBackgroundResource(R.drawable.selection_screen_group_down);
                }
                break;
            case R.id.sync_data_base_checkbox:
                mCurrentDownloadSeelected = DOWNLOAD_BASE_SELECTED;
                updateChecked();
                updateDataContextColor();
                mInquestListAdapter.notifyDataSetChanged();
                break;
            case R.id.sync_data_contact_checkbox:
                mCurrentDownloadSeelected = DOWNLOAD_CONTACT_SELECTED;
                updateChecked();
                updateDataContextColor();
                mInquestListAdapter.notifyDataSetChanged();
                break;
            case R.id.sync_data_schedule_checkbox:
                mCurrentDownloadSeelected = DOWNLOAD_SCHEDULE_SELECTED;
                updateChecked();
                updateDataContextColor();
                mInquestListAdapter.notifyDataSetChanged();
                break;
            case R.id.sync_data_start_donwload_btn:
                startDownload();
                break;
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case Utils.MSG_DOWNLOAD_BASE_DATA:
                    boolean suc = (boolean)msg.obj;
                    if(!suc){
                        Utils.stopProgressDialog();
                        mBaseDataImg.setVisibility(View.VISIBLE);
                    }
                    break;
                case Utils.MSG_ANALYSIS_DATA:
                    boolean suc1 = (boolean)msg.obj;
                    if(suc1){
                        Bundle bundle = msg.getData();
                        String coredataUpdateTime = bundle.getString("update_time","");
                        if(coredataUpdateTime != null && !"".equals(coredataUpdateTime)) {
                            saveCoredataTime(coredataUpdateTime);
                            mBaseDataImg.setVisibility(View.GONE);
                        }
                    }else{
                        mBaseDataImg.setVisibility(View.VISIBLE);
                    }
                    Utils.stopProgressDialog();
                    break;
            }
            return false;
        }
    });

    private CompoundButton.OnCheckedChangeListener mCheckChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mShare.put(SHARE_DOWNLOAD_NEED_CLEAR,isChecked);
            mShare.commit();
            mIsNeedClear = isChecked;
        }
    };

    private void intiView(View root){
        mBaseDataContext = (TextView) root.findViewById(R.id.sync_data_base_context);
        mBaseDataImg = (ImageView) root.findViewById(R.id.sync_data_base_img);
        mBaseDataCheck = (CheckBox) root.findViewById(R.id.sync_data_base_checkbox);
        mBaseDataCheck.setOnClickListener(this);
        mContactContext = (TextView) root.findViewById(R.id.sync_data_contact_context);
        mContactImg = (ImageView) root.findViewById(R.id.sync_data_contact_img);
        mContactCheck = (CheckBox) root.findViewById(R.id.sync_data_contact_checkbox);
        mContactCheck.setOnClickListener(this);
        mScheduleContext = (TextView) root.findViewById(R.id.sync_data_schedule_context);
        mScheduleImg = (ImageView) root.findViewById(R.id.sync_data_schedule_img);
        mScheduleCheck = (CheckBox) root.findViewById(R.id.sync_data_schedule_checkbox);
        mScheduleCheck.setOnClickListener(this);
        mInquestLayout = root.findViewById(R.id.sync_data_inquest_layout);
        mInquestLayout.setOnClickListener(this);
        mInquestContext = (TextView) root.findViewById(R.id.sync_data_inquest_context);
        mInquestImg = (ImageView) root.findViewById(R.id.sync_data_inquest_shrink);
        mInquestList = (ListViewInScrollView) root.findViewById(R.id.sync_history_data_list);
        mInquestListAdapter = new InquestListAdapter(getActivity());
        mInquestList.setAdapter(mInquestListAdapter);
        mNeedClearData = (CheckBox) root.findViewById(R.id.sync_data_clear_data);
        if(mIsNeedClear){
            mNeedClearData.setChecked(true);
        }
        mNeedClearData.setOnCheckedChangeListener(mCheckChangedListener);
        mStartDownload = (Button) root.findViewById(R.id.sync_data_start_donwload_btn);
        mStartDownload.setOnClickListener(this);
        updateChecked();
        updateDataContextColor();
    }

    private void initIquestListData(){
        for (int i = 0;i < INQUEST_LIST_SHOW_COUNT;i++){
            int[] dates = Utils.getAppointDate(i);
            InquestListItemData data = new InquestListItemData();
            data.setDate(dates[0] + (dates[1] < 10 ? "0" + dates[1] : "" + dates[1]));
            data.setDateShow(dates[0] + "年" + dates[1] + "月 勘验数据");
            data.setDataDownloadUrl("");
            mInquestListDatas.add(data);
        }
    }

    private void updateChecked(){
        switch (mCurrentDownloadSeelected){
            case DOWNLOAD_BASE_SELECTED:
                mBaseDataCheck.setChecked(true);
                mContactCheck.setChecked(false);
                mScheduleCheck.setChecked(false);
                break;
            case DOWNLOAD_CONTACT_SELECTED:
                mBaseDataCheck.setChecked(false);
                mContactCheck.setChecked(true);
                mScheduleCheck.setChecked(false);
                break;
            case DOWNLOAD_SCHEDULE_SELECTED:
                mBaseDataCheck.setChecked(false);
                mContactCheck.setChecked(false);
                mScheduleCheck.setChecked(true);
                break;
            default:
                mBaseDataCheck.setChecked(false);
                mContactCheck.setChecked(false);
                mScheduleCheck.setChecked(false);
                break;
        }
        mShare.put(SHARE_DOWNLOAD_SELECTED,mCurrentDownloadSeelected);
        mShare.commit();
    }

    private void updateDataContextColor(){
        switch (mCurrentDownloadSeelected){
            case DOWNLOAD_BASE_SELECTED:
                mBaseDataContext.setTextColor(getResources().getColor(R.color.text_common_blue_color));
                mContactContext.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                mScheduleContext.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                break;
            case DOWNLOAD_CONTACT_SELECTED:
                mBaseDataContext.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                mContactContext.setTextColor(getResources().getColor(R.color.text_common_blue_color));
                mScheduleContext.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                break;
            case DOWNLOAD_SCHEDULE_SELECTED:
                mBaseDataContext.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                mContactContext.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                mScheduleContext.setTextColor(getResources().getColor(R.color.text_common_blue_color));
                break;
            default:
                mBaseDataContext.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                mContactContext.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                mScheduleContext.setTextColor(getResources().getColor(R.color.text_common_gray_color));
                break;
        }
    }

    private class InquestListAdapter extends BaseAdapter{
        private Context mContext;

        public InquestListAdapter(Context context) {
            super();
            mContext = context;
        }

        @Override
        public int getCount() {
            return mInquestListDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mInquestListDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.sync_data_inquest_list_item,null);
            TextView name = (TextView)convertView.findViewById(R.id.inquest_item_name_text);
            name.setText(mInquestListDatas.get(position).getDateShow());
            CheckBox check = (CheckBox) convertView.findViewById(R.id.inquest_item_check);
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentDownloadSeelected = position + 3;
                    notifyDataSetChanged();
                    mShare.put(SHARE_DOWNLOAD_SELECTED,mCurrentDownloadSeelected);
                    mShare.commit();
                    updateChecked();
                    updateDataContextColor();
                }
            });
            if(mCurrentDownloadSeelected - 3 == position){
                check.setChecked(true);
            }else{
                check.setChecked(false);
            }
            return convertView;
        }
    }

    /**
     * class for inquest list data
     * */
    private class InquestListItemData{
        private String date;
        private String dateShow;
        private String dataDownloadUrl;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDateShow() {
            return dateShow;
        }

        public void setDateShow(String dateShow) {
            this.dateShow = dateShow;
        }

        public String getDataDownloadUrl() {
            return dataDownloadUrl;
        }

        public void setDataDownloadUrl(String dataDownloadUrl) {
            this.dataDownloadUrl = dataDownloadUrl;
        }
    }


    private void initSyncDataState(){
        StringMap map = new StringMap();
        map.putString("token",mShare.getString("token",""));
        String json = "{\"coredata\":\""+ mShare.getString(Utils.SHARE_SYNC_BASE_DATA_CONDITION,"") +"\",\"addresslist\":\"" + mShare.getString(Utils.SHARE_SYNC_CONTACT_CONDITION,"") +
                "\",\"schedule\":\"" + mShare.getString(Utils.SHARE_SYNC_SCHEDULE_CONDITION,"") + "\"}";
        map.putString("condition",json);
        Netroid.PostHttp("/checkBasedataUpdate", map, new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONObject result = response.getJSONObject("data");
                    iscoredataTime = "1".equals(result.getString("coredata"));
                    isaddresslistTime = "1".equals(result.getString("addresslist"));
                    isscheduleTime = "1".equals(result.getString("schedule"));
                } catch (JSONException e) {
                    Log.i("zhangsh","onSuccess exception",e);
                    iscoredataTime = false;
                    isaddresslistTime = false;
                    isscheduleTime = false;
                }
                if(iscoredataTime){
                    mBaseDataImg.setVisibility(View.VISIBLE);
                }else{
                    mBaseDataImg.setVisibility(View.GONE);
                }
                if(isaddresslistTime){
                    mContactImg.setVisibility(View.VISIBLE);
                }else{
                    mContactImg.setVisibility(View.GONE);
                }
                if(isscheduleTime){
                    mScheduleImg.setVisibility(View.VISIBLE);
                }else{
                    mScheduleImg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(NetroidError error) {
                Log.i("zhangsh","onError",error);
                iscoredataTime = false;
                isaddresslistTime = false;
                isscheduleTime = false;
                mBaseDataImg.setVisibility(View.GONE);
                mContactImg.setVisibility(View.GONE);
                mScheduleImg.setVisibility(View.GONE);
            }
        });
    }

    private void startDownload(){
        AjaxParams params = new AjaxParams();
        params.put("token",mShare.getString("token",""));
        if(mCurrentDownloadSeelected >= 3) {
            //mStartTime = Utils.getAppointDate(mCurrentDownloadSeelected - 3);
            mStartTime = Utils.getAppointMonthString(mCurrentDownloadSeelected - 3);
        }else{
            mStartTime = null;
        }
        String json = "";
        switch (mCurrentDownloadSeelected){
            case DOWNLOAD_BASE_SELECTED:
                if(mIsNeedClear) {
                    json = "{\"dataType\":\"coredata\",\"startTime\":\"\",\"cascading\":\"n\"}";
                }else{
                    json = "{\"dataType\":\"coredata\",\"startTime\":\"" + mShare.getString(Utils.SHARE_SYNC_BASE_DATA_CONDITION,"") + "\",\"cascading\":\"n\"}";
                }
                params.put("condition",json);
                Utils.downloadBaseData(getActivity(),DOWNLOAD_DATA_URL,params,Utils.BASE_DATA_FILE_PATH,mHandler,mIsNeedClear);
                break;
            case DOWNLOAD_CONTACT_SELECTED:
                if(mIsNeedClear) {
                    json = "{\"dataType\":\"addresslist\",\"startTime\":\"\",\"cascading\":\"n\"}";
                }else{
                    json = "{\"dataType\":\"addresslist\",\"startTime\":\"" + mShare.getString(Utils.SHARE_SYNC_CONTACT_CONDITION,"") + "\",\"cascading\":\"n\"}";
                }
                break;
            case DOWNLOAD_SCHEDULE_SELECTED:
                if(mIsNeedClear) {
                    json = "{\"dataType\":\"schedule\",\"startTime\":\"\",\"cascading\":\"n\"}";
                }else{
                    json = "{\"dataType\":\"schedule\",\"startTime\":\"" + mShare.getString(Utils.SHARE_SYNC_SCHEDULE_CONDITION,"") + "\",\"cascading\":\"n\"}";
                }
                break;
            case DOWNLOAD_INQUEST_FIRSTMONTH:
            case DOWNLOAD_INQUEST_SECONDMONTH:
            case DOWNLOAD_INQUEST_THIRDMONTH:
            case DOWNLOAD_INQUEST_FOURTHMONTH:
            case DOWNLOAD_INQUEST_FIFTHMONTH:
            case DOWNLOAD_INQUEST_SIXTHMONTH:
                String endTime = Utils.getAppointMonthString(1);
                if(mStartTime != null){
                    json = "{\"dataType\":\"investigate\",\"startTime\":\"" + endTime + "\",\"endTime\":\"" + mStartTime + "\",\"cascading\":\"y\"}";
                    //json = "{\"dataType\":\"investigate\",\"startTime\":\"2016-05-01\",\"endTime\":\"2016-06-01\",\"cascading\":\"y\"}";
                }
                params.put("condition",json);
                Utils.downloadHistory(getActivity(),DOWNLOAD_DATA_URL,params,Utils.HISTORY_DATA_FILE_PATH_GZ,mHandler,"HistoryCase");
                break;
        }
    }

    private void saveCoredataTime(String time){
        mShare.put(Utils.SHARE_SYNC_BASE_DATA_CONDITION,time);
        mShare.commit();
        User user = EvidenceApplication.db.findAllByWhere(User.class,"userId = \"" + mShare.getString("user_id" +
                "","") + "\"").get(0);
        user.setCoredataUpdateTime(time);
        EvidenceApplication.db.update(user);
    }

    private void saveAddressListTime(String time){
        mShare.put(Utils.SHARE_SYNC_CONTACT_CONDITION,time);
        mShare.commit();
        User user = EvidenceApplication.db.findAllByWhere(User.class,"userId = \"" + mShare.getString("userId","") + "\"").get(0);
        user.setAddresslistUpdateTime(time);
        EvidenceApplication.db.update(user);
    }

    private void saveScheduleTime(String time){
        mShare.put(Utils.SHARE_SYNC_SCHEDULE_CONDITION,time);
        mShare.commit();
        User user = EvidenceApplication.db.findAllByWhere(User.class,"userId = \"" + mShare.getString("userId","") + "\"").get(0);
        user.setScheduleUpdateTime(time);
        EvidenceApplication.db.update(user);
    }

}
