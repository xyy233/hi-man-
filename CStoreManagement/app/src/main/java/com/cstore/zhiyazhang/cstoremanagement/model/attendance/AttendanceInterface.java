package com.cstore.zhiyazhang.cstoremanagement.model.attendance;

import com.cstore.zhiyazhang.cstoremanagement.bean.AttendanceBean;
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler;

/**
 * Created by zhiya.zhang
 * on 2018/3/6 11:20.
 */

public interface AttendanceInterface {
    /**
     * 修改为单人大夜
     */
    public void changeDY(MyHandler handler);

    /**
     * 得到考勤数据
     *
     * @param date 选择的日期
     */
    public void getAttendanceData(String date, MyHandler handler);

    /**
     * 考勤审核
     * @param uId 为空代表审核全部
     * @param type 0=全部  1==单个  2=取消
     */
    public void changeAttendance(String uId, String type, MyHandler handler);

    /**
     * 获得考勤排班数据
     * @param ab 获得人的数据
     */
    public void getPaibanAttendance(AttendanceBean ab, MyHandler handler);

}
