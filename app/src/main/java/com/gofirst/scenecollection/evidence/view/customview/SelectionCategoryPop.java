package com.gofirst.scenecollection.evidence.view.customview;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.utils.Utils;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/9/2.
 */
public class SelectionCategoryPop extends PopupWindow implements View.OnClickListener{

    private Activity mActivity;
    protected View mMainView;
    private View mRootView;
    private boolean mIsInquest = false;

    //criminal
    //private Button mSelectionCriminalAll;
    //private Button mSelectionCriminalYes;
    //private Button mSelectionCriminalNo;
    //murder
    //private Button mSelectionMurderAll;
    //private Button mSelectionMurderYes;
    //private Button mSelectionMurderNo;
    //flip
    //private GridView mFlipGrid;
    //private List<CsDicts> mFlipDatas = new ArrayList<>();
    //private FlipGridAdapter mFlipGridAdapter;
    //prospect
    //private GridView mProsepctGrid;
    //private List<HyEmployees> mProspectDats = new ArrayList<>();
    //private ProspectGridAdapter mProspectGridAdapter;

    //reset and save button click
    private HashMap<String,String> mSaveDatas = new HashMap<>();

    private Button mResetBtn;
    private Button mSaveBtn;
    private SelectionPopInterFace mPopInterface;
    //private String[] mSceneDates = {"不限","当天","近三天","近一周","近一个月","近两个月"};
    private String[] mSceneDates = {"不限","当天","近三天","近一周","近一个月"};
    private int mSceneSelected = 0;
    private GridView mSceneDate;
    private SceneDateAdapter mAdapter;
    private String mDateSelected = "";
    private EditText mAddress;
    private boolean mShowSceneType = false;
    private View mSceneTypeViewLine;
    private TextView mSceneTypeTextView;
    private EditText mSceneType;
    private EditText mSceneEmployee;
    private TextView select_time;//选择报警时间
    private int year, monthOfYear, dayOfMonth, hourOfDay, minute,dateToTemp;
    public interface SelectionPopInterFace{
        void resetButtonClick(HashMap<String,String> results);
        void saveButtonClick(HashMap<String,String> results);
    }

    public void setSelectionPopInterFace(SelectionPopInterFace interFace){
        this.mPopInterface = interFace;
    }

