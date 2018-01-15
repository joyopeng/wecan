package com.gofirst.scenecollection.evidence.view.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.model.UnUpLoadBlock;
import com.gofirst.scenecollection.evidence.model.UnUploadJson;
import com.gofirst.scenecollection.evidence.model.User;
import com.gofirst.scenecollection.evidence.sync.UpdateNewCaseService;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.Netroid;
import com.gofirst.scenecollection.evidence.utils.OSUtil;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.utils.StringMap;
import com.gofirst.scenecollection.evidence.utils.Utils;
import com.gofirst.scenecollection.evidence.view.customview.NoScrollViewPager;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;
import com.gofirst.scenecollection.evidence.view.fragment.ApplyFragment;
import com.gofirst.scenecollection.evidence.view.fragment.HomePageFragment;
import com.gofirst.scenecollection.evidence.view.fragment.LinkmanFragment;
import com.gofirst.scenecollection.evidence.view.fragment.ManagerFragment;
import com.gofirst.scenecollection.evidence.view.fragment.PersonCenterFragment;
import com.gofirst.scenecollection.evidence.view.fragment.SceneProspectFragment;

import net.tsz.afinal.db.sqlite.DbModel;
import net.tsz.afinal.http.AjaxParams;

import org.androidpn.client.Constants;
import org.androidpn.client.ServiceManager;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnClickListener {

    private String dataFilePath= AppPathUtil.getCachePath()+"/"+ OSUtil.getUUid()+".txt";

    private ViewPager viewPager;
    private List<View> viewList;
    //private TextView ActionBarText, rightTittle;
    //private ImageView rightImage;
    private LinearLayout ll_tabs;
    //private RelativeLayout tittleLinearLayout;
    private SharePre sharePre;

    /**
     * search title links
     * */
    private View mSearchTitleLayout; //title search layout
    private TextView mSearchTitleName;  //title name
    private View mSearchTitleAlarmLayout;  //title 警情布局
    private View mSearchTitleSearchLayout;  //title search child layout
    private ImageView mSearchTitleAlarmImg;  //title 警情布局的图片
    private ImageView mSearchTitleAlarmImg2;  //title 警情布局的图片
    private EditText mSearchTitleSearchEdit;  //title search Edit
    private ImageView mSearchTitleSearchBtn;  //title search button
    private ImageView mSearchTitleBackBtn;    //title back button
    private ImageView mSearchTitleAddImg;
//    private DownLoadAsync.DownLoadAsyncListener downLoadAsyncListener;

    public interface SearchTitleClick{
        void searchBtnClick(String key);
        void backBtnClick();
    }
    /**
     * 图标切换
     */
    private ImageView newestStateImage, linkmanImage,
            applyImage, personCenterImage;


    /**
     * 四个导航按钮
     */
    LinearLayout newestStateLinearLayout, linkmanLinearLayout,
            applyLinearLayout, personCenterLinearlayout;

    /**
     * 作为页面容器的ViewPager
     */
    //ViewPager mViewPager;
    NoScrollViewPager mViewPager;
    /**
     * 页面集合
     */
    List<Fragment> fragmentList;

    /**
     * 四个Fragment（页面）
     */
    //NewestStateFragment NewestStateFragment;
    HomePageFragment HomePageFragment;
    LinkmanFragment LinkmanFragment;
    ApplyFragment ApplyFragment;
    PersonCenterFragment PersonCenterFragment;
    SceneProspectFragment mSceneProspectFragment;  //scene prospect fragment
    ManagerFragment mManagerFragment; // manager fragment
    //覆盖层
    ImageView imageviewOvertab;

    //屏幕宽度
    int screenWidth;
    //当前选中的项
    int currenttab = -1;
    TextView button;
    private String curDatestr = "";
//    private UniversalLoadingView universalLoadingView;

    //add push on
    private ServiceManager mPushManager;
    //add push off

    public FragmentTransaction mFragmentTransaction;
    public FragmentManager fragmentManager;
    public String curFragmentTag = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharePre = new SharePre(MainActivity.this, "user_info", Context.MODE_WORLD_READABLE |MODE_MULTI_PROCESS);
        mViewPager = (NoScrollViewPager) this.findViewById(R.id.search_viewpager);
        newestStateLinearLayout = (LinearLayout) findViewById(R.id.newest_state_linearlayout);
        linkmanLinearLayout = (LinearLayout) findViewById(R.id.linkman_linearlayout);
        applyLinearLayout = (LinearLayout) findViewById(R.id.apply_linearlayout);
        personCenterLinearlayout = (LinearLayout) findViewById(R.id.person_center_linearlayout);
        ll_tabs = (LinearLayout) findViewById(R.id.ll_tabs);
        button = (TextView) findViewById(R.id.button);
        newestStateLinearLayout.setOnClickListener(this);
        linkmanLinearLayout.setOnClickListener(this);
        applyLinearLayout.setOnClickListener(this);
        personCenterLinearlayout.setOnClickListener(this);
        fragmentList = new ArrayList<Fragment>();
        //ApplyFragment = new ApplyFragment();
        //NewestStateFragment = new NewestStateFragment();
        HomePageFragment=new HomePageFragment();
        PersonCenterFragment = new PersonCenterFragment();
        //LinkmanFragment = new LinkmanFragment();
        mSceneProspectFragment = new SceneProspectFragment();
        mManagerFragment = new ManagerFragment();
        fragmentList.clear();

//        fragmentList.add(NewestStateFragment);HomePageFragment
        fragmentList.add(HomePageFragment);
        fragmentList.add(mSceneProspectFragment);
        //fragmentList.add(LinkmanFragment);
        fragmentList.add(mManagerFragment);
        //fragmentList.add(ApplyFragment);
        fragmentList.add(PersonCenterFragment);

        screenWidth = getResources().getDisplayMetrics().widthPixels;

        mViewPager.setAdapter(new MyFrageStatePagerAdapter(getSupportFragmentManager()));


        //search title layout
        mSearchTitleLayout = findViewById(R.id.search_title_layout);
        mSearchTitleName = (TextView) findViewById(R.id.title_bar_tv);  //title name
        mSearchTitleAlarmLayout = findViewById(R.id.title_bar_alarm_layout);  //title 警情布局
        mSearchTitleSearchLayout = findViewById(R.id.title_bar_layout);
        mSearchTitleAlarmImg = (ImageView) findViewById(R.id.title_bar_alarm_img); //title 警情布局的图片
      //  mSearchTitleAlarmImg.setOnClickListener(this);
        mSearchTitleAlarmImg2 = (ImageView) findViewById(R.id.title_bar_alarm_img2); //title 警情布局的图片
        mSearchTitleAlarmImg2.setOnClickListener(this);
        mSearchTitleAddImg = (ImageView) findViewById(R.id.title_bar_add_img); //title 警情布局的图片
       // mSearchTitleAddImg.setOnClickListener(this);
        mSearchTitleSearchEdit = (EditText) findViewById(R.id.title_bar_search_edit);  //title search Edit
        mSearchTitleSearchBtn = (ImageView) findViewById(R.id.title_bar_search_img);
        mSearchTitleSearchBtn.setOnClickListener(this);
        mSearchTitleBackBtn = (ImageView) findViewById(R.id.search_title_back_img);
        mSearchTitleBackBtn.setOnClickListener(this);
        findViewById(R.id.title_bar_add_layout).setOnClickListener(this);
        findViewById(R.id.title_bar_alarm_layout).setOnClickListener(this);
        newestStateImage = (ImageView) findViewById(R.id.newest_state_imageview);
        linkmanImage = (ImageView) findViewById(R.id.linkman_imageview);
        applyImage = (ImageView) findViewById(R.id.apply_imageview);
        personCenterImage = (ImageView) findViewById(R.id.person_center_imageview);
        Log.d("token1", sharePre.getString("token", ""));
/*
        if(isNeedUpload()){
            showNeedUpload();
        }*/
        findViewById(R.id.title_bar_alarm).setOnClickListener(this);
        findViewById(R.id.title_bar_add_layout).setOnClickListener(this);

        View rootView = findViewById(R.id.rootView);
        ViewUtil.expandViewTouchDelegate(findViewById(R.id.title_bar_alarm),rootView,50,50,50,50);
        ViewUtil.expandViewTouchDelegate(findViewById(R.id.title_bar_add_layout),rootView,50,50,50,50);
        Intent serviceIntent = new Intent(this, UpdateNewCaseService.class);
        bindService(serviceIntent,mConnect,BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mUpdateBinder = (UpdateNewCaseService.UpdateNewCaseBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mUpdateBinder = null;
        }
    };

    private UpdateNewCaseService.UpdateNewCaseBinder mUpdateBinder;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkBaseData();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("zhangsh","MainActivity onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnect);
        sharePre = null;
        Log.i("zhangsh","MainActivity onDestroy");
    }

    /**
     * 定义自己的ViewPager适配器。
     * 也可以使用FragmentPagerAdapter。关于这两者之间的区别，可以自己去搜一下。
     */
    class MyFrageStatePagerAdapter extends FragmentStatePagerAdapter {

        public MyFrageStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);

        }

        /**
         * 每次更新完成ViewPager的内容后，调用该接口，此处复写主要是为了让导航按钮上层的覆盖层能够动态的移动
         */
        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);//这句话要放在最前面，否则会报错
            //获取当前的视图是位于ViewGroup的第几个位置，用来更新对应的覆盖层所在的位置
            int currentItem = mViewPager.getCurrentItem();
            if (currentItem == currenttab) {
                return;
            }
