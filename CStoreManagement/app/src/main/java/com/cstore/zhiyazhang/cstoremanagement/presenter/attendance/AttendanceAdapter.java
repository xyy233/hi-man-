package com.cstore.zhiyazhang.cstoremanagement.presenter.attendance;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cstore.zhiyazhang.cstoremanagement.R;
import com.cstore.zhiyazhang.cstoremanagement.bean.AttendanceBean;
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication;
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener;

import java.util.List;

/**
 * Created by zhiya.zhang
 * on 2018/3/8 12:32.
 */

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    public List<AttendanceBean> data;
    private ItemClickListener onClick;

    public AttendanceAdapter(List<AttendanceBean> data, ItemClickListener onClick) {
        this.data = data;
        this.onClick = onClick;
    }

    public void setData(List<AttendanceBean> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void updateData(AttendanceBean ab) {
        for (AttendanceBean nowAb : data) {
            if (nowAb.getUId().equals(ab.getUId())) {
                nowAb.setStatus(ab.getStatus());
                break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public AttendanceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance, parent, false));
    }

    @Override
    public void onBindViewHolder(AttendanceAdapter.ViewHolder holder, int position) {
        holder.bind(onClick, data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView attendanceCard;
        private TextView name, attendanceDate, userId, crb, ybdy, drdy, jr;
        private ImageView attendanceStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            attendanceCard = (CardView) itemView.findViewById(R.id.attendance_card);
            name = (TextView) itemView.findViewById(R.id.name);
            attendanceDate = (TextView) itemView.findViewById(R.id.attendance_date);
            userId = (TextView) itemView.findViewById(R.id.user_id);
            crb = (TextView) itemView.findViewById(R.id.crb);
            ybdy = (TextView) itemView.findViewById(R.id.ybdy);
            drdy = (TextView) itemView.findViewById(R.id.drdy);
            jr = (TextView) itemView.findViewById(R.id.jr);
            attendanceStatus = (ImageView) itemView.findViewById(R.id.attendance_status);
        }

        public void bind(final ItemClickListener onClick, final AttendanceBean ab) {
            Context context = MyApplication.instance().getApplicationContext();
            name.setText(ab.getUName());
            attendanceDate.setText(ab.getBusiDate());
            userId.setText(ab.getUId());
            crb.setText(String.valueOf(ab.getCrHour()));
            ybdy.setText(String.valueOf(ab.getDyHour()));
            drdy.setText(String.valueOf(ab.getDrHour()));
            jr.setText(String.valueOf(ab.getFHour()));
            switch (ab.getStatus()) {
                case 0:
                    attendanceStatus.setImageDrawable(context.getDrawable(R.drawable.no_attendance));
                    break;
                case 1:
                    attendanceStatus.setImageDrawable(context.getDrawable(R.drawable.is_attendance));
                    break;
                default:
                    attendanceStatus.setImageDrawable(context.getDrawable(R.drawable.no_attendance));
                    break;
            }
            attendanceCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick.onItemEdit(ab, 0);
                }
            });
        }

    }
}
