package com.gofirst.scenecollection.evidence.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.view.adapter.NoteListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxirann 2016/5/6.
 */
public class TakeNoteFragment extends Fragment{


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.take_note_fragment,null);
        ListView listView =(ListView)view.findViewById(R.id.note_list);
        List<String> list = new ArrayList<>();
        list.add("笔录人");
        list.add("制图人");
        list.add("证人");
        NoteListAdapter noteListAdapter = new NoteListAdapter(list,getActivity());
        noteListAdapter.setCaseId(getArguments().getString("caseId"));
        noteListAdapter.setFather(getArguments().getString("father"));
        listView.setAdapter(noteListAdapter);
        return view;
    }

}
