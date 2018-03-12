package com.cstore.zhiyazhang.cstoremanagement.view.attendance;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cstore.zhiyazhang.cstoremanagement.R;
import com.cstore.zhiyazhang.cstoremanagement.bean.AttendanceBean;
import com.cstore.zhiyazhang.cstoremanagement.bean.PaibanAttendanceBean;
import com.cstore.zhiyazhang.cstoremanagement.presenter.attendance.AttendancePresenter;
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar;
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity;

import org.jetbrains.annotations.Nullable;

/**
 * Created by zhiya.zhang
 * on 2018/3/9 9:54.
 */

public class AttendanceItemActivity extends MyActivity implements View.OnClickListener {

    private LinearLayout loading, dataDetailBox;
    private TextView name, attendanceDate, userId, crb, ybdy, drdy, jr, bbType, paibanHour, dayHour, fHour, paibanDateOn, paibanDateOff, checkInOn, checkInOff, loadingText, dialog_edit;
    private ImageView attendanceStatus, offImg, onImg;
    private Button cancelAttendance, doneAttendance, loadingRetry, dialog_cancel, dialog_save;
    private ProgressBar loadingProgress;

    private AttendanceBean ab;
    private Animation showAnimation, hideAnimation;
    private AttendancePresenter presenter = new AttendancePresenter(this);
    private AlertDialog.Builder builder;
    private AlertDialog saveDialog;
    private View dialogView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_attendance_item;
    }

    @Override
    protected void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        loading = (LinearLayout) findViewById(R.id.loading);
        name = (TextView) findViewById(R.id.name);
        attendanceDate = (TextView) findViewById(R.id.attendance_date);
        userId = (TextView) findViewById(R.id.user_id);
        crb = (TextView) findViewById(R.id.crb);
        ybdy = (TextView) findViewById(R.id.ybdy);
        drdy = (TextView) findViewById(R.id.drdy);
        jr = (TextView) findViewById(R.id.jr);
        bbType = (TextView) findViewById(R.id.bb_type);
        paibanHour = (TextView) findViewById(R.id.paiban_hour);
        dayHour = (TextView) findViewById(R.id.day_hour);
        fHour = (TextView) findViewById(R.id.f_hour);
        paibanDateOn = (TextView) findViewById(R.id.paiban_date_on);
        paibanDateOff = (TextView) findViewById(R.id.paiban_date_off);
        checkInOn = (TextView) findViewById(R.id.check_in_on);
        checkInOff = (TextView) findViewById(R.id.check_in_off);
        attendanceStatus = (ImageView) findViewById(R.id.attendance_status);
        offImg = (ImageView) findViewById(R.id.off_img);
        onImg = (ImageView) findViewById(R.id.on_img);
        cancelAttendance = (Button) findViewById(R.id.cancel_attendance);
        doneAttendance = (Button) findViewById(R.id.done_attendance);
        dataDetailBox = (LinearLayout) findViewById(R.id.data_detail_box);
        loadingText = (TextView) findViewById(R.id.loading_text);
        loadingRetry = (Button) findViewById(R.id.loading_retry);
        loadingProgress = (ProgressBar) findViewById(R.id.loading_progress);

        dialogView = View.inflate(this, R.layout.dialog_cashdaily, null);

        dialog_edit = (TextView) dialogView.findViewById(R.id.dialog_edit);
        dialog_save = (Button) dialogView.findViewById(R.id.dialog_save);
        dialog_cancel = (Button) dialogView.findViewById(R.id.dialog_cancel);

        builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(true);

        saveDialog = builder.create();

        showAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in);
        hideAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out);

        ab = (AttendanceBean) getIntent().getSerializableExtra("data");

        if (ab == null) {
            showPrompt(getString(R.string.system_error));
            finish();
            return;
        }
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setTitle(ab.getUName());
        setSupportActionBar(toolbar);
        setAttendanceData();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("data", ab);
        setResult(0, i);
        super.onBackPressed();

    }

    /**
     * 写入考勤数据到View中
     */
    private void setAttendanceData() {
        name.setText(ab.getUName());
        attendanceDate.setText(ab.getWorkDate());
        userId.setText(ab.getUId());
        crb.setText(String.valueOf(ab.getCrHour()));
        ybdy.setText(String.valueOf(ab.getDrHour()));
        drdy.setText(String.valueOf(ab.getDrHour()));
        jr.setText(String.valueOf(ab.getFHour()));
        if (ab.getStatus() == 0) {
            attendanceStatus.setImageDrawable(this.getDrawable(R.drawable.no_attendance));
        } else {
            attendanceStatus.setImageDrawable(this.getDrawable(R.drawable.is_attendance));
        }

        if (ab.getBbType() != null) {
            bbType.setText(ab.getBbType().getBbName());
            paibanHour.setText(String.valueOf(ab.getBbType().getPaibanHour()));
            dayHour.setText(String.valueOf(ab.getBbType().getDayHour()));
            fHour.setText(String.valueOf(ab.getBbType().getFHour()));
        }

        if (ab.getBusiDate().equals(CStoreCalendar.getCurrentDate(0))) {
            showBtn();
        } else {
            hideBtn();
        }
    }

    private void showBtn() {
        cancelAttendance.setVisibility(View.VISIBLE);
        doneAttendance.setVisibility(View.VISIBLE);
        cancelAttendance.setAnimation(showAnimation);
        doneAttendance.setAnimation(showAnimation);
    }

    private void hideBtn() {
        cancelAttendance.setVisibility(View.GONE);
        doneAttendance.setVisibility(View.GONE);
        cancelAttendance.setAnimation(hideAnimation);
        doneAttendance.setAnimation(hideAnimation);

    }

    @Override
    protected void initClick() {
        cancelAttendance.setOnClickListener(this);
        doneAttendance.setOnClickListener(this);
        dayHour.setClickable(true);
        dayHour.setOnClickListener(this);
        dialog_cancel.setOnClickListener(this);
        dialog_save.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_attendance:
                if (ab.getStatus() == 0) {
                    showPrompt("您当前未审核，无法取消审核！");
                } else {
                    presenter.changeAttendance("2");
                }
                break;
            case R.id.done_attendance:
                if (ab.getStatus() == 1) {
                    showPrompt("您已通过审核，无法再次提交！");
                } else {
                    presenter.changeAttendance("1");
                }

                break;
            case R.id.loading_retry:
                loadingText.setVisibility(View.VISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);
                loadingRetry.setVisibility(View.GONE);
                presenter.getPaibanAttendance();
                break;
            case R.id.loading:
                showPrompt(getString(R.string.wait_loading));
                break;
            case R.id.day_hour:
                saveDialog.show();
                break;
            case R.id.dialog_cancel:
                saveDialog.cancel();
                dialog_edit.setText("");
                break;
            case R.id.dialog_save:
                if (dialog_edit.getText().toString().equals("") || dialog_edit.getText() == null) {
                    showPrompt(getString(R.string.please_edit_value));
                    break;
                } else {
                    presenter.ChangeDayHour(ab, dialog_edit.getText().toString());
                }

        }
    }


    @Override
    protected void initData() {
        presenter.getPaibanAttendance();
    }

    @Override
    public <T> void showView(T aData) {
        PaibanAttendanceBean pab = (PaibanAttendanceBean) aData;
        paibanDateOn.setText(pab.getPbStart());
        paibanDateOff.setText(pab.getPbEnd());
        checkInOn.setText(pab.getWorkStart());
        checkInOff.setText(pab.getWorkEnd());
        if (pab.getOnBitmap() == null) {
            onImg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.user_no_img));
        } else {
            onImg.setImageBitmap(pab.getOnBitmap());
        }
        if (pab.getOffBitmap() == null) {
            offImg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.user_no_img));
        } else {
            offImg.setImageBitmap(pab.getOffBitmap());
        }
        dataDetailBox.setVisibility(View.VISIBLE);
        dataDetailBox.setAnimation(showAnimation);
    }

    @Override
    public void errorDealWith() {
        loadingText.setVisibility(View.GONE);
        loadingProgress.setVisibility(View.GONE);
        loadingRetry.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public Object getData1() {
        return this;
    }

    @Nullable
    @Override
    public Object getData2() {
        return ab;
    }

    @Override
    public void hideLoading() {
        loading.setVisibility(View.GONE);
        attendanceStatus.setVisibility(View.VISIBLE);
        attendanceStatus.setAnimation(showAnimation);
    }

    @Nullable
    @Override
    public Object getData3() {
        return ab.getUId();
    }

    @Override
    public <T> void updateDone(T uData) {
        String type = (String) uData;
        if (type.equals("1")) {
            attendanceStatus.setImageDrawable(getDrawable(R.drawable.is_attendance));
            ab.setStatus(1);
        } else if (type.equals("2")) {
            attendanceStatus.setImageDrawable(getDrawable(R.drawable.no_attendance));
            ab.setStatus(0);
        }
    }

    @Override
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
        attendanceStatus.setVisibility(View.GONE);
    }

    @Override
    public <T> void requestSuccess2(T rData) {
        dayHour.setText(dialog_edit.getText());
        saveDialog.cancel();
        dialog_edit.setText("");
    }

}
