package com.gofirst.scenecollection.evidence.view.easytagdragview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.utils.ToastUtil;
import com.gofirst.scenecollection.evidence.view.easytagdragview.adapter.AbsTipAdapter;
import com.gofirst.scenecollection.evidence.view.easytagdragview.adapter.AddTipAdapter;
import com.gofirst.scenecollection.evidence.view.easytagdragview.adapter.DragTipAdapter;
import com.gofirst.scenecollection.evidence.view.easytagdragview.bean.SimpleTitleTip;
import com.gofirst.scenecollection.evidence.view.easytagdragview.bean.Tip;
import com.gofirst.scenecollection.evidence.view.easytagdragview.widget.DragDropGirdView;
import com.gofirst.scenecollection.evidence.view.easytagdragview.widget.TipItemView;

import java.util.ArrayList;
import java.util.List;


public class EasyTipDragView extends RelativeLayout implements AbsTipAdapter.DragDropListener, TipItemView.OnDeleteClickListener{
    private DragDropGirdView dragDropGirdView;
    private GridView addGridView;
    private AddTipAdapter addTipAdapter;
    private DragTipAdapter dragTipAdapter;
    private OnDataChangeResultCallback dataResultCallback;
    private OnCompleteCallback completeCallback;
    private ArrayList<Tip> lists;
    View view;
    private boolean isOpen = false;

    public EasyTipDragView(Context context) {
        super(context);
        initView();
    }

    public EasyTipDragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public EasyTipDragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EasyTipDragView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    public void setViewMode() {
        if (dragTipAdapter != null)
            dragTipAdapter.setViewMode(true);
        if (view != null) {
            view.findViewById(R.id.add_area).setVisibility(INVISIBLE);
            view.findViewById(R.id.text).setVisibility(INVISIBLE);
        }
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }
       /* close();*/
        dragTipAdapter = new DragTipAdapter(getContext(), this, this);
        dragTipAdapter.setFirtDragStartCallback(new DragTipAdapter.OnFirstDragStartCallback() {
            @Override
            public void firstDragStartCallback() {
                //编辑模式回调
                if (completeCallback != null)
                    completeCallback.onStartDrag();
            }
        });
        addTipAdapter = new AddTipAdapter();
        //加载view
        view = LayoutInflater.from(getContext()).inflate(R.layout.view_easytagdrag, this);
        dragDropGirdView = (DragDropGirdView) view.findViewById(R.id.tagdrag_view);
        dragDropGirdView.getDragDropController().addOnDragDropListener(dragTipAdapter);

        dragDropGirdView.setDragShadowOverlay((ImageView) view.findViewById(R.id.tile_drag_shadow_overlay));
        dragDropGirdView.setAdapter(dragTipAdapter);
        addGridView = (GridView) view.findViewById(R.id.add_gridview);
        addGridView.setAdapter(addTipAdapter);
        addGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dragTipAdapter.getData().add(addTipAdapter.getData().get(position));
                dragTipAdapter.refreshData();
                addTipAdapter.getData().remove(position);
                addTipAdapter.refreshData();
            }
        });
    }

    @Override
    public DragDropGirdView getDragDropGirdView() {
        return dragDropGirdView;
    }

    @Override
    public void onDataSetChangedForResult(ArrayList<Tip> lists) {
        this.lists = lists;
        if (dataResultCallback != null) {
            dataResultCallback.onDataChangeResult(lists);
        }
    }

    @Override
    public void onDeleteClick(Tip entity, int position, View view) {
        SimpleTitleTip simpleTitleTip = (SimpleTitleTip) entity;
        String id = simpleTitleTip.getFieldId();
        if (TextUtils.equals(id,"SCENE_PHOTO") || TextUtils.equals(id,"SCENE_INVESTIGATION_EXT")
                || TextUtils.equals(id,"SCENE_LAW_CASE_EXT") || TextUtils.equals(id,"SCENE_PICTURE$1082"))
        {
            ToastUtil.show(view.getContext(),"此项为必须项不能删除", Toast.LENGTH_SHORT);
            return;
        }
        addTipAdapter.getData().add(entity);
        addTipAdapter.refreshData();
        dragTipAdapter.getData().remove(position);
        dragTipAdapter.refreshData();
    }

    public void setDragData(List<Tip> tips,int defaultPosition) {
        lists = new ArrayList<>(tips);
        dragTipAdapter.setData(tips,defaultPosition);
    }

    public void setAddData(List<Tip> tips) {
        addTipAdapter.setData(tips);
    }

    public void setDataResultCallback(OnDataChangeResultCallback dataResultCallback) {
        this.dataResultCallback = dataResultCallback;
    }

    public void setOnCompleteCallback(OnCompleteCallback callback) {
        this.completeCallback = callback;
    }

    public void setSelectedListener(TipItemView.OnSelectedListener selectedListener) {
        dragTipAdapter.setItemSelectedListener(selectedListener);
    }

  /*  public void close() {
        setVisibility(View.GONE);
        isOpen = false;
    }*/

   /* public void open() {
        setVisibility(View.VISIBLE);
        isOpen = true;
    }*/

    //每次由于拖动排序,添加或者删除item时会回调
    public interface OnDataChangeResultCallback {
        void onDataChangeResult(ArrayList<Tip> tips);
    }

    //在最后点击"完成"关闭EasyTipDragView时回调
    public interface OnCompleteCallback {
        void onComplete(ArrayList<Tip> tips);
        void onStartDrag();
    }

    public boolean isOpen() {
        return isOpen;
    }

    //点击返回键监听
    public boolean onKeyBackDown() {
        //如果处于编辑模式，则取消编辑模式
        if (dragTipAdapter.isEditing()) {
            dragTipAdapter.cancelEditingStatus();
            return true;
        } else {
            //关闭该view
           /* close();*/
            return false;
        }
    }

    public void saveClose(){
        dragTipAdapter.cancelEditingStatus();
        if (completeCallback != null) {
            completeCallback.onComplete(lists);
        }
       /* close();*/
    }

    public void setFinishEditing(){
        dragTipAdapter.setFinishEditing();
    }
}
