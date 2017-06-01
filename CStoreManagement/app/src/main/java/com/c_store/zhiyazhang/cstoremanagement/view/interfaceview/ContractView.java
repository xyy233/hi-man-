package com.c_store.zhiyazhang.cstoremanagement.view.interfaceview;

import android.view.MotionEvent;
import android.view.View;

import com.c_store.zhiyazhang.cstoremanagement.bean.ContractBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.ContractTypeBean;
import com.c_store.zhiyazhang.cstoremanagement.bean.UserBean;
import com.c_store.zhiyazhang.cstoremanagement.presenter.contract.ContractAdapter;

import java.util.List;

/**
 * Created by zhiya.zhang
 * on 2017/5/10 14:34.
 */

public interface ContractView {

    //得到是否是通过搜索查询
    boolean isSearch();

    //如果是通过搜索查询到的就在此获得搜索关键字
    String getSearchMessage();

    //得到查的是哪个类
    ContractTypeBean getContractType();

    //监听短按一下
    void toShortClick(View view, ContractBean cb);

    //显示加载中
    void showLoading();

    //隐藏加载中
    void hideLoading();

    //把数据展示出来
    void showView(ContractAdapter adapter);

    //显示错误
    void showFailedError(String errorMessage);

    //长按加
    void touchAdd(ContractBean cb, int positon, MotionEvent event);

    //长按减
    void touchLess(ContractBean cb, int positon, MotionEvent event);

    //排序
    String getSort();

    //得到当前页数
    int getPage();

    //写入当前页数
    void setPage(int page);

    //上拉加载
    void pullLoading(ContractAdapter adapter);

    //把没信息提示显示出来
    void showNoMessage();

    //得到要修改的数据
    List<ContractBean> getContractList();

    //更新成功
    void updateDone();

}
