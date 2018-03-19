package com.gofirst.scenecollection.evidence.view.customview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.utils.DateTimeUtil;
import com.gofirst.scenecollection.evidence.view.speechRecognition.Config;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizer;
import com.unisound.client.SpeechSynthesizerListener;
import com.unisound.client.SpeechUnderstander;
import com.unisound.client.SpeechUnderstanderListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author maxiran.
 */
public class Text extends LinearLayout implements BaseView {

    /**
     * 当前识别状态
     */
    enum AsrStatus {
        idle, recording, recognizing
    }

    private TextView click2Input;
    private String name;
    private String saveKey;
    private boolean isOrg = false;
    private String reg;
    private boolean isNum;
    private View view;
    private String isRequiredField;
    private boolean viewWithoutToast;

    private EditText input;
    //云知声
    private SpeechUnderstander mUnderstander;
    private SpeechSynthesizer mTTSPlayer;
    private AsrStatus statue = AsrStatus.idle;
    private static String arraySampleStr[] = new String[] { "RATE_AUTO  ", "RATE_16K  ", "RATE_8K  " };
    private static int arraySample[] = new int[] { SpeechConstants.ASR_SAMPLING_RATE_BANDWIDTH_AUTO,
            SpeechConstants.ASR_SAMPLING_RATE_16K, SpeechConstants.ASR_SAMPLING_RATE_8K };
    private static int currentSample = 0;
    private StringBuffer mAsrResultBuffer;
    //private ProgressBar mVolume;
    private int num =0;
    private BroadcastReceiver receiver;

    public void setReg(String reg) {
        this.reg = reg;
    }

    public Text(Context context) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.normal_edit_text_layout, this, true);
        click2Input = (TextView) view.findViewById(R.id.click_to_input);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("id_card_info".equals(action)) {
                    if ("身份证号码".equals(name)) {
                        if (!TextUtils.isEmpty(intent.getStringExtra("card_num"))) {
                            click2Input.setText(intent.getStringExtra("card_num"));
                        }
                    } else if ("地址".equals(name) || "户籍地".equals(name)) {
                        if (!TextUtils.isEmpty(intent.getStringExtra("address"))) {
                            click2Input.setText(intent.getStringExtra("address"));
                        }
                    } else if ("姓名".equals(name)) {
                        if (!TextUtils.isEmpty(intent.getStringExtra("name"))) {
                            click2Input.setText(intent.getStringExtra("name"));
                        }
                    } else if ("年龄".equals(name)) {
                        String birthday = intent.getStringExtra("birthday");
//                        if (!TextUtils.isEmpty(birthday) && birthday.length() >= 4) {
//                            String year = birthday.substring(0, 4);
//                            long time = System.currentTimeMillis();
//                            Calendar cal = Calendar.getInstance();
//                            cal.setTimeInMillis(time);
//                            click2Input.setText(String.valueOf(cal.get(Calendar.YEAR) - Integer.parseInt(year)));
//                        }
                        click2Input.setText(birthday);
                    }
                } else if ("ic".equals(action)) {
                    if ("身份证号码".equals(name)) {
                        click2Input.setText("52020319820709201X");
                    } else if ("地址".equals(name) || "户籍地".equals(name)) {
                        click2Input.setText("江苏省苏州市相城区御苑家园69幢202室");
                    } else if ("姓名".equals(name)) {
                        click2Input.setText("王德刚");
                    } else if ("年龄".equals(name)) {
                        click2Input.setText("36");
                    }
                } else if ("id_no".equals(action)) {
                    if ("年龄".equals(name)) {
                        if (!TextUtils.isEmpty(intent.getStringExtra("age"))) {
                            click2Input.setText(intent.getStringExtra("age"));
                        }
                    }
                }
            }
        };
        context.registerReceiver(receiver, new IntentFilter("id_card_info"));
        context.registerReceiver(receiver, new IntentFilter("id_no"));
