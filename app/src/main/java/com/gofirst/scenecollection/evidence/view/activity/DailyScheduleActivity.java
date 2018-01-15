package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.HyOrganizations;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.ScheduleEmployee;
import com.gofirst.scenecollection.evidence.utils.ScheduleObject;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.view.customview.CalendarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import android.widget.CalendarView;

/**
 * Created by Administrator on 2016/6/29.
 */
public class DailyScheduleActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "DailyScheduleActivity";
    private ImageView mBackImg;
    private TextView mTitleText;

    private Button mBtnPostSelect;
    private Button mDateSelect;
    private DataSelectActivity mPopDateSelect;
    private DateSelectAdapter mDateSelectAdapter;
    private HyOrganizations mCurrentHyOrg;
    private SharePre mSharePre;
    private String mCurrentPositionId;
    private String mCurrentDate;

    private Spinner mSpinnerSchedulePolicePosition = null;
    private Spinner mSpinnerScheduleDate = null;
    //private CalendarView mCalenderSchedule = null;
    private CalendarView mCalenderSchedule = null;
    private TextView mTVDailyScheduleShow = null;
    private ListView mLVDailySchedulePersons = null;

    private static int mCurrentDatePosition = -1;
    private static int mCurrentPolicePosition = -1;
    private static String mSelectDate = "";
    //hyorganization list
    private static ArrayList<HyOrganizations> mHyOrganizations = new ArrayList<HyOrganizations>();
    private static ArrayList<String> mOrgSpinners = new ArrayList<String>();

    private static ArrayList<ScheduleObject> mScheduleObjects = new ArrayList<ScheduleObject>();
    //private static ArrayList<ScheduleObject> mSelectScheduleObjects = new ArrayList<ScheduleObject>();
    private static ArrayList<ScheduleEmployee> mInitScheduleEmployees = new ArrayList<ScheduleEmployee>();
    private static ArrayList<ScheduleEmployee> mCurrentScheduleEmployees = new ArrayList<ScheduleEmployee>();
    private static ArrayList<String> mScheduleEmployeeNames = new ArrayList<String>();
    private int mSelectDay = 0;

    private boolean mIsDefaultDate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.daily_schedule_layout);
        mSharePre = new SharePre(this, "user_info", Context.MODE_PRIVATE);
        mCurrentPositionId = mSharePre.getString("organizationId","");
        new GetScheDuleThread().start();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_schedule_police_position:
                Intent intent = new Intent("com.gofirst.scenecollection.evidence.view.activity.OrganizationsSelectActivity");
                startActivityForResult(intent,1000);
                break;
            case R.id.btn_schedule_date:
                if(mDateSelectAdapter == null) {
                    mDateSelectAdapter = new DateSelectAdapter(DailyScheduleActivity.this, dateList);
                    mPopDateSelect.setmListener(mDateSelectAdapter);
                }
                mPopDateSelect.setItemClick(mDateSelectItemClick);
                mPopDateSelect.showAtLocation(findViewById(R.id.daily_main_layout),Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.secondary_back_img:
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null){
            return;
        }
        Log.i(TAG,"onActivityResult resultCode = " + resultCode + ";select_org_id = " + data.getStringExtra("select_org_id"));
        mCurrentPositionId = data.getStringExtra("select_org_id");
        mBtnPostSelect.setText(data.getStringExtra("select_org_name"));
        mCurrentHyOrg = EvidenceApplication.db.findAllByWhere(HyOrganizations.class,"organizationId = \"" + mCurrentPositionId + "\"").get(0);
        getScheDuleLists(mCurrentPositionId,mCurrentDate);
    }

    /**
     * methods
     * */
    List<String> dateList = new ArrayList<String>();
    private void initView(){
        mBackImg = (ImageView) findViewById(R.id.secondary_back_img);
        mBackImg.setOnClickListener(this);
        mTitleText = (TextView) findViewById(R.id.secondary_title_tv);
        mTitleText.setText("日常排班");

        mCurrentHyOrg = EvidenceApplication.db.findAllByWhere(HyOrganizations.class,"organizationId = \"" + mSharePre.getString("organizationId","") + "\"").get(0);
        mBtnPostSelect = (Button) findViewById(R.id.btn_schedule_police_position);
        mBtnPostSelect.setOnClickListener(this);
        mBtnPostSelect.setText(mCurrentHyOrg.getOrganizationName());
        initDatelist();
        mDateSelect = (Button) findViewById(R.id.btn_schedule_date);
        mDateSelect.setText(dateList.get(1).toString());
        mCurrentDate = dateList.get(1).toString();
        mDateSelect.setOnClickListener(this);
        mPopDateSelect = new DataSelectActivity(this);

        //date spinner
        mSpinnerScheduleDate = (Spinner) findViewById(R.id.spinner_schedule_date);
        //mSpinnerScheduleDate.setAdapter(new ArrayAdapter<String>(this,R.layout.spinner_item_tv,
        //        dateList));
        //mSpinnerScheduleDate.setOnItemSelectedListener(mDateListener);
        //mSpinnerScheduleDate.setSelection(1,false);
        //position spinner
        mSpinnerSchedulePolicePosition = (Spinner)findViewById(R.id.spinner_schedule_police_position);
        //mSpinnerSchedulePolicePosition.setAdapter(new ArrayAdapter<String>(this,R.layout.spinner_item_tv,
        //        mOrgSpinners));
        //mSpinnerSchedulePolicePosition.setOnItemSelectedListener(mPostitionListener);
        //show textview
        mTVDailyScheduleShow = (TextView) findViewById(R.id.tv_daily_schedule_show);
        mTVDailyScheduleShow.setText(getCurrentDate());
        //calendarview
        mCalenderSchedule = (CalendarView) findViewById(R.id.calender_schedule);
        mCalenderSchedule.setOnItemClickListener(new CalendarView.OnItemClickListener() {
            @Override
            public void OnItemClick(Date date) {
                mSelectDate = "" + (1900 + date.getYear()) +(date.getMonth() + 1) + date.getDate();
                mSelectDay = date.getDate() - 1;
                if(mSelectDay >= mScheduleObjects.size()){
                    //mTVDailyScheduleShow.setText("");
                    return;
                }
                mCurrentScheduleEmployees = (ArrayList<ScheduleEmployee>)mScheduleObjects.get(mSelectDay).getScheduleEmployees().clone();
                if(View.VISIBLE != mTVDailyScheduleShow.getVisibility()){
                    mTVDailyScheduleShow.setVisibility(View.VISIBLE);
                }
                if(mCurrentScheduleEmployees.size() > 0) {
                    mTVDailyScheduleShow.setText((1900 + date.getYear()) + "年" + (date.getMonth() + 1) +
                            "月" + date.getDate() + "日值班人员：");
                }else{
                    mTVDailyScheduleShow.setText((1900 + date.getYear()) + "年" + (date.getMonth() + 1) +
                            "月" + date.getDate() + "日值班人员：     未排班");
                }
                mLVDailySchedulePersons.setAdapter(new ListViewAdapter(DailyScheduleActivity.this));
            }
        });
        //mCalenderSchedule.clickToMonth(getCurrentYear(),getCurrentMonth());
        getScheDuleLists(mCurrentPositionId,mCurrentDate);
        //show listview
        mLVDailySchedulePersons = (ListView) findViewById(R.id.lv_daily_schedule_persons);
        //mLVDailySchedulePersons.setAdapter(new ListViewAdapter(this));
        mLVDailySchedulePersons.setDivider(null);
    }

    private class GetScheDuleThread extends Thread{
        public GetScheDuleThread(){
            getOrganizations();
        }

        @Override
        public void run() {

        }
    }

    void getScheDuleLists(String id,String sj){
        Log.i("zhangsh","getScheDuleLists id = " + id + ";sj = " + sj);
        if(sj.isEmpty()){
            sj = "201608";
        }
        String month = sj.substring(6, sj.length() - 1).trim();
        StringMap params = new StringMap();
        params.putString("depid",TextUtils.isEmpty(id)?"1":id);//id.isEmpty()?"1":id);
        params.putString("sj",sj.substring(0, 4) + (month.length() == 1 ? ("0" + month) : month));
        Netroid.PostHttp("/schedule", params, new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray schedules = response.getJSONArray("data");
                    JSONObject jsonObj = null;
                    Log.i(TAG,"getSchedule onSuccess schedules = " + schedules.toString());
                    //createFile(schedules.toString(),"/textFiles.txt");
                    int size = schedules.length();
                    Date date = new Date();
                    StringBuilder dayEmployees = null;   //every day employees
                    mScheduleObjects.clear();
                    mScheduleEmployeeNames.clear();
                    for (int i = 0;i < size; i++){
                        jsonObj = (JSONObject) schedules.get(i);
                        ScheduleObject object = new ScheduleObject();
                        String s = "";
                        s = jsonObj.getString("white_start");
                        if(!s.isEmpty()) {
                            date.setTime(Long.valueOf(s));
                            object.setWhite_start(date.getHours() + date.getMinutes() + "");
                        }
                        s = jsonObj.getString("white_end");
                        if(!s.isEmpty()) {
                            date.setTime(Long.valueOf(s));
                            object.setWhite_end(date.getHours() + date.getMinutes() + "");
                        }
                        s = jsonObj.getString("black_start");
                        if(!s.isEmpty()) {
                            date.setTime(Long.valueOf(s));
                            object.setBlack_start(date.getHours() + date.getMinutes() + "");
                        }
                        s = jsonObj.getString("black_end");
                        if(!s.isEmpty()) {
                            date.setTime(Long.valueOf(s));
                            object.setBlack_end(date.getHours() + date.getMinutes() + "");
                        }
                        object.setMouth_data(jsonObj.getString("mouth_data"));
                        object.setWeek(jsonObj.getString("week"));

                        JSONArray dutyEmps = jsonObj.getJSONArray("white");
                        JSONObject dutyEmpObj = null;
                        int length = dutyEmps.length();
                        ScheduleEmployee employee = null;
                        mInitScheduleEmployees.clear();
                        dayEmployees = new StringBuilder();
                        for(int j = 0;j < length;j++){
                            dutyEmpObj = (JSONObject) dutyEmps.get(j);
                            employee = new ScheduleEmployee();
                            employee.setMouth_data(jsonObj.getString("mouth_data"));
                            employee.setEmployeeId(dutyEmpObj.getString("employeeId"));
                            employee.setEmployeeName(dutyEmpObj.getString("employeeName"));
                            dayEmployees.append(dutyEmpObj.getString("employeeName").substring(0,1));
                            employee.setEmployeeTel(dutyEmpObj.getString("employeeTel"));
                            employee.setDutyType("白班");
                            mInitScheduleEmployees.add(employee);
                        }
                        dutyEmps = jsonObj.getJSONArray("black");
                        length = dutyEmps.length();
                        Log.i("zhangsh","length2 = " + length);
                        for(int j = 0;j < length;j++){
                            dutyEmpObj = (JSONObject) dutyEmps.get(j);
                            employee.setMouth_data(jsonObj.getString("mouth_data"));
                            employee.setEmployeeId(dutyEmpObj.getString("employeeId"));
                            employee.setEmployeeName(dutyEmpObj.getString("employeeName"));
                            dayEmployees.append(dutyEmpObj.getString("employeeName").substring(0,1));
                            employee.setEmployeeTel(dutyEmpObj.getString("employeeTel"));
                            employee.setDutyType("夜班");
                            mInitScheduleEmployees.add(employee);
                        }
                        object.setScheduleEmployees((ArrayList<ScheduleEmployee>)mInitScheduleEmployees.clone());
                        mScheduleObjects.add(object); //.add(object);
                        mScheduleEmployeeNames.add(dayEmployees.toString());
                    }
                    updateCalendarView();
                }catch (JSONException je){
                    Log.i(TAG,"get http schedule onSuccess JSONException",je);
                }
            }

            @Override
            public void onError(NetroidError error) {
                Log.i(TAG,"get http schedule onError",error);
            }
        });
    }

    private void updateCalendarView(){
        mCalenderSchedule.setDaysShow((ArrayList<String>)mScheduleEmployeeNames.clone());
        //mCalenderSchedule.clickToMonth(Integer.valueOf(mSelectDate.substring(0,4)),Integer.valueOf(mSelectDate.substring(4,6)));
        mCalenderSchedule.clickToMonth(Integer.valueOf(mCurrentDate.substring(0,4)),Integer.valueOf(mCurrentDate.substring(6,mCurrentDate.length() - 1)));
        if(mIsDefaultDate){
            mCalenderSchedule.setDefaultItemClick(new Date());
            mIsDefaultDate = false;
        }
    }

    String getCurrentDate(){
        String s = "";
        Date d = new Date();
        //SimpleDateFormat date = new SimpleDateFormat("yyyy年MM月dd日");
        //s += date.format(d) + "值班人员：";
        s += (1900 + d.getYear()) + "年" + (d.getMonth() + 1) + "月" + d.getDate() + "日值班人员：";
        return s;
    }

    String getCurrentYearAndMonth(){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        String month = format.format(date);
        return month;
    }
    int getCurrentMonth(){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM");
        return Integer.valueOf(format.format(date));
    }

    public static SQLiteDatabase getSQLiteDatabase(Context context) {

        String path = "/evidence.db";
        File file = new File(AppPathUtil.getDBPath() + path);
        return SQLiteDatabase.openOrCreateDatabase(file.getAbsoluteFile(), null);
    }

    void getOrganizations(){
        SQLiteDatabase database = getSQLiteDatabase(this);
        Cursor cursor = database.query("HyOrganizations",new String[]{"organizationId,organizationCname,organizationName"},null,null,null,null,"id asc");
        mHyOrganizations.clear();
        mOrgSpinners.clear();
        while (cursor.moveToNext()){
            HyOrganizations org = new HyOrganizations();
            org.setOrganizationId(cursor.getInt(cursor.getColumnIndex("organizationId")));
            mOrgSpinners.add(cursor.getString(cursor.getColumnIndex("organizationName")));
            org.setOrganizationCname(cursor.getString(cursor.getColumnIndex("organizationCname")));
            org.setOrganizationName(cursor.getString(cursor.getColumnIndex("organizationName")));
            mHyOrganizations.add(org);
        }
        cursor.close();
    }

    // police positon item selected listener
    private AdapterView.OnItemSelectedListener mPostitionListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(mCurrentDatePosition != -1 && mCurrentPolicePosition != -1) {
                String selectDate = dateList.get(mCurrentDatePosition).toString().trim();
                String month = selectDate.substring(6, selectDate.length() - 1).trim();
                getScheDuleLists(String.valueOf(mHyOrganizations.get(position).getOrganizationId()),
                        selectDate.substring(0, 4) + (month.length() == 1 ? ("0" + month) : month));
            }
            mCurrentPolicePosition = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    // date select item selected listener
    private AdapterView.OnItemSelectedListener mDateListener = new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selectDate = dateList.get(position).toString().trim();
            String month = selectDate.substring(6,selectDate.length() - 1).trim();
            mSelectDate = selectDate.substring(0, 4) + (month.length() == 1 ? ("0" + month) : month);
            //mCalenderSchedule.clickToMonth(year,month);
            if(mCurrentDatePosition != -1 && mCurrentPolicePosition != -1) {
                getScheDuleLists(String.valueOf(mHyOrganizations.get(mCurrentPolicePosition).getOrganizationId()),
                        mSelectDate);
            }
            mCurrentDatePosition = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private class ListViewAdapter extends BaseAdapter{
        private LayoutInflater inflater;
        private Context context;

        public ListViewAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            int count = 0;
            if(mScheduleObjects != null && mScheduleObjects.size() > 0) {
                ScheduleObject obj = mScheduleObjects.get(mSelectDay);
                if(obj.getScheduleEmployees() != null){
                    count = obj.getScheduleEmployees().size();
                }
            }
            Log.i(TAG,"getCount count = " + count);
            return count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.daily_schedule_listitem_layout,null);
                holder.shift = (TextView)convertView.findViewById(R.id.item_daynight);
                holder.shiftDetail = (TextView) convertView.findViewById(R.id.item_schedule_detail);
                holder.btnCall = (ImageButton) convertView.findViewById(R.id.img_schedule_call);
                holder.btnSms = (ImageButton) convertView.findViewById(R.id.img_schedule_sms);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            if(position%2 ==0) {
                convertView.setBackgroundColor(Color.BLACK);
            }
            ScheduleEmployee employee = mCurrentScheduleEmployees.get(position);
            holder.shift.setText(employee.getDutyType());
            //holder.shift.setTextColor(context.getResources().getColor(R.color.milky_white));
            String dutyTimes = "";
            if ("白班".equals(employee.getDutyType())){
                dutyTimes = mScheduleObjects.get(mSelectDay).getWhite_start() +
                        mScheduleObjects.get(mSelectDay).getWhite_end();
            }else{
                dutyTimes = mScheduleObjects.get(mSelectDay).getBlack_start() +
                        mScheduleObjects.get(mSelectDay).getBlack_end();
            }
            holder.shiftDetail.setText(employee.getEmployeeName() + "  值班时间 " + dutyTimes);
            holder.btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"btnCall onClick position = " + position + ";call = " + mCurrentScheduleEmployees.get(position).getEmployeeTel());
                    String num = mCurrentScheduleEmployees.get(position).getEmployeeTel();
                    if(num != null && !"".equals(num)) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + num));
                        startActivity(intent);
                    }
                }
            });
            holder.btnSms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"btnSms onClick position = " + position + ";call = " + mCurrentScheduleEmployees.get(position).getEmployeeTel());
                }
            });
            return convertView;
        }
    }

    private class ViewHolder{
        TextView shift;
        TextView shiftDetail;
        ImageButton btnCall;
        ImageButton btnSms;
    }

    private void createFile(String file,String fileName){
        File newFile = new File(Environment.getExternalStorageDirectory(),fileName);
        try {
            FileOutputStream fop = new FileOutputStream(newFile);
            fop.write(file.getBytes("UTF-8"));
            fop.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * init date list
     * */
    private void initDatelist(){
        long currentTime = System.currentTimeMillis();
        dateList.clear();
        Date date = new Date(currentTime);
        int year = 1900 + date.getYear();
        int month = date.getMonth();
        if(month == 0){
            dateList.add((year -1) + "年，12月");
        }else{
            dateList.add(year + "年，" + month + "月");
        }
        dateList.add(year + "年，" + (month + 1) + "月");
        if(month == 11){
            dateList.add((year + 1) + "年，1月");
        }else{
            dateList.add(year + "年，" + (month + 2)+ "月");
        }
    }

    /**
     * date popwindow list adapter
     * */
    private class DateSelectAdapter extends BaseAdapter{
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
    /**
     * date popwindow list onitemclick
     * */
    private AdapterView.OnItemClickListener mDateSelectItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i("zhangsh","onItemClick date = " + dateList.get(position));
            if (!TextUtils.equals(mCurrentDate,dateList.get(position)))
                mTVDailyScheduleShow.setText("请选择一个值班日期");
            mCurrentDate = dateList.get(position);
            mDateSelect.setText(mCurrentDate);
            getScheDuleLists(mCurrentPositionId,mCurrentDate);
            mPopDateSelect.dismiss();
        }
    };
}
