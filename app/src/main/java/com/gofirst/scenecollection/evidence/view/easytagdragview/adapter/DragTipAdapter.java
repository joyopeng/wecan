package com.gofirst.scenecollection.evidence.view.easytagdragview.adapter;

import android.content.ClipData;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.utils.ToastUtil;
import com.gofirst.scenecollection.evidence.view.easytagdragview.bean.Tip;
import com.gofirst.scenecollection.evidence.view.easytagdragview.listener.OnItemSelectListener;
import com.gofirst.scenecollection.evidence.view.easytagdragview.widget.DragDropGirdView;
import com.gofirst.scenecollection.evidence.view.easytagdragview.widget.TipItemView;

import java.util.ArrayList;

/**
 * Created by Wenhuaijun on 2016/5/26 0026.
 */
public class DragTipAdapter extends AbsTipAdapter implements View.OnLongClickListener, TipItemView.OnDeleteClickListener {
    private boolean isEditing = false;
    private static final ClipData EMPTY_CLIP_DATA = ClipData.newPlainText("", "");
    private TipItemView.OnSelectedListener mListener;
    private TipItemView.OnDeleteClickListener deleteClickListener;
    private OnFirstDragStartCallback callback;
    private TipItemView lastView;
    private boolean isViewMode = false;
    public DragTipAdapter(Context context, DragDropListener dragDropListener, TipItemView.OnDeleteClickListener deleteClickListener) {
        super(context, dragDropListener);
        this.deleteClickListener = deleteClickListener;

    }

    public void setViewMode(boolean viewMode) {
        isViewMode = viewMode;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TipItemView view = null;
        if (convertView != null && convertView instanceof TipItemView) {
            view = (TipItemView) convertView;
        } else {
            view = (TipItemView) View.inflate(mContext, R.layout.view_tag_item, null);
        }
        if (isEditing) {
            view.showDeleteImg();
        } else {
            view.hideDeleteImg();
        }

        //设置点击监听
        view.setItemListener(position, mListener);
        view.setOnLongClickListener(isViewMode ? new ViewModeClickListener() : this);
        //设置删除监听
        view.setDeleteClickListener(position, deleteClickListener);
        //绑定数据
        view.renderData(getItem(position));
        view.setOnItemSelectListener(new OnItemSelectListener() {
            @Override
            public void onItemSelect(TipItemView tipItemView) {
                if (lastView != null){
                    lastView.setIsSelect(false);
                }
                tipItemView.setIsSelect(true);
                lastView = tipItemView;
            }
        });
        if (position == defaultSelectPostion) {
            if (lastView != null){
                lastView.setIsSelect(false);
            }
            view.setIsSelect(true);
            lastView = view;
        }
        if (defaultSelectPostion == 0)
            notifyDataSetChanged();
        return view;
    }

    @Override
    protected Tip getDragEntity(View view) {
        return ((TipItemView) view).getDragEntity();
    }

    public void setItemSelectedListener(TipItemView.OnSelectedListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public boolean onLongClick(View v) {
        //开启编辑模式
        startEditingStatus(v);
        return true;
    }

    //删除按钮点击时
    @Override
    public void onDeleteClick(Tip entity, int position, View view) {
        tips.remove(position);
        refreshData();

    }

    public void refreshData() {
        notifyDataSetChanged();
        mDragDropListener.onDataSetChangedForResult(tips);
    }

    public ArrayList<Tip> getData() {
        return tips;
    }

    public void setFirtDragStartCallback(OnFirstDragStartCallback callback) {
        this.callback = callback;
    }

    public interface OnFirstDragStartCallback {
        void firstDragStartCallback();
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void cancelEditingStatus() {
        isEditing = false;
        notifyDataSetChanged();
    }

    private void startEditingStatus(View v) {
        if (!isEditing) {
            isEditing = true;
            if (callback != null) {
                callback.firstDragStartCallback();
            }
            notifyDataSetChanged();
        }
        v.startDrag(EMPTY_CLIP_DATA, new View.DragShadowBuilder(),
                DragDropGirdView.DRAG_FAVORITE_TILE, 0);
    }

    public void setFinishEditing(){
        if (isEditing)
            isEditing = false;
        notifyDataSetChanged();
    }
    class ViewModeClickListener implements View.OnLongClickListener{

        @Override
        public boolean onLongClick(View v) {
            ToastUtil.show(v.getContext(),"已经勘查结束不能排序和删除", Toast.LENGTH_SHORT);
            return true;
        }
    }
}
