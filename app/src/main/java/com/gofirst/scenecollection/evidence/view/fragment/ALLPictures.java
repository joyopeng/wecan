package com.gofirst.scenecollection.evidence.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.view.adapter.AllPicturesAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.ListViewAdapter;
import com.gofirst.scenecollection.evidence.view.customview.BaseView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/6/29.
 */
public class ALLPictures extends Fragment {
    private ExpandableListView expandableListView;
    private AllPicturesAdapter treeViewAdapter;
    public String[] groups = { "现场盲拍","概貌", "重点", "细目","其他" };

    public String[][] child = { { "" },{ "" }, { "" }, { "" }, { "", "" } };

    public String[] typePicture={"","1","2","3","4","9"};

    private ListView mListView;
    private ListViewAdapter mListViewAdapter;
    private ArrayList<ArrayList<HashMap<String,Object>>> mArrayList;
    private String caseId,father,mode,templateId;
    private boolean isAddRec;

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
        Log.d("onCreateView", "onCreateViewall");
        View view = inflater.inflate(R.layout.scene_all_photos,null);
        caseId = getArguments().getString("caseId");
        father = getArguments().getString("father");
        mode = getArguments().getString("mode");
        templateId = getArguments().getString("templateId");
        isAddRec = getArguments().getBoolean(BaseView.ADDREC);
        init(view);
//        Init(view);
        return view;
    }

    private void init(View view){
        mListView=(ListView) view.findViewById(R.id.listView);
        initData();
        mListViewAdapter=new ListViewAdapter(father,mode,caseId,templateId,mArrayList, getActivity(),isAddRec);
        mListView.setAdapter(mListViewAdapter);
        List<Fragment> list = getFragmentManager().getFragments();
        for (Fragment fragment : list){
            if (fragment instanceof UnClassPictures){
                UnClassPictures unClassPictures = (UnClassPictures) fragment;
                unClassPictures.setOnBelongToListener(new UnClassPictures.BelongToListener() {
                    @Override
                    public void onBelongTo() {
                        initData();
                        mListViewAdapter=new ListViewAdapter(father,mode,caseId,templateId,mArrayList, getActivity(),isAddRec);
                        mListView.setAdapter(mListViewAdapter);
                    }

                    });
            }
        }
    }
    private void initData(){
        mArrayList=new ArrayList<ArrayList<HashMap<String,Object>>>();
        mArrayList.clear();
        HashMap<String, Object> hashMap=null;
        ArrayList<HashMap<String,Object>> arrayListForEveryGridView;

        for (int i = 0; i < typePicture.length; i++) {
            arrayListForEveryGridView=new ArrayList<HashMap<String,Object>>();
            List<RecordFileInfo> list = EvidenceApplication.db.
                    findAllByWhere(RecordFileInfo.class,
                            "photoType = '" + typePicture[i] + "' and caseId = '" + caseId + "'and fileType = 'png' and father = '"+father+"'");


            for (int j = 0; j < list.size(); j++) {
                hashMap=new HashMap<String, Object>();
                hashMap.put("content", list.get(j).getContractionsFilePath());
                arrayListForEveryGridView.add(hashMap);
            }
            mArrayList.add(arrayListForEveryGridView);
        }

    }

   /* private void initData1(){
        GridViewAdapter.GridViewData gridViewData;
        for (int i = 0; i < typePicture.length; i++) {
            List<RecordFileInfo> list = EvidenceApplication.db.
                    findAllByWhere(RecordFileInfo.class, "belongTo = '" + typePicture[i] + "'");

            for (int j = 0; j < list.size(); j++) {
                gridViewData=new GridViewAdapter.GridViewData();
                gridViewData.getPicturePath(list.get(j).getContractionsFilePath());
                mArrayList.add(gridViewData);
            }
            mArrayList.add();
        }

    }*/

    List<RecordFileInfo> list = EvidenceApplication.db.findAllByWhere(RecordFileInfo.class,
            "photoType = '' and caseId = '" + caseId + "'and fileType = 'png' and father = '"+father+"'");

    /*private void Init(View view){
        treeViewAdapter = new AllPicturesAdapter(getActivity(),
                AllPicturesAdapter.PaddingLeft >> 1);
        expandableListView=(ExpandableListView)view.findViewById(R.id.expandableListView);

        List<AllPicturesAdapter.TreeNode> treeNode = treeViewAdapter.GetTreeNode();
        for (int i = 0; i < groups.length; i++)
        {
            AllPicturesAdapter.TreeNode node = new AllPicturesAdapter.TreeNode();
            node.parent = groups[i];
            ++++++++
            for (int ii = 0; ii < child[i].length; ii++)
            {
                node.childs.add(child[i][ii]);
            }
            treeNode.add(node);
        }

        treeViewAdapter.UpdateTreeNode(treeNode);
        expandableListView.setAdapter(treeViewAdapter);
    }*/

    public void onResume(){
        super.onResume();
        Log.d("onResume", "onResumeall");

        initData();
        mListViewAdapter=new ListViewAdapter(father,mode,caseId,templateId,mArrayList, getActivity(),isAddRec);
        mListView.setAdapter(mListViewAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("onPause", "onPauseall");
    }


}

