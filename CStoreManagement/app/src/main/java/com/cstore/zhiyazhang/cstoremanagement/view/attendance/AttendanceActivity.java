package com.cstore.zhiyazhang.cstoremanagement.view.attendance;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cstore.zhiyazhang.cstoremanagement.R;
import com.cstore.zhiyazhang.cstoremanagement.bean.AttendanceBean;
import com.cstore.zhiyazhang.cstoremanagement.presenter.attendance.AttendanceAdapter;
import com.cstore.zhiyazhang.cstoremanagement.presenter.attendance.AttendancePresenter;
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar;
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity;
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil;
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.MyLinearlayoutManager;
import com.zhiyazhang.mykotlinapplication.utils.recycler.ItemClickListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * Created by zhiya.zhang
 * on 2018/3/6 9:56.
 */
public class AttendanceActivity extends MyActivity implements View.OnClickListener {
    private AttendancePresenter presenter = new AttendancePresenter(this);
    private LinearLayout dateUtil, loading;
    private ProgressBar loadingPro;
    private TextView loadingText, dateYear, dateMonth, dateDay;
    private Button done, loadingRetry;
    private MyTimeUtil myTimeUtil = MyTimeUtil.INSTANCE;
    private AttendanceAdapter adapter;
    private Animation showAction, hideAction;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order;
    }

    @Override
    protected void initView() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        loading = (LinearLayout) findViewById(R.id.orderLoading);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.orderRecycler);
        done = (Button) findViewById(R.id.done);
        loadingPro = (ProgressBar) findViewById(R.id.orderpro);
        loadingText = (TextView) findViewById(R.id.orderprotext);
        loadingRetry = (Button) findViewById(R.id.orderretry);
        dateUtil = (LinearLayout) myToolbar.findViewById(R.id.date_util);
        dateYear = (TextView) dateUtil.findViewById(R.id.year);
        dateMonth = (TextView) dateUtil.findViewById(R.id.month);
        dateDay = (TextView) dateUtil.findViewById(R.id.day);

        showAction = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in);
        hideAction = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out);

        done.setText("全部审核");
        myToolbar.setTitle(getString(R.string.attendance));
        myToolbar.setNavigationIcon(R.drawable.ic_action_back);
        dateUtil.setVisibility(View.VISIBLE);
        myTimeUtil.setTextViewDate(dateUtil, CStoreCalendar.getCurrentDate(0));
        setSupportActionBar(myToolbar);
        recyclerView.setLayoutManager(new MyLinearlayoutManager(this, LinearLayout.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new AttendanceAdapter(new ArrayList<AttendanceBean>(), new ItemClickListener() {
            @Override
            public <T> void onItemEdit(T data, int position) {
                AttendanceBean ab = (AttendanceBean) data;
                Intent i = new Intent(AttendanceActivity.this, AttendanceItemActivity.class);
                i.putExtra("data", ab);
                startActivityForResult(i, 0);
            }

            @Override
            public <T> void onItemRemove(T data, int position) {
            }

            @Override
            public void onItemLongClick(@NotNull RecyclerView.ViewHolder view, int position) {
            }

            @Override
            public void onItemClick(@NotNull RecyclerView.ViewHolder view, int position) {
            }

        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter = new AttendancePresenter(this);
        if (data != null) {
            AttendanceBean ab = (AttendanceBean) data.getSerializableExtra("data");
            if (ab != null) {
                adapter.updateData(ab);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void initClick() {
        loadingRetry.setOnClickListener(this);
        dateUtil.setOnClickListener(this);
        loading.setOnClickListener(this);
        done.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.orderretry:
                loadingPro.setVisibility(View.VISIBLE);
                loadingText.setVisibility(View.VISIBLE);
                loadingRetry.setVisibility(View.GONE);
                presenter.getAttendanceData();
                break;
            case R.id.date_util:
                showDatePickDlg();
                break;
            case R.id.orderLoading:
                showPrompt(getString(R.string.wait_loading));
                break;
            case R.id.done:
                allAttendance();
                break;
        }
    }

    /**
     * 全部考勤
     */
    private void allAttendance() {
        if (Objects.equals(getData2(), CStoreCalendar.getCurrentDate(0))) {
            presenter.changeAttendance("0");
        } else {
            showPrompt("当前时间不可审核！");
            done.setVisibility(View.GONE);
            done.setAnimation(hideAction);
        }
    }

    /**
     * 显示、初始化日期选择器
     */
    private void showDatePickDlg() {
        Calendar calendar = myTimeUtil.getCalendarByString(CStoreCalendar.getCurrentDate(0));
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String m;
                String d;
                if (month + 1 < 10) {
                    m = "0" + (month + 1) + "月";
                } else {
                    m = String.valueOf(month + 1) + "月";
                }
                if (dayOfMonth < 10) {
                    d = "0" + dayOfMonth;
                } else {
                    d = String.valueOf(dayOfMonth);
                }
                String textYear = String.valueOf(year) + "年";
                dateYear.setText(textYear);
                dateMonth.setText(m);
                dateDay.setText(d);
                presenter.getAttendanceData();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    protected void initData() {
        presenter.getAttendanceData();
    }

    @Override
    public <T> void showView(T aData) {
        List<AttendanceBean> data = (ArrayList<AttendanceBean>) aData;
        adapter.setData(data);
        if (Objects.equals(getData2(), CStoreCalendar.getCurrentDate(0))) {
            done.setVisibility(View.VISIBLE);
            done.setAnimation(showAction);
        } else {
            done.setVisibility(View.GONE);
            done.setAnimation(hideAction);
        }
    }

    @Override
    public void errorDealWith() {
        loadingPro.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
        loadingRetry.setVisibility(View.VISIBLE);
    }

    @Override
    public <T> void updateDone(T uData) {
        presenter.getAttendanceData();
    }

    @Override
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public Object getData1() {
        return this;
    }

    /**
     * @return 选择的时间
     */
    @Nullable
    @Override
    public Object getData2() {
        return myTimeUtil.getTextViewDate(dateUtil);
    }

    /**
     * 检查是否有为单人大夜是否需要修改
     */
    @Override
    public <T> void requestSuccess(T rData) {
        if (adapter.data.size() > 0 && adapter.data.get(0).getDrdy()) {
            new AlertDialog.Builder(new android.view.ContextThemeWrapper(this, R.style.AlertDialogCustom))
                    .setTitle("提示")
                    .setMessage("只有一人上大夜班，是否将其班别改为单人大夜？")
                    .setPositiveButton("更改大夜班班别", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            presenter.changeDY();
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();

        }
    }

    /**
     * 修改单人大夜完成
     */
    @Override
    public <T> void requestSuccess2(T rData) {
        presenter.getAttendanceData();
    }
}
