package com.cstore.zhiyazhang.cstoremanagement.view.interface_view

import com.cstore.zhiyazhang.cstoremanagement.bean.ContractTypeBean
import com.cstore.zhiyazhang.cstoremanagement.presenter.contract.ContractTypeAdapter

/**
 * Created by zhiya.zhang
 * on 2017/6/12 15:08.
 */
interface ContractTypeView {
    fun toActivity(ctb: ContractTypeBean)
    fun showLoading()
    fun hideLoading()
    fun showView(adapter: ContractTypeAdapter)
    fun showFailedError(errorMessage: String)
}