    public SelectionCategoryPop(Activity activity,boolean inquest,boolean showSceneType) {
        super(activity);
        this.mActivity = activity;
        this.mIsInquest = inquest;
        this.mShowSceneType = showSceneType;
        mMainView = LayoutInflater.from(mActivity).inflate(R.layout.selection_category_pop_layout,null);
        this.setContentView(mMainView);
        //this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setWidth(Utils.dp2Px(mActivity,269));
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        //ColorDrawable dw = new ColorDrawable(Color.argb(128,0,0,0));
        ColorDrawable dw = new ColorDrawable(Color.argb(255,0,0,0));
        this.setBackgroundDrawable(dw);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.setAnimationStyle(R.style.category_pop_show_style);
        initDatas();

        select_time =(TextView) mMainView.findViewById(R.id.select_time);
        select_time.setOnClickListener(this);

        mRootView = mMainView.findViewById(R.id.selection_pop_layout);
        mRootView.setOnClickListener(this);
        if(mIsInquest){
            ((TextView)mMainView.findViewById(R.id.create_date_tv)).setText("勘验时间");
            ((TextView)mMainView.findViewById(R.id.employee_tv)).setText("勘验人");
        }else{
            ((TextView)mMainView.findViewById(R.id.create_date_tv)).setText("报警时间");
            ((TextView)mMainView.findViewById(R.id.employee_tv)).setText("报警人");
        }
        mAddress = (EditText) mMainView.findViewById(R.id.selection_scene_address);
        mSceneTypeViewLine = mMainView.findViewById(R.id.selection_scene_type_view_line);
        mSceneTypeTextView = (TextView) mMainView.findViewById(R.id.selection_scene_type_tv);
        mSceneType = (EditText) mMainView.findViewById(R.id.selection_scene_type);
        if(mShowSceneType){
            mSceneTypeTextView.setVisibility(View.VISIBLE);
            mSceneType.setVisibility(View.VISIBLE);
            mSceneTypeViewLine.setVisibility(View.VISIBLE);
        }
        mSceneEmployee = (EditText) mMainView.findViewById(R.id.selection_scene_employee);
        mSceneDate = (GridView) mMainView.findViewById(R.id.selection_screen_inquest_date);
        mAdapter = new SceneDateAdapter(activity);
        mSceneDate.setAdapter(mAdapter);

        /*mSelectionCriminalAll = (Button)mMainView.findViewById(R.id.selection_criminal_all);
        mSelectionCriminalAll.setOnClickListener(this);
        mSelectionCriminalYes = (Button)mMainView.findViewById(R.id.selection_criminal_yes);
        mSelectionCriminalYes.setOnClickListener(this);
        mSelectionCriminalNo = (Button)mMainView.findViewById(R.id.selection_criminal_no);
        mSelectionCriminalNo.setOnClickListener(this);
        mSelectionMurderAll = (Button)mMainView.findViewById(R.id.selection_murder_all);
        mSelectionMurderAll.setOnClickListener(this);
        mSelectionMurderYes = (Button)mMainView.findViewById(R.id.selection_murder_yes);
        mSelectionMurderYes.setOnClickListener(this);
        mSelectionMurderNo = (Button)mMainView.findViewById(R.id.selection_murder_no);
        mSelectionMurderNo.setOnClickListener(this);
        setCriminalButtonBg();
        setMurderButtonBg();
        mFlipGrid = (GridView) mMainView.findViewById(R.id.selection_screen_flip_grid);
        mFlipGridAdapter = new FlipGridAdapter(mActivity,mFlipDatas);
        mFlipGrid.setAdapter(mFlipGridAdapter);
        mProsepctGrid = (GridView) mMainView.findViewById(R.id.selection_screen_prospect_grid);
        mProspectGridAdapter = new ProspectGridAdapter(mActivity,mProspectDats);
        mProsepctGrid.setAdapter(mProspectGridAdapter);*/

        mResetBtn = (Button) mMainView.findViewById(R.id.selection_screen_reset_btn);
        mResetBtn.setOnClickListener(this);
        mSaveBtn = (Button) mMainView.findViewById(R.id.selection_screen_save_btn);
        mSaveBtn.setOnClickListener(this);

       /* if(select_time.getText().toString()!=null&&select_time.getText().toString().length()>0) {
            Calendar calendar = Calendar.getInstance();
            String nowTime[]=select_time.getText().toString().split("-");

            year = Integer.parseInt(nowTime[0]);
            monthOfYear = Integer.parseInt(nowTime[1]);
            dayOfMonth = Integer.parseInt(nowTime[2]);


        }else{
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            monthOfYear = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            //hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            //minute = calendar.get(Calendar.MINUTE);
        }*/
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //backgroundAlpha(1f,mActivity);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        if(mAdapter != null){
            mDateSelected = mSaveDatas.get("date");
            mAdapter.setSelectedItem(mDateSelected);
            mAdapter.notifyDataSetChanged();
            mAddress.setText(mSaveDatas.get("address"));
            mSceneType.setText(mSaveDatas.get("scene_type"));
            mSceneEmployee.setText(mSaveDatas.get("employee"));
        }
        //backgroundAlpha(0.8f,mActivity);
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        //backgroundAlpha(0.8f,mActivity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.selection_pop_layout:
                dismiss();
                break;
            /*case R.id.selection_criminal_all:
                mSaveDatas.put("criminal","all");
                setCriminalButtonBg();
                break;
            case R.id.selection_criminal_yes:
                mSaveDatas.put("criminal","yes");
                setCriminalButtonBg();
                break;
            case R.id.selection_criminal_no:
                mSaveDatas.put("criminal","no");
                setCriminalButtonBg();
                break;
            case R.id.selection_murder_all:
                mSaveDatas.put("murder","all");
                setMurderButtonBg();
                break;
            case R.id.selection_murder_yes:
                mSaveDatas.put("murder","yes");
                setMurderButtonBg();
                break;
            case R.id.selection_murder_no:
                mSaveDatas.put("murder","no");
                setMurderButtonBg();
                break;*/
            case R.id.selection_screen_reset_btn:
                if(mPopInterface != null){
                    resetButtonClick();
                    select_time.setText("");
                    mPopInterface.resetButtonClick(mSaveDatas);
                }
                break;
            case R.id.selection_screen_save_btn:
                if(mPopInterface != null){
                   // mSaveDatas.put("date",mDateSelected);
                    mSaveDatas.put("date",select_time.getText().toString());
                    mSaveDatas.put("address",mAddress.getText().toString().trim());
                    mSaveDatas.put("scene_type",mSceneType.getText().toString().trim());
                    mSaveDatas.put("employee",mSceneEmployee.getText().toString().trim());
                    mPopInterface.saveButtonClick(mSaveDatas);
                    dismiss();
                }
                break;
            case R.id.select_time:
               // Toast.makeText(mActivity,"hajdjaf",Toast.LENGTH_SHORT).show();

