package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.Application.EvidenceApplication;
import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.model.CsDicts;
import com.gofirst.scenecollection.evidence.model.CsDictsFavorites;

import java.util.ArrayList;
import java.util.List;

/**
 * @author maxiran
 */

@SuppressWarnings("ALL")
public class SpinnerPop implements ArrowTabView.ArrowChangeListener,View.OnClickListener{

    private LinearLayout container,oftenConainer;
    private List<View> levelItem = new ArrayList<>();
    private String name;
    private String rootKey;
    private TextView allBtn,oftenBtn,outPutText;
    private View allLine,oftenLine;
    private PopupWindow popupWindow;
    private  ListView oftenList;

    public SpinnerPop(Context context, TextView inputDate, String name, String rootKey) {
        this.name = name;
        this.rootKey = rootKey;
        initView(context, inputDate);
    }

    private void initView(Context context, final TextView inputDate) {
        outPutText = inputDate;
        View view = LayoutInflater.from(context).inflate(R.layout.multi_level_list_case_main_layout, null);
        allBtn = (TextView) view.findViewById(R.id.all_btn);
        oftenBtn = (TextView) view.findViewById(R.id.often_btn);
        allLine = view.findViewById(R.id.all_line);
        oftenLine = view.findViewById(R.id.often_line);
        allBtn.setOnClickListener(this);
        oftenBtn.setOnClickListener(this);
        container = (LinearLayout) view.findViewById(R.id.container);
        oftenConainer = (LinearLayout) view.findViewById(R.id.often_container);
        oftenList = (ListView) view.findViewById(R.id.often_list);
        oftenList.setDividerHeight(0);
        oftenList.setAdapter(new OftenContentAdapter(getOftenListData()));
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ((TextView)view.findViewById(R.id.title).findViewById(R.id.secondary_title_tv)).setText(name);
        view.findViewById(R.id.title).findViewById(R.id.secondary_back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        TextView finish = (TextView) view.findViewById(R.id.title).findViewById(R.id.secondary_right_tv);
        finish.setVisibility(View.VISIBLE);
        finish.setText("完成");
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = getLastLevelName(levelItem.size() - 1);
                if (text!=null) {
                    inputDate.setText(text.getText());
                    inputDate.setTag(text.getTag());
                }
                popupWindow.dismiss();
            }
        });
        addSelectLevelList(context, 1, "");
        popupWindow.setAnimationStyle(R.style.tabpopstyle);
        popupWindow.setFocusable(true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (!popupWindow.isShowing())
            popupWindow.showAtLocation(inputDate, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onArrowDirectChange(boolean isArrowUp, int currentLevel, String parentKey) {
        if (isArrowUp) {
            removeAllLevelListView();
            removeNextLevelSelectItem(currentLevel - 1);
            addNewList(currentLevel, parentKey);
        } else {
            removeLevelListView(currentLevel);
        }
    }


    private void addNewList(int currentLevel, String parentKey) {
        ListView listView = new ListView(container.getContext());
        listView.setTag(currentLevel);
        ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1);
        sa.setDuration(80);
        listView.setDividerHeight(0);
        listView.setLayoutAnimation(new LayoutAnimationController(sa));
        container.addView(listView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        String sql = currentLevel == 1 ? "rootKey = '" + rootKey + "'" + " and dictLevel = '1'" : "rootKey = '" + rootKey + "'" + " and parentKey = '" + parentKey + "'" + "and dictLevel = '" + currentLevel + "'";
        List<CsDicts> list = EvidenceApplication.db.findAllByWhere(CsDicts.class, sql);
        listView.setAdapter(new listContentAdapter(list, currentLevel));
    }

    private void removeLevelListView(int level) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);
            if (view instanceof ListView && view.getTag() != null && (int) view.getTag() == level)
                container.removeView(view);
        }
    }

    private void removeAllLevelListView() {
        for (int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);
            if (view instanceof ListView)
                container.removeView(view);
        }
    }
    private void removeNextLevelSelectItem(int level) {
        for (int i = levelItem.size() - 1; i > level; i--) {
            container.removeView(levelItem.get(i));
            levelItem.remove(i);
        }
    }

    private void addSelectLevelList(Context context, int nextLevel, String parentKey) {
        View LevelMenu = LayoutInflater.from(context).inflate(R.layout.multi_level_select_item, container, false);
        container.addView(LevelMenu);
        TextView levelName = (TextView) LevelMenu.findViewById(R.id.choose_name);
        levelName.setText("选择" + name);
        final ArrowTabView firstArrow = (ArrowTabView) LevelMenu.findViewById(R.id.arrow);
        firstArrow.setListener(this);
        firstArrow.setCurrentLevel(nextLevel);
        firstArrow.setParentKey(parentKey);
        levelItem.add(LevelMenu);
        firstArrow.changState();
        LevelMenu.findViewById(R.id.choose_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstArrow.changState();
            }
        });
    }


    private TextView getLastLevelName(int position) {
        TextView chooseContent = (TextView) levelItem.get(position).findViewById(R.id.choose_content);
        String text = (String) chooseContent.getText();
        if (position == 0 && TextUtils.isEmpty(text)) {
            return null;
        }
        return TextUtils.isEmpty(text) ? getLastLevelName(position - 1) : chooseContent;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.all_btn:
                changeTabState(allBtn,oftenBtn,allLine,oftenLine,container,oftenConainer);
                break;
            case R.id.often_btn:
                changeTabState(oftenBtn,allBtn,oftenLine,allLine,oftenConainer,container);

                break;
        }
    }

    private void changeTabState(TextView self,TextView target,View selfLine,View targetLine,LinearLayout... container){
        self.setTextColor(Color.parseColor("#2EA1EC"));
        selfLine.setBackgroundColor(Color.parseColor("#2EA1EC"));
        target.setTextColor(R.color.all_text_color);
        targetLine.setBackgroundColor(R.color.all_view_stroke);
        container[0].setVisibility(View.VISIBLE);
        container[1].setVisibility(View.INVISIBLE);
    }

    private List<CsDicts> getOftenListData(){
        List<CsDicts> oftenData = new ArrayList<>();
        List<CsDictsFavorites> list = EvidenceApplication.db.findAllByWhere(CsDictsFavorites.class,"rootKey = '" + rootKey + "'");
        for (CsDictsFavorites csDictsFavorites : list){
            oftenData.addAll(EvidenceApplication.db.findAllByWhere(CsDicts.class,"sid = '" + csDictsFavorites.getDictsId()
                    + "' and rootKey = '" + rootKey + "'"));
        }
        return oftenData;
    }
    public class listContentAdapter extends BaseAdapter {

        private List<CsDicts> list;
        private int currentLevel;

        public listContentAdapter(List<CsDicts> list, int currentLevel) {
            this.list = list;
            this.currentLevel = currentLevel;
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
            if (convertView == null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lost_good_item, parent, false);
            TextView name = (TextView) convertView.findViewById(R.id.goods_desc);
            name.setText(list.get(position).getDictValue1());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("currentLevel",currentLevel+"");
                    Log.d("currentSize",levelItem.size() +"");
                    TextView chooseContent = (TextView) levelItem.get(currentLevel - 1).findViewById(R.id.choose_content);
                    ArrowTabView arrowTabView = (ArrowTabView) levelItem.get(currentLevel - 1).findViewById(R.id.arrow);
                    chooseContent.setText(list.get(position).getDictValue1());
                    chooseContent.setTag(list.get(position).getDictKey());
                    arrowTabView.changState();
                    removeLevelListView(currentLevel);
                    List<CsDicts> listArea = EvidenceApplication.db.findAllByWhere(CsDicts.class, "rootKey = 'AJLBDM'" +
                            " and dictLevel = '" + (currentLevel + 1) + "' and parentKey = '" + list.get(position).getDictKey()+"'");
                    if (listArea.size() != 0)
                        addSelectLevelList(v.getContext(), currentLevel + 1, list.get(position).getDictKey());
                }
            });
            return convertView;
        }
    }


    public class OftenContentAdapter extends BaseAdapter {

        private List<CsDicts> list;
        private int currentLevel;

        public OftenContentAdapter(List<CsDicts> list) {
            this.list = list;
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
            if (convertView == null)
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lost_good_item, parent, false);
            TextView name = (TextView) convertView.findViewById(R.id.goods_desc);
            name.setText(list.get(position).getDictValue1());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    outPutText.setText(list.get(position).getDictValue1());
                    outPutText.setTag(list.get(position).getDictKey());
                    if (popupWindow.isShowing())
                        popupWindow.dismiss();
                }
            });
            return convertView;
        }
    }
}




