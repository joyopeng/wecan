package com.gofirst.scenecollection.evidence.view.logic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsSceneCases;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ImgFileListActivity extends Activity implements OnItemClickListener{

	ListView listView;
	Util util;
	ImgFileListAdapter listAdapter;
	List<FileTraversal> locallist;

	private ImageView secondary_back_img;
	private TextView secondary_title_tv;
	private TextView secondary_right_tv;
	private String caseId,father;
	private long alarmTime;
	private boolean addRec;

	public static final int REQUSET = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(1);
		setContentView(R.layout.imgfilelist);
		InitData();
		listView=(ListView) findViewById(R.id.listView1);
		util=new Util(this);
		locallist=util.LocalImgFileList(alarmTime);
		List<HashMap<String, String>> listdata=new ArrayList<HashMap<String,String>>();
		Bitmap bitmap[] = null;
		if (locallist!=null) {
			bitmap=new Bitmap[locallist.size()];
			for (int i = 0; i < locallist.size(); i++) {
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("filecount", locallist.get(i).filecontent.size()+"张");
				map.put("imgpath", locallist.get(i).filecontent.get(0)==null?null:(locallist.get(i).filecontent.get(0)));
				map.put("filename", locallist.get(i).filename);
				listdata.add(map);
			}
		}
		listAdapter=new ImgFileListAdapter(this, listdata);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);
		
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent=new Intent(this,ImgsActivity.class);
		Bundle bundle=new Bundle();
		bundle.putParcelable("data", locallist.get(arg2));
		intent.putExtras(bundle);
		intent.putExtra("caseId", caseId);
		intent.putExtra("father", father);
		intent.putExtra(BaseView.ADDREC, addRec);
		//startActivity(intent);
		startActivityForResult(intent, REQUSET);
	}

	private void InitData(){
		secondary_back_img=(ImageView)findViewById(R.id.search_title_layout)
				.findViewById(R.id.secondary_back_img);
		secondary_title_tv=(TextView)findViewById(R.id.search_title_layout)
				.findViewById(R.id.secondary_title_tv);
		secondary_right_tv=(TextView)findViewById(R.id.search_title_layout)
				.findViewById(R.id.secondary_right_tv);
		secondary_back_img.setVisibility(View.GONE);
		secondary_title_tv.setText("选择相册");
		secondary_right_tv.setVisibility(View.GONE);
		caseId = getIntent().getStringExtra("caseId");
		if(getIntent().getStringExtra("father")!=null) {
			father = getIntent().getStringExtra("father");
		}
        addRec = getIntent().getBooleanExtra(BaseView.ADDREC,false);

		List<CsSceneCases> csSceneCases = EvidenceApplication.db.findAllByWhere(CsSceneCases.class, "caseNo = '" + caseId + "'");
		if (csSceneCases != null && csSceneCases.size() > 0) {
			String datetime = csSceneCases.get(0).getAlarmDatetime();
			if (!TextUtils.isEmpty(datetime)) {
				Calendar calendar = Calendar.getInstance();
				try {
					calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime));
					alarmTime = calendar.getTimeInMillis();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
        }
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		//requestCode标示请求的标示   resultCode表示有数据
		if (requestCode == ImgFileListActivity.REQUSET && resultCode == RESULT_OK) {
			finish();
		}
	}
	
}
