package com.c_store.zhiyazhang.cstoremanagement.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhiya.zhang
 * on 2017/5/10 13:47.
 */

public class ContractResult implements Serializable {
    private int total;
    private int page;
    private List<ContractBean> detail;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List getDetail() {
        return detail;
    }

    public void setDetail(List detail) {
        this.detail = detail;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
