package com.gofirst.scenecollection.evidence.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.AppPathUtil;
import com.gofirst.scenecollection.evidence.utils.OSUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by maxiran on 2016/5/6.
 */

@SuppressWarnings("ALL")
public class PlayRecordAdapter extends BaseAdapter{
    private List<RecordFileInfo> audioFiles;

    public PlayRecordAdapter(List<RecordFileInfo> audioFiles) {
        this.audioFiles = audioFiles;
    }

    @Override
    public int getCount() {
        return audioFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null){
            holder = new Holder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.play_record_item,parent,false);
            holder.name = (TextView)convertView.findViewById(R.id.audio_name);
            holder.time = (TextView)convertView.findViewById(R.id.audio_time);
            holder.date = (TextView)convertView.findViewById(R.id.audio_date);
            convertView.setTag(holder);
        }else {
            holder = (Holder)convertView.getTag();
        }
        holder.name.setText(audioFiles.get(position).getChild());
        int time = createRecTime(AppPathUtil.getDataPath() + "/" +audioFiles.get(position).getFilePath(),parent.getContext());
        holder.time.setText("时长："+getStandardTime(new Date(time),"mm分 ss秒"));
        holder.date.setText(getStandardTime(audioFiles.get(position).getFileDate(),"yyyy-MM-dd HH:mm:ss"));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(AppPathUtil.getDataPath() + "/" + audioFiles.get(position).getFilePath())), "audio/amr");
                v.getContext().startActivity(intent);
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Context context = v.getContext();
                View view = LayoutInflater.from(context).inflate(R.layout.delete_pop, null);
                int wid = OSUtil.dip2px(context,300);
                int hei = OSUtil.dip2px(context,45);
                final PopupWindow popupWindow = new PopupWindow(view, wid,
                        hei);
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean deleteFileSucess = new File(AppPathUtil.getDataPath() + "/" + audioFiles.get(position).getFilePath()).delete();
                        Toast.makeText(context, deleteFileSucess? "删除成功" : "删除失败",Toast.LENGTH_SHORT).show();
                        if (deleteFileSucess){
                            EvidenceApplication.db.deleteByWhere(RecordFileInfo.class,"filePath = '" + audioFiles.get(position).getFilePath() + "'" );
                            audioFiles.remove(position);
                            notifyDataSetChanged();
                        }
                        popupWindow.dismiss();
                    }
                });
                popupWindow.showAtLocation(v, Gravity.CENTER,0,0);
                notifyDataSetChanged();
                return false;
            }
        });
        return convertView;
    }

    class Holder{
        private TextView name,time,date;
    }

    private int createRecTime(String path, Context context){
        try {
            MediaPlayer mp = MediaPlayer.create(context, Uri.parse(path));
            return mp.getDuration();
        }catch (Exception e){
            return 300;
        }

    }

    public String getStandardTime(Date date, String macther) {
        SimpleDateFormat sdf = new SimpleDateFormat(macther);
        if (date != null)
        return sdf.format(date);
        return "";
    }
}