                //添加单击事件--设置日期


                if(select_time.getText().toString()!=null&&select_time.getText().toString().length()>0) {
                    Calendar calendar = Calendar.getInstance();
                    String nowTime[]=select_time.getText().toString().split("-");

                    year = Integer.parseInt(nowTime[0]);
                    monthOfYear = Integer.parseInt(nowTime[1])-1;
                    dayOfMonth = Integer.parseInt(nowTime[2]);


                }else{
                    Calendar calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    monthOfYear = calendar.get(Calendar.MONTH);
                    dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    //hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                    //minute = calendar.get(Calendar.MINUTE);
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth)
                    {
                        String daytemp="";
                        String Monthtemp="";
                        if((dayOfMonth)/10<1){
                            daytemp="0";
                        }else{
                            daytemp="";
                        }

                        if((monthOfYear + 1)/10>=1){
                            Monthtemp="";
                        }else{
                            Monthtemp="0";
                        }
                        select_time.setText( year + "-" + Monthtemp+(monthOfYear + 1) + "-" +daytemp+ dayOfMonth);
                    }
                }, year, monthOfYear, dayOfMonth);//dayOfMonth

                datePickerDialog.show();
                break;
        }
    }

    /*private void setCriminalButtonBg(){
        mSelectionCriminalAll.setBackgroundResource("all".equals(mSaveDatas.get("criminal")) ? R.drawable.selection_screen_item_selected : R.drawable.selection_screen_item_normal);
        mSelectionCriminalAll.setTextColor("all".equals(mSaveDatas.get("criminal")) ?  Color.WHITE : Color.BLACK);
        mSelectionCriminalYes.setBackgroundResource("yes".equals(mSaveDatas.get("criminal")) ? R.drawable.selection_screen_item_selected : R.drawable.selection_screen_item_normal);
        mSelectionCriminalYes.setTextColor("yes".equals(mSaveDatas.get("criminal")) ?  Color.WHITE : Color.BLACK);
        mSelectionCriminalNo.setBackgroundResource("no".equals(mSaveDatas.get("criminal")) ? R.drawable.selection_screen_item_selected : R.drawable.selection_screen_item_normal);
        mSelectionCriminalNo.setTextColor("no".equals(mSaveDatas.get("criminal")) ?  Color.WHITE : Color.BLACK);
    }

    private void setMurderButtonBg(){
        mSelectionMurderAll.setBackgroundResource("all".equals(mSaveDatas.get("murder")) ? R.drawable.selection_screen_item_selected : R.drawable.selection_screen_item_normal);
        mSelectionMurderAll.setTextColor("all".equals(mSaveDatas.get("murder")) ?  Color.WHITE : Color.BLACK);
        mSelectionMurderYes.setBackgroundResource("yes".equals(mSaveDatas.get("murder")) ? R.drawable.selection_screen_item_selected : R.drawable.selection_screen_item_normal);
        mSelectionMurderYes.setTextColor("yes".equals(mSaveDatas.get("murder")) ?  Color.WHITE : Color.BLACK);
        mSelectionMurderNo.setBackgroundResource("no".equals(mSaveDatas.get("murder")) ? R.drawable.selection_screen_item_selected : R.drawable.selection_screen_item_normal);
        mSelectionMurderNo.setTextColor("no".equals(mSaveDatas.get("murder")) ?  Color.WHITE : Color.BLACK);
    }*/

    private void resetButtonClick(){
        /*mSaveDatas.put("criminal","all");
        setCriminalButtonBg();
        mSaveDatas.put("murder","all");
        setMurderButtonBg();
        mSaveDatas.put("flip","");
        mFlipGridAdapter.setSelectedItem(0);
        mFlipGridAdapter.notifyDataSetChanged();
        mSaveDatas.put("prospect","");
        mProspectGridAdapter.setSelectedItem(0);
        mProspectGridAdapter.notifyDataSetChanged();*/
        mAdapter.setSelectedItem(0);
        mAdapter.notifyDataSetChanged();
        mDateSelected = "";
        mAddress.setText("");
        mSceneType.setText("");
        mSceneEmployee.setText("");
        initDatas();
    }

    private void initDatas(){
        mSaveDatas.clear();
        mSaveDatas.put("date","");
        mSaveDatas.put("address","");
        mSaveDatas.put("scene_type","");
        mSaveDatas.put("employee","");
        //mFlipDatas = EvidenceApplication.db.findAllByWhere(CsDicts.class,"rootKey = \"FDWPCDDM\"" );
        //mProspectDats = EvidenceApplication.db.findAllByWhere(HyEmployees.class,"employeeStatus = \"1\"");
    }

    private class SceneDateAdapter extends BaseAdapter{
        private int select = 0;
        private Context mCtx;
        public SceneDateAdapter(Context context) {
            super();
            this.mCtx = context;
        }

        public void setSelectedItem(int select){
            this.select = select;
        }

        public void setSelectedItem(String value){
            if(value == null){
                this.select = 0;
                return;
            }
            int len = mSceneDates.length;
            for(int i = 0;i < len;i++){
                String s = getSelectedDate(mSceneDates[i]);
                if(value.equals(s)){
                    this.select = i;
                    break;
                }else if("".equals(value)){
                    this.select = 0;
                    break;
                }
            }
        }

        @Override
        public int getCount() {
            return mSceneDates.length;
        }

        @Override
        public Object getItem(int position) {
            return mSceneDates[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int pos, View convertView, ViewGroup parent) {
            convertView =  LayoutInflater.from(mCtx).inflate(R.layout.selection_screen_pop_grid_item,null);
            Button btnClick = (Button) convertView.findViewById(R.id.selection_screen_item_btn);
            btnClick.setText(mSceneDates[pos]);
            btnClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSceneSelected = pos;
                    select = pos;
                    mDateSelected = getSelectedDate(mSceneDates[pos]);

                    notifyDataSetChanged();
                }
            });
            if(select == pos){
                //btnClick.setBackgroundResource(R.drawable.selection_screen_item_selected);
                btnClick.setTextColor(mCtx.getResources().getColor(R.color.selection_category_grid_item_text_press_color));
                btnClick.setBackgroundResource(R.drawable.selection_grid_item_press_bg);
            }else{
                //btnClick.setBackgroundResource(R.drawable.selection_screen_item_normal);
                btnClick.setTextColor(mCtx.getResources().getColor(R.color.selection_category_grid_item_text_normal_color));
                btnClick.setBackgroundResource(R.drawable.selection_grid_item_normal_bg);
            }
            return convertView;
        }
    }

    private String getSelectedDate(String dt){
        String result = "";
        final  Calendar calendar = Calendar.getInstance();
        //Date date = new Date(System.currentTimeMillis());
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if(mSceneDates[2].equals(dt)){
            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH) - 2);
        } else if(mSceneDates[3].equals(dt)){
            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH) - 6);
        } else if(mSceneDates[4].equals(dt)){
            calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH) - 1);
        } /*else if(mSceneDates[5].equals(dt)){
            calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH) - 2);
        }*/
        if(mSceneDates[0].equals(dt)){
            result = "";
        }else{
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            //result = String.valueOf(calendar.get(Calendar.YEAR)) + "-" + (month.length() == 1 ? ("0" + month) : month) + "-" + (day.length() == 1 ? ("0" + day) : day);
            result = String.valueOf(calendar.get(Calendar.YEAR)) + "-" + (month.length() == 1 ? ("0" + month) : month) + "-" + (day.length() == 1 ? ("0" + day) : day) + " 00:00:00";
        }
        //特殊处理
        //result = select_time.getText().toString();
        return result;
    }

