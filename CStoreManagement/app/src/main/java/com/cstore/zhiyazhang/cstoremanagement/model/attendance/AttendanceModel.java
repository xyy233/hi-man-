package com.cstore.zhiyazhang.cstoremanagement.model.attendance;

import android.os.Message;

import com.cstore.zhiyazhang.cstoremanagement.bean.AttendanceBean;
import com.cstore.zhiyazhang.cstoremanagement.bean.UserAttendanceBean;
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql;
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil;
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication;
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler;
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil;

import java.util.ArrayList;

/**
 * Created by zhiya.zhang
 * on 2018/3/6 11:19.
 */

public class AttendanceModel implements AttendanceInterface {

    @Override
    public void getAttendanceData(final String date, final MyHandler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                String ip = MyApplication.getIP();
                if (!SocketUtil.judgmentIP(ip, msg, handler))
                    return;
                String sql = MySql.INSTANCE.getAttendanceData(date);
                String sqlResult = SocketUtil.initSocket(ip, sql).inquire();
                ArrayList<AttendanceBean> result = new ArrayList<>();
                if (sqlResult.equals("[]") || sqlResult.equals("")) {
                    msg.obj = result;
                    msg.what = MyHandler.SUCCESS;
                    handler.sendMessage(msg);
                    return;
                }
                try {
                    result.addAll(GsonUtil.getAttendance(sqlResult));
                } catch (Exception e) {
                }
                if (result.size() != 0) {
                    for (AttendanceBean ab : result) {
                        UserAttendanceBean uab = getUserAttendanceData(ab.getUId(), date, ip, msg, handler);
                        if (uab == null)
                            return;
                        ab.setPbData(uab);
                    }
                    msg.obj = result;
                    msg.what = MyHandler.SUCCESS;
                    handler.sendMessage(msg);
                } else {
                    msg.obj = sqlResult;
                    msg.what = MyHandler.ERROR;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    /**
     * 获得某人详细考勤数据
     *
     * @return 如果是null最外层直接return
     */
    private UserAttendanceBean getUserAttendanceData(final String uId, final String date, final String ip, final Message msg, final MyHandler handler) {
        String sql = MySql.INSTANCE.getAttendanceUserData(date, uId);
        String sqlResult = SocketUtil.initSocket(ip, sql).inquire();
        ArrayList<UserAttendanceBean> uabData = new ArrayList<>();
        try {
            uabData.addAll(GsonUtil.getAttendanceUser(sqlResult));
        } catch (Exception e) {
        }
        if (uabData.size() > 0) {
            return uabData.get(0);
        } else {
            msg.obj = sqlResult;
            msg.what = MyHandler.ERROR;
            handler.sendMessage(msg);
            return null;
        }
    }

    @Override
    public void judgmentDYIsOnly(MyHandler handler) {

    }

    @Override
    public void changeDY(MyHandler handler) {

    }

    @Override
    public void changeAttendance(String uId, MyHandler handler) {

    }
}
