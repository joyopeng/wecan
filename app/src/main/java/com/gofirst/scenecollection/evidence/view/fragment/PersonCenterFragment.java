package com.gofirst.scenecollection.evidence.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.HyOrganizations;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.SharePre;
import com.gofirst.scenecollection.evidence.view.activity.ChangePassword;
import com.gofirst.scenecollection.evidence.view.activity.CheckUpdate;
import com.gofirst.scenecollection.evidence.view.activity.NoteActivity;

import java.io.File;
import java.util.List;

public class PersonCenterFragment extends Fragment implements View.OnClickListener {
    private View mPwd, mAbout, mSign, person_center_sign;
    private TextView mEmployeeName, mEmployeeAccount, mWorkPosition;
    private SharePre sharePre;
    private String mName = "", mAccount = "", mPosition = "", employeeNo = "";
    private ImageView person_center_sign_tv;
    private Button mAppExitBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
//		System.out.println("OneFragment  onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.personsetfragment, container, false);
        sharePre = new SharePre(getActivity(), "user_info", Context.MODE_PRIVATE);
        mName = sharePre.getString("prospectPerson", "未知");
        mAccount = sharePre.getString("user_name", "未知");
        employeeNo = sharePre.getString("user_id", "");
        String organizationId = sharePre.getString("organizationId", "");
        if (!"".equals(organizationId)) {
            List<HyOrganizations> organizations =
                    EvidenceApplication.db.findAllByWhere(HyOrganizations.class, "organizationId = \"" + organizationId + "\"");
            if (organizations != null && organizations.size() > 0)
                mPosition = organizations.get(0).getOrganizationName();
        }
        Init(view);
        return view;
    }


    private void Init(View view) {
        mEmployeeName = (TextView) view.findViewById(R.id.person_center_name_tv);
        mEmployeeName.setText("姓名: " + mName);
        mEmployeeAccount = (TextView) view.findViewById(R.id.person_center_account_tv);
        mEmployeeAccount.setText("账号: " + mAccount);
        mWorkPosition = (TextView) view.findViewById(R.id.person_center_position_tv);
        mWorkPosition.setText("单位: " + mPosition);
        person_center_sign = (View) view.findViewById(R.id.person_center_sign);
        person_center_sign_tv = (ImageView) view.findViewById(R.id.person_center_sign_tv);
        mPwd = view.findViewById(R.id.person_center_update_pwd_btn);
        mPwd.setOnClickListener(this);
        mAbout = view.findViewById(R.id.person_center_about_btn);
        mAbout.setOnClickListener(this);
        mSign = view.findViewById(R.id.person_center_sign_btn);
        mSign.setOnClickListener(this);
        mAppExitBtn = (Button) view.findViewById(R.id.person_center_exit_btn);
        mAppExitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_center_update_pwd_btn:
                Intent update = new Intent(getActivity(), ChangePassword.class);
                startActivity(update);
                break;
            case R.id.person_center_about_btn:
                Intent about = new Intent(getActivity(), CheckUpdate.class);
                startActivity(about);
                break;
            case R.id.person_center_exit_btn:
                //Intent intentExit = new Intent(getActivity(), Login.class);
                //intentExit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //startActivity(intentExit);
                Intent exitIntent = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                exitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(exitIntent);
                break;
            case R.id.person_center_sign_btn:
                Intent intent = new Intent(getActivity(), NoteActivity.class);
                intent.putExtra("isselfSign", true);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        File signfile = new File(AppPathUtil.getDataPath() + "/selfsign/" + employeeNo + "&_thum_&.jpg");
        if (signfile.exists()) {
            person_center_sign.setVisibility(View.VISIBLE);
            person_center_sign_tv.setImageBitmap(BitmapFactory.decodeFile(signfile.getAbsolutePath()));
        }
    }
}

