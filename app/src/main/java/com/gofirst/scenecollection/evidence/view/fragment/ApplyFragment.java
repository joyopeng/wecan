package com.gofirst.scenecollection.evidence.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.activity.AddNewCase;
import com.gofirst.scenecollection.evidence.view.activity.DailyScheduleActivity;
import com.gofirst.scenecollection.evidence.view.activity.NvestigatQuery;
import com.gofirst.scenecollection.evidence.view.activity.SceneProspectList;

public class ApplyFragment extends Fragment implements View.OnClickListener{

	View view;
	LinearLayout newestStateLayout,sceneInvestigationLayout,
	nvestigationQueryLayout,everydayDutyLayout;
	private SharePre sharePre;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
//		System.out.println("OneFragment  onCreate");
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		if (view==null){
			view=inflater.inflate(R.layout.applyfragment, null);
			newestStateLayout=(LinearLayout)view.findViewById(R.id.newest_state_layout);
			sceneInvestigationLayout=(LinearLayout)view.findViewById(R.id.scene_investigation_layout);
			nvestigationQueryLayout=(LinearLayout)view.findViewById(R.id.nvestigation_query_layout);
			everydayDutyLayout=(LinearLayout)view.findViewById(R.id.everyday_duty_layout);
			newestStateLayout.setOnClickListener(this);
			sceneInvestigationLayout.setOnClickListener(this);
			nvestigationQueryLayout.setOnClickListener(this);
			everydayDutyLayout.setOnClickListener(this);
			sharePre = new SharePre(getActivity(), "user_info", Context.MODE_PRIVATE);

		}

		return view;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.newest_state_layout:
				Intent intent = new Intent(getContext(),AddNewCase.class);
				startActivity(intent);
				break;

			case R.id.scene_investigation_layout:
				Intent intent1 = new Intent(getContext(),SceneProspectList.class);
				startActivity(intent1);

				break;

			case R.id.nvestigation_query_layout:
//				Intent intent2 = new Intent(getContext(),NvestigationQuery.class);
				Intent intent2 = new Intent(getContext(),NvestigatQuery.class);
				startActivity(intent2);

				break;

			case R.id.everyday_duty_layout:
				Intent intent3 = new Intent(getContext(),DailyScheduleActivity.class);
				startActivity(intent3);
				/*FinalHttp fh = new FinalHttp();
				fh.configRequestExecutionRetryCount(3);// 请求错误重试次数
				fh.configTimeout(30000);// 超时时间
				fh.download(PublicMsg.BASEURL + "/app/firstbasedata?token=" + sharePre.getString("token", "")
						, "/mnt/sdcard/testapk.txt", new AjaxCallBack() {
					@Override
					public void onLoading(long count, long current) {
						Log.d("下载进度", "" + count);
						//textView.setText("下载进度：" + current + "/" + count);
					}

					// @Override
					public void onSuccess(File t) {
						Log.d("下载进度", "" + t == null ? "null" : t.getAbsoluteFile().toString());
						//textView.setText(t == null ? "null" : t.getAbsoluteFile().toString());
					}

				});*/

				break;

		}
	}



}
