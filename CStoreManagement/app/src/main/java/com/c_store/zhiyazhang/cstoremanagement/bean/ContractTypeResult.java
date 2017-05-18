package com.c_store.zhiyazhang.cstoremanagement.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhiya.zhang
 * on 2017/5/9 16:39.
 */

public class ContractTypeResult implements Serializable {
    //总量
    private int total;
    //详细信息
    private List<ContractTypeBean> detail;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ContractTypeBean> getDetail() {
        return detail;
    }

    public void setDetail(List<ContractTypeBean> detail) {
        this.detail = detail;
    }
}
