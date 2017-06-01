package com.c_store.zhiyazhang.cstoremanagement.presenter.scrap;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.ScrapContractBean;
import com.c_store.zhiyazhang.cstoremanagement.utils.MyApplication;
import com.c_store.zhiyazhang.cstoremanagement.utils.onclick.RecyclerAddLessClickListener;
import com.c_store.zhiyazhang.cstoremanagement.utils.recycler.onMoveAndSwipedListener;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhiya.zhang
 * on 2017/5/24 10:16.
 */

public class BarCodeScrapAdapter extends RecyclerView.Adapter<BarCodeScrapAdapter.ViewHolder> implements onMoveAndSwipedListener {

    private ArrayList<ScrapContractBean> scbs;
    private RecyclerAddLessClickListener onClick;
    private int type; //0=有条形码报废，数量强制为1;  1=无条形码报废，数量强制为0;

    public BarCodeScrapAdapter() {
        this.scbs = new ArrayList<>();
    }

    public BarCodeScrapAdapter(ArrayList<ScrapContractBean> scbs, int type) {
        if (scbs == null || scbs.size() == 0) {
            this.scbs = new ArrayList<>();
        } else {
            this.scbs = scbs;
        }
        this.type = type;
    }

    public void setOnItemClickLitener(RecyclerAddLessClickListener onClick) {
        this.onClick = onClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.scrap_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

/*        Animation animation = AnimationUtils.loadAnimation(MyApplication.getContext(), R.anim.anim_recycler_item_show);
        holder.view.startAnimation(animation);

        AlphaAnimation aa1 = new AlphaAnimation(1.0f, 0.1f);
        aa1.setDuration(400);
        holder.layout.startAnimation(aa1);

        AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
        aa.setDuration(400);*/

        holder.contractName.setText(scbs.get(position).getScrapName());
        //holder.scrapCount.setText(Integer.toString(scbs.get(position).getNowMrkCount() == 0 ? 1 : scbs.get(position).getNowMrkCount()));
        if (type==0){
            if (scbs.get(position).getNowMrkCount() == 0) {
                scbs.get(position).setNowMrkCount(1);
            }
        }

        holder.scrapCount.setText(Integer.toString(scbs.get(position).getNowMrkCount()));

        //已修改发送给服务器的不允许再编辑
        switch (scbs.get(position).getIsScrap()) {
            case 0:
                holder.add.setVisibility(View.VISIBLE);
                holder.less.setVisibility(View.VISIBLE);
                holder.add.setEnabled(true);
                holder.less.setEnabled(true);
                holder.layout.setCardBackgroundColor(ContextCompat.getColor(MyApplication.getContext(), R.color.cstore_white));
                break;
            case 1:
                holder.add.setVisibility(View.GONE);
                holder.less.setVisibility(View.GONE);
                holder.add.setEnabled(false);
                holder.less.setEnabled(false);
                holder.layout.setCardBackgroundColor(ContextCompat.getColor(MyApplication.getContext(), R.color.yingshu));
                break;
        }

        //holder.layout.startAnimation(aa);

        if (onClick != null) {
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick.addItemOnClick(holder.scrapCount, holder.getLayoutPosition());//在这里用itemclick代表+
                }
            });
            holder.less.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick.lessItemOnClick(holder.scrapCount, holder.getLayoutPosition());//在这里用itemlongclick代表-
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return scbs != null ? scbs.size() : 0;
    }

    //添加新数据
    public void addItem(ScrapContractBean newDatas) {
        scbs.add(newDatas);
    }

    //删除数据
    public void removeItem(int position) {
        scbs.remove(position);
        notifyDataSetChanged();
    }


    //得到list中的数据
    public ArrayList<ScrapContractBean> getList() {
        return scbs;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(scbs, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        scbs.remove(position);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.scrap_contract_name)
        TextView contractName;
        @BindView(R.id.less)
        ImageButton less;
        @BindView(R.id.edit_scrap_count)
        TextView scrapCount;
        @BindView(R.id.add)
        ImageButton add;
        @BindView(R.id.item_layout)
        CardView layout;
        View view;

        ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
