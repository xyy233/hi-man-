package com.c_store.zhiyazhang.cstoremanagement.presenter.contract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.ContractBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.ContractResult;
import com.c_store.zhiyazhang.cstoremanagement.utils.onclick.RecyclerOnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhiya.zhang
 * on 2017/5/10 14:58.
 */

public class ContractAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ContractResult cr;

    private List<ContractBean> cbs;
    private RecyclerOnItemClickListener onClick;
    private Context context;
    private boolean isEdit;
    private static final int TYPE_ITEM = 0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //顶部FootView
    private int load_more_status = 0;//上拉加载更多状态-默认为0
    static final int PULLUP_LOAD_MORE = 0;//上拉加载更多
    public static final int LOADING_MORE = 1;//正在加载中


    void setOnItemClickLitener(RecyclerOnItemClickListener recyclerOnItemClickListener) {
        this.onClick = recyclerOnItemClickListener;
    }

    public void setIsEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    ContractAdapter(ContractResult cr, Context context) {
        this.cr = cr;
        this.cbs = cr.getDetail();
        this.context = context;
    }

    public List<ContractBean> getCbs() {
        return cbs;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.commodify_item, parent, false));
        } else if (viewType == TYPE_FOOTER) {
            return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.footview, parent, false));
        }
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).cid.setText(cbs.get(position).getcId());
            ((ViewHolder) holder).cname.setText(cbs.get(position).getcName());
            ((ViewHolder) holder).cin.setText(Integer.toString(cbs.get(position).getInventory()));
            ((ViewHolder) holder).cnc.setText(Integer.toString(cbs.get(position).getTonightCount()));
            ((ViewHolder) holder).cprice.setText(Double.toString(cbs.get(position).getcPrice()));
            ((ViewHolder) holder).editCdc.setText(Integer.toString(cbs.get(position).getTodayCount()));
            if (cbs.get(position).getTodayStore() != 0) {
                ((ViewHolder) holder).card.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
            } else {
                ((ViewHolder) holder).card.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_color));
            }
            Glide.with(context).load(cbs.get(position).getImg_url()).placeholder(R.mipmap.loading).error(R.mipmap.load_error).crossFade().centerCrop().into(((ViewHolder) holder).img);
            if (onClick != null) {
                //不知道为什么明明设置了限制，但是在编辑模式下点击card还是会执行监听事件，只能通过强行enabled强行禁止掉
                if (isEdit) {
                    ((ViewHolder) holder).add.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            onClick.onTouchAddListener(cbs.get(holder.getLayoutPosition()), holder.getLayoutPosition(), event);
                            return true;
                        }
                    });
                    ((ViewHolder) holder).less.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            onClick.onTouchLessListener(cbs.get(holder.getLayoutPosition()), holder.getLayoutPosition(), event);
                            return true;
                        }
                    });
                    ((ViewHolder) holder).card.setEnabled(false);
                } else {
                    ((ViewHolder) holder).card.setEnabled(true);
                    ((ViewHolder) holder).card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onClick.onItemClick(holder.itemView, holder.getLayoutPosition());
                        }
                    });
                }
            }
            /*if (isEdit) {
                ((ViewHolder) holder).img.setVisibility(View.GONE);
            } else {
                ((ViewHolder) holder).img.setVisibility(View.VISIBLE);
            }*/
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder fHolder = (FooterViewHolder) holder;
            switch (load_more_status) {
                case PULLUP_LOAD_MORE:
                    fHolder.footLoading.setText(context.getResources().getString(R.string.loadingUp));
                    break;
                case LOADING_MORE:
                    fHolder.footLoading.setText(context.getResources().getString(R.string.loading));
                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            if (cr.getTotal() > 10 && cbs.size() < cr.getTotal()) {
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;
            }
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        //当前页不足10且已有的数据少于总数据才会添加底部加载
        if (cr.getTotal() > 10 && cbs.size() < cr.getTotal()) {
            return cbs.size() + 1;
        }
        return cbs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.my_commodify)
        public CardView card;
        @BindView(R.id.commodify_img)
        ImageView img;
        @BindView(R.id.cid)
        TextView cid;
        @BindView(R.id.cname)
        TextView cname;
        @BindView(R.id.cprice)
        TextView cprice;
        @BindView(R.id.cin)
        TextView cin;
        @BindView(R.id.cnc)
        TextView cnc;
        @BindView(R.id.add)
        public ImageButton add;
        @BindView(R.id.less)
        public ImageButton less;
        @BindView(R.id.edit_cdc)
        public TextView editCdc;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    //底部加载
    class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.foot)
        LinearLayout foot;
        @BindView(R.id.footLoading)
        TextView footLoading;
        @BindView(R.id.footProgress)
        ProgressBar footProgress;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    void addItem(List<ContractBean> newDatas) {
        cbs.addAll(newDatas);
        notifyDataSetChanged();
    }

    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     */
    public void changeMoreStatus(int statud) {
        load_more_status = statud;
        notifyDataSetChanged();
    }
}
