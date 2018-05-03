package com.cstore.zhiyazhang.cstoremanagement.view.panorama

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import kotlinx.android.synthetic.main.activity_panorama.*
import kotlinx.android.synthetic.main.toolbar_layout.*

/**
 * Created by zhiya.zhang
 * on 2018/4/25 16:10.
 */
class Panorama(override val layoutId: Int = R.layout.activity_panorama) : MyActivity() {
    private lateinit var option: VrPanoramaView.Options
    private lateinit var vrView: VrPanoramaView
    override fun initView() {
        my_toolbar.title = getString(R.string.panorama)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(my_toolbar)
        option = VrPanoramaView.Options()
        option.inputType = VrPanoramaView.Options.TYPE_MONO
        vrView = vr_view
        vrView.setInfoButtonEnabled(false)
        vrView.setStereoModeButtonEnabled(false)
        vrView.setEventListener(EventListener())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_panorama_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.open_file -> {
                if (ContextCompat.checkSelfPermission(this@Panorama, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@Panorama, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1);
                } else {
                    selectFromAlbum()//打开相册
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun selectFromAlbum() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val uri = data!!.data
            if (uri != null) {
                val bmp = MediaStore.Images.Media.getBitmap(this@Panorama.contentResolver, uri)
                vrView.loadImageFromBitmap(bmp, option)
            }
        }
    }

    override fun initClick() {
    }

    override fun initData() {
    }

    override fun onPause() {
        vrView.pauseRendering()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        vrView.resumeRendering()
    }

    override fun onDestroy() {
        super.onDestroy()
        vrView.shutdown()
    }

    private class EventListener : VrPanoramaEventListener() {
        override fun onLoadSuccess() {
            super.onLoadSuccess()
            //加载成功
        }

        override fun onLoadError(errorMessage: String?) {
            super.onLoadError(errorMessage)
            //加载失败
        }

        override fun onClick() {
            super.onClick()
            //点击
        }

        override fun onDisplayModeChanged(newDisplayMode: Int) {
            super.onDisplayModeChanged(newDisplayMode)
            //修改显示模式
        }
    }

}