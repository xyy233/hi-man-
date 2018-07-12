package com.cstore.zhiyazhang.cstoremanagement.presenter.attendance;

import com.cstore.zhiyazhang.cstoremanagement.R;
import com.cstore.zhiyazhang.cstoremanagement.bean.AttendanceBean;
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener;
import com.cstore.zhiyazhang.cstoremanagement.model.attendance.AttendanceInterface;
import com.cstore.zhiyazhang.cstoremanagement.model.attendance.AttendanceModel;
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar;
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity;
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication;
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler;
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil;
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by zhiya.zhang
 * on 2018/3/6 11:18.
 */

public class AttendancePresenter {
    private GenericView view;
    private AttendanceInterface model = new AttendanceModel();

    public AttendancePresenter(GenericView view) {
        this.view = view;
    }

    public void getAttendanceData() {
        if ((!PresenterUtil.INSTANCE.judgmentInternet(view)))
            return;

        assert (MyActivity) view.getData1() != null;
        final MyHandler handler = new MyHandler();
        handler.writeListener(new MyListener() {
            @Override
            public void listenerOther(@NotNull Object data) {

            }

            @Override
            public void listenerSuccess(@NotNull Object data) {
                List<AttendanceBean> abs = (List<AttendanceBean>) data;
                if (abs.size() == 1 && abs.get(0).getBusiDate().equals(CStoreCalendar.getCurrentDate(0))) {
                    view.showView(data);
                    view.requestSuccess(data);
                } else {
                    view.showView(data);
                }
                view.hideLoading();
                handler.cleanAll();
            }

            @Override
            public void listenerFailed(@NotNull String errorMessage) {
                view.showPrompt(errorMessage);
                view.errorDealWith();
                handler.cleanAll();
            }

        });
        if (view.getData2() == null) {
            view.showPrompt(MyApplication.instance().getApplicationContext().getString(R.string.system_error));
            view.errorDealWith();
        } else {
            String value = ((String) view.getData2());
            model.getAttendanceData(value, handler);
        }
    }

    public void getPaibanAttendance() {
        if ((!PresenterUtil.INSTANCE.judgmentInternet(view)))
            return;

        assert (MyActivity) view.getData1() != null;
        final MyHandler handler = new MyHandler();
        handler.writeListener(new MyListener() {
            @Override
            public void listenerOther(@NotNull Object data) {

            }

            @Override
            public void listenerSuccess(@NotNull Object data) {
                view.showView(data);
                view.hideLoading();
                handler.cleanAll();
            }

            @Override
            public void listenerFailed(@NotNull String errorMessage) {
                view.showPrompt(errorMessage);
                view.errorDealWith();
                handler.cleanAll();
            }

        });
        model.getPaibanAttendance((AttendanceBean) view.getData2(), handler);
    }

    public void changeAttendance(final String type) {
        if ((!PresenterUtil.INSTANCE.judgmentInternet(view)))
            return;

        assert (MyActivity) view.getData1() != null;
        final MyHandler handler = new MyHandler();
        handler.writeListener(new MyListener() {
            @Override
            public void listenerOther(@NotNull Object data) {

            }

            @Override
            public void listenerFailed(@NotNull String errorMessage) {
                view.showPrompt(errorMessage);
                view.hideLoading();
                handler.cleanAll();
            }

            @Override
            public void listenerSuccess(@NotNull Object data) {
                view.updateDone(type);
                view.hideLoading();
                handler.cleanAll();
            }
        });
        String uId = null;
        if (view.getData3() != null) {
            uId = String.valueOf(view.getData3());
        }
        model.changeAttendance(uId, type, handler);
    }

    public void changeDY() {
        if ((!PresenterUtil.INSTANCE.judgmentInternet(view)))
            return;

        assert (MyActivity) view.getData1() != null;
        final MyHandler handler = new MyHandler();
        handler.writeListener(new MyListener() {
            @Override
            public void listenerOther(@NotNull Object data) {

            }

            @Override
            public void listenerFailed(@NotNull String errorMessage) {
                view.showPrompt(errorMessage);
                view.hideLoading();
                handler.cleanAll();
            }

            @Override
            public void listenerSuccess(@NotNull Object data) {
                view.requestSuccess2(data);
                handler.cleanAll();
            }
        });
        model.changeDY(handler);
    }


    /**
     * 修改上班时数
     */
    public void ChangeDayHour(final AttendanceBean ab, String dyHour) {
        if ((!PresenterUtil.INSTANCE.judgmentInternet(view)))
            return;
        assert (MyActivity) view.getData1() != null;
        final MyHandler handler = new MyHandler();
        handler.writeListener(new MyListener() {
            @Override
            public void listenerOther(@NotNull Object data) {

            }

            @Override
            public void listenerFailed(@NotNull String errorMessage) {
                view.showPrompt(errorMessage);
                view.hideLoading();
                handler.cleanAll();
            }

            @Override
            public void listenerSuccess(@NotNull Object data) {
                view.requestSuccess2(data);
                view.hideLoading();
                view.showPrompt(MyApplication.instance().getApplicationContext().getString(R.string.saveDone));
                handler.cleanAll();
            }
        });
        model.ChangeDayHour(ab, dyHour, handler);
    }


}
