package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.BaseTemp;
import com.gofirst.scenecollection.evidence.model.ProspectPreViewItemData;
import com.gofirst.scenecollection.evidence.model.TemplateSort;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.utils.Utils;
import com.gofirst.scenecollection.evidence.view.adapter.ProspectViewpagerAdapter;
import com.gofirst.scenecollection.evidence.view.customview.ArrowTabView;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.MultipleChoiceDialogTemplate;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;
import com.gofirst.scenecollection.evidence.view.easytagdragview.EasyTipDragView;
import com.gofirst.scenecollection.evidence.view.easytagdragview.bean.SimpleTitleTip;
import com.gofirst.scenecollection.evidence.view.easytagdragview.bean.Tip;
import com.gofirst.scenecollection.evidence.view.easytagdragview.widget.TipItemView;
import com.gofirst.scenecollection.evidence.view.fragment.CaseBasicInfoFragment;
import com.gofirst.scenecollection.evidence.view.fragment.EvidenceFragment;
import com.gofirst.scenecollection.evidence.view.fragment.ProspectVideoFragment;
import com.gofirst.scenecollection.evidence.view.fragment.SceneBlind;
import com.gofirst.scenecollection.evidence.view.fragment.SceneDirectionFragment;
import com.gofirst.scenecollection.evidence.view.fragment.ScenePhotos;
import com.gofirst.scenecollection.evidence.view.fragment.ScenePlan;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.gofirst.scenecollection.evidence.sync.FloatWindowService.isCameraPreview;

/**
 * @author maxiran
 */
public class ProspectInterface extends FragmentActivity implements ArrowTabView.ArrowChangeListener,
        ViewPager.OnPageChangeListener, TipItemView.OnSelectedListener, EasyTipDragView.OnCompleteCallback {

    private OnActivityResumeListener onActivityResumeListener;
    private ViewPager prospectPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private PopupWindow popupWindow;
    private List<Tip> simpleTitleTips;
    private EasyTipDragView easyTipDragView;
    private TextView dragViewTiltle;
    private ArrowTabView arrowTabView;
    private int defaultPosition = 0;
    List<ProspectPreViewItemData> list;
    private TextView finishTag;
    private String findType;
    private String[] fieldIds =
            {
                    "SCENE_LAW_CASE_EXT",
                    "SCENE_ENVIRONMENT_EXT",
                    "SCENE_INVESTIGATION_EXT",
                    "SCENE_BULLETPRINT",
                    "SCENE_PHYSICAL_EVIDENCE",
                    "SCENE_FOOTPRINT",
                    "SCENE_BIO_EVIDENCE",
                    "SCENE_SPECIALPRINT",
                    "SCENE_TOXIC_EVIDENCE",
                    "SCENE_ELECTRO_EVIDENCE",
                    "SCENE_VIDEO_EVIDENCE",
                    "SCENE_FILE_EVIDENCE",
                    "SCENE_BODY_PHOTO",
                    "SCENE_OTHER_EVIDENCE",
                    "SCENE_ANALYSIS_SUGGESTION",
                    "SCENE_HANDPRINT",
                    "SCENE_WORK_FEEDBACK",
                    "SCENE_VIDEO",
                    "SCENE_BLIND_SHOOT",
                    "SCENE_PHOTO",
                    "SCENE_TOOLMARK",
                    "SCENE_COLLECTED_MATERIAL",
                    "SCENE_PICTURE$1082",
                    "SCENE_PICTURE$1010",
                    "SCENE_SIGN",
                    "SCENE_COLLECT_RESULT",
                    "SCENE_RECEPTION_DISPATCH_EXT",
            };

    @SuppressWarnings("ALL")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.prospect_interface);
        prospectPager = (ViewPager) findViewById(R.id.prospect_pager);
        ((TextView) findViewById(R.id.title).findViewById(R.id.secondary_title_tv)).setText("现场勘查");
        TextView rightView = (TextView) findViewById(R.id.secondary_right_tv);
