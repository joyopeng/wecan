package com.gofirst.scenecollection.evidence.view.speechRecognition;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.R;
import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizer;
import com.unisound.client.SpeechSynthesizerListener;
import com.unisound.client.SpeechUnderstander;
import com.unisound.client.SpeechUnderstanderListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 云知声识别实例程序
 * 
 * @author
 * 
 */

public class ASRMedicalOnlineActivity extends Activity implements OnClickListener {

	/**
	 * 当前识别状态
	 */
	enum AsrStatus {
		idle, recording, recognizing
	}

	private ProgressBar mVolume;
	private EditText mRecognizerResultText;
	private Button mRecognizerButton;
	private View mStatusView;
	private View mStatusLayout;
	private ImageView mLogoImageView;
	private TextView mStatusTextView;
	private Button mSampleButton;
	private Dialog mSampleDialog;
	private static String arraySampleStr[] = new String[] { "RATE_AUTO  ", "RATE_16K  ", "RATE_8K  " };
	private static int arraySample[] = new int[] { SpeechConstants.ASR_SAMPLING_RATE_BANDWIDTH_AUTO,
		SpeechConstants.ASR_SAMPLING_RATE_16K, SpeechConstants.ASR_SAMPLING_RATE_8K };
	private static int currentSample = 0;

	private AsrStatus statue = AsrStatus.idle;
	private SpeechUnderstander mUnderstander;
	@SuppressWarnings("unused")
	private String mRecognizerText = "";
	private SpeechSynthesizer mTTSPlayer;
	private StringBuffer mAsrResultBuffer;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(1);
		setContentView(R.layout.activity_medical_online_asr);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.status_bar_main);

		mVolume = (ProgressBar) findViewById(R.id.volume_progressbar);
		mRecognizerResultText = (EditText) findViewById(R.id.recognizer_result_et);
		mSampleButton = (Button) findViewById(R.id.sample_button);
		mStatusView = findViewById(R.id.status_panel);
		mStatusTextView = (TextView) findViewById(R.id.status_show_textview);
		mStatusLayout = findViewById(R.id.offline_asr_status_layout);
		mLogoImageView = (ImageView) findViewById(R.id.logo_imageview);
		mSampleButton.setOnClickListener(this);
		mSampleButton.setText(arraySampleStr[0]);

		mRecognizerButton = (Button) findViewById(R.id.recognizer_btn);
		mAsrResultBuffer = new StringBuffer();

		initData();

		// 初始化对象
		initRecognizer();
	}

	/**
	 * 初始化
	 */
	private void initRecognizer() {

		// 创建语音理解对象，appKey和 secret通过 http://dev.hivoice.cn/ 网站申请
		mUnderstander = new SpeechUnderstander(this, Config.appKey, Config.secret);

		// 创建语音合成对象
		mTTSPlayer = new SpeechSynthesizer(this, Config.appKey, Config.secret);
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

					mRecognizerButton.setEnabled(true);
					statue = AsrStatus.idle;
					mRecognizerButton.setText(R.string.click_say);
					mStatusLayout.setVisibility(View.GONE);
					mRecognizerResultText.requestFocus();
					mRecognizerResultText.setSelection(0);
					break;
				case SpeechConstants.ASR_EVENT_VOLUMECHANGE:
					// 说话音量实时返回
					int volume = (Integer)mUnderstander.getOption(SpeechConstants.GENERAL_UPDATE_VOLUME);
					mVolume.setProgress(volume);
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
					mRecognizerButton.setText(R.string.give_up);
					mStatusTextView.setText(R.string.just_recognizer);
					break;
				case SpeechConstants.ASR_EVENT_SPEECH_DETECTED:
					//用户开始说话
					log_v("onSpeakStart");
					mStatusTextView.setText(R.string.speaking);
					break;
				case SpeechConstants.ASR_EVENT_RECORDING_START:
					//录音设备打开，开始识别，用户可以开始说话
					mStatusTextView.setText(R.string.please_speak);
					mRecognizerButton.setEnabled(true);
					statue = AsrStatus.recording;
					mRecognizerButton.setText(R.string.say_over);
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
					if ("".equals(mRecognizerResultText.getText().toString())) {
						mRecognizerResultText.setText(R.string.no_hear_sound);
					}
				}
			}
		});
		mUnderstander.init("");
	}

	/**
	 * 初始化按钮
	 */
	private void initData() {

		// 采样率
		mSampleDialog = new Dialog(this, R.style.dialog);
		mSampleDialog.setContentView(R.layout.sample_list_item);
		mSampleDialog.findViewById(R.id.rate_16k_text).setOnClickListener(this);
		mSampleDialog.findViewById(R.id.rate_8k_text).setOnClickListener(this);
		mSampleDialog.findViewById(R.id.rate_auto_text).setOnClickListener(this);

		mRecognizerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (statue == AsrStatus.idle) {

					mRecognizerButton.setEnabled(false);
					mAsrResultBuffer.delete(0, mAsrResultBuffer.length());
					mRecognizerResultText.setText("");
					mStatusView.setVisibility(View.VISIBLE);
					mStatusLayout.setVisibility(View.VISIBLE);
					mLogoImageView.setVisibility(View.GONE);
					// 在收到 onRecognizerStart 回调前，录音设备没有打开，请添加界面等待提示，
					// 录音设备打开前用户说的话不能被识别到，影响识别效果。
					mStatusTextView.setText(R.string.opening_recode_devices);
					
					
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
				} else if (statue == AsrStatus.recording) {
					stopRecord();
				} else if (statue == AsrStatus.recognizing) {
					// 取消识别
					mUnderstander.cancel();

					mRecognizerButton.setText(R.string.click_say);
					statue = AsrStatus.idle;
				}
			}
		});
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
		Toast.makeText(ASRMedicalOnlineActivity.this, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 停止录音
	 */
	public void stopRecord() {
		mStatusTextView.setText(R.string.just_recognizer);
		mUnderstander.stop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.sample_button:
				mSampleDialog.show();
				break;

			case R.id.rate_auto_text:
				currentSample = 0;
				setSample(currentSample);
				break;

			case R.id.rate_16k_text:
				currentSample = 1;
				setSample(currentSample);
				break;

			case R.id.rate_8k_text:
				currentSample = 2;
				setSample(currentSample);
				break;

			default:
				break;
		}

	}

	private void setSample(int index) {
		mSampleButton.setText(arraySampleStr[index]);
		mSampleDialog.dismiss();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mUnderstander != null) {
			mUnderstander.stop();
		}
		// 关闭语音合成引擎
		if (mTTSPlayer != null) {
			mTTSPlayer.stop();
		}
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
						mRecognizerResultText.setText(mAsrResultBuffer.toString());
					}
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
