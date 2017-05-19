package com.c_store.zhiyazhang.cstoremanagement.presenter.contract;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.ContractTypeBean;
import com.c_store.zhiyazhang.cstoremanagement.utils.onclick.RecyclerOnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhiya.zhang
 * on 2017/5/10 8:56.
 */

public class ContractTypeAdapter extends RecyclerView.Adapter<ContractTypeAdapter.ViewHolder> {


    public List<ContractTypeBean> ctbs;
    private RecyclerOnItemClickListener onClick;

    void setOnItemClickLitener(RecyclerOnItemClickListener onClick) {
        this.onClick = onClick;
    }

    ContractTypeAdapter(List<ContractTypeBean> ctbs) {
        this.ctbs = ctbs;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.contract_type_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.type.setText(ctbs.get(position).getTypeName());
        holder.inventory.setText(Integer.toString(ctbs.get(position).getInventory()));
        holder.tonightCount.setText(Integer.toString(ctbs.get(position).getTonightCount()));
        holder.todayCount.setText(Integer.toString(ctbs.get(position).getTodayCount()));
        if (ctbs.get(position).isChangeColor()){
            holder.itemView.setBackgroundColor(Color.YELLOW);
        }else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
        if (onClick != null) {
            holder.contract_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });
            holder.contract_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onClick.onItemLongClick(holder.itemView, holder.getLayoutPosition());
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return ctbs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.contract_item)
        LinearLayout contract_item;
        @BindView(R.id.type)
        TextView type;
        @BindView(R.id.inventory)
        TextView inventory;
        @BindView(R.id.tonightCount)
        TextView tonightCount;
        @BindView(R.id.todayCount)
        TextView todayCount;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
