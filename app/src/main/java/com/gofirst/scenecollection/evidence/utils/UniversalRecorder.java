package com.gofirst.scenecollection.evidence.utils;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.model.DataTemp;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;
import com.gofirst.scenecollection.evidence.view.fragment.SceneInfoFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * 不可以同时使用一个引用来录音和视频
 * 直接保存录音索引到数据库
 *
 * @author maxiran 2016-5-24
 */
public class UniversalRecorder {

    private MediaRecorder mMediaRecorder;
    public boolean isRecing;
    private OnRecListener onRecListener;
    private Camera mCamera;
    private String father;
    private RecordFileInfo recordFileInfo;
    private Context context;
    private String caseId;
    //add on
    private boolean isAnchar = false;
    private String section = "";
    //add off

    public UniversalRecorder(String caseId, String father, Context context) {
        this.caseId = caseId;
        this.context = context;
        this.father = father;
    }

    /**
     * 开始录像
     */
    public void startVideoRec(Camera camera, String child) {
        if (prepareVideoRecorder(camera, child)) {
            mMediaRecorder.start();
            isRecing = true;
            if (onRecListener != null) {
                onRecListener.onRecStart();
            }
        } else {
            releaseMediaRecorder();
            isRecing = false;
        }
    }

    /**
     * 开始录音
     */
    public void startAudioRec(String child) {
        if (!isRecing) {
            mMediaRecorder = new MediaRecorder();
            recordFileInfo = new RecordFileInfo();
            String secPath = ViewUtil.getCurrentTime("yyyyMMdd") + "/" + caseId + "/" + father;
            File file = new File(AppPathUtil.getDataPath() + "/" + secPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            String fileName = System.currentTimeMillis() + ".amr";
            String savePath = new File(file,fileName).toString();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setOutputFile(savePath);
            recordFileInfo.setId(ViewUtil.getUUid());
            recordFileInfo.setCaseId(caseId);
            recordFileInfo.setFileDate(new Date());
            recordFileInfo.setFilePath(secPath + "/" + fileName);
            recordFileInfo.setFileType("audio");
            recordFileInfo.setFather(father);
            //add 20160719 start
            recordFileInfo.setPhotoType(isAnchar ? "anchor" : "");
            if (isAnchar) {
                recordFileInfo.setSection(section);
            }
            recordFileInfo.setAttachmentId(UUID.randomUUID().toString().replace("-", ""));
//            recordFileInfo.setId(UUID.randomUUID().toString().replace("-", ""));
            //add 20160719 end
            recordFileInfo.setChild(child);
            try {
                mMediaRecorder.prepare();
                mMediaRecorder.start();
                if (onRecListener != null) {
                    onRecListener.onRecStart();
                }
                isRecing = true;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                isRecing = false;
            } catch (IOException e) {
                isRecing = false;
                e.printStackTrace();

            }

        }
    }

    private void saveDataTemp(RecordFileInfo recordFileInfo) {
        try {
            String id = ViewUtil.getUUid();
            DataTemp recDataTemp = SceneInfoFragment.getDataTemp(recordFileInfo.getCaseId(), recordFileInfo.getFather() + recordFileInfo.getId() + "recData");
            JSONObject recObject = new JSONObject(JSON.toJSONString(recordFileInfo));
            recObject.put("refKeyId", id);
            recObject.put("type", ViewUtil.getType(recordFileInfo));
            recObject.put("SCENE_TYPE", recordFileInfo.getFather());
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

    /**
     * 停止录音
     */
    public void stopAudioRec() {
        if (isRecing) {
            mMediaRecorder.stop();
            if (onRecListener != null) {
                onRecListener.onRecStop();
            }
            EvidenceApplication.db.save(recordFileInfo);
            if (isAnchar) {
                saveDataTemp(recordFileInfo);
            }
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
            context.sendBroadcast(new Intent().setAction("audio"));
            mMediaRecorder.release();
            isRecing = false;
        }
    }

    /**
     * 停止录像
     */
    public void stopVideoRec() {
        if (isRecing) {
            mMediaRecorder.stop();
            mCamera.unlock();
            if (onRecListener != null) {
                onRecListener.onRecStop();
            }
            EvidenceApplication.db.save(recordFileInfo);
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
            context.sendBroadcast(new Intent().setAction("video"));
            releaseMediaRecorder();
            isRecing = false;
        }
    }

    public interface OnRecListener {
        void onRecStart();

        void onRecStop();
    }

    /**
     * 设置录音开始停止监听
     *
     * @param onRecListener 录制监听
     */
    public void setOnRecListener(OnRecListener onRecListener) {
        this.onRecListener = onRecListener;
    }

    private boolean prepareVideoRecorder(Camera camera, String child) {
        mCamera = camera;
        String secPath = ViewUtil.getCurrentTime("yyyyMMdd") + "/" + caseId + "/" + father;
        File file = new File(AppPathUtil.getDataPath() + "/" + secPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        profile.videoFrameWidth = 1280;
        profile.videoFrameHeight = 720;
        mMediaRecorder = new MediaRecorder();
        recordFileInfo = new RecordFileInfo();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(profile);
        String fileName = System.currentTimeMillis() + ".mp4";
        mMediaRecorder.setOutputFile(new File(file,
                fileName).toString());
        recordFileInfo.setId(ViewUtil.getUUid());
        recordFileInfo.setCaseId(caseId);
        recordFileInfo.setPhotoType("");
        recordFileInfo.setFileDate(new Date());
        recordFileInfo.setFilePath(secPath + fileName);
        recordFileInfo.setFileType("video");
        recordFileInfo.setFather(father);
        recordFileInfo.setChild(child);
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }


    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }

    public String getCurrentPath() {
        String s = recordFileInfo.getFilePath();
        if (s == null || s.isEmpty()) {
            s = "";
        }
        return s;
    }

    public String getAttachmentId() {
        String s = recordFileInfo.getAttachmentId();
        if (s == null || s.isEmpty()) {
            s = "";
        }
        return s;
    }

    public RecordFileInfo getRecordFileInfo() {
        if (recordFileInfo != null) {
            return recordFileInfo;
        }
        return null;
    }

    public void setIsAchar(boolean is) {
        isAnchar = is;
    }

    public void setSection(String s) {
        this.section = s;
    }
}
