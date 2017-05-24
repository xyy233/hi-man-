package com.c_store.zhiyazhang.cstoremanagement.bean;

import java.util.List;

/**
 * Created by zhiya.zhang
 * on 2017/5/22 16:46.
 */

public class UserResult {
    private int total;
    private List<UserBean> users;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<UserBean> getUsers() {
        return users;
    }

    public void setUsers(List<UserBean> users) {
        this.users = users;
    }
}
