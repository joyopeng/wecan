package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.view.adapter.AllQueryFragmentAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.DialogDridviewAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.caseTypeDialogDridviewAdapter;
import com.gofirst.scenecollection.evidence.view.customview.PullUpRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2016/7/1.
 */
public class NvestigatQuery extends Activity implements OnClickListener{
    private TextView ActionBarText;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
    private String[] crackedDate=new String[9];


    private ListView allQuery_fragment_listview;
    private PullUpRefreshLayout refreshLayout;
    private AllQueryFragmentAdapter adapter;
    private List<AllQueryFragmentAdapter.AllQueryFragmentData> list = new ArrayList<AllQueryFragmentAdapter.AllQueryFragmentData>();
    private SharePre sharePre;
    Context context;
    private int currentCount = 0;
    private int start=0;
    private int end =2;
    private LinearLayout find;
    private ImageView finddata,more;
    private TextView startTimeText,endTimeText;
    private EditText findEdit;
    private ImageView exit;
    private Dialog UserNameDialog;
    private String[] titles = new String[]
            { "不限", "当天", "本周内", "本月内", "其他"};
    private  String[] caseType=new String[]{
            "入室盗窃","盗窃商店","盗窃单位","盗窃工地","盗窃路财","盗窃汽车","抢劫","强奸","其他盗窃"};
    private String[] area=new String []{"昆山市","张家港市","常熟市","太仓市","相城区","吴中区",
            "工业园区","高新区","吴江区","姑苏区"};
    private String startDateTemp="",endDateTemp="",prospectTemp="",statusTemp="",CaseTypeTemp="",
    caseNo,sceneDetail,areaCode,search_beforeTime,search_afterTime;
    private String conTemp;
    ;
    private EditText editCondition;
    private LinearLayout timeLinearLayout;
    private String type="timeDialog";
    private View mSearchTitleLayout; //title search layout
    private TextView mSearchTitleName;  //title name

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nvestigat_query);
        Init();
        sharePre=new SharePre(NvestigatQuery.this,"user_info",Context.MODE_PRIVATE);
        String prospectPerson=sharePre.getString("prospectPerson","");
        allQuery_fragment_listview=(ListView)findViewById(R.id.allQuery_fragment_listview);
        adapter = new AllQueryFragmentAdapter(prospectPerson,NvestigatQuery.this,list);
        allQuery_fragment_listview.setAdapter(adapter);

