package com.cstore.zhiyazhang.cstoremanagement.presenter.ordercategory

import android.content.Context
import android.os.Handler
import android.view.MotionEvent
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.CategoryItemBean
import com.cstore.zhiyazhang.cstoremanagement.model.MyListener
import com.cstore.zhiyazhang.cstoremanagement.model.ordercategory.CategoryInterface
import com.cstore.zhiyazhang.cstoremanagement.model.ordercategory.CategoryItemModel
import com.cstore.zhiyazhang.cstoremanagement.utils.ConnectionDetector
import com.cstore.zhiyazhang.cstoremanagement.utils.recycler.RecyclerOnTouch
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.CategoryItemView
import com.cstore.zhiyazhang.cstoremanagement.view.interfaceview.GenericView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by zhiya.zhang
 * on 2017/7/27 11:43.
 */
class CategoryItemPresenter(private val gView: GenericView, private val cView: CategoryItemView, private val context: Context) {
    private val mInterface: CategoryInterface = CategoryItemModel()
    private val mHandler = Handler()

    fun getAllItem(adapter: CategoryItemAdapter?) {
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.showPrompt(context.getString(R.string.noInternet))
            gView.hideLoading()
            return
        }
        mInterface.getAllItemByCategory(cView.nowId, cView.sort, object : MyListener {

            override fun listenerSuccess(data: String)  {
                val categoryItems= Gson().fromJson<ArrayList<CategoryItemBean>>(data, object : TypeToken<ArrayList<CategoryItemBean>>() {}.type)
                if (categoryItems.size == 0) {
                    gView.errorDealWith()
                    gView.hideLoading()
                    return
                }
                mHandler.post(object : CategoryRunnable(categoryItems, adapter, "category") {})
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        })
    }

    fun getAllShelf(adapter: CategoryItemAdapter?) {
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.showPrompt(context.getString(R.string.noInternet))
            gView.hideLoading()
            return
        }
        mInterface.getAllItemByShelf(cView.nowId, cView.sort, object : MyListener {

            override fun listenerSuccess(data: String)  {
                val categoryItems= Gson().fromJson<ArrayList<CategoryItemBean>>(data, object : TypeToken<ArrayList<CategoryItemBean>>() {}.type)
                if (categoryItems.size == 0) {
                    gView.errorDealWith()
                    gView.hideLoading()
                    return
                }
                mHandler.post(object : CategoryRunnable(categoryItems, adapter, "shelf") {})
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        })
    }

    fun getAllSearch(adapter: CategoryItemAdapter?, keywords: String) {
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.showPrompt(context.getString(R.string.noInternet))
            gView.hideLoading()
            return
        }
        mInterface.getUnitItemByKeywords(keywords, object : MyListener {

            override fun listenerSuccess(data: String)  {
                val categoryItems= Gson().fromJson<ArrayList<CategoryItemBean>>(data, object : TypeToken<ArrayList<CategoryItemBean>>() {}.type)
                if (categoryItems.size == 0) {
                    gView.errorDealWith()
                    gView.hideLoading()
                    return
                }
                mHandler.post(object : CategoryRunnable(categoryItems, adapter, "search") {})
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        })
    }


    fun getAllSelf(adapter: CategoryItemAdapter?) {
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.showPrompt(context.getString(R.string.noInternet))
            gView.hideLoading()
            return
        }
        mInterface.getAllItemBySelfId(cView.nowId, cView.sort, object : MyListener {

            override fun listenerSuccess(data: String)  {
                val categoryItems= Gson().fromJson<ArrayList<CategoryItemBean>>(data, object : TypeToken<ArrayList<CategoryItemBean>>() {}.type)
                if (categoryItems.size == 0) {
                    gView.errorDealWith()
                    gView.hideLoading()
                    return
                }
                mHandler.post(object : CategoryRunnable(categoryItems, adapter, "self") {})
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        })
    }

    fun getAllNOP(adapter: CategoryItemAdapter?) {
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.showPrompt(context.getString(R.string.noInternet))
            gView.hideLoading()
            return
        }
        mInterface.getNewItemById(cView.nowId, cView.sort, object : MyListener {


            override fun listenerSuccess(data: String)  {
                val categoryItems= Gson().fromJson<ArrayList<CategoryItemBean>>(data, object : TypeToken<ArrayList<CategoryItemBean>>() {}.type)
                if (categoryItems.size == 0) {
                    gView.errorDealWith()
                    gView.hideLoading()
                    return
                }
                mHandler.post(object : CategoryRunnable(categoryItems, adapter, "nop") {})
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        })
    }

    fun getAllFresh(adapter:CategoryItemAdapter?){
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.showPrompt(context.getString(R.string.noInternet))
            gView.hideLoading()
            return
        }
        mInterface.getAllFreshItem(cView.nowId,cView.nowMidId,cView.sort, object : MyListener {

            override fun listenerSuccess(data: String)  {
                val categoryItems= Gson().fromJson<ArrayList<CategoryItemBean>>(data, object : TypeToken<ArrayList<CategoryItemBean>>() {}.type)
                if (categoryItems.size == 0) {
                    gView.errorDealWith()
                    gView.hideLoading()
                    return
                }
                mHandler.post(object : CategoryRunnable(categoryItems, adapter, "fresh") {})
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.errorDealWith()
                gView.hideLoading()
            }
        })
    }

    fun updateAllCategory() {
        gView.showLoading()
        if (!ConnectionDetector.getConnectionDetector().isOnline) {
            gView.showPrompt(context.getString(R.string.noInternet))
            gView.hideLoading()
            return
        }
        mInterface.updateAllCategory(cView.categoryList, object : MyListener {

            override fun listenerSuccess(data: String) {
                gView.hideLoading()
                cView.updateDone()
            }

            override fun listenerFailed(errorMessage: String) {
                gView.showPrompt(errorMessage)
                gView.hideLoading()
            }
        })
    }

    private open inner class CategoryRunnable internal constructor(internal val cbs: ArrayList<CategoryItemBean>, internal var adapter: CategoryItemAdapter?, internal val isType: String) : Runnable {
        override fun run() {
            if (adapter == null) {
                adapter = CategoryItemAdapter(cbs, context, object : RecyclerOnTouch {
                    override fun <T> onClickImage(objects: T, position: Int) {
                    }

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
                gView.showView(adapter)
                gView.hideLoading()
            }
        }

    }
}