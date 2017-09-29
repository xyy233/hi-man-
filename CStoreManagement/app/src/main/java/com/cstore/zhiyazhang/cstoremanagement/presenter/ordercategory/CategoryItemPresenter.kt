package com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory

import android.content.Context
import android.view.MotionEvent
import com.cstore.zhiyazhang.cstoremanagement.bean.CategoryItemBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.ordercategory.CategoryInterface
import com.cstore.zhiyazhang.cstoremanagement.model.ordercategory.CategoryItemModel
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyHandler
import com.cstore.zhiyazhang.cstoremanagement.utils.PresenterUtil
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.RecyclerOnTouch
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.CategoryItemView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView

/**
 * Created by zhiya.zhang
 * on 2017/7/27 11:43.
 */
class CategoryItemPresenter(private val gView: GenericView, private val cView: CategoryItemView, private val context: Context, private val activity: MyActivity) {
    private val mInterface: CategoryInterface = CategoryItemModel()

    fun getAllItem() {
        if (cView.nowId=="-1"){
            gView.errorDealWith()
            gView.hideLoading()
            return
        }
        if (!PresenterUtil.judgmentInternet(gView)) return
        mInterface.getAllItemByCategory(cView.nowId, cView.sort, MyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                data as ArrayList<CategoryItemBean>
                if (data.size == 0) {
                    gView.errorDealWith()
                    gView.hideLoading()
                } else {
                    val adapter = GetAdapter(data, "category")
                    gView.showView(adapter)
                    gView.hideLoading()
                }
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        }))
    }

    fun getAllShelf() {
        if (!PresenterUtil.judgmentInternet(gView)) return
        mInterface.getAllItemByShelf(cView.nowId, cView.sort, MyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                data as ArrayList<CategoryItemBean>
                if (data.size == 0) {
                    gView.errorDealWith()
                    gView.hideLoading()
                } else {
                    val adapter = GetAdapter(data, "shelf")
                    gView.showView(adapter)
                    gView.hideLoading()
                }
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        }))
    }

    fun getAllSearch(keywords: String) {
        if (!PresenterUtil.judgmentInternet(gView)) return
        mInterface.getUnitItemByKeywords(keywords, MyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                data as ArrayList<CategoryItemBean>
                if (data.size == 0) {
                    gView.errorDealWith()
                    gView.hideLoading()
                } else {
                    val adapter = GetAdapter(data, "search")
                    gView.showView(adapter)
                    gView.hideLoading()
                }
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        }))
    }


    fun getAllSelf() {
        if (!PresenterUtil.judgmentInternet(gView)) return
        mInterface.getAllItemBySelfId(cView.nowId,cView.sort, MyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                data as ArrayList<CategoryItemBean>
                if (data.size == 0) {
                    gView.errorDealWith()
                    gView.hideLoading()
                } else {
                    val adapter = GetAdapter(data, "self")
                    gView.showView(adapter)
                    gView.hideLoading()
                }
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        }))
    }

    fun getAllNOP() {
        if (!PresenterUtil.judgmentInternet(gView)) return
        mInterface.getNewItemById(cView.nowId,cView.sort, MyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                data as ArrayList<CategoryItemBean>
                if (data.size == 0) {
                    gView.errorDealWith()
                    gView.hideLoading()
                } else {
                    val adapter = GetAdapter(data, "nop")
                    gView.showView(adapter)
                    gView.hideLoading()
                }
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        }))
    }

    fun getAllFresh() {
        if (!PresenterUtil.judgmentInternet(gView)) return
        mInterface.getAllFreshItem(cView.nowId,cView.nowMidId,cView.sort, MyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                data as ArrayList<CategoryItemBean>
                if (data.size == 0) {
                    gView.errorDealWith()
                    gView.hideLoading()
                } else {
                    val adapter = GetAdapter(data, "fresh")
                    gView.showView(adapter)
                    gView.hideLoading()
                }
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        }))
    }

    fun updateAllCategory() {
        if (!PresenterUtil.judgmentInternet(gView)) return
        mInterface.updateAllCategory(cView.categoryList, MyHandler.writeActivity(activity).writeListener(object : MyListener {
            override fun listenerSuccess(data: Any) {
                cView.updateDone()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.hideLoading()
            }
        }))
    }

    private fun GetAdapter(data: ArrayList<CategoryItemBean>, isType: String): CategoryItemAdapter {
        return CategoryItemAdapter(data, context, object : RecyclerOnTouch {
            override fun <T> onClickImage(objects: T, position: Int) {}

            override fun <T> onTouchAddListener(objects: T, event: MotionEvent, position: Int) {
                when (objects) {
                    is CategoryItemBean -> cView.touchAdd(objects, event, position)
                }
            }

            override fun <T> onTouchLessListener(objects: T, event: MotionEvent, position: Int) {
                when (objects) {
                    is CategoryItemBean -> cView.touchLess(objects, event, position)
                }
            }
        }, isType)
    }
}