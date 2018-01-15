package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechUnderstander;
import com.unisound.client.SpeechUnderstanderListener;

/**
 * Created by Administrator on 2017/6/7.
 */
public class AudioTest extends Activity  {

    private TextView start;
    private EditText editText;
    private String appKey="ymm5apwi5a2nl5qnw3l5huhtnlu3ogacir7255it";
    private String secret="0a240d1769eafd3d82d9008677ab5f72";
    private String law="law.hivoice.cn:2005";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_test);
        start = (TextView)findViewById(R.id.start);
        editText = (EditText)findViewById(R.id.editText);

        Init();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private void Init(){
        //1. 创建语音识别对象，appKey和secret通过 http://dev.hivoice.cn/ 网站申请
        SpeechUnderstander mUnderstander = new SpeechUnderstander(this, appKey, secret);
        //2. 设置参数，设置识别模式为在线识别。更多识别参数的设置可以参考《云知声USC API手册(Android)》
        mUnderstander.setOption(SpeechConstants.ASR_DOMAIN,law);
        //3. 语音识别对象回调监听
        mUnderstander.setListener(new SpeechUnderstanderListener() {
            //结果回调
            public void onResult(int type, String jsonResult) {
                switch (type) {
                    case SpeechConstants.ASR_RESULT_NET:
                    // 在线识别结果，通常onResult接口多次返回结果，识别结果以json格式返回，更多json返回信息参考附录3
                        break;
                }
            }
            //语音识别事件回调，支持的回调类型见4.2.3 事件回调
            public void onEvent(int type, int timeMs) {}
            //发生错误时回调，详见附录2错误列表说明
            public void onError(int type, String errorMSG) {}
        });
        //4. 识别引擎初始化
        mUnderstander.init(null);
        //5. 开始语音识别
        mUnderstander.start();
    }

}

