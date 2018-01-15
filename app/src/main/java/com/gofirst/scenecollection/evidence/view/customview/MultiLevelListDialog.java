package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.text.TextUtils;
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
import com.gofirst.scenecollection.evidence.model.Area;

import java.util.ArrayList;
import java.util.List;


public class MultiLevelListDialog implements ArrowTabView.ArrowChangeListener {

    private LinearLayout container;
    private List<View> levelItem = new ArrayList<>();
    private String name;

    public MultiLevelListDialog(Context context, TextView inputDate, String name) {
        this.name = name;
        initView(context, inputDate);
    }

    private void initView(Context context, final TextView inputDate) {
        View view = LayoutInflater.from(context).inflate(R.layout.multi_level_list_main_layout, null);
        container = (LinearLayout) view.findViewById(R.id.container);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.findViewById(R.id.title).findViewById(R.id.secondary_back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        ((TextView)view.findViewById(R.id.title).findViewById(R.id.secondary_title_tv)).setText(name);
        TextView finish = (TextView)view.findViewById(R.id.title).findViewById(R.id.secondary_right_tv);
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
        addSelectLevelList(context, 0, "");
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
            removeNextLevelSelectItem(currentLevel);
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
        listView.setLayoutAnimation(new LayoutAnimationController(sa));
        container.addView(listView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        String sql = currentLevel == 0 ? "compartmentLevel = '" + (currentLevel + 1) + "'" : "compartmentLevel = '" + (currentLevel + 1) + "' and compartmentUpNo = '" + parentKey + "'";
        List<Area> list = EvidenceApplication.db.findAllByWhere(Area.class, sql);
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

    private void  addSelectLevelList(Context context, int nextLevel, String parentKey) {
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

    public class listContentAdapter extends BaseAdapter {

        private List<Area> list;
        private int currentLevelTag;


        public listContentAdapter(List<Area> list, int currentLevel) {
            this.list = list;
            this.currentLevelTag = currentLevel;
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
            name.setText(list.get(position).getCompartmentName());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView chooseContent = (TextView) levelItem.get(currentLevelTag).findViewById(R.id.choose_content);
                    ArrowTabView arrowTabView = (ArrowTabView) levelItem.get(currentLevelTag).findViewById(R.id.arrow);
                    chooseContent.setText(list.get(position).getCompartmentName());
                    chooseContent.setTag(list.get(position).getCompartmentNo());
                    arrowTabView.changState();
                    List<Area> listArea = EvidenceApplication.db.findAllByWhere(Area.class, "compartmentLevel = '" + (currentLevelTag + 2) +
                            "' and compartmentUpNo = '" + list.get(position).getCompartmentNo()+ "'");
                    if (listArea.size() != 0)
                        addSelectLevelList(v.getContext(), currentLevelTag + 1, list.get(position).getCompartmentNo());
                }
            });
            return convertView;
        }
    }
}
