package com.gofirst.scenecollection.evidence.view.customview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gofirst.scenecollection.evidence.R;
import com.gofirst.scenecollection.evidence.sync.DepartmentReceiver;

import java.util.ArrayList;

/**
 * Created by tao
 *
 */
public class HorizNavView extends LinearLayout implements View.OnClickListener {


    //自定义View的根布局
    private View rootView;
    //RecyclerView
    private RecyclerView mRecyclerView;
    //RecyclerView宽度
    private int recyclerviewWidth;
    //上下文对象
    private Context context;


    //上一个
    private TextView tvPre;
    //下一个
    private TextView tvNext;


    private DepartmentReceiver departmentReceiver;
    private LocalBroadcastManager localBroadcastManager;




    //数据适配器
    private DataAdapter mAgeAdapter;
    //适配器数据源
    private ArrayList<String> datas = new ArrayList<String>();
    private ArrayList<String> datasId = new ArrayList<String>();

    public HorizNavView(Context context) {
        super(context);
    }

    public HorizNavView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.nav_layout, this, true);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        //tvPre = (TextView) rootView.findViewById(R.id.tv_pre);
        //tvNext = (TextView) rootView.findViewById(R.id.tv_next);
        //tvPre.setOnClickListener(this);
        //tvNext.setOnClickListener(this);
    }

    /**
     * 对外公布，设置数据
     * 前后各添加一个“ ”空数据以便让真正的数据源的第一个和最后一个可以中间显示
     *
     * @param datas
     */
    public void setDatas(ArrayList<String> datas,ArrayList<String> datasId) {
        //this.datas.add(" ");
        this.datas.clear();
        this.datas.addAll(datas);
        this.datasId.clear();
        this.datasId.addAll(datasId);
       // this.datas.add(" ");
    }


    /**
     * 对外公布，设置RecyclerView的宽度
     *
     * @param width
     */
    public void setRecyclerviewWidth(int width) {
        this.recyclerviewWidth = width;
    }


    /**
     * 对外公布
     * 初始化年龄滑动条
     */
    public void initAgeList() {

        localBroadcastManager = LocalBroadcastManager.getInstance(context);

        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);




        mRecyclerView.setLayoutManager(mLayoutManager);
        mAgeAdapter = new DataAdapter(context);
        mRecyclerView.setAdapter(mAgeAdapter);
        mRecyclerView.scrollToPosition(datas.size() - 1);

        /*mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // 效果在暂停时显示, 否则会导致重绘异常
                *//*if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mAgeAdapter.highlightItem(getMiddlePosition());
                    mRecyclerView.scrollToPosition(getScrollPosition());
                }*//*
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });*/

       // mAgeAdapter.highlightItem();
       // mAgeAdapter.highlightItem(getMiddlePosition());
    }

    /*private int getMiddlePosition() {
        return getScrollPosition() + (DataAdapter.ITEM_NUM / 2);
    }*/

    /*private int getScrollPosition() {
        return (int) ((double) mRecyclerView.computeHorizontalScrollOffset()
                / (double) mAgeAdapter.getItemStdWidth());
    }*/

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
           /* case R.id.tv_pre:
                //RecyclerView向后滚动一个数据
                toPre();
                break;
            case R.id.tv_next:
                //RecyclerView向前滚动一个数据
                toNext();
                break;*/
        }
    }

    /**
     * RecyclerView向前滚动一个数据
     */
    /*private void toNext() {
        if (getMiddlePosition() != (datas.size() - 2)) {
            mAgeAdapter.highlightItem(getMiddlePosition() + 1);
            mRecyclerView.scrollToPosition(getMiddlePosition() + 2);
        }

    }*/

    /**
     * RecyclerView向后滚动一个数据
     */
    /*private void toPre() {
        if (getMiddlePosition() >= 2) {
            mAgeAdapter.highlightItem(getMiddlePosition() - 1);
            mRecyclerView.scrollToPosition(getMiddlePosition() - 2);
        }

    }*/



    /**
     * 年龄的适配器
     * <p/>
     */
    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.AgeItemViewHolder> {

        public static final int ITEM_NUM = 3; // 每行拥有的Item数, 必须是奇数

        private int mHighlight = -1; // 高亮
        private Context context;

        public DataAdapter(Context context) {
            this.context = context;
        }


        @Override
        public AgeItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.view_age_item, parent, false);

            // 设置Item的宽度
            ViewGroup.LayoutParams lp = item.getLayoutParams();
            //lp.width = getItemStdWidth();

            return new AgeItemViewHolder(item);
        }

        @Override
        public void onBindViewHolder(AgeItemViewHolder holder, final int position) {
            Log.d("pos", position + "==");
            holder.getTextView().setText(datas.get(position));


            // 高亮显示
            if ((datas.size()-1)==position) {
                holder.getTextView().setTextSize(15);//20
                holder.getTextView().setTextColor(Color.BLUE);
            } else {
                holder.getTextView().setTextSize(15);
                holder.getTextView().setTextColor(Color.BLACK);
            }

            holder.getTextView().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("violetjack.testaction");
                    intent.putExtra("position",position);
                    intent.putExtra("orgId",datasId.get(position).toString());
                    intent.putExtra("orgIdName", datas.get(position).toString());
                    intent.putStringArrayListExtra("datas", datas);
                    intent.putStringArrayListExtra("datasId", datasId);
                    getContext().sendBroadcast(intent);
                }
            });
        }

        // 高亮中心, 更新前后位置
        public void highlightItem() {
            mHighlight = datas.size()-1;
            //int offset = ITEM_NUM / 2;
           // for (int i = position - offset; i <= position + offset; ++i)
                notifyItemChanged(mHighlight);
        }

        // 判断是否是高亮
        public boolean isSelected(int position) {
            return mHighlight == position;
        }


        @Override
        public int getItemCount() {
            return datas.size();
        }

        // 获取标准宽度
        public int getItemStdWidth() {
            return recyclerviewWidth / ITEM_NUM;
        }

        // ViewHolder
        public class AgeItemViewHolder extends RecyclerView.ViewHolder {

            private TextView mTextView;

            public AgeItemViewHolder(View itemView) {
                super(itemView);

                mTextView = (TextView) itemView.findViewById(R.id.tv);
                mTextView.setTag(this);
            }

            public TextView getTextView() {
                return mTextView;
            }
        }
    }
}