//        editCondition.findViewById(R.id.)
        /*refreshLayout = (PullUpRefreshLayout) findViewById(R.id.refresh_layout);
        find=(RelativeLayout)findViewById(R.id.find);

            CaseTypeTemp="";
 //           getAllData("/prospects/query");

        refreshLayout.setColorSchemeColors(R.color.pull_color);
        refreshLayout.setOnLoadListener(new PullUpRefreshLayout.onLoadListener() {
            @Override
            public void onLoad() {
                Log.d("onRefresh", "上拉");
                CaseTypeTemp = "";
                //             getAllData("/prospects/query");
            }
        });*/
    }

    public void Init(){
        /*ActionBarText = (TextView) (this.findViewById(R.id.tittle_bar))
                .findViewById(R.id.actiobar_textView);
        ActionBarText.setText("勘查查询");*/
        mSearchTitleName = (TextView)findViewById(R.id.title_bar_layout)
                            .findViewById(R.id.title_bar_tv);
        mSearchTitleName.setText("历史查询");

        //find=(LinearLayout)findViewById(R.id.find);
        //findEdit=(EditText)findViewById(R.id.find_edit);
        //finddata=(ImageView)findViewById(R.id.finddata);
        //finddata.setOnClickListener(this);
        //more=(ImageView)findViewById(R.id.more);
        //more.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.finddata:
                areaCode="";
                startDateTemp="";
                endDateTemp="";
                CaseTypeTemp="";
                prospectTemp="";
                statusTemp="";
                caseNo=findEdit.getText().toString();
                sceneDetail=findEdit.getText().toString();
                search_beforeTime="";
                search_afterTime="";
                getAllData("/prospects/query");
               /* try {
                    conTemp = URLEncoder.encode(findEdit.getText().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
*/
//                getLacalData(findEdit.getText().toString());
                Toast.makeText(NvestigatQuery.this,"查询",Toast.LENGTH_SHORT).show();
                break;
            case R.id.more:
                showDialog();
                areaCode="";
                startDateTemp="";
                endDateTemp="";
                CaseTypeTemp="";
                prospectTemp="";
                statusTemp="";
                caseNo=findEdit.getText().toString();
                sceneDetail=findEdit.getText().toString();
                getAllData("/prospects/query");
//                getLacalData(findEdit.getText().toString());
                Toast.makeText(NvestigatQuery.this,"更多",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void getAllData(String mathName){
        StringMap params = new StringMap();
        params.putString("areaCode", areaCode);//
        params.putString("startDate", startDateTemp);//date类型
        params.putString("endDate", endDateTemp);
        params.putString("caseType", CaseTypeTemp);
        params.putString("prospect", prospectTemp);
        params.putString("status", statusTemp);
        params.putString("pageSize", "10");
        params.putString("pageIndex", "1");
        params.putString("caseNo", caseNo);
        params.putString("sceneDetail", sceneDetail);
        params.putString("search_beforeTime", search_beforeTime);

        params.putString("search_afterTime", search_afterTime);
        params.putString("token", sharePre.getString("token", ""));
        Log.d("Token", sharePre.getString("token", ""));

        Netroid.PostHttp(mathName, params, new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {

                Log.d("Allresponse", "" + response);
                list.clear();
                try {
                    if(response.getBoolean("success")){
                        JSONArray JsonArray = response.getJSONArray("data");
                        Log.d("JsonArraylength", "" + JsonArray.length());
                        JSONObject JsonObjectdata;
                        AllQueryFragmentAdapter.AllQueryFragmentData allQueryFragmentData;

                        for (int i = 0; i < JsonArray.length(); i++) {
                            JsonObjectdata = JsonArray.getJSONObject(i);
                            Log.d("cases", "" + JsonObjectdata);
                            allQueryFragmentData = new AllQueryFragmentAdapter.AllQueryFragmentData();
                            Log.d("allid", JsonArray.getJSONObject(i).getString("id"));
                            allQueryFragmentData.setId(JsonObjectdata.getString("id"));
                            allQueryFragmentData.setInvestigationPlace(JsonObjectdata.getString("sceneDetail"));
                            allQueryFragmentData.setExposureProcess(JsonObjectdata.getString("exposureProcess"));
                            allQueryFragmentData.setSceneRegionalism(JsonObjectdata.getString("sceneRegionalism"));

                            allQueryFragmentData.setCrackedDate(JsonObjectdata.getString("crackedDate"));
                            allQueryFragmentData.setStatus(JsonObjectdata.getString("status"));
                            allQueryFragmentData.setSceneInvestigationId("sceneInvestigationId");//勘验Id
                            allQueryFragmentData.setCaseType("caseType");
                            list.add(allQueryFragmentData);
                        }
                        adapter.notifyDataSetChanged();
                        if(JsonArray.length()==0){
                            Toast.makeText(NvestigatQuery.this,"暂无数据",Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(NetroidError error) {
                Log.d("error", "" + error);
            }
        });
    }

    private void showDialog() {
        TextView Submit_btn, Cancel_btn;
        TextView Content;
        Button findBtn;
        final GridView timeGridview,caseTypeGridview,areaGridview;
        final EditText oldPassword, newPassword, newPasswordAgain;
        UserNameDialog = new Dialog(NvestigatQuery.this, R.style.Dialog);
        UserNameDialog.setContentView(R.layout.common_dialog);
        UserNameDialog.setCanceledOnTouchOutside(false);// 点击Dialog外部可以关闭Dialog
        findBtn=(Button)UserNameDialog.findViewById(R.id.find_btn);
        startTimeText=(TextView)UserNameDialog.findViewById(R.id.start_time);
        endTimeText=(TextView)UserNameDialog.findViewById(R.id.end_time);
        exit=(ImageView)UserNameDialog.findViewById(R.id.exit);
        timeLinearLayout=(LinearLayout)UserNameDialog.findViewById(R.id.time_linearLayout);


        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserNameDialog.dismiss();
            }
        });



        //案发时间
        timeGridview = (GridView) UserNameDialog.findViewById(R.id.time_gridview);
        type="timeDialog";
        final DialogDridviewAdapter timeAdapter = new DialogDridviewAdapter(titles, timeGridview, this, new DialogDridviewAdapter.TabListener() {
            @Override
            public void onTab(String name) {
                Toast.makeText(NvestigatQuery.this,name,Toast.LENGTH_SHORT).show();
                switch (name){
                    case "当天":
                        search_beforeTime=""+getPeriodDate("0");
                        search_afterTime="";
                        timeLinearLayout.setVisibility(View.GONE);

                        break;
                    case "其他":
                        /*search_beforeTime=""+getPeriodDate("1");
                        search_afterTime="";*/
                        timeLinearLayout.setVisibility(View.VISIBLE);

                        break;
                    case "本周内":
                        search_beforeTime=""+getPeriodDate("2");
                        search_afterTime="";
                        timeLinearLayout.setVisibility(View.GONE);
                        break;
                    case "本月内":
                        search_beforeTime=""+getPeriodDate("3");
                        Log.d("search_beforeTime",search_beforeTime);
                        search_afterTime="";
                        timeLinearLayout.setVisibility(View.GONE);
                        break;
                    case "不限":
                        search_beforeTime="";
                        search_afterTime="";
                        timeLinearLayout.setVisibility(View.GONE);
                        break;
                }
                startTimeText.setText("");
                endTimeText.setText("");
            }
        });


        startTimeText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // new DatePickerDialog(context, listener,mYear,mMonth, mDay).show();
               /* DatePickDialog datePickDialog = new DatePickDialog(NvestigatQuery.this);
                datePickDialog.datePickDialog(startTimeText,"");
                if (!startTimeText.equals(""))
                {
                    search_beforeTime=startTimeText.getText().toString().replace("-","");
                }*/

            }
        });


        endTimeText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               /* DatePickDialog datePickDialog = new DatePickDialog(NvestigatQuery.this);
                datePickDialog.datePickDialog(endTimeText,"");
                if (!endTimeText.equals("")) {
                    search_afterTime = endTimeText.getText().toString().replace("-", "");
                }*/
            }
        });
        timeGridview.setAdapter(timeAdapter);
        //案件类型
        caseTypeGridview = (GridView) UserNameDialog.findViewById(R.id.casetype_gridview);
        caseTypeDialogDridviewAdapter caseTypeAdapter = new caseTypeDialogDridviewAdapter(caseType, caseTypeGridview, this, new caseTypeDialogDridviewAdapter.TabListener() {
            @Override
            public void onTab(String name) {
                Toast.makeText(NvestigatQuery.this,name,Toast.LENGTH_SHORT).show();
                CaseTypeTemp=name;
            }
        });
        caseTypeGridview.setAdapter(caseTypeAdapter);
        /*//按键区域
        areaGridview = (GridView) UserNameDialog.findViewById(R.id.area_gridview);
        DialogDridviewAdapter areaAdapter = new DialogDridviewAdapter(area, areaGridview, this, new DialogDridviewAdapter.TabListener() {
            @Override
            public void onTab(String name) {
                Toast.makeText(NvestigatQuery.this,name,Toast.LENGTH_SHORT).show();
            }
        });
        areaGridview.setAdapter(areaAdapter);
*/

        findBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                areaCode="";
                startDateTemp="";
                endDateTemp="";
                //CaseTypeTemp="";
                prospectTemp="";
                statusTemp="";
  //              search_beforeTime="";
  //              search_afterTime="";
                caseNo="";
                sceneDetail="";
                getAllData("/prospects/query");

                UserNameDialog.dismiss();
            }
        });



            UserNameDialog.show();
        }

    private void InitDialog(){


    }




    public  StringBuilder getPeriodDate(String dateType) {
        Calendar c = Calendar.getInstance(); // 当时的日期和时间
        int day; // 需要更改的天数
        switch (dateType) {

            case "0": // 一天前
                day = c.get(Calendar.DAY_OF_MONTH) - 1;
                c.set(Calendar.DAY_OF_MONTH, day);
                // System.out.println(df.format(c.getTime()));
                break;
            case "1": // 三天前
                day = c.get(Calendar.DAY_OF_MONTH) - 3;
                c.set(Calendar.DAY_OF_MONTH, day);
                // System.out.println(df.format(c.getTime()));
                break;
            case "2": // 三天前
                day = c.get(Calendar.DAY_OF_MONTH) - 7;
                c.set(Calendar.DAY_OF_MONTH, day);
                // System.out.println(df.format(c.getTime()));
                break;
            case "3": // 一个月前
                day = c.get(Calendar.DAY_OF_MONTH) - 30;
                c.set(Calendar.DAY_OF_MONTH, day);
                // System.out.println(df.format(c.getTime()));
                break;

        }
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        StringBuilder strForwardDate = new StringBuilder().append(mYear).append(
                (mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append(
                (mDay < 10) ? "0" + mDay : mDay);
        System.out.println("strDate------->"+strForwardDate+"-"+c.getTimeInMillis());
        return strForwardDate;
        //return c.getTimeInMillis();
    }




/*public void getLacalData(String condition)  {
        Log.d("condition",condition);
        //condition="盗窃";
        List<CsSceneCases> listData = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                "exposureProcess like '%" + condition + "%' or sceneDetail like '%"+ condition +"%' " +
                        "or caseNo like '%"+ condition +"%'");
        *//*String status="3";
        List<CsSceneCases> listData = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                "status = '"+ status +"'");*//*

        list.clear();
       if(listData.size()!=0){
           AllQueryFragmentAdapter.AllQueryFragmentData allQueryFragmentData;
           for (int i = 0; i < listData.size(); i++) {

               allQueryFragmentData = new AllQueryFragmentAdapter.AllQueryFragmentData();

               //allQueryFragmentData.setId(list.get(i).getId().);
               if(!listData.get(i).getStatus().equals("0")) {
                   allQueryFragmentData.setInvestigationPlace(listData.get(i).getSceneDetail());
                   allQueryFragmentData.setExposureProcess(listData.get(i).getExposureProcess());
                   allQueryFragmentData.setSceneRegionalism(listData.get(i).getSceneRegionalism());

                   allQueryFragmentData.setCrackedDate(listData.get(i).getCrackedDate());


                   allQueryFragmentData.setStatus(listData.get(i).getStatus());
                   //allQueryFragmentData.setSceneInvestigationId(list.get(i).get);//勘验Id
                   allQueryFragmentData.setCaseType(listData.get(i).getCaseType());
                   allQueryFragmentData.setCaseId(listData.get(i).getCaseNo());
                   allQueryFragmentData.setTemplateId(listData.get(i).getTemplateId());
                   list.add(allQueryFragmentData);
               }
           }
           adapter.notifyDataSetChanged();

       }
    }*/






}
