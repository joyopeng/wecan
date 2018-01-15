package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.NetroidError;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.Application.PublicMsg;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.BaseTemp;
import com.gofirst.scenecollection.evidence.model.CaseBasicInfo;
import com.gofirst.scenecollection.evidence.model.CommonExtField;
import com.gofirst.scenecollection.evidence.model.CommonTemplateDetail;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.ProspectPreViewItemData;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.model.SceneFileConjunction;
import com.gofirst.scenecollection.evidence.model.TemplateSort;
import com.gofirst.scenecollection.evidence.model.UnUploadJson;
import com.gofirst.scenecollection.evidence.sync.UnUploadSingleJson;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.Utils;
import com.gofirst.scenecollection.evidence.view.adapter.ProspectPreviewAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.SelectCaseTypeAdapter;
import com.gofirst.scenecollection.evidence.view.customview.UniversalLoadingView;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;
import com.gofirst.scenecollection.evidence.view.fragment.SceneInfoFragment;

import net.tsz.afinal.db.sqlite.DbModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @author maxiran
 */
public class ProspectPreview extends Activity implements View.OnClickListener, SelectCaseTypeAdapter.OnTabListener {

    private FloatingActionButton button;
    private ArrayList<ProspectPreViewItemData> withCaseList = new ArrayList<>();
    private ArrayList<ProspectPreViewItemData> allList = new ArrayList<>();
    private PopupWindow popupWindow;
    private boolean isSelect;
    private ProspectPreviewAdapter adapter;
    private UniversalLoadingView universalLoadingView;
    private String mode = "";
    public String caseId;
    private GridView gridView;
    private Button finish_btn;
    private String templateId;
    private String status;
    private View mShadowView;
    private View mBackBtn;
    public static final int FINISH_TEXT = 1;
    private SharedPreferences uploadfilerec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.prospect_preview);
        gridView = (GridView) findViewById(R.id.card_grid);
        finish_btn = (Button) findViewById(R.id.finish_btn);

        caseId = getIntent().getStringExtra("caseId");
        mode = getIntent().getStringExtra("mode");
        status = getIntent().getStringExtra("status");
        templateId = getIntent().getStringExtra("templateId");
        if (mode != null && mode.equals("find")) {
            finish_btn.setVisibility(View.GONE);
        }
        List<CsSceneCases> listSceneCases = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                "caseNo = '" + caseId + "'");
        if (listSceneCases.size() != 0) {
            if (listSceneCases.get(0).getStatus().equals("2")) {
                finish_btn.setText("开始勘验");
            } else {
                finish_btn.setText("勘验结束");
            }
        }
        finish_btn.setOnClickListener(this);
        getPopData();
        initPreviewItem();
        button = (FloatingActionButton) findViewById(R.id.pop_btn);
        button.setSize(FloatingActionButton.SIZE_MINI);
        button.setIcon(R.drawable.add_cross);
        button.setSize(FloatingActionButton.SIZE_NORMAL);
        button.setColorNormal(Color.parseColor("#82A8CB"));
        button.setOnClickListener(this);
        mShadowView = findViewById(R.id.prospect_preview_shadow);
        mShadowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("zhangsh", "mShadowView onTouch");
                return true;
            }
        });

        mBackBtn = findViewById(R.id.secondary_back_img);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.secondary_title_tv)).setText("现场勘查");
        //gridView.setAdapter(adapter = new ProspectPreviewAdapter(caseId, withCaseList, ProspectPreview.this));
        gridView.setAdapter(adapter = new ProspectPreviewAdapter(caseId, withCaseList, ProspectPreview.this, status));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!finish_btn.getText().toString().equals("开始勘验")) {
                    universalLoadingView = new UniversalLoadingView(ProspectPreview.this);
                    universalLoadingView.startLoading();
                    Intent intent = new Intent(ProspectPreview.this, ProspectInterface.class)
                            .putExtra("which_tab", position);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("tabList", withCaseList);
                    intent.putExtras(bundle);
                    intent.putExtra("caseId", caseId);
                    intent.putExtra("mode", mode);
                    intent.putExtra("templateId", getIntent().getStringExtra("templateId"));
                    intent.putExtra("isSimpleCase",getIntent().getBooleanExtra("dealType", false));
                    startActivity(intent);
                } else {
                    //Toast.makeText(ProspectPreview.this, "请点击开始接勘", Toast.LENGTH_SHORT).show();
                    //showPreviewShadow();
                }
            }
        });
        uploadfilerec = getSharedPreferences(PublicMsg.UPLOADFILE_PREFRENCE, MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pop_btn:
                if (!isSelect) {
                    initPop();
                } else {
                    popupWindow.dismiss();
                    adapter.notifyDataSetChanged();
                }
                isSelect = !isSelect;
                button.setIcon(!isSelect ? R.drawable.add_cross : R.drawable.close);
                break;

            case R.id.finish_btn:

                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new Date());

                if (finish_btn.getText().toString().equals("勘验结束") && isHasCseType() && isHasPhoto() && isHasDirection()) {

                    CaseBasicInfo caseBasicInfo = ViewUtil.getCaseBasicInfo(caseId, "SCENE_INVESTIGATION_EXT");
                    JSONObject jsonObject = null;
                    try {

                        DataTemp temp= ViewUtil.getDataTemp(caseId, "SCENE_INVESTIGATION_EXT");
                        JSONObject jsonObject1 = new JSONObject(temp.getData());
                        jsonObject1.put("INVESTIGATION_DATE_TO", timeStamp);
                        temp.setData(jsonObject1.toString());
                        EvidenceApplication.db.update(temp);


                        jsonObject = new JSONObject(caseBasicInfo.getJson());
                        jsonObject.put("INVESTIGATION_DATE_TO", timeStamp);

                        caseBasicInfo.setJson(jsonObject.toString());
                        EvidenceApplication.db.update(caseBasicInfo);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Utils.startProgressDialog(ProspectPreview.this,"压缩文件","压缩...",false,false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            saveModelAudio();
                            //add save 描点信息到dataTemp start
                            saveSceneFileConjunctionDetails();
                            //add save 描点信息到dataTemp end
                            addWaitingUploadJson();
                            String timeStamp = new SimpleDateFormat("yyyyMMdd")
                                    .format(new Date());
                            File filedirec = new File(AppPathUtil.getDataPath() +"/"+ timeStamp + "/" + caseId);
                            if (filedirec.exists() && filedirec.isDirectory()) {
                                uploadfilerec.edit().putLong(caseId, getTotalSizeOfFilesInDir(filedirec)).commit();
                            }

                            Message message = new Message();
                            message.what = FINISH_TEXT;
                            handler.sendMessage(message);
                        }
                    }).start();

                } else if (finish_btn.getText().toString().equals("开始勘验")) {
                    List<CsSceneCases> listSceneCases = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                            "caseNo = '" + caseId + "'");
                    if (listSceneCases.size() != 0) {
                        CsSceneCases csSceneCases = listSceneCases.get(0);
                        csSceneCases.setStatus("1");
                        EvidenceApplication.db.update(csSceneCases);

                        CaseBasicInfo caseBasicInfo = ViewUtil.getCaseBasicInfo(caseId, "SCENE_INVESTIGATION_EXT");
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(caseBasicInfo.getJson());
                            jsonObject.put("INVESTIGATION_DATE_FROM", timeStamp);

                            caseBasicInfo.setJson(jsonObject.toString());
                            EvidenceApplication.db.update(caseBasicInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        finish();
                        universalLoadingView = new UniversalLoadingView(ProspectPreview.this);
                        universalLoadingView.startLoading();
                        Intent intent = new Intent(ProspectPreview.this, ProspectInterface.class)
                                .putExtra("which_tab", 0);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("tabList", withCaseList);
                        intent.putExtras(bundle);
                        intent.putExtra("caseId", caseId);
                        intent.putExtra("mode", mode);
                        intent.putExtra("templateId", getIntent().getStringExtra("templateId"));
                        intent.putExtra("isSimpleCase",getIntent().getBooleanExtra("dealType", false));
                        startActivity(intent);
                    }
                }
                break;
        }

    }




    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FINISH_TEXT:
                    // 在这里可以进行UI操作
                    Utils.stopProgressDialog();
                    Toast.makeText(ProspectPreview.this, "勘验结束", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
                    break;
            }
        }
    };


    private boolean isHasCseType(){
        String field = getSceneField("SCENE_LAW_CASE_EXT","CASE_TYPE");
        if ("勘验信息".equals(field))
            Toast.makeText(this,"请填写案件类别",Toast.LENGTH_SHORT).show();
        return !"勘验信息".equals(field);
    }

    private boolean isHasPhoto(){
        List<CsSceneCases> listSceneCases = EvidenceApplication.db.findAllByWhere(CsSceneCases.class,
                "caseNo = '" + caseId + "'");
        if (listSceneCases.size() != 0) {
            CsSceneCases csSceneCases = listSceneCases.get(0);
            if("0".equals(csSceneCases.getDealType())){
                return true;
            }
        }
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "caseId = '" + caseId + "' and father = 'SCENE_PHOTO'");
        if (list == null || list.size() < 5)
            Toast.makeText(this,"请至少拍摄5张现场照片",Toast.LENGTH_SHORT).show();
        return list != null && list.size() >=5 ;
    }

    private boolean isHasDirection(){
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
                "caseId = '" + caseId + "' and father = 'SCENE_PICTURE$1082'");
        if (list == null || list.size() == 0)
            Toast.makeText(this,"请至少截取1张现场方位图",Toast.LENGTH_SHORT).show();
        return list != null && list.size() != 0;
    }
    private void initPop() {
        initPopCheckState();
        View view = getLayoutInflater().inflate(R.layout.select_case_type_pop, null);
        GridView typeGridView = (GridView) view.findViewById(R.id.type_grid);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        typeGridView.setAdapter(new SelectCaseTypeAdapter(allList, this));
        popupWindow.setAnimationStyle(R.style.tabpopstyle);
        popupWindow.showAtLocation(button, Gravity.CENTER, -40, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        withCaseList.clear();
        initPreviewItem();
        if (finish_btn.getText().toString().equals("开始勘验")) {
            showPreviewShadow();
        } else {
            hidePreviewShadow();
        }
        gridView.setAdapter(adapter = new ProspectPreviewAdapter(caseId, withCaseList, ProspectPreview.this, status));
        if (universalLoadingView != null)
            universalLoadingView.stopLoading();
    }

    @SuppressWarnings("ALL")
    private Bitmap getResImage(int res) {
        return ((BitmapDrawable) getResources().getDrawable(res)).getBitmap();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    private void getPopData() {
        //获取模块名
        getAllData();
        //获取配置
        getALLConfig();
        for (ProspectPreViewItemData data : allList)
            Log.d("isRec", data.getName() + data.isNeedRec());
    }

    @Override
    public void onTab(int position, String name, boolean isSelect) {
        ProspectPreViewItemData item = allList.get(position);
        if (isSelect) {
            withCaseList.add(item);
        } else {
            ProspectPreViewItemData removeItem = null;
            for (ProspectPreViewItemData itemData : withCaseList) {
                if (item.getName().equals(itemData.getName())) {
                    removeItem = itemData;
                }
            }
            if (removeItem != null)
                withCaseList.remove(removeItem);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 获取所有的模块
     */
    private void getAllData() {
        List<BaseTemp> baseTemps = EvidenceApplication.db.findAllByWhere(BaseTemp.class, "templateType = '0' and templateLevel = '1'");
        for (BaseTemp baseTemp : baseTemps) {
            String tableName = baseTemp.getTableName();
            ProspectPreViewItemData data = new ProspectPreViewItemData();
            data.setName(baseTemp.getSceneName());
            data.setField(tableName);
            //配置描述信息
            allList.add(data);
        }
    }

    /**
     * 获取关联案件类型的模块
     */
    private void initPreviewItem() {
        initItemFromJson();
        boolean isSimpleCase = getIntent().getBooleanExtra("dealType",false);
        if (withCaseList.size() == 0){
            if (isSimpleCase){
                ProspectPreViewItemData prospectPreViewItemData = new ProspectPreViewItemData();
                prospectPreViewItemData.setName("现场照片");
                prospectPreViewItemData.setField("SCENE_PHOTO");
                prospectPreViewItemData.setEditOrCamera(true);
                withCaseList.add(prospectPreViewItemData);

                ProspectPreViewItemData prospect = new ProspectPreViewItemData();
                prospect.setName("勘验信息");
                prospect.setField("SCENE_INVESTIGATION_EXT");
                prospect.setNeedRec(true);
                withCaseList.add(prospect);

                ProspectPreViewItemData caseInfo = new ProspectPreViewItemData();
                caseInfo.setName("案件信息");
                caseInfo.setField("SCENE_LAW_CASE_EXT");
                caseInfo.setNeedRec(true);
                withCaseList.add(caseInfo);

                ProspectPreViewItemData dir = new ProspectPreViewItemData();
                dir.setName("现场方位图");
                dir.setField("SCENE_PICTURE$1082");
                dir.setNeedRec(true);
                withCaseList.add(dir);

                initAwaysItem();
            }else {
              /* initItemFromLastCaseJson();
                if (withCaseList.size() == 0)*/
                    initItemFromDb();
            }
        }

    }

    private void initItemFromDb() {
        String templateId = getIntent().getStringExtra("templateId");
        List<CommonTemplateDetail> commonTemplateDetails = EvidenceApplication.db.findAllByWhere(CommonTemplateDetail.class,
                "templateId = '" + templateId + "' and templateType = '0' and templateLevel = '1'", "positionSort asc");
        for (CommonTemplateDetail commonTemplateDetail : commonTemplateDetails) {
            ProspectPreViewItemData data = new ProspectPreViewItemData();
            data.setField(commonTemplateDetail.getTableName());
            data.setName(commonTemplateDetail.getSceneName());
            withCaseList.add(data);
        }
        match();
        initAwaysItem();
    }

    private void initItemFromJson() {
        List<TemplateSort> list = EvidenceApplication.db.findAllByWhere(TemplateSort.class, "caseId = '" + caseId + "'", "sort asc");
        for (TemplateSort templateSort : list) {
            ProspectPreViewItemData data = new ProspectPreViewItemData();
            data.setField(templateSort.getFatherKey());
            data.setName(templateSort.getFatherValue());
            data.setNeedRec(templateSort.isSiNeedRec());
            withCaseList.add(data);
        }
        match();
        initAwaysItem();

    }

    private void initItemFromLastCaseJson() {
        List<DbModel> dbModels =
                EvidenceApplication.db.findDbModelListBySQL("select caseId from TemplateSort order by date desc limit 0,1");
        if (dbModels == null || dbModels.size() == 0)
            return;
        String lastCaseId = dbModels.get(0).getString("caseId");
        if (TextUtils.isEmpty(lastCaseId))
            return;
        List<TemplateSort> list = EvidenceApplication.db.findAllByWhere(TemplateSort.class, "caseId = '" + lastCaseId + "'", "sort asc");
        for (TemplateSort templateSort : list) {
            ProspectPreViewItemData data = new ProspectPreViewItemData();
            data.setField(templateSort.getFatherKey());
            data.setName(templateSort.getFatherValue());
            data.setNeedRec(templateSort.isSiNeedRec());
            withCaseList.add(data);
        }
        match();
        initAwaysItem();
    }

    private void match() {
        //与所有item匹配配置
        for (int i = 0; i < withCaseList.size(); i++) {
            ProspectPreViewItemData item = withCaseList.get(i);
            for (ProspectPreViewItemData all : allList) {
                if (item.getField().equals(all.getField())) {
                    withCaseList.remove(i);
                    withCaseList.add(i, all);
                }
            }
        }
    }

    /**
     * 获取所有模块配置信息
     */
    private void getALLConfig() {
        for (ProspectPreViewItemData data : allList) {
            String id = data.getField();
            if ("SCENE_PHOTO".equals(id))
                data.setEditOrCamera(true);
        }
        //获取是否录音-
        List<CommonTemplateDetail> commonTemplateDetails1 = EvidenceApplication.db.findAllByWhere(CommonTemplateDetail.class, "tableName = 'SCENE_TOP_RECORD' and templateLevel = '0'");
        for (CommonTemplateDetail commonTemplateDetail : commonTemplateDetails1) {
            List<CommonExtField> photoItems = EvidenceApplication.db.findAllByWhere(CommonExtField.class, "templateId = '" + commonTemplateDetail.getTemplateId() + "'" + " and sceneType = 'SCENE_TOP_RECORD'");
            builderConfig(photoItems, "setNeedRec", boolean.class);
        }


    }

    /**
     * 配置模块
     *
     * @param configItems 配置信息表
     * @param methodName  配置方法名
     * @param paramClass  配置参数类
     */
    @SuppressWarnings("ALL")
    private <T> void builderConfig(List<CommonExtField> configItems, String methodName, Class<T> paramClass) {
        for (CommonExtField commonExtField : configItems) {
            String itemId = commonExtField.getField();
            for (ProspectPreViewItemData data : allList) {
                if (data.getField().equals(itemId)) {
                    Class itemClass = data.getClass();
                    Method method = null;
                    try {
                        method = itemClass.getDeclaredMethod(methodName, paramClass);
                        method.invoke(data, true);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void initPopCheckState() {
        for (ProspectPreViewItemData clear : allList)
            clear.setSelected(false);
        for (int i = 0; i < withCaseList.size(); i++) {
            ProspectPreViewItemData item = withCaseList.get(i);
            for (ProspectPreViewItemData all : allList) {
                if (item.getField().equals(all.getField()))
                    all.setSelected(true);
            }
        }
    }

    private void initAwaysItem() {
        SharePre sharePre = new SharePre(ProspectPreview.this, "user_info", Context.MODE_PRIVATE);
        for (ProspectPreViewItemData data : withCaseList) {
            String father = data.getField();
            switch (father) {
                // 案件信息
                case "SCENE_LAW_CASE_EXT":
                    data.setDesc(getIntent().getStringExtra("caseInfo"));
                    break;
                // 现场环境
                case "SCENE_ENVIRONMENT_EXT":
                    //    getWeatherInfo(data);
                    break;
                //接勘信息
                case "SCENE_RECEPTION_DISPATCH_EXT":
                    data.setDesc("接勘人 " + sharePre.getString("prospectPerson", ""));
                    break;
                //勘验信息
                case "SCENE_INVESTIGATION_EXT":
                    data.setDesc(getSceneField(father, "INVEST_NOTE_ID"));
                    if ("勘验信息".equals(getSceneField(father, "WEATHER_NAME")))
                        getWeatherInfo();
                    break;
            }
        }
    }

    private String getSceneField(String father, String key) {
        List<CaseBasicInfo> caseBasicInfos = EvidenceApplication.db.findAllByWhere(CaseBasicInfo.class, "caseId = '" + caseId + "' and father = '" + father + "'");
        if (caseBasicInfos == null || caseBasicInfos.size() == 0 || TextUtils.isEmpty(ViewUtil.safeGetJsonValue(key, caseBasicInfos.get(0).getJson()))) {
            return "勘验信息";
        }
        return ViewUtil.safeGetJsonValue(key, caseBasicInfos.get(0).getJson());
    }

    private void getWeatherInfo() {
        SharePre user_info = new SharePre(ProspectPreview.this, "user_info", Context.MODE_PRIVATE);
        Netroid.GetHttp("/app/weather", new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        Log.d("weather",response.toString());
                        JSONObject data = response.getJSONObject("data");
                        CaseBasicInfo caseBasicInfo = ViewUtil.getCaseBasicInfo(caseId, "SCENE_INVESTIGATION_EXT");
                        JSONObject jsonObject = new JSONObject(caseBasicInfo.getJson());
                        if ( data.has("weatherCode")){
                            jsonObject.put("WEATHER",data.getString("weatherCode"));
                            jsonObject.put("WEATHER_NAME", data.getString("weather"));
                        }
                        jsonObject.put("WIND",ViewUtil.getDictKey("XCFXDM",data.getString("wind")));
                        jsonObject.put("WIND_NAME",data.getString("wind"));
                        jsonObject.put("ENV_TEMPERATURE",data.getString("temperature"));
                        jsonObject.put("ENV_MOISTNESS",data.getString("humidity"));
                        caseBasicInfo.setJson(jsonObject.toString());
                        EvidenceApplication.db.update(caseBasicInfo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(NetroidError error) {

            }
        }, user_info.getString("token", ""));

    }



    private void addWaitingUploadJson() {
        UnUploadJson unUploadJson = getUnUploadJson();
        String json = getDataTempJson();
        String caseFolder = AppPathUtil.getZipPath() + "/" + unUploadJson.getCaseId() + "/";
        if (!json.equals("[]")) {
            unUploadJson.setJson(json);
            EvidenceApplication.db.update(unUploadJson);
            /*CompressUtil.saveCaseJsonToFile(json, caseFolder + unUploadJson.getCaseId() + ".txt");
            List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,"caseId = '" + caseId + "'");
            for (RecordFileInfo recordFileInfo : list){
                File file = new File(AppPathUtil.getDataPath() + "/" + recordFileInfo.getFilePath());
                CompressUtil.copyFile(file.getAbsolutePath(),caseFolder + recordFileInfo.getFilePath());
            }
            CompressUtil.zip(AppPathUtil.getZipPath() + "/" + unUploadJson.getCaseId(),"badcompy");*/
        }
        finishCaseState();
    }

    private UnUploadJson getUnUploadJson() {
        List<UnUploadJson> list = EvidenceApplication.db.findAllByWhere(UnUploadJson.class, "caseId = '" + caseId + "'");
        SharePre sharePre = new SharePre(ProspectPreview.this, "user_info", Context.MODE_PRIVATE);
        if (list == null || list.size() == 0) {
            UnUploadJson unUploadJson = new UnUploadJson();
            unUploadJson.setId(ViewUtil.getUUid());
            unUploadJson.setCaseId(caseId);
            unUploadJson.setUserId(sharePre.getString("user_id",""));
            EvidenceApplication.db.save(unUploadJson);
        }

        return EvidenceApplication.db.findAllByWhere(UnUploadJson.class, "caseId = '" + caseId + "'").get(0);
    }

    /**
     * 获取案件所有模块json
     */
    private String getDataTempJson() {
        List<DataTemp> dataTemps = EvidenceApplication.db.findAllByWhere(DataTemp.class, "caseId = '" + caseId + "'");
        for (DataTemp dataTemp : dataTemps) {
            String json = dataTemp.getData();
            if (json == null)
                continue;
            dataTemp.setData(insertMainId(json));
        }
        return JSON.toJSONString(dataTemps);
    }


    private String insertMainId(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            if (!isExistMainId(jsonObject)) {
                jsonObject.put("MAIN_ID", UnUploadSingleJson.getIvestId(caseId, "SCENE_INVESTIGATION_EXT"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject != null ? jsonObject.toString() : "";
    }

    private boolean isExistMainId(JSONObject jsonObject) {
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.equals("MAIN_ID")) {
                return true;
            }
        }
        return false;
    }

    private void finishCaseState() {
        List<CsSceneCases> list = EvidenceApplication.db.findAllByWhere(CsSceneCases.class, "caseNo = '" + caseId + "'");
        if (list.size() != 0) {
            CsSceneCases csSceneCases = list.get(0);
            csSceneCases.setStatus("3");
            csSceneCases.setTemplateId(getIntent().getStringExtra("templateId"));
            EvidenceApplication.db.update(csSceneCases);
        }
    }

    /**
     * save SceneFileConjunction details to DataTemp when case done
     * 勘验结束时在DataTemp表中保存SceneFileConjunction表的json信息
     */
    private void saveSceneFileConjunctionDetails() {
        List<SceneFileConjunction> sceneFileConjunctions = EvidenceApplication.db.findAllByWhere(SceneFileConjunction.class,
                "attachmentId in (select a.attachmentId from RecordFileInfo a where a.caseId = \"" + caseId + "\")");
        //List<DataTemp> temps = new ArrayList<DataTemp>();
        DataTemp temp = null;
        for (SceneFileConjunction item : sceneFileConjunctions) {
            temp = new DataTemp();
            temp.setCaseId(caseId);
            temp.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            temp.setDataType("scene_file_conjunction");
            temp.setFather("SCENE_BLIND_SHOOT");
            temp.setData(JSON.toJSONString(item));
            EvidenceApplication.db.save(temp);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hidePreviewShadow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void saveModelAudio() {
        for (ProspectPreViewItemData prospectPreViewItemData : withCaseList) {
            List<RecordFileInfo> list = getAudioRec(prospectPreViewItemData.getField());
            if (!prospectPreViewItemData.isNeedRec() || list.size() == 0)
                continue;
            String refKey = getLastLevelKey(prospectPreViewItemData.getField());
            DataTemp dataTemp = SceneInfoFragment.getDataTemp(caseId, prospectPreViewItemData.getField());
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(dataTemp.getData() != null ? dataTemp.getData() : "{}");
                jsonObject.put("SCENE_TYPE", prospectPreViewItemData.getField());
                dataTemp.setData(jsonObject.toString());
                dataTemp.setDataType("scene_investigation_data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (RecordFileInfo recordFileInfo : list) {

                if (jsonObject != null) {
                    String lastId = null;
                    try {
                        lastId = jsonObject.getString("ATTACHMENT_ID");
                        jsonObject.put("ATTACHMENT_ID", lastId != null ? lastId + "," + recordFileInfo.getId() : recordFileInfo.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        try {
                            jsonObject.put("ATTACHMENT_ID", lastId != null ? lastId + "," + recordFileInfo.getId() : recordFileInfo.getId());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                }

                try {
                    jsonObject.put("SECTION", "SCENE_TOP_RECORD");
                    String id = ViewUtil.getUUid();
                    DataTemp recDataTemp = SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getId() + "recData");
                    JSONObject recObject = new JSONObject(JSON.toJSONString(recordFileInfo));
                    recObject.put("refKeyId", id);
                    recObject.put("type", ViewUtil.getType(recordFileInfo));
                    recObject.put("SCENE_TYPE", recObject.getString("father"));
                    recDataTemp.setDataType("common_attachment");
                    recDataTemp.setData(recObject.toString());
                    EvidenceApplication.db.update(recDataTemp);

                    recDataTemp = SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getId() + "audioData");
                    recObject = new JSONObject("{}");
                    recObject.put("ID", id);
                    recObject.put("INVESTIGATION_ID", recordFileInfo.getCaseId());
                    recObject.put("SECTION", "SCENE_TOP_RECORD");
                    recObject.put("SCENE_TYPE", recordFileInfo.getFather());
                    recObject.put("ATTACHMENT_ID", recordFileInfo.getId());
                    recDataTemp.setDataType("scene_investigation_data");
                    recDataTemp.setData(recObject.toString());
                    EvidenceApplication.db.update(recDataTemp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            EvidenceApplication.db.update(dataTemp);
        }
    }

    private List<RecordFileInfo> getAudioRec(String father) {
        return EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "fileType = 'audio' and caseId = '"
                + caseId + "' and father = '" + father + "'" + " and photoType != 'anchor'");
    }

    private String getLastLevelKey(String father) {
        List<DataTemp> list = EvidenceApplication.db.findAllByWhere(DataTemp.class, "caseId = '" + caseId + "' and father = '" + father + "'");
        if (list != null && list.size() != 0) {
            try {
                String string = list.get(0).getData();
                if (string != null) {
                    JSONObject jsonObject = new JSONObject(string);
                    return jsonObject.getString("ID");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private void showPreviewShadow() {
        if (mShadowView != null && View.GONE == mShadowView.getVisibility()) {
            mShadowView.setVisibility(View.VISIBLE);
        }
    }

    private void hidePreviewShadow() {
        if (mShadowView != null && View.VISIBLE == mShadowView.getVisibility()) {
            mShadowView.setVisibility(View.GONE);
        }
    }

    private long getTotalSizeOfFilesInDir(final File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }

}
