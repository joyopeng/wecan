package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.activity.SeconddaryView;

/**
 * Created by Administrator on 2016/10/20.
 */
public class SecondaryView extends LinearLayout {

    private TextView click2Input;
    private String name;
    private String saveKey;
    public SecondaryView(Context context) {
        super(context);
    }

    public SecondaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SecondaryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void initView(final String mode, final String name, String text, String saveKey, final String... args) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.secondary_view_layout, this, true);
        TextView viewName = (TextView) view.findViewById(R.id.name);
        click2Input = (TextView) view.findViewById(R.id.click_to_input);
        click2Input.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!click2Input.getText().toString().equals("无")){
                Intent intent = new Intent(v.getContext(), SeconddaryView.class);
                intent.putExtra("title",name);
                intent.putExtra("caseId",args[0]);
                intent.putExtra("father",args[1]);
                intent.putExtra("templateId",args[2]);
                intent.putExtra("mode",mode);
                intent.putExtra(BaseView.ADDREC,Boolean.valueOf(args[3]));
                v.getContext().startActivity(intent);
                }
            }
        });
        this.name = name;
        this.saveKey = saveKey;
        viewName.setText(name);
        click2Input.setText(text != null && !TextUtils.isEmpty(text)  ? text : mode.equals(BaseView.EDIT) ? "点击输入" : "无");
    }
}