//			imageMove(mViewPager.getCurrentItem());
            currenttab = mViewPager.getCurrentItem();
            Log.d("currenttab", "" + currenttab);

            mSearchTitleSearchEdit.setText("");
            switch (currenttab) {
                case 0:
                    //                  tittleLinearLayout.setVisibility(View.VISIBLE);
                    //                 ActionBarText.setText("最新警情");
                    mSearchTitleName.setText("首页");
                    hideSearchBackBtn();
                    showSearchEditLayout(true);

                    newestStateLinearLayout.setBackgroundResource(R.color.bottomblue);
                    linkmanLinearLayout.setBackgroundResource(R.color.bottomdeepblue);
                    applyLinearLayout.setBackgroundResource(R.color.bottomdeepblue);
                    personCenterLinearlayout.setBackgroundResource(R.color.bottomdeepblue);

                    break;
                case 2:

                    mSearchTitleName.setText("管理");
                    showSearchEditLayout(false);

                    //rightImage.setVisibility(View.INVISIBLE);
                    newestStateLinearLayout.setBackgroundResource(R.color.bottomdeepblue);
                    linkmanLinearLayout.setBackgroundResource(R.color.bottomblue);
                    applyLinearLayout.setBackgroundResource(R.color.bottomdeepblue);
                    personCenterLinearlayout.setBackgroundResource(R.color.bottomdeepblue);

                    break;
                case 1:
                    mSearchTitleName.setText("现场");
                    hideSearchBackBtn();
                    showSearchEditLayout(true);

                    newestStateLinearLayout.setBackgroundResource(R.color.bottomdeepblue);
                    linkmanLinearLayout.setBackgroundResource(R.color.bottomdeepblue);
                    applyLinearLayout.setBackgroundResource(R.color.bottomblue);
                    personCenterLinearlayout.setBackgroundResource(R.color.bottomdeepblue);

                    break;
                case 3:
                    //tittleLinearLayout.setVisibility(View.VISIBLE);
                    //ActionBarText.setText("个人中心");
                    mSearchTitleName.setText("个人中心");
                    hideSearchBackBtn();
                    showSearchEditLayout(false);

                    //rightImage.setVisibility(View.INVISIBLE);
                    newestStateLinearLayout.setBackgroundResource(R.color.bottomdeepblue);
                    linkmanLinearLayout.setBackgroundResource(R.color.bottomdeepblue);
                    applyLinearLayout.setBackgroundResource(R.color.bottomdeepblue);
                    personCenterLinearlayout.setBackgroundResource(R.color.bottomblue);

                    break;
            }

        }

    }

    /**
     * 移动覆盖层
     *
     * @param moveToTab 目标Tab，也就是要移动到的导航选项按钮的位置
     *                  第一个导航按钮对应0，第二个对应1，以此类推
     */
    private void imageMove(int moveToTab) {
        int startPosition = 0;
        int movetoPosition = 0;

        startPosition = currenttab * (screenWidth / 4);
        movetoPosition = moveToTab * (screenWidth / 4);
        //平移动画
        TranslateAnimation translateAnimation = new TranslateAnimation(startPosition, movetoPosition, 0, 0);
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(0);
        imageviewOvertab.startAnimation(translateAnimation);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newest_state_linearlayout:
                changeView(0);
                break;
            case R.id.linkman_linearlayout:
                changeView(2);
                break;
            case R.id.apply_linearlayout:
                changeView(1);
                break;

            case R.id.person_center_linearlayout:
                changeView(4);
                break;
            case R.id.title_bar_alarm:
                Intent intent = new Intent(MainActivity.this, MoreNewCase.class);
                startActivity(intent);
                break;
            case R.id.title_bar_alarm_layout:
                Intent intent2 = new Intent(MainActivity.this, MoreNewCase.class);
                startActivity(intent2);
                break;
            case R.id.title_bar_search_img:
                searchButtonClick(mSearchTitleSearchEdit.getText().toString().trim());
                break;
            case R.id.search_title_back_img:
                bachButtonClick();
                break;
            case R.id.title_bar_add_layout:
                Intent intent1 = new Intent(MainActivity.this, AddNewCase.class);
                startActivity(intent1);
                /*Intent intent1 = new Intent(MainActivity.this, ASRMedicalOnlineActivity.class);
                startActivity(intent1);*/
                break;
            default:
                break;
        }
    }

    /**
     * search title btn click
     */


    private void searchButtonClick(String key) {
        int item = mViewPager.getCurrentItem();
        switch (item) {
            case 0:
                HomePageFragment.searchBtnClick(key);
                break;
            case 1:
                mSceneProspectFragment.searchBtnClick(key);
                break;
            case 2:

                break;
            case 3:

                break;
            default:
                break;
        }
    }

    private void bachButtonClick() {
        int item = mViewPager.getCurrentItem();
        switch (item) {
            case 0:

                break;
            case 1:
                //LinkmanFragment.backBtnClick();
                break;
            case 2:
                break;
            case 3:

                break;
            default:
                break;
        }
    }

    public void showSearchBackBtn() {
        mSearchTitleBackBtn.setVisibility(View.VISIBLE);
    }

    public void hideSearchBackBtn() {
        mSearchTitleBackBtn.setVisibility(View.INVISIBLE);
    }

    public void showSearchEditLayout(boolean show) {
        if (show) {
            //if (mSearchTitleAlarmLayout.getVisibility() == View.GONE) {
            //    return;
           // }
            mSearchTitleAlarmLayout.setVisibility(View.GONE);
            ViewGroup.LayoutParams layout = mSearchTitleSearchLayout.getLayoutParams();
            layout.height = Utils.dp2Px(this, 100);
            mSearchTitleSearchLayout.setLayoutParams(layout);
        } else {
            if (mSearchTitleAlarmLayout.getVisibility() == View.VISIBLE) {
                return;
            }
            //mSearchTitleAlarmLayout.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layout = mSearchTitleSearchLayout.getLayoutParams();
            layout.height = Utils.dp2Px(this, 54);
            mSearchTitleSearchLayout.setLayoutParams(layout);
        }
    }

    //////////////////////////////////////////////////////////////////////
    //手动设置ViewPager要显示的视图
    private void changeView(int desTab) {
        Log.i("MainActivity","changeView start time = " + System.currentTimeMillis());
        mViewPager.setCurrentItem(desTab, true);
        Log.i("MainActivity","changeView end time = " + System.currentTimeMillis());
    }

    public static SQLiteDatabase getSQLiteDatabase(Context context) {
        File file = new File(AppPathUtil.getDBPath()+ "/evidence.db");
        return SQLiteDatabase.openOrCreateDatabase(file.getAbsoluteFile(), null);

    }

    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Utils.MSG_DOWNLOAD_BASE_DATA:
                    boolean suc = (boolean)msg.obj;
                    if(!suc){
                        Utils.stopProgressDialog();
                        EvidenceApplication.SHOW_DOWNLOAD_BASEDATA_DIALOG = false;
                        HomePageFragment.searchBtnClick("");
                    }
                    break;
                case Utils.MSG_ANALYSIS_DATA:
                    boolean suc1 = (boolean)msg.obj;
                    if(suc1){
                        Bundle bundle = msg.getData();
                        String coredataUpdateTime = bundle.getString("update_time","");
                        if(coredataUpdateTime != null && !"".equals(coredataUpdateTime)) {
                            sharePre.put(Utils.SHARE_SYNC_BASE_DATA_CONDITION,coredataUpdateTime);
                            sharePre.commit();
                            List<User> userList = EvidenceApplication.db.findAllByWhere(User.class,"userId = \"" + sharePre.getString("userId","") + "\"");
                            if (userList.size() != 0){
                                User user = userList.get(0);
                                user.setCoredataUpdateTime(coredataUpdateTime);
                                EvidenceApplication.db.update(user);
                            }
                        }
                    }
                    EvidenceApplication.SHOW_DOWNLOAD_BASEDATA_DIALOG = false;
                    Utils.stopProgressDialog();
                    HomePageFragment.searchBtnClick("");
                    break;
                case 1:
                    Utils.stopProgressDialog();
                    //universalLoadingView.stopLoading();
                    // new Handler().postDelayed(new PushThread(MainActivity.this), 1000);
                    break;
                case 2:
                    //Toast.makeText(MainActivity.this,"zhangsh test",Toast.LENGTH_SHORT).show();
                    //universalLoadingView.stopLoading();
                    break;
                case 3:
                    SharedPreferences share = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                            Context.MODE_PRIVATE);
                    String pushUserName = share.getString(Constants.XMPP_USERNAME, "");
                    String pushPwd = share.getString(Constants.XMPP_PASSWORD, "");

                    String userId = sharePre.getString("userId", "");
                    Log.i("zhangsh", "PushThread pushname = " + pushUserName + ";pushPwd = " + pushPwd +
                            ";userId = " + userId);
                    StringMap params = new StringMap();
                    params.putString("userId", userId);
                    params.putString("appId", pushUserName);
                    Netroid.PostHttp("/push", params, new Netroid.OnLister<JSONObject>() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.d("onSuccess", "onSuccess response " + response.toString());
                        }
                        @Override
                        public void onError(NetroidError error) {
                            Log.d("error", "" + error);
                        }
                    });
                    // universalLoadingView.stopLoading();
                    break;
                default:
                    break;
            }
        }
    };



    //add push on
    private class PushThread extends Thread {
        Context context;

        public PushThread(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            /*try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            SharedPreferences share = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                    Context.MODE_PRIVATE);
            String pushUserName = share.getString(Constants.XMPP_USERNAME, "");
            String pushPwd = share.getString(Constants.XMPP_PASSWORD, "");

            String userId = sharePre.getString("userId", "");
            Log.i("zhangsh", "PushThread pushname = " + pushUserName + ";pushPwd = " + pushPwd +
                    ";userId = " + userId);
            /*StringMap params = new StringMap();
            params.putString("userId", userId);
            params.putString("appId", pushUserName);
            Netroid.PostHttp("/push", params, new Netroid.OnLister<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d("onSuccess", "onSuccess response " + response.toString());
                }

                @Override
                public void onError(NetroidError error) {
                    Log.d("error", "" + error);
                }
            });*/
        }
    }

    private void checkBaseData(){
        List<DbModel> list = EvidenceApplication.db.findDbModelListBySQL("select * from CsDicts order by id limit 0,10");
        if (!(list != null && list.size() > 5)) {
            EvidenceApplication.SHOW_DOWNLOAD_BASEDATA_DIALOG = true;
            AlertDialog.Builder  builder= new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("更新提示");
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.log_text,null);
            builder.setView(view);
            TextView log = (TextView)view.findViewById(R.id.update_log);
            log.setText("首次使用必须下载更新基础数据,否则无法正常使用!");
            builder.setPositiveButton("下载",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            //downloadBaseData();
                            AjaxParams params = new AjaxParams();
                            params.put("token",sharePre.getString("token",""));
                            String startTime = "";
                            String json = "{\"dataType\":\"coredata\",\"startTime\":\"" + startTime + "\",\"cascading\":\"n\"}";
                            params.put("condition",json);
                            Utils.downloadBaseData(MainActivity.this, "/baseDataMulti", params, Utils.BASE_DATA_FILE_PATH, myHandler, true);
                            sharePre.put("deteleFile","1");
                            sharePre.commit();
                            dialog.dismiss();
                        }
                    });
            builder.setNegativeButton("退出",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            EvidenceApplication.SHOW_DOWNLOAD_BASEDATA_DIALOG = false;
                            finish();
                        }

                    });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    EvidenceApplication.SHOW_DOWNLOAD_BASEDATA_DIALOG = false;
                }
            });
            AlertDialog adlg = builder.create();
            adlg.show();

        }
    }



    private boolean isNeedUpload(){
        List<RecordFileInfo> fileInfoList = new ArrayList<>();
        List<RecordFileInfo> recordList = EvidenceApplication.db.findAll(RecordFileInfo.class);
        for (RecordFileInfo recordFileInfo : recordList) {
            if (recordFileInfo.getContractionsFilePath() != null) {
                RecordFileInfo recordFileInfo1 = new RecordFileInfo();
                recordFileInfo.setFilePath(recordFileInfo.getContractionsFilePath());
                fileInfoList.add(recordFileInfo1);
                continue;
            }
            if (recordFileInfo.getFilePath() != null) {
                fileInfoList.add(recordFileInfo);
            }
        }

        List<UnUpLoadBlock> Uploadlist = new ArrayList<>();
        List<DbModel> dbModels = EvidenceApplication.db.findDbModelListBySQL("select * from UnUpLoadBlock order by id limit 0,3");
        for (DbModel dbModel : dbModels) {
            UnUpLoadBlock block = new UnUpLoadBlock();
            block.setBlockTotal(dbModel.getInt("blockTotal"));
            block.setParentPath(dbModel.getString("parentPath"));
            block.setBlockIndex(dbModel.getInt("blockIndex"));
            block.setPath(dbModel.getString("path"));
            block.setId(dbModel.getString("id"));
            Uploadlist.add(block);
        }

        List<UnUploadJson> unUnloadJsonlist = new ArrayList<>();
        dbModels = EvidenceApplication.db.findDbModelListBySQL("select * from UnUploadJson order by id limit 0,1");
        for (DbModel dbModel : dbModels) {
            UnUploadJson unUploadJson = new UnUploadJson();
            unUploadJson.setCaseId(dbModel.getString("caseId"));
            unUploadJson.setJson(dbModel.getString("json"));
            unUnloadJsonlist.add(unUploadJson);
        }
        return fileInfoList.size() != 0 || Uploadlist.size() != 0 || unUnloadJsonlist.size() != 0;
    }

    private void showNeedUpload(){
        AlertDialog.Builder  builder= new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("上传提示");
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.log_text,null);
        builder.setView(view);
        TextView log = (TextView)view.findViewById(R.id.update_log);
        log.setText("有需要上传的文件，是否继续上传！");
        builder.setPositiveButton("上传",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //UpLoadService.setManualStart(true);
                        Intent intent = new Intent(MainActivity.this,SyncDataActivity.class).putExtra("show_view_item","2");
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //no operate
                    }

                });
        AlertDialog adlg = builder.create();
        adlg.show();
    }


}
