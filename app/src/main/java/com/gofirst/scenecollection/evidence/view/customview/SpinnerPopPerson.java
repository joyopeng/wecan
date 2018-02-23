package com.gofirst.scenecollection.evidence.view.customview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.HyEmployees;
import com.gofirst.scenecollection.evidence.model.HyOrganizations;
import com.gofirst.scenecollection.evidence.utils.SharePre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/11.
 */
public class SpinnerPopPerson implements View.OnClickListener {

    private LinearLayout container, oftenConainer, searchContainer, searchShow;
    private List<View> levelItem = new ArrayList<>();
    private static String name;
    private String rootKey;
    private TextView allBtn, oftenBtn, outPutText, searchBtn;
    private View allLine, oftenLine, searchLine;
    private PopupWindow popupWindow;
    private ListView oftenList, searchList;
    private SharePre sharePre;
    private ExpandListView allDepartmentList, allDepartmentPeopleList;
    private List<HyOrganizations> allDepartmentData;
    private List<HyEmployees> allDepartmentPeopleData;

    public HashMap<Integer, Boolean> isSelectedData;
    private Boolean isUpdate = false;


    private EditText searchTxt;
    private ImageView searchImg;
    private View show_gray;

    // private DepartmentNameListAdapter departmentNameAdapter;
    //  private List<DepartmentNameListAdapter.DepartmentNameListData> list = new ArrayList<>();

    private HorizNavView horizNavView;
    private ArrayList<String> datas;
    private ArrayList<String> datasId;
    private LinearLayout horiznaview_linearLayout;
    private RecyclerView rec;
    private static final String ACTION = "violetjack.testaction";

    private Boolean departmentRefresh = false;//部门刷新
    private Boolean departmentPeopleRefresh = false;//部门人员刷新
    public GridView selectGridView;

    private LinkedList<HyEmployees> listName;

    public SpinnerPopPerson(Context context, TextView inputDate, String name, String rootKey) {
        this.name = name;
        this.rootKey = rootKey;
        initView(context, inputDate);
    }

    private void initView(final Context context, final TextView inputDate) {
        outPutText = inputDate;
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_pop_person_layout, null);
        sharePre = new SharePre(context, "user_info", Context.MODE_PRIVATE);
        selectGridView = (GridView) view.findViewById(R.id.GridView1);
        IntentFilter filter = new IntentFilter(ACTION);
        context.registerReceiver(receiver, filter);
        allBtn = (TextView) view.findViewById(R.id.all_btn);
        oftenBtn = (TextView) view.findViewById(R.id.often_btn);
        searchBtn = (TextView) view.findViewById(R.id.search_btn);
        oftenBtn.setText("本单位");
        allLine = view.findViewById(R.id.all_line);
        oftenLine = view.findViewById(R.id.often_line);
        searchLine = view.findViewById(R.id.search_line);
        allBtn.setOnClickListener(this);
        oftenBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        container = (LinearLayout) view.findViewById(R.id.container);
        oftenConainer = (LinearLayout) view.findViewById(R.id.often_container);
        searchContainer = (LinearLayout) view.findViewById(R.id.search_container);
        oftenList = (ListView) view.findViewById(R.id.often_list);
        searchList = (ListView) view.findViewById(R.id.search_list);

        oftenList.setDividerHeight(0);

        show_gray = (View) view.findViewById(R.id.show_gray);
        InitLayout(view, context, outPutText);

