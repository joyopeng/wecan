package com.gftxcky.draw;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class SelModeActivity extends Activity{
	
	Dialog dialog;
	private String caseId;
	private String father;
	private String info;

	public void onBackPressed()
	{
		dialog.cancel();
		finish();
	}

	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if(getIntent().getStringExtra("caseId")!=null) {
			caseId = getIntent().getStringExtra("caseId");
		}
		if(getIntent().getStringExtra("father")!=null) {
			father = getIntent().getStringExtra("father");
		}
		if(getIntent().getStringExtra("info")!=null) {
			info = getIntent().getStringExtra("info");
		}
		dialog = new Dialog(this,R.style.SelectModelDialogTheme);
		dialog.setTitle("选择模型");
		dialog.setCancelable(true);
		//dialog.setCancelable(false);
		dialog.setContentView(R.layout.selmodeactivity_dialog);
		((Button)dialog.findViewById(R.id.normalMode)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
				Intent intent = new Intent();
//				intent.setClass(SelModeActivity.this, NormalModeActivity.class);
				intent.setClass(SelModeActivity.this,ExperModeActivity.class);
				intent.putExtra("ID", "111");
				intent.putExtra("caseId",caseId);
				intent.putExtra("father",father);
				intent.putExtra("info", info);
				SelModeActivity.this.startActivity(intent);
				SelModeActivity.this.finish();
			}
		});
		((Button)dialog.findViewById(R.id.expertMode)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
				Intent intent = new Intent();
				intent.setClass(SelModeActivity.this,ExperModeActivity.class);
				intent.putExtra("ID", "111,,,,超级管理员");
				intent.putExtra("caseId", caseId);
				intent.putExtra("father",father);
				intent.putExtra("info", info);
				SelModeActivity.this.startActivity(intent);
				SelModeActivity.this.finish();
			}
		});
		((Button)dialog.findViewById(R.id.editnormalmode)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
				Intent intent = new Intent();
//				intent.setClass(SelModeActivity.this,ShowBitMapActivity.class);
				intent.setClass(SelModeActivity.this,ExperModeActivity.class);
				intent.putExtra("type","none");
				intent.putExtra("id", "22");
				intent.putExtra("caseId",caseId);
				intent.putExtra("father",father);
				intent.putExtra("info", info);
				SelModeActivity.this.startActivity(intent);
				SelModeActivity.this.finish();				
			}
		});
		((Button)dialog.findViewById(R.id.editexperMode)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
				Intent intent = new Intent();
//				intent.setClass(SelModeActivity.this,ShowBitMapActivity.class);
				intent.setClass(SelModeActivity.this,ExperModeActivity.class);
				intent.putExtra("type","none");
				intent.putExtra("id","23");
				intent.putExtra("caseId",caseId);
				intent.putExtra("father",father);
				intent.putExtra("info", info);
				SelModeActivity.this.startActivity(intent);
				SelModeActivity.this.finish();				
			}
		});
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				SelModeActivity.this.finish();
			}
		});
		dialog.show();
	}
}
