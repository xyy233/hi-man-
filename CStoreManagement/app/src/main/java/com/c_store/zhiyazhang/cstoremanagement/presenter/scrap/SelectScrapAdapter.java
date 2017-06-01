package com.c_store.zhiyazhang.cstoremanagement.presenter.scrap;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.c_store.zhiyazhang.cstoremanagement.R;
import com.c_store.zhiyazhang.cstoremanagement.bean.ScrapContractBean;
import com.c_store.zhiyazhang.cstoremanagement.utils.MyApplication;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhiya.zhang
 * on 2017/5/27 10:47.
 */

public class SelectScrapAdapter extends RecyclerView.Adapter<SelectScrapAdapter.ViewHoler> {

    private ArrayList<ScrapContractBean> scbs;

    public SelectScrapAdapter(ArrayList<ScrapContractBean> scbs) {
        if (scbs == null || scbs.size() == 0) {
            this.scbs = new ArrayList<>();
        } else {
            this.scbs = scbs;
        }
    }

    @Override
    public ViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHoler(LayoutInflater.from(parent.getContext()).inflate(R.layout.scrap_select_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHoler holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(MyApplication.getContext(), R.anim.anim_recycler_item_show);
        holder.v.startAnimation(animation);

        AlphaAnimation aa1 = new AlphaAnimation(1.0f, 0.1f);
        aa1.setDuration(400);
        holder.v.startAnimation(aa1);

        holder.scrapName.setText(scbs.get(position).getScrapName());
        holder.mrkCount.setText(Integer.toString(scbs.get(position).getNowMrkCount()));
        switch (scbs.get(position).getIsScrap()) {
            case 0:
                holder.isScrapIcon.setVisibility(View.GONE);
                break;
            case 1:
                holder.isScrapIcon.setVisibility(View.VISIBLE);
                break;
        }

        AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
        aa.setDuration(400);
        holder.v.startAnimation(aa);
    }

    @Override
    public int getItemCount() {
        return scbs != null ? scbs.size() : 0;
    }

    class ViewHoler extends RecyclerView.ViewHolder {

        @BindView(R.id.scrap_name)
        TextView scrapName;
        @BindView(R.id.mrk_count)
        TextView mrkCount;
        @BindView(R.id.isScrapIcon)
        ImageView isScrapIcon;
        @BindView(R.id.v)
        LinearLayout v;

        ViewHoler(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
