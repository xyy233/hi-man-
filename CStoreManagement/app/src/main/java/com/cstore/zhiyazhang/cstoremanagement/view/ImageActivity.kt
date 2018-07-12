package com.cstore.zhiyazhang.cstoremanagement.view

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.bean.ContractBean
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyToast
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.synthetic.main.activity_image.*


/**
 * Created by zhiya.zhang
 * on 2017/6/21 14:55.
 */
class ImageActivity(override val layoutId: Int = R.layout.activity_image) : MyActivity() {
    /**
     * Glide回调监听
     */
    private val errorListener = object : RequestListener<String, GlideDrawable> {
        override fun onException(e: Exception?, model: String?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
            try {
                imageProgress.visibility = View.GONE
                MyToast.getShortToast(e!!.message!!)
            } catch (e: Exception) {

            }
            return false
        }

        override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
            imageProgress.visibility = View.GONE
            return false
        }
    }

    override fun initView() {
        val photoView = findViewById<PhotoView>(R.id.zoom_image)
        photoView.isEnabled = true
        val type = intent?.getIntExtra("type", 0)
        if (type == 0) {
            val cb = intent?.getSerializableExtra("cb") as ContractBean
            Glide.with(this@ImageActivity)
                    .load(cb.img_url)
                    .error(R.mipmap.load_error)
                    .crossFade()
                    .listener(errorListener)
                    .into(photoView)
        } else {
            val ba = intent?.getByteArrayExtra("bmp")
            Glide.with(this@ImageActivity)
                    .load(ba)
                    .crossFade()
                    .into(photoView)
        }
        //photoView.setOnPhotoTapListener { _, _, _ -> finishAfterTransition() }
        photoView.setOnClickListener { finishAfterTransition() }
    }

    override fun initClick() {
    }

    override fun initData() {
    }
}