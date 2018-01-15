package com.gofirst.scenecollection.evidence.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.view.adapter.VideoGridAdapter;
import com.gofirst.scenecollection.evidence.view.customview.ViewUtil;

import java.util.List;

/**
 * Created by maxiran on 2016/4/26.
 */
public class ProspectVideoFragment extends Fragment {

    private BroadcastReceiver receiver;
    private String caseId;
    private String father;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prospect_video_layout, container, false);
        final GridView videoGrid = (GridView) view.findViewById(R.id.video_grid);
        caseId = getArguments().getString("caseId");
        father = getArguments().getString("father");
        VideoGridAdapter videoGridAdapter;
        videoGrid.setAdapter(videoGridAdapter = new VideoGridAdapter(getVideoFiles(), caseId, father));
        String mode = getArguments().getString("mode");
        videoGridAdapter.setMode(mode);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getVideoFiles();
                videoGrid.setAdapter(new VideoGridAdapter(getVideoFiles(), caseId, father));
            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter("video"));
        return view;
    }

    private List<RecordFileInfo> getVideoFiles() {
        return EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" + caseId + "' and father = '" + father + "'", "fileDate desc");
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        save();
        super.onDestroy();
    }

    private void save() {
        for (RecordFileInfo recordFileInfo : getVideoFiles())
            try {
                ViewUtil.saveAttchment(recordFileInfo, ViewUtil.getUUid());
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
    }
}