        oftenList.setAdapter(new OftenContentAdapter(context, getOftenListData(), listName));
        new OftenContentAdapter(context, getOftenListData(), listName).setListener(new OftenContentAdapter.OnListener() {
            @Override
            public void onItemClick(String employeeName, int employeeId, Boolean isSel) {
                //Toast.makeText(context,"tf"+position,Toast.LENGTH_SHORT).show();

                if (isSel) {
                    for (int i = 0; i < listName.size(); i++) {
                        if (listName.get(i).getEmployeeId().equals(employeeId)) {
                            isUpdate = true;
                            return;
                        }
                    }
                    if (!isUpdate) {
                        listName.addAll(EvidenceApplication.db.findAllByWhere(HyEmployees.class,
                                "employeeId ='" + employeeId + "'"));

                        selectGridView.setAdapter(new GetSelectDepartmentPeopleAdapter(listName));
                    } else {
                        Toast.makeText(context, "已添加", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!isSel) {
                    for (int i = 0; i < listName.size(); i++) {
                        if (listName.get(i).getEmployeeId().equals(employeeId)) {
                            listName.remove((i--));
                            break;
                        }
                    }
                    selectGridView.setAdapter(new GetSelectDepartmentPeopleAdapter(listName));
                }
                isUpdate = false;
                if (datas.size() == 1) {
                    allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context, getAllDepartmentPoepleListData(), listName));
                } else {

                    //getDatas(context,orgId,orgIdName);
                    List<HyEmployees> listAllDepartmentPeople = EvidenceApplication.db.findAllByWhere(HyEmployees.class,
                            "organizationId = '" + datasId.get(datas.size() - 1) + "'");
                    if (listAllDepartmentPeople.size() > 0) {
                        departmentPeopleRefresh = true;
                        allDepartmentPeopleData.addAll(listAllDepartmentPeople);
                        allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context, allDepartmentPeopleData, listName));
                    } else {
                        allDepartmentPeopleData.clear();
                        allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context, allDepartmentPeopleData, listName));
                    }
                }
            }
        });
        new AllDepartmentPeopleAdapter(context, getOftenListData(), listName).setAllListener(new AllDepartmentPeopleAdapter.OnAllListener() {
            @Override
            public void onItemClick(String employeeName, int employeeId, Boolean isSel) {
                //Toast.makeText(context,"tf"+position,Toast.LENGTH_SHORT).show();

                if (isSel) {
                    for (int i = 0; i < listName.size(); i++) {
                        if (listName.get(i).getEmployeeId().equals(employeeId)) {
                            isUpdate = true;
                            return;
                        }
                    }
                    if (!isUpdate) {
                        listName.addAll(EvidenceApplication.db.findAllByWhere(HyEmployees.class,
                                "employeeId ='" + employeeId + "'"));

                        selectGridView.setAdapter(new GetSelectDepartmentPeopleAdapter(listName));
                    } else {
                        Toast.makeText(context, "已添加", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!isSel) {
                    for (int i = 0; i < listName.size(); i++) {
                        if (listName.get(i).getEmployeeId().equals(employeeId)) {
                            listName.remove((i--));
                            break;
                        }
                    }
                    selectGridView.setAdapter(new GetSelectDepartmentPeopleAdapter(listName));

                }
                oftenList.setAdapter(new OftenContentAdapter(context, getOftenListData(), listName));
                isUpdate = false;
            }
        });

        new SearchAdapter(context, getSearListData(searchTxt.getText().toString()), listName).setSearchListener(new SearchAdapter.OnSearchListener() {
            @Override
            public void onItemClick(String employeeName, int employeeId, Boolean isSel) {
                if (isSel) {
                    for (int i = 0; i < listName.size(); i++) {
                        if (listName.get(i).getEmployeeId().equals(employeeId)) {
                            isUpdate = true;
                            return;
                        }
                    }
                    if (!isUpdate) {
                        listName.addAll(EvidenceApplication.db.findAllByWhere(HyEmployees.class,
                                "employeeId ='" + employeeId + "'"));

                        selectGridView.setAdapter(new GetSelectDepartmentPeopleAdapter(listName));
                    } else {
                        Toast.makeText(context, "已添加", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!isSel) {
                    for (int i = 0; i < listName.size(); i++) {
                        if (listName.get(i).getEmployeeId().equals(employeeId)) {
                            listName.remove((i--));
                            break;
                        }
                    }
                    selectGridView.setAdapter(new GetSelectDepartmentPeopleAdapter(listName));
                }
                isUpdate = false;
                if (datas.size() == 1) {
                    allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context, getAllDepartmentPoepleListData(), listName));
                } else {

                    //getDatas(context,orgId,orgIdName);
                    List<HyEmployees> listAllDepartmentPeople = EvidenceApplication.db.findAllByWhere(HyEmployees.class,
                            "organizationId = '" + datasId.get(datas.size() - 1) + "'");
                    if (listAllDepartmentPeople.size() > 0) {
                        departmentPeopleRefresh = true;
                        allDepartmentPeopleData.addAll(listAllDepartmentPeople);
                        allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context, allDepartmentPeopleData, listName));
                    } else {
                        allDepartmentPeopleData.clear();
                        allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context, allDepartmentPeopleData, listName));
                    }
                }
            }
        });

        selectGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(context, "已添加" + position, Toast.LENGTH_SHORT).show();
                HyEmployees employees = listName.get(position);
                SharePre sharePre = new SharePre(context, "user_info", Context.MODE_PRIVATE);
                int userId = Integer.parseInt(sharePre.getString("userId", "0"));
                if ("现场勘验人员".equals(name) && employees.getEmployeeId() == userId) {
                    Toast.makeText(context, "主勘人员,不能删除", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("现场指挥人员".equals(name) && listName.size() == 1) {
                    Toast.makeText(context, "现场指挥人员必须填写", Toast.LENGTH_SHORT).show();
                    return;
                }
                listName.remove(position);
                selectGridView.setAdapter(new GetSelectDepartmentPeopleAdapter(listName));
                oftenList.setAdapter(new OftenContentAdapter(context, getOftenListData(), listName));
                if (datas.size() == 1) {
                    allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context, getAllDepartmentPoepleListData(), listName));
                } else {

                    //getDatas(context,orgId,orgIdName);
                    List<HyEmployees> listAllDepartmentPeople = EvidenceApplication.db.findAllByWhere(HyEmployees.class,
                            "organizationId = '" + datasId.get(datas.size() - 1) + "'");
                    if (listAllDepartmentPeople.size() > 0) {
                        departmentPeopleRefresh = true;
                        allDepartmentPeopleData.clear();
                        allDepartmentPeopleData.addAll(listAllDepartmentPeople);
                        allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context, allDepartmentPeopleData, listName));
                    } else {
                        allDepartmentPeopleData.clear();
                        allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context, allDepartmentPeopleData, listName));
                    }
                }
            }
        });


        allDepartmentList = (ExpandListView) view.findViewById(R.id.all_list);
        allDepartmentList.setDividerHeight(0);
        allDepartmentList.setAdapter(new AllDepartmentAdapter(context, getAllDepartmentListData()));


        allDepartmentPeopleList = (ExpandListView) view.findViewById(R.id.allpeople_list);
        allDepartmentPeopleList.setDividerHeight(0);
        allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context, getAllDepartmentPoepleListData(), listName));


        // horiznaview_linearLayout.setVisibility(View.GONE);

        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ((TextView) view.findViewById(R.id.title).findViewById(R.id.secondary_title_tv)).setText(name);
        view.findViewById(R.id.title).findViewById(R.id.secondary_back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        TextView finish = (TextView) view.findViewById(R.id.title).findViewById(R.id.secondary_right_tv);
        finish.setVisibility(View.VISIBLE);
        finish.setText("完成");
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = "";
                String nameT = "";
                if (listName.size() == 0) {
                    inputDate.setTag(id);
                    inputDate.setText("请输入");
                } else {
                    HyEmployees ee;
                    Iterator<HyEmployees> it = listName.descendingIterator();
                    while (it.hasNext()) {
                        ee = it.next();
                        id = ee.getEmployeeId() + "," + id;
                        nameT = ee.getEmployeeName() + "、" + nameT;
                    }
                    inputDate.setTag(id.substring(0, id.length() - 1));
                    inputDate.setText(nameT.substring(0, nameT.length() - 1));
                }
                popupWindow.dismiss();
            }
        });
        // addSelectLevelList(context, 1, "");
        popupWindow.setAnimationStyle(R.style.tabpopstyle);
        popupWindow.setFocusable(true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (!popupWindow.isShowing())
            popupWindow.showAtLocation(inputDate, Gravity.BOTTOM, 0, 0);
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION)) {
                int position = intent.getIntExtra("position", 0);
                String orgId = intent.getStringExtra("orgId");
                String orgIdName = intent.getStringExtra("orgIdName");
                ArrayList<String> datasTemp, datasIdTemp = new ArrayList<>();
                datasTemp = intent.getStringArrayListExtra("datas");
                datasIdTemp = intent.getStringArrayListExtra("datasId");
                //tvResult.setText(result);
                datas.clear();
                datasId.clear();
                if (position == 0) {
                    allDepartmentList.setAdapter(new AllDepartmentAdapter(context, getAllDepartmentListData()));
                    allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context, getAllDepartmentPoepleListData(), listName));
                    datas.clear();
                    datasId.clear();
                    datas.add(orgIdName);
                    datasId.add(orgId);
                    horizNavView.setDatas(datas, datasId);
                    horizNavView.initAgeList();


                } else {
                    for (int i = 0; i < position + 1; i++) {
                        datas.add(datasTemp.get(i).toString());
                        datasId.add(datasIdTemp.get(i).toString());
                    }
                    /*String orgId = list.get(position).getOrganizationId().toString();
                    String orgIdName = list.get(position).getOrganizationName().toString();
                    getDatas();*/
                    horizNavView.setDatas(datas, datasId);
                    horizNavView.initAgeList();
                    //getDatas(context,orgId,orgIdName,false);
                    getDatas(context, datasId.get(position), datas.get(position), false);

                }
            }
        }
    };


    private void InitLayout(View view, final Context context, TextView outPutText) {
        searchShow = (LinearLayout) view.findViewById(R.id.search_show);
        searchShow.setVisibility(View.GONE);

        searchTxt = (EditText) view.findViewById(R.id.search_txt);
        searchImg = (ImageView) view.findViewById(R.id.search_img);
        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchList.setAdapter(new SearchAdapter(context, getSearListData(searchTxt.getText().toString()), listName));
            }
        });
        listName = new LinkedList<>();
        //listName =getAllDepartmentPoepleListData();
        String[] temp = null;
        String[] tempId = null;
        if (outPutText.getText().toString().equals("请输入")) {
            listName.clear();
        } else {
            temp = outPutText.getText().toString().split("、");
            tempId = outPutText.getTag().toString().split(",");
            for (int i = 0; i < temp.length; i++) {
                listName.addAll(EvidenceApplication.db.findAllByWhere(HyEmployees.class,
                        "employeeId ='" + tempId[i] + "'"));

            }
        }

        // listName.addAll(EvidenceApplication.db.findAllByWhere(HyEmployees.class,
        //         "employeeId ='" + sharePre.getString("userId","") + "'"));

        GetSelectDepartmentPeopleAdapter getSelectDepartmentPeopleAdapter;
        getSelectDepartmentPeopleAdapter = new GetSelectDepartmentPeopleAdapter(listName);
        selectGridView.setAdapter(getSelectDepartmentPeopleAdapter);

        horiznaview_linearLayout = (LinearLayout) view.findViewById(R.id.horiznaview_linearLayout);
        horizNavView = (HorizNavView) view.findViewById(R.id.custom_rec);
        rec = (RecyclerView) horizNavView.findViewById(R.id.recycler);
        //准备数据
        String orgId = sharePre.getString("organizationId", "");
        List<HyOrganizations> list = EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                "organizationBusiUpId = '0'");

        List<HyOrganizations> listOrgId = EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                "organizationId = '" + list.get(0).getOrganizationId().toString() + "'");
        datas = new ArrayList<String>();
        datasId = new ArrayList<String>();
        //for (int i = 0; i < 5; i++) {
        if (list.size() > 0) {
            datas.add(list.get(0).getOrganizationName().toString());
            datasId.add(list.get(0).getOrganizationId().toString());
        }
        // }

        //添加监听器，获取到RecyclerView的宽度以设置item的宽度
        final ViewTreeObserver vt = rec.getViewTreeObserver();
        vt.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                rec.getViewTreeObserver().removeOnPreDrawListener(this);
                horizNavView.setDatas(datas, datasId);
                horizNavView.setRecyclerviewWidth(rec.getMeasuredWidth());
                horizNavView.initAgeList();
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message); // 将Message对象发送出去

                return true;
            }
        });

    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    // 在这里可以进行UI操作
                    horiznaview_linearLayout.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };


    private void addNewList(int currentLevel, String parentKey) {
        /*ListView listView = new ListView(container.getContext());
        listView.setTag(currentLevel);
        ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1);
        sa.setDuration(80);
        listView.setDividerHeight(0);
        listView.setLayoutAnimation(new LayoutAnimationController(sa));
        container.addView(listView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        *///String sql = currentLevel == 1 ? "rootKey = '" + rootKey + "'" + " and dictLevel = '1'" : "rootKey = '" + rootKey + "'" + " and parentKey = '" + parentKey + "'" + "and dictLevel = '" + currentLevel + "'";
        //List<CsDicts> list = EvidenceApplication.db.findAllByWhere(CsDicts.class, sql);
        //listView.setAdapter(new listContentAdapter(list, currentLevel));


    }

    private void removeLevelListView(int level) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);
            if (view instanceof ListView && view.getTag() != null && (int) view.getTag() == level)
                container.removeView(view);
        }
    }

    private void removeAllLevelListView() {
        for (int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);
            if (view instanceof ListView)
                container.removeView(view);
        }
    }

    private void removeNextLevelSelectItem(int level) {
        for (int i = levelItem.size() - 1; i > level; i--) {
            container.removeView(levelItem.get(i));
            levelItem.remove(i);
        }
    }

    private void addSelectLevelList(Context context, int nextLevel, String parentKey) {


        View LevelMenu = LayoutInflater.from(context).inflate(R.layout.spinner_pop_all_person_layout, container, false);
        container.addView(LevelMenu);
        /*TextView levelName = (TextView) LevelMenu.findViewById(R.id.choose_name);
        levelName.setText("选择" + name);
        final ArrowTabView firstArrow = (ArrowTabView) LevelMenu.findViewById(R.id.arrow);
        firstArrow.setListener(this);
        firstArrow.setCurrentLevel(nextLevel);
        firstArrow.setParentKey(parentKey);
        levelItem.add(LevelMenu);
        firstArrow.changState();
        LevelMenu.findViewById(R.id.choose_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstArrow.changState();
            }
        });*/
    }


    private TextView getLastLevelName(int position) {
        TextView chooseContent = (TextView) levelItem.get(position).findViewById(R.id.choose_content);
        String text = (String) chooseContent.getText();
        if (position == 0 && TextUtils.isEmpty(text)) {
            return null;
        }
        return TextUtils.isEmpty(text) ? getLastLevelName(position - 1) : chooseContent;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_btn:
                changeTabState(allBtn, oftenBtn, searchBtn, allLine, oftenLine, searchLine, container, oftenConainer, searchContainer);
                horiznaview_linearLayout.setVisibility(View.VISIBLE);
                searchShow.setVisibility(View.GONE);
                break;
            case R.id.often_btn:
                changeTabState(oftenBtn, allBtn, searchBtn, oftenLine, allLine, searchLine, oftenConainer, container, searchContainer);
                horiznaview_linearLayout.setVisibility(View.GONE);
                searchShow.setVisibility(View.GONE);
                break;
            case R.id.search_btn:
                changeTabState(searchBtn, oftenBtn, allBtn, searchLine, oftenLine, allLine, searchContainer, oftenConainer, container);
                horiznaview_linearLayout.setVisibility(View.GONE);
                searchShow.setVisibility(View.VISIBLE);
                break;
            case R.id.search_img:
                //searchList.setAdapter(new SearchAdapter(context, getOftenListData(), listName));
                break;
        }
    }

    private void changeTabState(TextView self, TextView target, TextView third,
                                View selfLine, View targetLine, View thirdLine,
                                LinearLayout... container) {
        self.setTextColor(Color.parseColor("#2EA1EC"));
        selfLine.setBackgroundColor(Color.parseColor("#2EA1EC"));
        target.setTextColor(Color.parseColor("#767676"));
        targetLine.setBackgroundColor(Color.parseColor("#CBCBCB"));
        third.setTextColor(Color.parseColor("#767676"));
        thirdLine.setBackgroundColor(Color.parseColor("#CBCBCB"));

        container[0].setVisibility(View.VISIBLE);
        container[1].setVisibility(View.INVISIBLE);
        container[2].setVisibility(View.INVISIBLE);
    }

    private List<HyEmployees> getOftenListData() {
        List<HyEmployees> oftenData = new ArrayList<>();
        //List<HyEmployees> list = EvidenceApplication.db.findAllByWhere(HyEmployees.class,"employeeId = '" + rootKey + "'");
        /*for (HyEmployees csDictsFavorites : list){
            oftenData.addAll(EvidenceApplication.db.findAllByWhere(HyEmployees.class,"organizationId = '" + list.getDictsId()
                    + "' and rootKey = '" + rootKey + "'"));
        }*/
        List<HyEmployees> list = new ArrayList<>();
        //for(int i=0;i<listName.size();i++){
        list = EvidenceApplication.db.findAllByWhere(HyEmployees.class, "employeeId = '" + sharePre.getString("userId", "") + "'");
        // }
        //if(list.size()>0) {
        oftenData.addAll(EvidenceApplication.db.findAllByWhere(HyEmployees.class,
                "organizationId = '" + list.get(0).getOrganizationId() + "'"));
        // }
        return oftenData;
    }

    private List<HyOrganizations> getAllDepartmentListData() {//allDepartmentList

        allDepartmentData = new ArrayList<>();
        allDepartmentData.clear();
        List<HyOrganizations> list = EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                "organizationBusiUpId = '0'");

       /* List<HyOrganizations> listOrgId = EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                "organizationId = '"+list.get(0).getOrganizationId().toString()+"'");*/
        allDepartmentData.addAll(EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                "organizationBusiUpId = '" + list.get(0).getOrganizationId() + "'"));
        return allDepartmentData;
    }


    private List<HyEmployees> getAllDepartmentPoepleListData() {//allDepartmentList

        allDepartmentPeopleData = new ArrayList<>();
        allDepartmentPeopleData.clear();
        List<HyOrganizations> list = EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                "organizationBusiUpId = '0'");

       /* List<HyOrganizations> listOrgId = EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                "organizationId = '"+list.get(0).getOrganizationId().toString()+"'");*/
        allDepartmentPeopleData.addAll(EvidenceApplication.db.findAllByWhere(HyEmployees.class,
                "organizationId = '" + list.get(0).getOrganizationId() + "'"));
        return allDepartmentPeopleData;
    }

    public class listContentAdapter extends BaseAdapter {

        private List<CsDicts> list;
        private int currentLevel;

        public listContentAdapter(List<CsDicts> list, int currentLevel) {
            this.list = list;
            this.currentLevel = currentLevel;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lost_good_item, parent, false);
            TextView name = (TextView) convertView.findViewById(R.id.goods_desc);
            name.setText(list.get(position).getDictValue1());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("currentLevel", currentLevel + "");
                    Log.d("currentSize", levelItem.size() + "");
                    TextView chooseContent = (TextView) levelItem.get(currentLevel - 1).findViewById(R.id.choose_content);
                    ArrowTabView arrowTabView = (ArrowTabView) levelItem.get(currentLevel - 1).findViewById(R.id.arrow);
                    chooseContent.setText(list.get(position).getDictValue1());
                    chooseContent.setTag(list.get(position).getDictKey());
                    arrowTabView.changState();
                    removeLevelListView(currentLevel);
                    List<CsDicts> listArea = EvidenceApplication.db.findAllByWhere(CsDicts.class, "rootKey = 'AJLBDM'" +
                            " and dictLevel = '" + (currentLevel + 1) + "' and parentKey = '" + list.get(position).getDictKey() + "'");
                    if (listArea.size() != 0)
                        addSelectLevelList(v.getContext(), currentLevel + 1, list.get(position).getDictKey());
                }
            });
            return convertView;
        }
    }


    public static class OftenContentAdapter extends BaseAdapter {

        private List<HyEmployees> list;
        private List<HyEmployees> listName;
        private int currentLevel;
        public static OnListener onListener;
        private Boolean IsSel = false;

        private Context context;
        // 用来控制CheckBox的选中状况
        private HashMap<Integer, Boolean> isSelectedData;

        public OftenContentAdapter(Context context, List<HyEmployees> list, List<HyEmployees> listName) {
            this.list = list;
            this.context = context;
            this.listName = listName;
            isSelectedData = new HashMap<Integer, Boolean>();
            init();
        }

        // 初始化 设置所有checkbox都为未选择
        public void init() {

            for (int i = 0; i < list.size(); i++) {
                // isSelectedData.put(i, true);
                getIsSelected().put(i, false);
                for (int j = 0; j < listName.size(); j++) {
                    if (list.get(i).getEmployeeId().equals(listName.get(j).getEmployeeId())) {
                        getIsSelected().put(i, true);
                    }
                }
            }

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_pop_person_item, parent, false);
            TextView nametext = (TextView) convertView.findViewById(R.id.tv_device_name);
            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
            nametext.setText(list.get(position).getEmployeeName());


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isSelectedData.get(position)) {

                        //
                        HyEmployees employees = list.get(position);
                        SharePre sharePre = new SharePre(context, "user_info", Context.MODE_PRIVATE);
                        int userId = Integer.parseInt(sharePre.getString("userId", "0"));
                        if ("现场勘验人员".equals(name) && employees.getEmployeeId() == userId) {
                            Toast.makeText(context, "主勘人员,不能删除", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if ("现场指挥人员".equals(name) && listName.size() == 1) {
                            Toast.makeText(context, "现场指挥人员必须填写", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //

                        isSelectedData.put(position, false);
                        setIsSelected(isSelectedData);
                        Resources resources = context.getResources();
                        Drawable btnDrawable = resources.getDrawable(R.drawable.multi_uncheckbox);
                        checkBox.setBackgroundDrawable(btnDrawable);
                        if (onListener != null) {
                            //onListener.onItemClick(position);
                            onListener.onItemClick(list.get(position).getEmployeeName().toString(),
                                    list.get(position).getEmployeeId(), false);
                        }
                    } else {
                        isSelectedData.put(position, true);
                        setIsSelected(isSelectedData);
                        Resources resources = context.getResources();
                        Drawable btnDrawable = resources.getDrawable(R.drawable.multi_checkbok);
                        checkBox.setBackgroundDrawable(btnDrawable);
                        if (onListener != null) {
                            //onListener.onItemClick(position);
                            onListener.onItemClick(list.get(position).getEmployeeName().toString(),
                                    list.get(position).getEmployeeId(), true);
                        }
                    }

                }

            });

            checkBox.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    if (isSelectedData.get(position)) {
                        //
                        HyEmployees employees = list.get(position);
                        SharePre sharePre = new SharePre(context, "user_info", Context.MODE_PRIVATE);
                        int userId = Integer.parseInt(sharePre.getString("userId", "0"));
                        if ("现场勘验人员".equals(name) && employees.getEmployeeId() == userId) {
                            Toast.makeText(context, "主勘人员,不能删除", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if ("现场指挥人员".equals(name) && listName.size() == 1) {
                            Toast.makeText(context, "现场指挥人员必须填写", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //
                        isSelectedData.put(position, false);
                        setIsSelected(isSelectedData);
                        if (onListener != null) {
                            //    onListener.onItemClick(position);
                            onListener.onItemClick(list.get(position).getEmployeeName().toString(),
                                    list.get(position).getEmployeeId(), false);
                        }

                    } else {
                        isSelectedData.put(position, true);
                        setIsSelected(isSelectedData);
                        if (onListener != null) {
                            //    onListener.onItemClick(position);
                            onListener.onItemClick(list.get(position).getEmployeeName().toString(),
                                    list.get(position).getEmployeeId(), true);
                        }
                    }

                }
            });


            checkBox.setChecked(getIsSelected().get(position));

            return convertView;
        }


        public interface OnListener {
            //void onItemClick(int position);
            void onItemClick(String employeeName, int employeeId, Boolean isSel);
        }

        public void setListener(OnListener listener) {
            this.onListener = listener;
        }


        public HashMap<Integer, Boolean> getIsSelected() {
            return isSelectedData;
        }

        public void setIsSelected(HashMap<Integer, Boolean> isSelectedData) {
            isSelectedData = isSelectedData;
        }


    }


    public class AllDepartmentAdapter extends BaseAdapter {

        private List<HyOrganizations> list;
        private int currentLevel;
        private Context context;


        public AllDepartmentAdapter(Context context, List<HyOrganizations> list) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lost_good_item, parent, false);
            TextView name = (TextView) convertView.findViewById(R.id.goods_desc);
            name.setText(list.get(position).getOrganizationName());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* outPutText.setText(list.get(position).getOrganizationName());
                    outPutText.setTag(list.get(position).getOrganizationName());
                    if (popupWindow.isShowing())
                        popupWindow.dismiss();*/
                    String orgId = list.get(position).getOrganizationId().toString();
                    String orgIdName = list.get(position).getOrganizationName().toString();


                    getDatas(context, orgId, orgIdName, true);

                    /*List<HyOrganizations> listAllDepartment = EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                            "organizationBusiUpId = '" + orgId + "'");
                    allDepartmentData.clear();
                    allDepartmentPeopleData.clear();
                    allDepartmentData = new ArrayList<>();
                    if (listAllDepartment.size() > 0) {
                        departmentRefresh = true;
                        allDepartmentData.addAll(listAllDepartment);
                        allDepartmentList.setAdapter(new AllDepartmentAdapter(allDepartmentData));


                        datas.add(orgIdName);
                        horizNavView.setDatas(datas,datasId);
                        horizNavView.initAgeList();


                    } else {
                        allDepartmentData.clear();
                        allDepartmentList.setAdapter(new AllDepartmentAdapter(allDepartmentData));
                    }

                    List<HyEmployees> listAllDepartmentPeople = EvidenceApplication.db.findAllByWhere(HyEmployees.class,
                            "organizationId = '" + orgId + "'");
                    if (listAllDepartmentPeople.size() > 0) {
                        departmentPeopleRefresh = true;
                        allDepartmentPeopleData.addAll(listAllDepartmentPeople);
                        allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(allDepartmentPeopleData));
                    } else {
                        allDepartmentPeopleData.clear();
                        allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(allDepartmentPeopleData));
                    }*/
                }
            });
            return convertView;
        }
    }





   /* public class AllDepartmentPeopleAdapter extends BaseAdapter {

        private List<HyEmployees> list;
        private int currentLevel;

        public AllDepartmentPeopleAdapter(List<HyEmployees> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //if (convertView == null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_pop_person_item, parent, false);
            TextView name = (TextView) convertView.findViewById(R.id.tv_device_name);
            name.setText(list.get(position).getEmployeeName());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   *//* outPutText.setText(list.get(position).getEmployeeName());
                    outPutText.setTag(list.get(position).getEmployeeName());
                    if (popupWindow.isShowing())
                        popupWindow.dismiss();*//*
                }
            });
            return convertView;
        }
    }*/


    public class GetSelectDepartmentPeopleAdapter extends BaseAdapter {

        private List<HyEmployees> list;
        private int currentLevel;

        public GetSelectDepartmentPeopleAdapter(List<HyEmployees> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.get_select_department_people__item, parent, false);
            TextView name = (TextView) convertView.findViewById(R.id.tv_device_name);
            name.setText(list.get(position).getEmployeeName());
           /* convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    outPutText.setText(list.get(position).getEmployeeName());
                    outPutText.setTag(list.get(position).getEmployeeName());
                    if (popupWindow.isShowing())
                        popupWindow.dismiss();
                }
            });*/
            return convertView;
        }
    }


    public static class AllDepartmentPeopleAdapter extends BaseAdapter {

        private List<HyEmployees> list;
        private List<HyEmployees> listName;
        private int currentLevel;
        public static OnAllListener onAllListener;
        private Boolean IsSel = false;

        private Context context;
        // 用来控制CheckBox的选中状况
        private HashMap<Integer, Boolean> isSelectedData;

        public AllDepartmentPeopleAdapter(Context context, List<HyEmployees> list, List<HyEmployees> listName) {
            this.list = list;
            this.context = context;
            this.listName = listName;
            isSelectedData = new HashMap<Integer, Boolean>();
            init();
        }

        // 初始化 设置所有checkbox都为未选择
        public void init() {

            for (int i = 0; i < list.size(); i++) {
                // isSelectedData.put(i, true);
                getIsSelected().put(i, false);
                for (int j = 0; j < listName.size(); j++) {
                    if (list.get(i).getEmployeeId().equals(listName.get(j).getEmployeeId())) {
                        getIsSelected().put(i, true);
                    }
                }
            }

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_pop_person_item, parent, false);
            TextView nametext = (TextView) convertView.findViewById(R.id.tv_device_name);
            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
            nametext.setText(list.get(position).getEmployeeName());


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isSelectedData.get(position)) {
                        //
                        HyEmployees employees = list.get(position);
                        SharePre sharePre = new SharePre(context, "user_info", Context.MODE_PRIVATE);
                        int userId = Integer.parseInt(sharePre.getString("userId", "0"));
                        if ("现场勘验人员".equals(name) && employees.getEmployeeId() == userId) {
                            Toast.makeText(context, "主勘人员,不能删除", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if ("现场指挥人员".equals(name) && listName.size() == 1) {
                            Toast.makeText(context, "现场指挥人员必须填写", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //
                        isSelectedData.put(position, false);
                        setIsSelected(isSelectedData);
                        Resources resources = context.getResources();
                        Drawable btnDrawable = resources.getDrawable(R.drawable.multi_uncheckbox);
                        checkBox.setBackgroundDrawable(btnDrawable);
                        if (onAllListener != null) {
                            //onListener.onItemClick(position);
                            onAllListener.onItemClick(list.get(position).getEmployeeName().toString(),
                                    list.get(position).getEmployeeId(), false);
                        }
                    } else {
                        isSelectedData.put(position, true);
                        setIsSelected(isSelectedData);
                        Resources resources = context.getResources();
                        Drawable btnDrawable = resources.getDrawable(R.drawable.multi_checkbok);
                        checkBox.setBackgroundDrawable(btnDrawable);
                        if (onAllListener != null) {
                            //onListener.onItemClick(position);
                            onAllListener.onItemClick(list.get(position).getEmployeeName().toString(),
                                    list.get(position).getEmployeeId(), true);
                        }
                    }

                }

            });

            checkBox.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    if (isSelectedData.get(position)) {
                        //
                        HyEmployees employees = list.get(position);
                        SharePre sharePre = new SharePre(context, "user_info", Context.MODE_PRIVATE);
                        int userId = Integer.parseInt(sharePre.getString("userId", "0"));
                        if ("现场勘验人员".equals(name) && employees.getEmployeeId() == userId) {
                            Toast.makeText(context, "主勘人员,不能删除", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if ("现场指挥人员".equals(name) && listName.size() == 1) {
                            Toast.makeText(context, "现场指挥人员必须填写", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //
                        isSelectedData.put(position, false);
                        setIsSelected(isSelectedData);
                        if (onAllListener != null) {
                            //    onListener.onItemClick(position);
                            onAllListener.onItemClick(list.get(position).getEmployeeName().toString(),
                                    list.get(position).getEmployeeId(), false);
                        }

                    } else {
                        isSelectedData.put(position, true);
                        setIsSelected(isSelectedData);
                        if (onAllListener != null) {
                            //    onListener.onItemClick(position);
                            onAllListener.onItemClick(list.get(position).getEmployeeName().toString(),
                                    list.get(position).getEmployeeId(), true);
                        }
                    }

                }
            });


            checkBox.setChecked(getIsSelected().get(position));

            return convertView;
        }


        public interface OnAllListener {
            //void onItemClick(int position);
            void onItemClick(String employeeName, int employeeId, Boolean isSel);
        }

        public void setAllListener(OnAllListener onAllListener) {
            this.onAllListener = onAllListener;
        }


        public HashMap<Integer, Boolean> getIsSelected() {
            return isSelectedData;
        }

        public void setIsSelected(HashMap<Integer, Boolean> isSelectedData) {
            isSelectedData = isSelectedData;
        }
    }


    public static class SearchAdapter extends BaseAdapter {

        private List<HyEmployees> list;
        private List<HyEmployees> listName;
        private int currentLevel;
        public static OnSearchListener onSearchListener;
        private Boolean IsSel = false;

        private Context context;
        // 用来控制CheckBox的选中状况
        private HashMap<Integer, Boolean> isSelectedData;

        public SearchAdapter(Context context, List<HyEmployees> list, List<HyEmployees> listName) {
            this.list = list;
            this.context = context;
            this.listName = listName;
            isSelectedData = new HashMap<Integer, Boolean>();
            init();
        }

        // 初始化 设置所有checkbox都为未选择
        public void init() {

            for (int i = 0; i < list.size(); i++) {
                // isSelectedData.put(i, true);
                getIsSelected().put(i, false);
                for (int j = 0; j < listName.size(); j++) {
                    if (list.get(i).getEmployeeId().equals(listName.get(j).getEmployeeId())) {
                        getIsSelected().put(i, true);
                    }
                }
            }

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_pop_person_search_item, parent, false);
            TextView nametext = (TextView) convertView.findViewById(R.id.tv_device_name);
            //TextView departmentName= (TextView) convertView.findViewById(R.id.department_name);
            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
            nametext.setText(list.get(position).getEmployeeName());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isSelectedData.get(position)) {
                        //
                        HyEmployees employees = list.get(position);
                        SharePre sharePre = new SharePre(context, "user_info", Context.MODE_PRIVATE);
                        int userId = Integer.parseInt(sharePre.getString("userId", "0"));
                        if ("现场勘验人员".equals(name) && employees.getEmployeeId() == userId) {
                            Toast.makeText(context, "主勘人员,不能删除", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if ("现场指挥人员".equals(name) && listName.size() == 1) {
                            Toast.makeText(context, "现场指挥人员必须填写", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //
                        isSelectedData.put(position, false);
                        setIsSelected(isSelectedData);
                        Resources resources = context.getResources();
                        Drawable btnDrawable = resources.getDrawable(R.drawable.multi_uncheckbox);
                        checkBox.setBackgroundDrawable(btnDrawable);
                        if (onSearchListener != null) {
                            //onListener.onItemClick(position);
                            onSearchListener.onItemClick(list.get(position).getEmployeeName().toString(),
                                    list.get(position).getEmployeeId(), false);
                        }
                    } else {
                        isSelectedData.put(position, true);
                        setIsSelected(isSelectedData);
                        Resources resources = context.getResources();
                        Drawable btnDrawable = resources.getDrawable(R.drawable.multi_checkbok);
                        checkBox.setBackgroundDrawable(btnDrawable);
                        if (onSearchListener != null) {
                            //onListener.onItemClick(position);
                            onSearchListener.onItemClick(list.get(position).getEmployeeName().toString(),
                                    list.get(position).getEmployeeId(), true);
                        }
                    }

                }

            });

            checkBox.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    if (isSelectedData.get(position)) {
                        //
                        HyEmployees employees = list.get(position);
                        SharePre sharePre = new SharePre(context, "user_info", Context.MODE_PRIVATE);
                        int userId = Integer.parseInt(sharePre.getString("userId", "0"));
                        if ("现场勘验人员".equals(name) && employees.getEmployeeId() == userId) {
                            Toast.makeText(context, "主勘人员,不能删除", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if ("现场指挥人员".equals(name) && listName.size() == 1) {
                            Toast.makeText(context, "现场指挥人员必须填写", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //

                        isSelectedData.put(position, false);
                        setIsSelected(isSelectedData);
                        if (onSearchListener != null) {
                            //    onListener.onItemClick(position);
                            onSearchListener.onItemClick(list.get(position).getEmployeeName().toString(),
                                    list.get(position).getEmployeeId(), false);
                        }

                    } else {
                        isSelectedData.put(position, true);
                        setIsSelected(isSelectedData);
                        if (onSearchListener != null) {
                            //    onListener.onItemClick(position);
                            onSearchListener.onItemClick(list.get(position).getEmployeeName().toString(),
                                    list.get(position).getEmployeeId(), true);
                        }
                    }

                }
            });


            checkBox.setChecked(getIsSelected().get(position));

            return convertView;
        }


        public interface OnSearchListener {
            //void onItemClick(int position);
            void onItemClick(String employeeName, int employeeId, Boolean isSel);
        }

        public void setSearchListener(OnSearchListener searchlistener) {
            this.onSearchListener = searchlistener;
        }


        public HashMap<Integer, Boolean> getIsSelected() {
            return isSelectedData;
        }

        public void setIsSelected(HashMap<Integer, Boolean> isSelectedData) {
            isSelectedData = isSelectedData;
        }


    }


    private void getDatas(Context context, String orgId, String orgIdName, Boolean isDelete) {


        List<HyOrganizations> listAllDepartment = EvidenceApplication.db.findAllByWhere(HyOrganizations.class,
                "organizationBusiUpId = '" + orgId + "'");
        allDepartmentData.clear();
        allDepartmentPeopleData.clear();
        allDepartmentData = new ArrayList<>();
        if (listAllDepartment.size() > 0) {
            departmentRefresh = true;
            allDepartmentData.addAll(listAllDepartment);
            allDepartmentList.setAdapter(new AllDepartmentAdapter(context, allDepartmentData));

        } else {
            allDepartmentData.clear();
            allDepartmentList.setAdapter(new AllDepartmentAdapter(context, allDepartmentData));
        }
        if (isDelete) {
            datas.add(orgIdName);
            datasId.add(orgId);
            horizNavView.setDatas(datas, datasId);
            horizNavView.initAgeList();
        }
        List<HyEmployees> listAllDepartmentPeople = EvidenceApplication.db.findAllByWhere(HyEmployees.class,
                "organizationId = '" + orgId + "'");
        if (listAllDepartmentPeople.size() > 0) {
            departmentPeopleRefresh = true;
            allDepartmentPeopleData.addAll(listAllDepartmentPeople);
            allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context, allDepartmentPeopleData, listName));
        } else {
            allDepartmentPeopleData.clear();
            allDepartmentPeopleList.setAdapter(new AllDepartmentPeopleAdapter(context, allDepartmentPeopleData, listName));
        }


    }


    private List<HyEmployees> getSearListData(String text) {

        List<HyEmployees> oftenData = new ArrayList<>();
        oftenData.addAll(EvidenceApplication.db.findAllByWhere(HyEmployees.class,
                "employeeId like '%" + text + "%' or employeeName like '%" + text + "%'"));
        return oftenData;

    }


}