//        rightView.setVisibility(View.VISIBLE);
        rightView.setText("同步");
        rightView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MultipleChoiceDialogTemplate dialogTemplate = new MultipleChoiceDialogTemplate(ProspectInterface.this,
                        "标签", (TextView) v, simpleTitleTips);
                dialogTemplate.setListener(new MultipleChoiceDialogTemplate.onResultListener() {
                    @Override
                    public void onResult(SparseArray<String> fatherKeys) {

                    }
                });
            }
        });
        findViewById(R.id.title).findViewById(R.id.secondary_back_img).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (easyTipDragView != null)
                    easyTipDragView.saveClose();
                saveUserDefineTemplate();
                finish();
            }
        });
        Intent intent = getIntent();
        list = (List<ProspectPreViewItemData>) intent.getSerializableExtra("tabList");
        int which_tab = getIntent().getIntExtra("which_t" +
                "ab", 0);
        String mode = getIntent().getStringExtra("mode");
        findType = getIntent().getStringExtra("findType");
        simpleTitleTips = new ArrayList<>();
        dragViewTiltle = (TextView) findViewById(R.id.drag_title);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        arrowTabView = (ArrowTabView) findViewById(R.id.arrow);
        if (getIntent().getBooleanExtra("isSimpleCase",false)){
            arrowTabView.setVisibility(View.INVISIBLE);
        }
        proData2Tip(list);
        noteFragmentDataChange();
        arrowTabView.setListener(this);
        pagerSlidingTabStrip.setOnPageChangeListener(this);
        prospectPager.setCurrentItem(which_tab, false);
        saveUserDefineTemplate();
        finishTag = (TextView) findViewById(R.id.finish_tag);
        finishTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                easyTipDragView.setFinishEditing();
                finishTag.setVisibility(View.INVISIBLE);
                arrowTabView.setVisibility(View.VISIBLE);
                dragViewTiltle.setText("切换栏目");
                easyTipDragView.saveClose();
                saveUserDefineTemplate();
                initItemFromJson();
            }
        });
    }


    private List<Fragment> initFragment() {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new CaseBasicInfoFragment());
        fragmentList.add(new CaseBasicInfoFragment());
        fragmentList.add(new CaseBasicInfoFragment());
        fragmentList.add(new EvidenceFragment());
        fragmentList.add(new EvidenceFragment());
        fragmentList.add(new EvidenceFragment());
        fragmentList.add(new EvidenceFragment());
        fragmentList.add(new EvidenceFragment());
        fragmentList.add(new EvidenceFragment());
        fragmentList.add(new EvidenceFragment());
        fragmentList.add(new EvidenceFragment());
        fragmentList.add(new EvidenceFragment());
        fragmentList.add(new EvidenceFragment());
        fragmentList.add(new EvidenceFragment());
        fragmentList.add(new CaseBasicInfoFragment());
        fragmentList.add(new EvidenceFragment());
        fragmentList.add(new CaseBasicInfoFragment());
        fragmentList.add(new ProspectVideoFragment());
        fragmentList.add(new ScenePhotos());
        fragmentList.add(new SceneBlind());
        fragmentList.add(new EvidenceFragment());
        fragmentList.add(new EvidenceFragment());
        fragmentList.add(new SceneDirectionFragment());
        fragmentList.add(new ScenePlan());
        fragmentList.add(new CaseBasicInfoFragment());
        fragmentList.add(new CaseBasicInfoFragment());
        fragmentList.add(new CaseBasicInfoFragment());
        List<Fragment> useFragment = new ArrayList<>();
        for (Tip tip : simpleTitleTips) {
            SimpleTitleTip simpleTitleTip = (SimpleTitleTip) tip;
            for (int i = 0; i < fieldIds.length; i++) {
                if (simpleTitleTip.getFieldId().equals(fieldIds[i])) {
                    Fragment fragment = fragmentList.get(i);
                    setAllFragmentArgs(fragment, fieldIds[i], isNeedRecord(fieldIds[i]) + "");
                    useFragment.add(fragment);
                }
            }
        }
        return useFragment;
    }

    @Override
    protected void onDestroy() {
        saveUserDefineTemplate();
        super.onDestroy();
    }

    @Override
    public void onArrowDirectChange(boolean isArrowUp, int current, String key) {
        if (isArrowUp && popupWindow == null) {
            initTabPop();
        } else if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
            dragViewTiltle.setVisibility(View.INVISIBLE);
            pagerSlidingTabStrip.setVisibility(View.VISIBLE);
            easyTipDragView.saveClose();
            saveUserDefineTemplate();
            initItemFromJson();
        }else{
            saveUserDefineTemplate();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d("tfposition","position"+position+"positionOffset"+positionOffset+"positionOffset"+positionOffsetPixels+"");
        if(findType!=null&&findType.equals("find")){
            //请求数据
            getFindListData("/history/investigateData");
        }

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTileSelected(Tip entity, int position, View view) {
        defaultPosition = position;
        if (popupWindow != null && popupWindow.isShowing()) {
            easyTipDragView.saveClose();
            popupWindow.dismiss();
            popupWindow = null;
            dragViewTiltle.setVisibility(View.INVISIBLE);
            pagerSlidingTabStrip.setVisibility(View.VISIBLE);
            arrowTabView.changState();
        }
    }

    @Override
    public void onComplete(ArrayList<Tip> tips) {
        simpleTitleTips.clear();
        simpleTitleTips.addAll(tips);
        noteFragmentDataChange();
        if (prospectPager.getAdapter().getCount() != 0) {
            prospectPager.setCurrentItem(defaultPosition);
        }
    }

    @Override
    public void onStartDrag() {
        finishTag.setVisibility(View.VISIBLE);
        arrowTabView.setVisibility(View.INVISIBLE);
        dragViewTiltle.setText("拖动排序");
    }


    public interface OnActivityResumeListener {
        void ActivityResumeListener();
    }

    public void setOnActivityResumeListener(OnActivityResumeListener onActivityResumeListener) {
        this.onActivityResumeListener = onActivityResumeListener;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isCameraPreview = false;
        if (onActivityResumeListener != null) {
            onActivityResumeListener.ActivityResumeListener();
        }
        initItemFromJson();
    }

    public void takeToast(View view) {
        Toast.makeText(ProspectInterface.this, "保存成功", Toast.LENGTH_SHORT).show();
    }


    private void setAllFragmentArgs(Fragment fragment, String father, String isNeedRec) {
        Bundle bundle = new Bundle();
        bundle.putString("father", father);
        bundle.putString("isNeedRec", isNeedRec);
        bundle.putString("templateId", getIntent().getStringExtra("templateId"));
        bundle.putString("caseId", getIntent().getStringExtra("caseId"));
        bundle.putString("mode", getIntent().getStringExtra("mode"));
        bundle.putBoolean(BaseView.ADDREC, getIntent().getBooleanExtra(BaseView.ADDREC,false));
        if (fragment instanceof SceneBlind) {
            bundle.putString("belongTo", "unclass");
        }
        if (fragment instanceof ScenePhotos) {
            bundle.putString("belongTo", "general");
        }
        fragment.setArguments(bundle);

    }

    private boolean isNeedRecord(String father) {
        for (ProspectPreViewItemData data : list) {
            if (data.getField().equals(father))
                return data.isNeedRec();
        }
        return false;
    }

    private void initTabPop() {
        View view = getLayoutInflater().inflate(R.layout.pager_tab_window, null);
        easyTipDragView = (EasyTipDragView) view.findViewById( R.id.easy_tip_drag_view);
        easyTipDragView.setSelectedListener(this);
        easyTipDragView.setDragData(simpleTitleTips,prospectPager.getCurrentItem());
        easyTipDragView.setAddData(getCouldAddTips());
        easyTipDragView.setOnCompleteCallback(this);
        if (TextUtils.equals(getIntent().getStringExtra("mode"), BaseView.VIEW))
            easyTipDragView.setViewMode();
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, point.y - getStatusBarHeight() -
                (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,91,getResources().getDisplayMetrics()));
        popupWindow.showAsDropDown(pagerSlidingTabStrip, 0, (int) getResources().getDimension(R.dimen.all_view_stroke) + 2);
        dragViewTiltle.setVisibility(View.VISIBLE);
        pagerSlidingTabStrip.setVisibility(View.INVISIBLE);
    }

    private List<Tip> getCouldAddTips() {
        List<BaseTemp> baseTemps = EvidenceApplication.db.findAllByWhere(BaseTemp.class, "templateType = '0'and templateLevel = '1'");
        List<Tip> tipList = new ArrayList<>();
        int tipsId = 0;
        for (BaseTemp baseTemp : baseTemps) {
            SimpleTitleTip simpleTitleTip = new SimpleTitleTip();
            simpleTitleTip.setId(tipsId++);
            simpleTitleTip.setFieldId(baseTemp.getTableName());
            simpleTitleTip.setTip(baseTemp.getSceneName());
            tipList.add(simpleTitleTip);
        }
        Iterator<Tip> iterator = tipList.iterator();
        while (iterator.hasNext()) {
            if (contains(iterator.next())) {
                iterator.remove();
            }
        }
        return tipList;
    }

    private boolean contains(Tip tip) {
        SimpleTitleTip simpleTitleTip = (SimpleTitleTip) tip;
        for (Tip titleTip : simpleTitleTips) {
            if (((SimpleTitleTip) titleTip).getFieldId().equals(simpleTitleTip.getFieldId())) {
                return true;
            }
        }
        return false;
    }

    private void noteFragmentDataChange() {
        ProspectViewpagerAdapter prospectViewpagerAdapter = new ProspectViewpagerAdapter(getSupportFragmentManager(), initFragment());
        prospectViewpagerAdapter.setTitles(simpleTitleTips);
        prospectPager.setAdapter(prospectViewpagerAdapter);
        pagerSlidingTabStrip.setViewPager(prospectPager);
    }

    private void proData2Tip(List<ProspectPreViewItemData> list) {
        simpleTitleTips.clear();
        int id = 0;
        for (ProspectPreViewItemData prospectPreViewItemData : list) {
            SimpleTitleTip simpleTitleTip = new SimpleTitleTip();
            simpleTitleTip.setId(id++);
            simpleTitleTip.setTip(prospectPreViewItemData.getName());
            simpleTitleTip.setFieldId(prospectPreViewItemData.getField());
            simpleTitleTip.setNeedRec(prospectPreViewItemData.isNeedRec());
            simpleTitleTips.add(simpleTitleTip);
        }
    }

    private void saveUserDefineTemplate() {
        String caseId = getIntent().getStringExtra("caseId");
        EvidenceApplication.db.deleteByWhere(TemplateSort.class, "caseId = '" + caseId + "'");
        int sort = 0;
        for (Tip tip : simpleTitleTips) {
            SimpleTitleTip simpleTitleTip = (SimpleTitleTip) tip;
            TemplateSort templateSort = new TemplateSort();
            templateSort.setId(ViewUtil.getUUid());
            templateSort.setFatherKey(simpleTitleTip.getFieldId());
            templateSort.setFatherValue(simpleTitleTip.getTip());
            templateSort.setSort(sort++);
            templateSort.setCaseId(caseId);
            templateSort.setDate(ViewUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
            templateSort.setSiNeedRec(simpleTitleTip.isNeedRec());
            EvidenceApplication.db.save(templateSort);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4 && resultCode == Activity.RESULT_OK && null != data) {
            String sdState = Environment.getExternalStorageState();
            if (!sdState.equals(Environment.MEDIA_MOUNTED)) {
                return;
            }
            new DateFormat();
            String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
            Bundle bundle = data.getExtras();
            //获取相机返回的数据，并转换为图片格式
            Bitmap bitmap = (Bitmap) bundle.get("data");
            FileOutputStream fout = null;
            File file = new File("/sdcard/pintu/");
            file.mkdirs();
            String filename = file.getPath() + name;
            try {
                fout = new FileOutputStream(filename);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    fout.flush();
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //显示图片

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (easyTipDragView != null)
                easyTipDragView.saveClose();
            saveUserDefineTemplate();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initItemFromJson() {
        List<TemplateSort> list = EvidenceApplication.db.findAllByWhere(TemplateSort.class, "caseId = '" + getIntent().getStringExtra("caseId") + "'", "sort asc");
        if (list != null && list.size() != 0)
            simpleTitleTips.clear();
        else
            return;
        int id = 0;
        for (TemplateSort templateSort : list) {
            SimpleTitleTip simpleTitleTip = new SimpleTitleTip();
            simpleTitleTip.setId(id++);
            simpleTitleTip.setTip(templateSort.getFatherValue());
            simpleTitleTip.setFieldId(templateSort.getFatherKey());
            simpleTitleTip.setNeedRec(templateSort.isSiNeedRec());
            simpleTitleTips.add(simpleTitleTip);
        }
        if (easyTipDragView != null && prospectPager != null){
            int lastPosition = prospectPager.getCurrentItem();
            easyTipDragView.setDragData(simpleTitleTips, lastPosition);
            noteFragmentDataChange();
            if (prospectPager.getAdapter().getCount() != 0) {
                prospectPager.setCurrentItem(lastPosition);
            }
        }

    }
    public int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    //目标表名称
    //String targetTableName;
    //勘验ID（微勘后台的UUID）
    //String investigationId;

    private void getFindListData(String MethodName){
        StringMap params = new StringMap();

        params.putString("ver", "1");
        params.putString("verName", Netroid.versionName);
        params.putString("deviceId", Netroid.dev_ID);
        params.putString("targetTableName","SCENE_LAW_CASE_EXT");
        params.putString("investigationId",getIntent().getStringExtra("caseId"));

        Netroid.PostHttp(MethodName, params, new Netroid.OnLister<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {


                Log.d("responsecomplete", "" + response);
                try {
                    if (response.getBoolean("success")) {

                       /* String response1=  response.getString("data");
                        JSONObject jSONObject = new JSONObject(response1);
                        Log.d("response1", response1 + "");

                        String data1 = jSONObject.getString("data");
                        JSONArray jsonArray =new JSONArray(data1);
                        Log.d("jsonArray",jsonArray+"");

                        JSONObject jsonObjectdata;
                        FindCsSceneCases findCsSceneCases = new FindCsSceneCases();*/

                    }
                    // materialRefreshLayout.finishRefreshLoadMore();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Utils.stopProgressDialog();
            }

            @Override
            public void onError(NetroidError error) {
                Log.d("error", "" + error);
                //   mSceneNoDetails.setVisibility(View.VISIBLE);
                // mSceneNoDetailsText.setText("加载失败，请检查网络！");
                Utils.stopProgressDialog();
            }
        });

    }


}

