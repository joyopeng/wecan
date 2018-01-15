package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.view.View;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.utils.UtilsPop;
import com.gofirst.scenecollection.evidence.view.adapter.MultiChoicAdapter;

import java.util.List;


public class MultiChoicePopWindow extends AbstractChoicePopWindow{

	
	private MultiChoicAdapter<String> mMultiChoicAdapter;
	
	public MultiChoicePopWindow(Context context,View parentView, List<String> list, boolean flag[])
	{
		super(context,parentView, list);
		
		initData(flag);
	}
	

	protected void initData(boolean flag[]) {
		// TODO Auto-generated method stub
		mMultiChoicAdapter = new MultiChoicAdapter<String>(mContext, mList, flag, R.drawable.multi_choicl_list_check);
		
		mListView.setAdapter(mMultiChoicAdapter);
		mListView.setOnItemClickListener(mMultiChoicAdapter);

		UtilsPop.setListViewHeightBasedOnChildren(mListView);

	}

	public boolean[] getSelectItem()
	{
		return mMultiChoicAdapter.getSelectItem();
	}



	
}
