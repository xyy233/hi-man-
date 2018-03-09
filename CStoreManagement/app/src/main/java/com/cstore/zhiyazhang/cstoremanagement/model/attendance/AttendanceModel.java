package com.cstore.zhiyazhang.cstoremanagement.model.attendance;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Message;

import com.cstore.zhiyazhang.cstoremanagement.R;
import com.cstore.zhiyazhang.cstoremanagement.bean.AttendanceBean;
import com.cstore.zhiyazhang.cstoremanagement.bean.PaibanAttendanceBean;
import com.cstore.zhiyazhang.cstoremanagement.bean.UserBBTypeBean;
import com.cstore.zhiyazhang.cstoremanagement.bean.UtilBean;
import com.cstore.zhiyazhang.cstoremanagement.sql.MySql;
import com.cstore.zhiyazhang.cstoremanagement.utils.CStoreCalendar;
import com.cstore.zhiyazhang.cstoremanagement.utils.GsonUtil;
import com.cstore.zhiyazhang.cstoremanagement.utils.MyApplication;
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler;
import com.cstore.zhiyazhang.cstoremanagement.utils.MyTimeUtil;
import com.cstore.zhiyazhang.cstoremanagement.utils.socket.SocketUtil;

import java.util.ArrayList;

import static com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.ERROR;
import static com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler.SUCCESS;

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
                String sql = MySql.getAttendanceData(date);
                String sqlResult = SocketUtil.initSocket(ip, sql).inquire();
                ArrayList<AttendanceBean> result = new ArrayList<>();
                if (sqlResult.equals("[]") || sqlResult.equals("")) {
                    msg.obj = result;
                    msg.what = SUCCESS;
                    handler.sendMessage(msg);
                    return;
                }
                try {
                    result.addAll(GsonUtil.getAttendance(sqlResult));
                } catch (Exception e) {
                }
                if (result.size() != 0) {
                    for (AttendanceBean ab : result) {
                        ab.setBusiDate(MyTimeUtil.deleteTime(ab.getBusiDate()));
                        ab.setWorkDate(MyTimeUtil.deleteTime(ab.getWorkDate()));
                        //添加班别数据
                        UserBBTypeBean ubb = getUserBBData(ab.getUId(), ab.getWorkDate(), ip, msg, handler);
                        if (ubb == null)
                            return;
                        ab.setBbType(ubb);
                    }
                    if (result.size() == 1 && date.equals(CStoreCalendar.getCurrentDate(0))) {
                        Boolean drdy = judgmentDYIsOnly(ip, msg, handler);
                        if (drdy == null) {
                            return;
                        }
                        result.get(0).setDrdy(drdy);
                    }
                    msg.obj = result;
                    msg.what = SUCCESS;
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
     * 判断是否是单人大夜
     */
    private Boolean judgmentDYIsOnly(String ip, Message msg, MyHandler handler) {
        String sql = MySql.judgmentDY();
        String sqlResult = SocketUtil.initSocket(ip, sql).inquire();
        ArrayList<UtilBean> result = new ArrayList<>();
        try {
            result.addAll(GsonUtil.getUtilBean(sqlResult));
        } catch (Exception e) {
        }
        if (result.size() > 0) {
            UtilBean ub = result.get(0);
            try {
                return ub.getValue().equals("1") && ub.getValue2().equals("1");
            } catch (Exception e) {
                return false;
            }
        } else {
            msg.obj = sqlResult;
            msg.what = ERROR;
            handler.sendMessage(msg);
            return null;
        }
    }

    /**
     * 获得某人详细班别数据
     * e
     *
     * @return 如果是null最外层直接return
     */
    private UserBBTypeBean getUserBBData(String uId, String date, String ip, Message msg, MyHandler handler) {
        String sql = MySql.getAttendanceBBData(date, uId);
        String sqlResult = SocketUtil.initSocket(ip, sql).inquire();
        ArrayList<UserBBTypeBean> ubbData = new ArrayList<>();
        try {
            ubbData.addAll(GsonUtil.getUserBBTypeBean(sqlResult));
        } catch (Exception e) {
        }
        if (ubbData.size() > 0) {
            return ubbData.get(0);
        } else {
            msg.obj = sqlResult;
            msg.what = SUCCESS;
            handler.sendMessage(msg);
            return null;
        }
    }

    /**
     * 获得某人详细考勤数据
     * e
     *
     * @return 如果是null最外层直接return
     */
    @Override
    public void getPaibanAttendance(final AttendanceBean ab, final MyHandler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                String ip = MyApplication.getIP();
                if (!SocketUtil.judgmentIP(ip, msg, handler))
                    return;

                String sql = MySql.getAttendanceUserData(ab.getWorkDate(), ab.getUId());
                String sqlResult = SocketUtil.initSocket(ip, sql).inquire();
                ArrayList<PaibanAttendanceBean> uabData = new ArrayList<>();
                try {
                    uabData.addAll(GsonUtil.getAttendanceUser(sqlResult));
                } catch (Exception e) {
                }
                if (uabData.size() > 0) {
                    //需要获得打卡图片
                    PaibanAttendanceBean result = uabData.get(0);

                    if (result.getOnJpg().equals("noImage")) {
                        Resources res = MyApplication.instance().getApplicationContext().getResources();
                        result.setOnBitmap(BitmapFactory.decodeResource(res, R.drawable.user_no_img));
                    } else {
                        result.setOnBitmap(SocketUtil.initSocket(ip).inquire(result.getOnJpg()));
                    }

                    if (result.getOffJpg().equals("noImage")) {
                        Resources res = MyApplication.instance().getApplicationContext().getResources();
                        result.setOffBitmap(BitmapFactory.decodeResource(res, R.drawable.user_no_img));
                    } else {
                        result.setOffBitmap(SocketUtil.initSocket(ip).inquire(result.getOffJpg()));
                    }

                    msg.obj = result;
                    msg.what = SUCCESS;
                    handler.sendMessage(msg);
                } else {
                    msg.obj = sqlResult;
                    msg.what = MyHandler.ERROR;
                    handler.sendMessage(msg);
                }

            }
        }).start();


    }

    @Override
    public void changeDY(final MyHandler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                String ip = MyApplication.getIP();
                if (!SocketUtil.judgmentIP(ip, msg, handler))
                    return;
                String sql = MySql.changeDY();
                String sqlResult = SocketUtil.initSocket(ip, sql).inquire();
                msg.obj = sqlResult;
                if (sqlResult.equals("[]") || sqlResult.equals("") || sqlResult.equals("0")) {
                    msg.what = SUCCESS;
                } else {
                    msg.what = ERROR;
                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public void changeAttendance(final String uId, final String type, final MyHandler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                String ip = MyApplication.getIP();
                if (!SocketUtil.judgmentIP(ip, msg, handler))
                    return;
                String sql;
                if (uId == null) {
                    //全部考勤
                    sql = MySql.changeAttendance(null, 1);
                } else {
                    if (type.equals("1")) {
                        //单个考勤
                        sql = MySql.changeAttendance(uId, 0);
                    } else {
                        sql = MySql.changeAttendance(uId, 2);
                    }
                }
                String sqlResult = SocketUtil.initSocket(ip, sql).inquire();
                try {
                    msg.obj = Integer.valueOf(sqlResult);
                    msg.what = SUCCESS;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    msg.obj = sqlResult;
                    msg.what = ERROR;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
}
