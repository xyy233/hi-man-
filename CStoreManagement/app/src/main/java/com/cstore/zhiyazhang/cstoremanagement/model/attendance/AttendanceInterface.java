package com.cstore.zhiyazhang.cstoremanagement.model.attendance;

import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler;

/**
 * Created by zhiya.zhang
 * on 2018/3/6 11:20.
 */

public interface AttendanceInterface {

    /**
     * 检查大夜班是否只有一个人
     */
    public void judgmentDYIsOnly(MyHandler handler);

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
     */
    public void changeAttendance(String uId, MyHandler handler);

}
