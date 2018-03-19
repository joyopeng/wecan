package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.LostGood;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;
import com.gofirst.scenecollection.evidence.view.customview.PhotoDialog;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;
import com.gofirst.scenecollection.evidence.view.fragment.SceneInfoFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import cn.com.keshengxuanyi.mobilereader.NFCReaderHelper;
import cn.com.keshengxuanyi.mobilereader.UserInfo;

//import com.YinanSoft.CardReaders.IDCardReader;
//import com.YinanSoft.CardReaders.UsbReader;
//import com.YinanSoft.Utils.FileUnits;

public class LostGoodDetail extends Activity {

    private static String appKey = "941c9b37d4dd4e569ff0320b21d9071c";

    private static String appSecret = "8eb5c020856040f7be7e52cff4ce3a77";

    private LostGood caseBasicInfo;
    private List<BaseView> viewLists;
    private String caseId, father;
    private boolean addRec;
    private NFCReaderHelper mNFCReaderHelper;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    static Handler uiHandler = null;
    private AsyncTask<Void, Void, String> nfcTask = null;
    private Context context = null;
    //for ID-Card
//    String path = "YinanSoft";
//    String filename = "armidse.bin";
//    private UsbController controller;
//    private IDCardReader idReader = null;
    byte[] bt = new byte[96];
    String saveKey = "";
    String section = "";  //section = caseBasicInfo.getId()

    private String IC_NO;
    private String NAME;
    private String SEX;
    private String AGE;
    private String ADDRESS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.lostdeatail_layout);
        uiHandler = new MyHandler(this);
        context = this;
        LinearLayout containerLayout = (LinearLayout) findViewById(R.id.container);
        String templateId = getIntent().getStringExtra("templateId");
        caseBasicInfo = getLostGood();
        String mode = getIntent().getStringExtra("mode");
        caseId = getIntent().getStringExtra("caseId");
        father = getIntent().getStringExtra("father");
        addRec = getIntent().getBooleanExtra(BaseView.ADDREC, false);
        LinearLayout take_ic_photo_layout = (LinearLayout) findViewById(R.id.take_ic_photo_layout);
        TextView photoView = (TextView) findViewById(R.id.take_ic_photo_btn);
        TextView idView = (TextView) findViewById(R.id.take_id_btn);
        ((TextView) findViewById(R.id.title).findViewById(R.id.secondary_title_tv)).setText(getIntent().getStringExtra("title"));
        TextView right = ((TextView) findViewById(R.id.title).findViewById(R.id.secondary_right_tv));
        right.setText("完成");
        right.setVisibility(BaseView.VIEW.equals(mode) ? View.INVISIBLE : View.VISIBLE);
        findViewById(R.id.title).findViewById(R.id.secondary_right_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (save())
                    finish();
            }
        });
        findViewById(R.id.title).findViewById(R.id.secondary_back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewLists = ViewUtil.getLayoutBaseViewLists(mode != null && !TextUtils.isEmpty(mode) ? mode : BaseView.EDIT, this, containerLayout, caseBasicInfo.getJson(), caseId, father, templateId, caseBasicInfo.getId());

        if ((mode != null && mode.equals(BaseView.EDIT)) || addRec) {
            for (BaseView baseView : viewLists) {
                if (baseView instanceof PhotoDialog) {
                    take_ic_photo_layout.setVisibility(View.VISIBLE);
                    PhotoDialog photoDialog = (PhotoDialog) baseView;
                    saveKey = photoDialog.getSaveKey();
                    section = photoDialog.getSection();
                    final String finalSaveKey = saveKey;
                    final String finalSection = section;
                    photoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(LostGoodDetail.this, PhotoDialogActivity.class);
                            intent.putExtra("caseId", caseId);
                            intent.putExtra("father", father);
                            intent.putExtra("name", finalSaveKey);
                            intent.putExtra("section", finalSection);
                            intent.putExtra(BaseView.ADDREC, addRec);
                            v.getContext().startActivity(intent);
                            //  Toast.makeText(v.getContext(), "勘查已经结束", Toast.LENGTH_SHORT).show();
                        }
                    });

                    idView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            readIDCardInfo();
                        }
                    });

                }


            }
        }

