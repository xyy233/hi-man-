package com.c_store.zhiyazhang.cstoremanagement.presenter.scrap;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.CategoryBean;
import com.c_store.zhiyazhang.cstoremanagement.utils.onclick.RecyclerOnItemClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhiya.zhang
 * on 2017/5/27 15:36.
 * RecyclerView的adapter
 */

public class NoCodeAdapter extends RecyclerView.Adapter<NoCodeAdapter.ViewHolder> {
    ArrayList<CategoryBean> cbs;
    RecyclerOnItemClickListener onClick;

    public NoCodeAdapter(ArrayList<CategoryBean> cbs, RecyclerOnItemClickListener onClick) {
        this.cbs = cbs;
        this.onClick = onClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_onlybtn, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.button.setText(cbs.get(position).getCategoryname());

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(holder.itemView, holder.getLayoutPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cbs != null ? cbs.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.categoryBtn)
        Button button;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