//        context.registerReceiver(receiver, new IntentFilter("ic"));

    }

    public Text(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Text(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public String getText() {
        return isOrg ? (String) click2Input.getTag() : click2Input.getText().toString();
    }

    @Override
    public String getViewName() {
        return name;
    }

    @Override
    public void initView(final String mode, final String name, final String text, String saveKey, String textColor, String dataType,String isRequiredField) {
        TextView viewName = (TextView) view.findViewById(R.id.name);
        click2Input.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals(BaseView.EDIT)) {
                    if (isOrg) {
                        new OrgDialog(v.getContext(), click2Input, name);
                    } else {
                        click2Pop(click2Input.getText().toString());
                    }
                } else if (!viewWithoutToast){
                    Toast.makeText(v.getContext(), "已经勘查结束", Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.name = name;
        this.saveKey = saveKey;
        this.isRequiredField = isRequiredField;
        viewName.setText(name);
        click2Input.setText(!TextUtils.isEmpty(text) ? text : mode.equals(BaseView.EDIT) ? "点击输入" : "无");
        isNum = ViewUtil.setTextColorAndInputManger(textColor, dataType, click2Input);
    }


    @Override
    public boolean validate() {
        return isOrg ? !TextUtils.isEmpty((String) click2Input.getTag()) :
                (!"点击输入".equals(click2Input.getText().toString()) && !"无".equals(click2Input.getText().toString()));
    }


    @Override
    public String getIsRequireField() {
        return isRequiredField;
    }

    @Override
    public String getSaveKey() {
        return saveKey;
    }

    @Override
    public void saveName(JSONObject jsonObject) throws JSONException {
        String text = click2Input.getText().toString();
        if (isOrg && !TextUtils.isEmpty(text) && !"点击输入".equals(text))
            jsonObject.put(saveKey + "_NAME", text);
    }

    @Override
    public boolean isID() {
        return isOrg;
    }

    @Override
    public void setID(String id) {
        if (isOrg)
            click2Input.setTag(id);
    }

    @Override
    public void setViewWithoutToast(boolean viewWithoutToast) {
        this.viewWithoutToast = viewWithoutToast;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (receiver != null) {
            getContext().unregisterReceiver(receiver);
            receiver = null;
        }
    }

    public void click2Pop(String text) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.text_pop, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
              final TextView popName = (TextView) view.findViewById(R.id.name);
        popName.setText(name);
         input = (EditText) view.findViewById(R.id.text_input);
        final ImageView start_mic = (ImageView) view.findViewById(R.id.satrt_mic);
        final ImageView show_mac = (ImageView) view.findViewById(R.id.show_mac);
        final LinearLayout bottm_linearLayout = (LinearLayout)view.findViewById(R.id.bottm_linearLayout);
        final ProgressBar mVolume = (ProgressBar) findViewById(R.id.volume_progressbar);
        if (isNum)
            input.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        input.setText(!text.equals("点击输入") ? text : "");
        input.setSelection(input.getText().toString().length());
        view.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing())
                    if(mUnderstander!=null) {
                        mUnderstander.cancel();
                    }
                //stopRecord();
                popupWindow.dismiss();
            }
        });

        mAsrResultBuffer = new StringBuffer();


        show_mac.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (num%2==0 ) {
                    show_mac.setBackgroundDrawable(getResources().getDrawable(R.drawable.text));
                    bottm_linearLayout.setVisibility(View.VISIBLE);
                    statue = AsrStatus.idle;
                    initData(start_mic);
                    initRecognizer(mVolume);
                } else {
                    show_mac.setBackgroundDrawable(getResources().getDrawable(R.drawable.mic));
                    bottm_linearLayout.setVisibility(View.GONE);
                    statue = AsrStatus.recognizing;
                    mUnderstander.cancel();
                    stopRecord();
                    start_mic.setBackgroundDrawable(getResources().getDrawable(R.drawable.record1));
                }
                num++;
            }

        });

        view.findViewById(R.id.finish).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    String text = input.getText().toString();
                    if (!TextUtils.isEmpty(text)){
                        if (TextUtils.isEmpty(reg)) {
                            click2Input.setText(text);
                            analysisID();
//                            mUnderstander.cancel();
                            stopRecord();
                            popupWindow.dismiss();
                        } else {
                            if (text.matches(reg)) {
                                click2Input.setText(text);
//                                mUnderstander.cancel();
                                stopRecord();
                                popupWindow.dismiss();

                            } else {
                                Toast.makeText(v.getContext(), "输入不合法", Toast.LENGTH_SHORT).show();
                                click2Input.setText("点击输入");
                            }
                        }
                    }else {
                        click2Input.setText("点击输入");
//                        mUnderstander.cancel();
                        stopRecord();
                        popupWindow.dismiss();

                    }
                }
            }
        });
        popupWindow.setAnimationStyle(R.style.tabpopstyle);
        popupWindow.setFocusable(true);
        if (!popupWindow.isShowing()) {
            popupWindow.showAtLocation(this, Gravity.BOTTOM, 0, 0);
            input.post(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager inputManager =
                            (InputMethodManager) input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            });
        }
    }

    public void clickPop(Context context, final TextView inputDate, String name) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.text_pop, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final TextView popName = (TextView) view.findViewById(R.id.name);
        popName.setText(name);
        input = (EditText) view.findViewById(R.id.text_input);
        final ImageView start_mic = (ImageView) view.findViewById(R.id.satrt_mic);
        String text = inputDate.getText().toString();
        input.setText(!text.equals("点击输入") ? text : "");

        input.setSelection(input.getText().toString().length());
        view.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
            }
        });
        view.findViewById(R.id.finish).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    String text = input.getText().toString();
                    inputDate.setText(TextUtils.isEmpty(text) ? "点击输入" : text);
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.setAnimationStyle(R.style.tabpopstyle);
        popupWindow.setFocusable(true);
        if (!popupWindow.isShowing())
            popupWindow.showAtLocation(this, Gravity.BOTTOM, 0, 0);
        input.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
    }

    private void analysisID() {
        if ("身份证号码".equals(name)) {
            String idNo = getText();
            if (!TextUtils.isEmpty(idNo) && idNo.length() == 18) {
                String year = idNo.substring(6, 10);
                String month = idNo.substring(10, 12);
                String day = idNo.substring(12, 14);
                String birthday = year + "-" + month + "-" + day;
                int gender =  idNo.charAt(16) - '0';
                int age = DateTimeUtil.getYear() - Integer.valueOf(year);
                Intent intent = new Intent();
                intent.setAction("id_no");
                intent.putExtra("birthday", birthday);
                intent.putExtra("age", String.valueOf(age));
                intent.putExtra("gender", gender % 2 == 1 ? "男" : "女");
                getContext().sendBroadcast(intent);
            }
        }
    }

    public void setIsOrg() {
        isOrg = true;
    }



    /**
     * 初始化按钮
     */
    private void initData(final ImageView imageView) {

        // 采样率
       /* mSampleDialog = new Dialog(this, R.style.dialog);
        mSampleDialog.setContentView(R.layout.sample_list_item);
        mSampleDialog.findViewById(R.id.rate_16k_text).setOnClickListener(this);
        mSampleDialog.findViewById(R.id.rate_8k_text).setOnClickListener(this);
        mSampleDialog.findViewById(R.id.rate_auto_text).setOnClickListener(this);*/

        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (statue == AsrStatus.idle) {
                    Log.d("TF", "AsrStatus.idle");
                    /*mRecognizerButton.setEnabled(false);
                    mAsrResultBuffer.delete(0, mAsrResultBuffer.length());
                    mRecognizerResultText.setText("");
                    mStatusView.setVisibility(View.VISIBLE);
                    mStatusLayout.setVisibility(View.VISIBLE);
                    mLogoImageView.setVisibility(View.GONE);
                    // 在收到 onRecognizerStart 回调前，录音设备没有打开，请添加界面等待提示，
                    // 录音设备打开前用户说的话不能被识别到，影响识别效果。
                    mStatusTextView.setText(R.string.opening_recode_devices);*/

                    mAsrResultBuffer.delete(0, mAsrResultBuffer.length());
                    mUnderstander.setOption(SpeechConstants.ASR_OPT_PRINT_LOG, true);
                    mUnderstander.setOption(SpeechConstants.ASR_OPT_CONTINUE_RECOGNIZE, true);

                    // 修改录音采样率
                    mUnderstander.setOption(SpeechConstants.ASR_SAMPLING_RATE, arraySample[currentSample]);


                    // 修改识别语音服务器地址
                    mUnderstander.setOption(SpeechConstants.ASR_SERVER_ADDR, "law.hivoice.cn:2005");
//					mUnderstander.setOption(SpeechConstants.ASR_SERVER_ADDR, "120.132.51.103:8080");

                    // 设置ASR结果按原始结果方式返回
                    mUnderstander.setOption(SpeechConstants.ASR_OPT_RETURN_ORIGIN_FORMAT, true);

                    mUnderstander.setOption(SpeechConstants.NLU_ENABLE, false);
                    // 修改识别领域
                    mUnderstander.setOption(SpeechConstants.ASR_DOMAIN, "law");
                    mUnderstander.setOption(SpeechConstants.ASR_OPT_SET_POST_PROCESS_PARAMS, "additionalService=app_dinobot_service;returnType=json;version=v2;sessionId=0;userId=test0000;context=0");
                    mUnderstander.start();
                    imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.record_on));
                } else if (statue == AsrStatus.recording) {
                    stopRecord();
                    Log.d("TF", "AsrStatus.recording");
                    imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.record1));
                } else if (statue == AsrStatus.recognizing) {
                    // 取消识别
                    mUnderstander.cancel();
                    Log.d("TF", "AsrStatus.recognizing");
                    // mRecognizerButton.setText(R.string.click_say);点击说话
                    statue = AsrStatus.idle;
                    imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.record1));
                }
            }
        });



    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        //mStatusTextView.setText(R.string.just_recognizer);//正在识别
        if (mUnderstander != null) {
            mUnderstander.stop();
        }
        // 关闭语音合成引擎
        if (mTTSPlayer != null) {
            mTTSPlayer.stop();
        }
    }


    /**
     * 打印日志信息
     *
     * @param msg
     */
    private void log_v(String msg) {
        Log.v("demo", msg);
    }

    @SuppressWarnings("unused")
    private void log_e(String msg) {
        Log.e("demo", msg);
    }

    private void hitErrorMsg(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void asrResultOperate (String jsonResult) {
        JSONObject asrJson;
        try {
            asrJson = new JSONObject(jsonResult);
            JSONArray asrJsonArray = asrJson.getJSONArray("net_asr");
            JSONObject asrJsonObject = asrJsonArray.getJSONObject(0);
            String asrJsonStatus = asrJsonObject.getString("result_type");
            log_v("asrJsonStatus="+asrJsonStatus);
            if (jsonResult.contains("recognition_result")) {
                String recognition_result = asrJsonObject.getString("recognition_result");
                if (recognition_result.contains("processResult")) {
                    JSONObject jsonObj = new JSONObject(recognition_result);
                    String processResult = jsonObj.getString("processResult");
                    if (!TextUtils.isEmpty(processResult)) {
                        mAsrResultBuffer.append(processResult);
                       // mRecognizerResultText.setText(mAsrResultBuffer.toString());
                        int index = input.getSelectionStart();
                        Editable editable = input.getText();
                        editable.insert(index, mAsrResultBuffer.toString());
                        //input.setText(mAsrResultBuffer.toString());

                        mAsrResultBuffer.delete(0, mAsrResultBuffer.length());

                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    /**
     * 初始化
     */
    private void initRecognizer(final ProgressBar mVolume) {

        // 创建语音理解对象，appKey和 secret通过 http://dev.hivoice.cn/ 网站申请
        mUnderstander = new SpeechUnderstander(getContext(), Config.appKey, Config.secret);

        // 创建语音合成对象
        mTTSPlayer = new SpeechSynthesizer(getContext(), Config.appKey, Config.secret);
        mTTSPlayer.setOption(SpeechConstants.TTS_SERVICE_MODE, SpeechConstants.TTS_SERVICE_MODE_NET);
        // 设置语音合成回调监听
        mTTSPlayer.setTTSListener(new SpeechSynthesizerListener() {

            @Override
            public void onEvent(int type) {
                switch (type) {
                    case SpeechConstants.TTS_EVENT_INIT:
                        // 初始化成功回调
                        break;
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_START:
                        // 开始合成回调
                        break;
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_END:
                        // 合成结束回调
                        break;
                    case SpeechConstants.TTS_EVENT_BUFFER_BEGIN:
                        // 开始缓存回调
                        break;
                    case SpeechConstants.TTS_EVENT_BUFFER_READY:
                        // 缓存完毕回调
                        break;
                    case SpeechConstants.TTS_EVENT_PLAYING_START:
                        // 开始播放回调
                        break;
                    case SpeechConstants.TTS_EVENT_PLAYING_END:
                        // 播放完成回调
                        break;
                    case SpeechConstants.TTS_EVENT_PAUSE:
                        // 暂停回调
                        break;
                    case SpeechConstants.TTS_EVENT_RESUME:
                        // 恢复回调
                        break;
                    case SpeechConstants.TTS_EVENT_STOP:
                        // 停止回调
                        break;
                    case SpeechConstants.TTS_EVENT_RELEASE:
                        // 释放资源回调
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onError(int type, String errorMSG) {
                // 语音合成错误回调
                hitErrorMsg(errorMSG);

            }
        });
        mTTSPlayer.init("");

        // 保存录音数据
        // recognizer.setRecordingDataEnable(true);
        mUnderstander.setListener(new SpeechUnderstanderListener() {
            @Override
            public void onResult(int type, String jsonResult) {
                switch (type) {
                    case SpeechConstants.ASR_RESULT_NET:
                        // 在线识别结果，通常onResult接口多次返回结果，保留识别结果组成完整的识别内容。
                        log_v("onRecognizerResult = >"+jsonResult);
                        if (! jsonResult.contains("net_nlu")) {
                            //取出语音识别结果
                            asrResultOperate(jsonResult);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onEvent(int type, int timeMs) {
                switch (type) {
                    case SpeechConstants.ASR_EVENT_NET_END:
                        log_v("onEnd");

                       // mRecognizerButton.setEnabled(true);
                        statue = AsrStatus.idle;
                       // mRecognizerButton.setText(R.string.click_say);
                       // mStatusLayout.setVisibility(View.GONE);
                       // mRecognizerResultText.requestFocus();
                       // mRecognizerResultText.setSelection(0);
                        break;
                    case SpeechConstants.ASR_EVENT_VOLUMECHANGE:
                        // 说话音量实时返回
                        int volume = 10;
                        volume=(Integer)mUnderstander.getOption(SpeechConstants.GENERAL_UPDATE_VOLUME);
                        Log.d("volume",""+volume);

                       // mVolume.setProgress(volume);

                        break;
                    case SpeechConstants.ASR_EVENT_VAD_TIMEOUT:
                        // 说话音量实时返回
                        log_v("onVADTimeout");
                        // 收到用户停止说话事件，停止录音
//					stopRecord();
                        break;
                    case SpeechConstants.ASR_EVENT_RECORDING_STOP:
                        // 停止录音，请等待识别结果回调
                        log_v("onRecordingStop");
                        statue = AsrStatus.recognizing;
                        //mRecognizerButton.setText(R.string.give_up);
                        //mStatusTextView.setText(R.string.just_recognizer);
                        break;
                    case SpeechConstants.ASR_EVENT_SPEECH_DETECTED:
                        //用户开始说话
                        log_v("onSpeakStart");
                        //mStatusTextView.setText(R.string.speaking);
                        break;
                    case SpeechConstants.ASR_EVENT_RECORDING_START:
                        //录音设备打开，开始识别，用户可以开始说话
                       // mStatusTextView.setText(R.string.please_speak);
                        //mRecognizerButton.setEnabled(true);
                        statue = AsrStatus.recording;
                       // mRecognizerButton.setText(R.string.say_over);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onError(int type, String errorMSG) {
                if (errorMSG != null) {
                    // 显示错误信息
                    hitErrorMsg(errorMSG);
                } else {
                   // if ("".equals(mRecognizerResultText.getText().toString())) {
                   //     mRecognizerResultText.setText(R.string.no_hear_sound);
                   // }
                }
            }
        });
        mUnderstander.init("");
    }










}