/*
    private class FlipGridAdapter extends BaseAdapter{
        private List<CsDicts> flipDatas = new ArrayList<>();
        private int select = 0;
        private Context mCtx;
        public FlipGridAdapter(Context context,List<CsDicts> dicts) {
            super();
            this.mCtx = context;
            this.flipDatas = dicts;
        }

        public void setSelectedItem(int select){
            this.select = select;
        }

        @Override
        public int getCount() {
            return flipDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return flipDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int pos, View convertView, ViewGroup parent) {
            convertView =  LayoutInflater.from(mCtx).inflate(R.layout.selection_screen_pop_grid_item,null);
            Button btnClick = (Button) convertView.findViewById(R.id.selection_screen_item_btn);
            CsDicts csDicts = mFlipDatas.get(pos);
            btnClick.setText("FDWPCDDM".equals(csDicts.getDictKey()) ? "不限" : csDicts.getDictValue1());
            btnClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select = pos;
                    mSaveDatas.put("flip",mFlipDatas.get(pos).getDictKey());
                    notifyDataSetChanged();
                }
            });
            if(select == pos){
                btnClick.setBackgroundResource(R.drawable.selection_screen_item_selected);
            }else{
                btnClick.setBackgroundResource(R.drawable.selection_screen_item_normal);
            }
            return convertView;
        }
    }

    private AdapterView.OnItemClickListener mFlipClickItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(mFlipGridAdapter != null){
                mFlipGridAdapter.setSelectedItem(position);
                mFlipGridAdapter.notifyDataSetChanged();
            }
        }
    };

    private class ProspectGridAdapter extends BaseAdapter{
        private List<HyEmployees> prospectDats = new ArrayList<>();
        private int selected = 0;
        private Context mContext;
        public ProspectGridAdapter(Context context,List<HyEmployees> employees) {
            super();
            this.mContext = context;
            this.prospectDats = employees;
        }

        public void setSelectedItem(int select){
            this.selected = select;
        }

        @Override
        public int getCount() {
            return prospectDats.size();
        }

        @Override
        public Object getItem(int position) {
            return prospectDats.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.selection_screen_pop_grid_item,null);
            Button btnClick = (Button) convertView.findViewById(R.id.selection_screen_item_btn);
            btnClick.setText(mProspectDats.get(position).getEmployeeName());
            btnClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected = position;
                    mSaveDatas.put("prospect",String.valueOf(mProspectDats.get(position).getEmployeeId()));
                    notifyDataSetChanged();
                }
            });
            if(selected == position){
                btnClick.setBackgroundResource(R.drawable.selection_screen_item_selected);
            }else{
                btnClick.setBackgroundResource(R.drawable.selection_screen_item_normal);
            }
            return convertView;
        }
    }

    private AdapterView.OnItemClickListener mProspectClickItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(mProspectGridAdapter != null){
                mProspectGridAdapter.setSelectedItem(position);
                mProspectGridAdapter.notifyDataSetChanged();
            }
        }
    };*/

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha,Activity context)
    {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();

        lp.alpha = bgAlpha; //0.0-1.0
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }
}
