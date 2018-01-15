package com.gofirst.scenecollection.evidence.view.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.PublicMsg;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.utils.SharePre;

/**
 * Created by Administrator on 2016/10/11.
 */
public class LoginIpSet extends Activity implements View.OnClickListener{


    private SharePre sharePre;
    private ImageView secondary_back_img;
    private TextView secondary_title_tv;
    private TextView secondary_right_tv;
    private EditText setip_edittext;
    private TextView restore,save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.login_ip_set);
        sharePre = new SharePre(LoginIpSet.this, "user_info", Context.MODE_PRIVATE);
        init();
    }


    private void init() {
        secondary_back_img=(ImageView)findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_back_img);
        secondary_title_tv=(TextView)findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_title_tv);
        secondary_right_tv=(TextView)findViewById(R.id.search_title_layout)
                .findViewById(R.id.secondary_right_tv);
        secondary_back_img.setVisibility(View.GONE);
        secondary_title_tv.setText("设置");
        secondary_right_tv.setVisibility(View.GONE);

        setip_edittext=(EditText)findViewById(R.id.setip_edittext);
        restore=(TextView)findViewById(R.id.restore);
        save=(TextView)findViewById(R.id.save);;
        restore.setOnClickListener(this);
        save.setOnClickListener(this);
        if(sharePre.getString("ip","").equals("")||sharePre.getString("ip","")==null){
            //PublicMsg.BASEURL=PublicMsg.BASEURLTEMP;
            setip_edittext.setText(PublicMsg.BASEURL);
        }else{
            setip_edittext.setText(sharePre.getString("ip",""));
        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.restore:
                PublicMsg.BASEURL=PublicMsg.BASEURLTEMP;
                sharePre.put("ip","");
                sharePre.commit();
                finish();
                break;
            case R.id.save:
                sharePre.put("ip", setip_edittext.getText().toString());
                sharePre.commit();
                if(!setip_edittext.getText().toString().equals("")){
                    PublicMsg.BASEURL= setip_edittext.getText().toString();
                }else {
                    Toast.makeText(LoginIpSet.this,"ip不能为空",Toast.LENGTH_SHORT).show();
                }
                finish();
                break;

        }
    }

}
