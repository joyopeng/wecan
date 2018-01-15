package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.widget.ListView;

import com.gofirst.scenecollection.evidence.view.adapter.SceneProspectListAdapter;

/**
 * @author maxiran
 *         待勘查列表
 */
public class SceneProspectList extends Activity {

    private SceneProspectListAdapter adapter;
    private String employeeName;
    private ListView sceneProspectListView;

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        SharePre sharePre = new SharePre(SceneProspectList.this, "user_info", Context.MODE_PRIVATE);
        employeeName = sharePre.getString("prospectPerson", "");
        setContentView(R.layout.scene_prospect_list);
        sceneProspectListView = (ListView) findViewById(R.id.scene_prospect_list);
        List<CsSceneCases> list = getHandCase();
       *//* adapter = new SceneProspectListAdapter(employeeName, list,"","");*//*
        sceneProspectListView.setAdapter(adapter);
    }

    public void Call(View v) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + "13598675432"));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private List<CsSceneCases> getHandCase() {
        return EvidenceApplication.db.findAllByWhere(CsSceneCases.class,"status = '1'","receiveCaseTime desc");
    }

    @Override
    protected void onResume() {
        List<CsSceneCases> list = getHandCase();
        adapter = new SceneProspectListAdapter(employeeName, list);
        sceneProspectListView.setAdapter(adapter);
        super.onResume();
    }*/
}
