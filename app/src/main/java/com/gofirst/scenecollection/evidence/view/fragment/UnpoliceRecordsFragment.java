package com.gofirst.scenecollection.evidence.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.adapter.UnpoliceRecordsFragmentAdapter;
import com.gofirst.scenecollection.evidence.view.adapter.UnpoliceRecordsFragmentAdapter.UnpoliceRecordsFragmentData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/5/14.
 */
public class UnpoliceRecordsFragment extends Fragment {

    private String[] sceneDetail={"香格里拉花园10幢905室","金利花园7幢602室",
            "阳光水榭3幢105室","彩虹新村10幢709室","吴中商场18幢102室","迎春家园16幢1007室",
            "苏苑新村2幢610室","迎春路1008号","美之国花园1幢908室"};
    private String[] sceneRegionalismName={"王振忠","李伟毅","李伟毅","张俊熙","王振忠",
            "李伟毅","黄敦儒","李伟毅","王振忠"};
    private String[] caseSolve={"1","2","0","1","0","2","1","1","0"};
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
    private String[] crackedDate=new String[9];

    private String[] exposureProcess={"违法犯罪警情,称家里面玻璃被打穿了，有人受伤，要民警到场","违法犯罪警情其他盗窃,家里面遭盗窃，损失价值一万元",
            "违法犯罪警情,称抓到一个偷窥别人洗澡的人","违法犯罪警情盗窃车辆,电瓶车在小区被盗窃，损失价值两千元",
            "违法犯罪警情,由于纠纷与邻居打架，造成人员受伤，要求民警到场","违法犯罪警情其他盗窃,家里面遭盗窃，丢失笔记本电脑、相机和人民币伍仟元，损失价值两万元",
            "诈骗警情，称遭到X金融公司诈骗，损失十万元","其他警情，两个由于冲突，相互殴打，其中一人受重伤","违法犯罪警情盗窃,称自行车被盗窃，损失一千元"};


    private ListView allQuery_fragment_listview;
    private UnpoliceRecordsFragmentAdapter adapter;
    private List<UnpoliceRecordsFragmentData> list = new ArrayList<UnpoliceRecordsFragmentData>();
    String BASEURL ="http://192.168.0.188:8888/EvidenceService/app/";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.allquery_fragment, null);


                        UnpoliceRecordsFragmentData unpoliceRecordsFragmentData;

                        list.clear();

        long baseTime = System.currentTimeMillis();
        for (int i = 0;i<crackedDate.length;i++) {
            crackedDate[i] = simpleDateFormat.format(new Date(baseTime + 2 * i * 60 * 1000));
        }

                        for (int i = 0; i < sceneDetail.length; i++) {
                            //	jsonObjectdata = jsonArray.getJSONObject(i);
                            unpoliceRecordsFragmentData = new UnpoliceRecordsFragmentData();
							/*allQueryFragmentData.setInvestigationPlace(jsonObjectdata.getString("sceneDetail"));
							allQueryFragmentData.setExposureProcess(jsonObjectdata.getString("exposureProcess"));
							allQueryFragmentData.setSceneRegionalismName(jsonObjectdata.getString("sceneRegionalismName"));
							allQueryFragmentData.setCrackedDate(jsonObjectdata.getString("crackedDate"));
							allQueryFragmentData.setCaseSolve(jsonObjectdata.getString("caseSolve"));*/

                            unpoliceRecordsFragmentData.setInvestigationPlace(sceneDetail[i]);
                            unpoliceRecordsFragmentData.setExposureProcess(exposureProcess[i]);
                            unpoliceRecordsFragmentData.setSceneRegionalismName(sceneRegionalismName[i]);
                            unpoliceRecordsFragmentData.setCrackedDate(crackedDate[i]);
                            unpoliceRecordsFragmentData.setCaseSolve(caseSolve[i]);
                            if (caseSolve[i].equals("2")) {
                                list.add(unpoliceRecordsFragmentData);
                            }
                        }

//                        adapter.notifyDataSetChanged();




        allQuery_fragment_listview=(ListView)view.findViewById(R.id.allQuery_fragment_listview);
        adapter = new UnpoliceRecordsFragmentAdapter(getActivity(),list);
        allQuery_fragment_listview.setAdapter(adapter);

        return view;
    }


}


