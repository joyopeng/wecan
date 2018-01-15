package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/15.
 */
public class DialogDridviewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<DialogDridviewData> pictures;
    private List<TextView> tabs = new ArrayList<>();
    private int lastPosition=1;
    Context context;
    GridView gridView;
    TabListener listener;
    private String type;

    public DialogDridviewAdapter(String[] titles,GridView gridView, Context context,TabListener listener)
    {
        this.listener=listener;
        pictures = new ArrayList<DialogDridviewData>();
        inflater = LayoutInflater.from(context);
        for (int i = 0; i < titles.length; i++)
        {
            DialogDridviewData DialogDridviewData = new DialogDridviewData(titles[i]);
            pictures.add(DialogDridviewData);
            TextView tab = (TextView) LayoutInflater.from(context).inflate(R.layout.dialog_gridview_item, null);
            if(i==1){
                tab.setBackgroundResource(R.drawable.radio_checked1);
                tab.setTextColor(Color.WHITE);
            }
            tabs.add(tab);

        }
    }

    @Override
    public int getCount()
    {
        if (null != pictures)
        {
            return pictures.size();
        } else
        {
            return 0;
        }
    }

    @Override
    public TextView getItem(int position)
    {
        return tabs.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView,  final ViewGroup parent)
    {
        final TextView textView = tabs.get(position);
        textView.setText(pictures.get(position).getTitle());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView lastTab = getItem(lastPosition);
                lastTab.setBackgroundResource(R.drawable.radio_unchecked1);
                textView.setBackgroundResource(R.drawable.radio_checked1);
                textView.setTextColor(Color.WHITE);
                lastTab.setTextColor(Color.BLACK);
                lastPosition = position;
                listener.onTab(pictures.get(position).getTitle());
            }
        });
        return textView;
    }

    public interface TabListener{
        void onTab(String name);
    }

}



class DialogDridviewData
{
    private String title;
//    private int imageId;

    public DialogDridviewData()
    {
        super();
    }

    public DialogDridviewData(String title)
    {
        super();
        this.title = title;
        //       this.imageId = imageId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }



}