//        initIDDeviceConfig();
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (null != mNfcPendingIntent) {
                mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, null,
                        null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mNfcAdapter != null) {
                mNfcAdapter.disableForegroundDispatch(this);
//				mNfcAdapter.disableForegroundNdefPush(this);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        try {
            setIntent(intent);
            resolvIntent(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    synchronized void resolvIntent(Intent intent) {
        try {
            String action = intent.getAction();
            // toast(action);
            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) || NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {

                if (nfcTask == null) {
                    nfcTask = new NFCReadTask(intent, context).executeOnExecutor(Executors
                            .newCachedThreadPool());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public boolean save() {
        String json = ViewUtil.viewSave2Json(this, viewLists, caseBasicInfo.getJson());
        if (json == null)
            return false;
        caseBasicInfo.setJson(json);
        if (getIntent().getStringExtra("id") != null) {
            EvidenceApplication.db.update(caseBasicInfo);
        } else {
            EvidenceApplication.db.save(caseBasicInfo);
        }
        save2Json();
        return true;
    }

    private void save2Json() {
        String caseId = getIntent().getStringExtra("caseId");
        DataTemp dataTemp = SceneInfoFragment.getDataTemp(caseId, caseBasicInfo.getId());
        try {
            JSONObject jsonObject = new JSONObject(caseBasicInfo.getJson());
            jsonObject.put("SCENE_TYPE", caseBasicInfo.getFather());
            dataTemp.setDataType("scene_investigation_data");
            saveRecFile(jsonObject);
            dataTemp.setData(jsonObject.toString());
            EvidenceApplication.db.update(dataTemp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private LostGood getLostGood() {
        String id = getIntent().getStringExtra("id");
        String caseId = getIntent().getStringExtra("caseId");
        String father = getIntent().getStringExtra("father");
        if (id != null)
            return EvidenceApplication.db.findById(id, LostGood.class);
        LostGood lostGood = new LostGood();
        lostGood.setFather(father);
        lostGood.setCaseId(caseId);
        lostGood.setId(ViewUtil.getUUid());
        lostGood.setJson("{}");
        return lostGood;
    }


    private void saveRecFile(JSONObject jsonObject) throws JSONException {
        List<RecordFileInfo> list = getRecFiles();
        if (list == null || list.size() == 0)
            return;
        String refKey = jsonObject.getString("ID");
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
                DataTemp recDataTemp = recordFileInfo.isAddRec() ? SceneInfoFragment.getAddRecDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getId() + "recData") :
                        SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getId() + "recData");
                JSONObject recObject = new JSONObject(JSON.toJSONString(recordFileInfo));
                recObject.put("refKeyId", !TextUtils.isEmpty(refKey) ? refKey : "");
                recObject.put("type", ViewUtil.getType(recordFileInfo));
                recObject.put("sceneType", recordFileInfo.getFather());
                recDataTemp.setDataType("common_attachment");
                recDataTemp.setData(recObject.toString());
                EvidenceApplication.db.update(recDataTemp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private List<RecordFileInfo> getRecFiles() {
        return EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "section = '" + caseBasicInfo.getId() + "'");
    }

//    private void initIDDeviceConfig() {
//        SharedPreferences settings = getSharedPreferences("com.YinanSoft.www",
//                MODE_PRIVATE);
//        String arm = settings.getString("armidse", "-1");
//        if (arm.equals("-1")) {
//            SharedPreferences.Editor editor = settings.edit();
//            editor.putString("armidse", "1");
//            editor.commit();
//            try {
//                InputStream is = this.getResources().openRawResource(
//                        R.raw.armidse);
//                FileUnits unit = new FileUnits();
//                unit.writeToSDfromInput(path, filename, is);
//                is.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        FileUnits units = new FileUnits();
//        //直接读卡文件
//        bt = units.readSDFile(path, filename);
////        Log.i("jiu", bt.length+"");
////        Toast.makeText(LostGoodDetail.this, bt.length+"", Toast.LENGTH_SHORT).show();
//        if (bt == null) {
//            Toast.makeText(LostGoodDetail.this, " 授权文件读取失败，请保证联网更新授权，或者重新安装本应用", Toast.LENGTH_SHORT).show();
//        }
//
//        idReader = new UsbReader(this);
//    }

    private void readIDCardInfo() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // 判断设备是否可用
        if (mNfcAdapter == null) {
            toast("该设备不支持nfc!");
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "请在系统设置中先启用NFC功能！", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            finish();
            return;
        }
//        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
//                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        mNFCReaderHelper = new NFCReaderHelper(this, uiHandler, appKey, appSecret);
    }

//    private void readIDCardInfo() {
//        controller = new UsbController(this,new DeviceStateChangeListener() {
//
//            @Override
//            public void onDeviceOpenSuccess(UsbRes ret) {
////                Toast.makeText(LostGoodDetail.this, " success", Toast.LENGTH_SHORT).show();
//                UsbRes res = controller.openIDCard();
//                if(res.resultCode==UsbRes.RESULT_OK) {
////                    Toast.makeText(LostGoodDetail.this, " RESULT_OK", Toast.LENGTH_SHORT).show();
//                    if (idReader != null && idReader.InitReader(bt)) {
////                        Toast.makeText(LostGoodDetail.this, " idReader", Toast.LENGTH_SHORT).show();
//                        String[] s = new String[1];
//                        IDCardInfo info = idReader.ReadAllCardInfo(s);
//                        if (info != null) {
//                            Intent intent = new Intent();
//                            intent.setAction("id_card_info");
//                            intent.putExtra("name", info.getName());
//                            intent.putExtra("card_num", info.getCardNum());
//                            intent.putExtra("gender", info.getGender());
//                            intent.putExtra("address", info.getAddress());
//                            String birthday = info.getBirthday();
//                            Date date = null;
//                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//                            try {
//                                date = sdf.parse(birthday);
//                                sdf = new SimpleDateFormat("yyyy-MM-dd");
//                                birthday = sdf.format(date);
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                            intent.putExtra("birthday", birthday);
//                            String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
//                            String path = timeStamp+"/"+caseId+"/identityCard";
//                            String strImgPath = AppPathUtil.getDataPath() +"/"+ path+"/";
//                            String name = caseBasicInfo.getId() + ".jpg";
//                            BitmapUtil.saveImgToDisk(LostGoodDetail.this,strImgPath, name, info.getPhoto());
//                            savePhotoData(path, name);
//                            sendBroadcast(intent);
//                        } else {
//                            Toast.makeText(LostGoodDetail.this, "读卡失败，请重新放证", Toast.LENGTH_SHORT).show();
//                        }
//                        idReader.ReleaseReader();
//                    } else {
//                        Toast.makeText(LostGoodDetail.this, "读卡器初始化失败", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(LostGoodDetail.this, "读卡器连接失败", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onDeviceOpenFail(UsbRes ret) {
//                Toast.makeText(LostGoodDetail.this, "读卡器连接失败", Toast.LENGTH_SHORT).show();
//            }
//        });
//        controller.open();
//    }

    public void savePhotoData(String path, String name) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date());
        String uuid = ViewUtil.getUUid();

        RecordFileInfo recordFileInfo = new RecordFileInfo();
        recordFileInfo.setId(uuid);
        recordFileInfo.setCaseId(caseId);
        recordFileInfo.setFilePath(path + "/" + name);
        recordFileInfo.setTwoHundredFilePath(path + "/" + name);
        recordFileInfo.setSection(caseBasicInfo.getId());
        recordFileInfo.setSaveTime(timeStamp);
        recordFileInfo.setChild(saveKey);
        recordFileInfo.setFileType("png");
        recordFileInfo.setAttachmentId(uuid);
        recordFileInfo.setType("0");
        recordFileInfo.setSceneType(father);
        recordFileInfo.setAddRec(getIntent().getBooleanExtra(BaseView.ADDREC, false));
        recordFileInfo.setFather(father);
        recordFileInfo.setInvestigationId("");
        recordFileInfo.setMainId("");
        recordFileInfo.setContent("");
        recordFileInfo.setDeleteFlag("");
        recordFileInfo.setRefKeyId(ViewUtil.getUUid());
        List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" + caseId
                + "' and father = '" + father + "' and child = '" + saveKey + "' and section = '" + caseBasicInfo.getId() + "'");
        if (list.size() > 0) {
            EvidenceApplication.db.update(recordFileInfo, "id='" + list.get(0).getId() + "'");
        } else {
            EvidenceApplication.db.save(recordFileInfo);
        }
    }

    private class NFCReadTask extends AsyncTask<Void, Void, String> {

        private Intent mIntent = null;
        private Context context = null;

        public NFCReadTask(Intent i, Context contextTemp) {
            mIntent = i;
            context = contextTemp;
        }

        @Override
        protected String doInBackground(Void... params) {

            String strCardInfo = mNFCReaderHelper.readCardWithIntent(mIntent);

            return strCardInfo;
        }

        @Override
        protected void onPostExecute(String strCardInfo) {
            super.onPostExecute(strCardInfo);
            nfcTask = null;

            if (null != strCardInfo && strCardInfo.length() > 1600) {
                UserInfo userInfo = mNFCReaderHelper.parsePersonInfo(strCardInfo);
                IC_NO = userInfo.id;
                NAME = userInfo.name;
                SEX = userInfo.sex;
                AGE = userInfo.exper + userInfo.exper2;
                ADDRESS = userInfo.address;
//                tvname.setText(userInfo.name);
//                tvsex.setText(userInfo.sex);
//                tvnation.setText(userInfo.nation);
//                tvbirthday.setText(userInfo.brithday);
//                tvcode.setText(userInfo.id);
//                tvaddress.setText(userInfo.address);
//                tvdate.setText(userInfo.exper + "-" + userInfo.exper2);
//                tvdepar.setText(userInfo.issue);
//
//                Bitmap bm = mNFCReaderHelper.decodeImagexxx(strCardInfo);
//                iv_zhaopian.setImageBitmap(bm);


            }


        }

    }

    private void fillContent() {
        for (BaseView v : viewLists) {
            if("IC_NO".equals(v.getSaveKey())){

            }
        }
    }

    class MyHandler extends Handler {
        private LostGoodDetail activity;

        MyHandler(LostGoodDetail activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1000:
                    String msgTemp = (String) msg.obj;
                    toast(msgTemp);
                    break;
            }
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
