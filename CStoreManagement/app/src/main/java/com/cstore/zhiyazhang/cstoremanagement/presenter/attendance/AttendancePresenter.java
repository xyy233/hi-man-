package com.cstore.zhiyazhang.cstoremanagement.presenter.attendance;

import com.cstore.zhiyazhang.cstoremanagement.model.MyListener;
import com.cstore.zhiyazhang.cstoremanagement.model.attendance.AttendanceInterface;
import com.cstore.zhiyazhang.cstoremanagement.model.attendance.AttendanceModel;
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity;
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler;
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil;
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView;

import org.jetbrains.annotations.NotNull;

/**
 * Created by zhiya.zhang
 * on 2018/3/6 11:18.
 */

public class AttendancePresenter {
    private static GenericView view;
    private static AttendanceInterface model = new AttendanceModel();

    public AttendancePresenter(GenericView view) {
        AttendancePresenter.view = view;
    }

    public static void getAttendanceData() {
        if ((!PresenterUtil.INSTANCE.judgmentInternet(view)))
            return;

        assert (MyActivity) view.getData1() != null;
        final MyHandler handler = new MyHandler().writeActivity((MyActivity) view.getData1());
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
        model.getAttendanceData((String) view.getData2(), handler);
    }
}
