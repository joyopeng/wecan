package com.gofirst.scenecollection.evidence.view.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.RecordFileInfo;
import com.gofirst.scenecollection.evidence.utils.BitmapUtils;
import com.gofirst.scenecollection.evidence.view.customview.TakeNoteDialog;

import java.util.List;

/**
 * Created by Administrator on 2016/6/8.
 */
public class NoteListAdapter extends BaseAdapter {

    private String mCaseId, father;
    private List<String> list;
    private Activity activity;

    public NoteListAdapter(List<String> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_fragment_item, parent, false);
        final LinearLayout group = (LinearLayout) convertView.findViewById(R.id.group);
        ImageView addNote = (ImageView) convertView.findViewById(R.id.add_note);
        List<RecordFileInfo> noteList = getSignPictures(list.get(position));
        if (noteList.size() == 0)
            group.addView(createNewNote(group, list.get(position)));
        //       addNote.setVisibility(noteList.size() != 0 ? View.INVISIBLE : View.VISIBLE);
        for (RecordFileInfo recordFileInfo : noteList) {
            Bitmap bitmap = BitmapUtils.revitionImageSize(recordFileInfo.getFilePath());
            group.addView(createDispNote(group, bitmap));
        }
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group.addView(createNewNote(group, list.get(position)));
            }
        });
        TextView textView = (TextView) convertView.findViewById(R.id.note_name);
        textView.setText(list.get(position));
        return convertView;
    }

    private View createNewNote(final LinearLayout group, final String name) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.take_note_people, group, false);
        final TextView textView = (TextView) view.findViewById(R.id.take_note_people);
        textView.setTag(group.getChildCount());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "签名" + (int) textView.getTag(), Toast.LENGTH_SHORT).show();
                TakeNoteDialog dialog = new TakeNoteDialog(textView, activity, name + (int) textView.getTag(), name, father);
                dialog.setCaseId(mCaseId);
            }
        });
        return view;
    }

    private View createDispNote(final LinearLayout group, Bitmap bitmap) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.take_note_people, group, false);
        final TextView textView = (TextView) view.findViewById(R.id.take_note_people);
        textView.setText("");
        if (bitmap != null) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, bitmap.getHeight());
            textView.setLayoutParams(params);
            textView.setBackground(new BitmapDrawable(bitmap));

        }
        return view;
    }

    public void setCaseId(String caseId) {
        this.mCaseId = caseId;
    }

    public void setFather(String father) {
        this.father = father;
    }

    private List<RecordFileInfo> getSignPictures(String belong) {
        return EvidenceApplication.db.findAllByWhere(RecordFileInfo.class, "caseId = '" +
                mCaseId + "' and father = '" + father + "' and belongTo = '" + belong + "'");
    }

}
