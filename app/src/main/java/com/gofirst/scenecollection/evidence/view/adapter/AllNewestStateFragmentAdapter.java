package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.activity.NewestStateDetail;

import java.util.ArrayList;
import java.util.List;

public class AllNewestStateFragmentAdapter extends BaseAdapter {

	//List<String> list;
	private List<AllNewestStateFragmentData> list = new ArrayList<AllNewestStateFragmentData>();
	private LayoutInflater inflater;
	private LinearLayout itemLinearLayout;
	Context context;
	private String prospectPerson;


	public AllNewestStateFragmentAdapter(String prospectPerson,Context context, List<AllNewestStateFragmentData> list) {
		// TODO Auto-generated constructor stub
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.prospectPerson=prospectPerson;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int arg0, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		//if (view == null) {
			viewHolder = new ViewHolder();

			view = inflater.inflate(R.layout.allstate_fragment_item, null);
			viewHolder.colourLinearLayout= (LinearLayout) view.findViewById(R.id.colour_LinearLayout);
			viewHolder.itemLinearLayout = (LinearLayout) view.findViewById(R.id.item_linearLayout);
			viewHolder.investigationPlace= (TextView)view.findViewById(R.id.investigationPlace);
			viewHolder.exposureProcess= (TextView)view.findViewById(R.id.exposureProcess);
			viewHolder.sceneRegionalism= (TextView)view.findViewById(R.id.sceneRegionalismName);

			viewHolder.crackedDate= (TextView)view.findViewById(R.id.crackedDate);
			viewHolder.status= (TextView)view.findViewById(R.id.caseSolve);


			view.setTag(viewHolder);
			viewHolder.itemLinearLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, NewestStateDetail.class);
					intent.putExtra("investigationPlace", list.get(arg0).getInvestigationPlace());
					intent.putExtra("exposureProcess", list.get(arg0).getExposureProcess());
					intent.putExtra("sceneRegionalism", prospectPerson);
					intent.putExtra("crackedDate", list.get(arg0).getCrackedDate());
					intent.putExtra("status", list.get(arg0).getStatus());
					intent.putExtra("id", list.get(arg0).getCaseId());
					intent.putExtra("reportTime", "");
					intent.putExtra("alarmPeople", "");
					context.startActivity(intent);
				}
			});



		/*}else {

			viewHolder = (ViewHolder) view.getTag();
		}*/

		viewHolder.investigationPlace.setText(list.get(arg0).getInvestigationPlace());
		viewHolder.exposureProcess.setText(list.get(arg0).getExposureProcess());
		viewHolder.sceneRegionalism.setText(prospectPerson);
		viewHolder.crackedDate.setText(list.get(arg0).getCrackedDate());
		if(list.get(arg0).getStatus().equals("0"))
		{
			viewHolder.status.setText("未出警");
			viewHolder.status.setTextColor(Color.parseColor("#FF5555"));
			viewHolder.colourLinearLayout.setBackgroundColor(Color.parseColor("#FF5555"));
		}else if(list.get(arg0).getStatus().equals("1")){
			viewHolder.status.setText("勘查中");
			viewHolder.status.setTextColor(Color.parseColor("#FFC1AA"));
			viewHolder.colourLinearLayout.setBackgroundColor(Color.parseColor("#FFC1AA"));

		}else if(list.get(arg0).getStatus().equals("3")){
			viewHolder.status.setText("完成");
			viewHolder.status.setTextColor(Color.parseColor("#8BABCE"));
			viewHolder.colourLinearLayout.setBackgroundColor(Color.parseColor("#8BABCE"));
		}

		return view;

	}


	private class ViewHolder {

		private LinearLayout itemLinearLayout;
		private TextView investigationPlace;
		private TextView exposureProcess;
		private TextView sceneRegionalism;
		private TextView crackedDate;
		private TextView status;
		private LinearLayout colourLinearLayout;
	}


	public static class AllNewestStateFragmentData {
		private String investigationPlace;
		private String exposureProcess;
		private String sceneRegionalism;
		private String crackedDate;
		private String status;
		private String caseId;

		private String reportTime;//报警时间
		private String alarmPeople;//报警人


		public String getReportTime() {
			return reportTime;
		}

		public void setReportTime(String reportTime) {
			this.reportTime = reportTime;
		}

		public String getAlarmPeople() {
			return alarmPeople;
		}

		public void setAlarmPeople(String alarmPeople) {
			this.alarmPeople = alarmPeople;
		}

		public String getInvestigationPlace() {
			return investigationPlace;
		}
		public void setInvestigationPlace(String investigationPlace) {
			this.investigationPlace = investigationPlace;
		}


		public String getExposureProcess() {
			return exposureProcess;
		}
		public void setExposureProcess(String exposureProcess) {
			this.exposureProcess = exposureProcess;
		}

		public String getSceneRegionalism() {
			return sceneRegionalism;
		}
		public void setSceneRegionalism(String sceneRegionalism) {
			this.sceneRegionalism = sceneRegionalism;
		}

		public String getCrackedDate() {
			return crackedDate;
		}
		public void setCrackedDate(String crackedDate) {
			this.crackedDate = crackedDate;
		}

		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}

		public String getCaseId() {
			return caseId;
		}
		public void setCaseId(String caseId) {
			this.caseId = caseId;
		}




	}
}
