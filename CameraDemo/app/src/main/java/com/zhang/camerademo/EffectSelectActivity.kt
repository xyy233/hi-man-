/*
 *
 * EffectSelectActivity.java
 * 
 * Created by Wuwang on 2017/2/25
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.zhang.camerademo

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.JsonReader
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast

import com.aiyaapp.camera.sdk.AiyaEffects
import com.aiyaapp.camera.sdk.base.ISdkManager
import com.aiyaapp.camera.sdk.base.Log
import com.zhang.camerademo.camera.LogUtils
import com.zhang.camerademo.camera.MenuAdapter
import com.zhang.camerademo.camera.MenuBean
import com.zhang.camerademo.util.ClickUtils

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.util.ArrayList

/**
 * Description:
 */
open class EffectSelectActivity : AppCompatActivity() {

    private var mStickerData: ArrayList<MenuBean>? = null
    private var mMenuView: RecyclerView? = null
    private var mStickerAdapter: MenuAdapter? = null
    private var mBtnStick: TextView? = null
    private var mBtnBeauty: TextView? = null
    private var mBeautyFlag = 0


    protected val sd: String
        get() = Environment.getExternalStorageDirectory().absolutePath

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    protected fun initData() {
        mMenuView = findViewById<View>(R.id.mMenuView) as RecyclerView
        mBtnStick = findViewById<View>(R.id.mLeft) as TextView
        mBtnBeauty = findViewById<View>(R.id.mRight) as TextView
        mBtnStick!!.isSelected = true
        refreshRightBtn()

        mStickerData = ArrayList()
        mMenuView!!.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mStickerAdapter = MenuAdapter(this, mStickerData)
        mStickerAdapter!!.setOnClickListener(object : ClickUtils.OnClickListener() {
            override fun onClick(v: View, type: Int, pos: Int, child: Int) {
                val m = mStickerData!![pos]
                val name = m.name
                if (name == "原始") {
                    AiyaEffects.getInstance().setEffect(null)
                    mStickerAdapter!!.checkPos = pos
                    v.isSelected = true
                } else if (name == "本地") {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "file/*"
                    startActivityForResult(intent, 101)
                } else {
                    AiyaEffects.getInstance().setEffect("assets/modelsticker/" + m.path)
                    mStickerAdapter!!.checkPos = pos
                    v.isSelected = true
                }
                mStickerAdapter!!.notifyDataSetChanged()
            }
        })
        mMenuView!!.adapter = mStickerAdapter
        initEffectMenu("modelsticker/stickers.json")
    }

    //刷新美颜按钮
    fun refreshRightBtn() {
        if (mBeautyFlag == 0) {
            mBtnBeauty!!.text = "美颜关"
            mBtnBeauty!!.isSelected = false
        } else {
            mBtnBeauty!!.text = "美颜" + mBeautyFlag
            mBtnBeauty!!.isSelected = true
        }
    }

    //初始化特效按钮菜单
    protected fun initEffectMenu(menuPath: String) {
        try {
            Log.e("解析菜单->" + menuPath)
            val r = JsonReader(InputStreamReader(assets.open(menuPath)))
            r.beginArray()
            while (r.hasNext()) {
                val menu = MenuBean()
                r.beginObject()
                var name: String
                while (r.hasNext()) {
                    name = r.nextName()
                    if (name == "name") {
                        menu.name = r.nextString()
                    } else if (name == "path") {
                        menu.path = r.nextString()
                    }
                }
                mStickerData!!.add(menu)
                Log.e("增加菜单->" + menu.name)
                r.endObject()
            }
            r.endArray()
            val bean = MenuBean()
            bean.name = "本地"
            bean.path = ""
            mStickerData!!.add(bean)
            mStickerAdapter!!.notifyDataSetChanged()
        } catch (e: IOException) {
            e.printStackTrace()
            mStickerAdapter!!.notifyDataSetChanged()
        }

    }

    //View的点击事件处理
    open fun onClick(view: View) {
        when (view.id) {
            R.id.mLeft -> {
                mMenuView!!.visibility = if (mMenuView!!.visibility == View.VISIBLE)
                    View.GONE
                else
                    View.VISIBLE
                view.isSelected = mMenuView!!.visibility == View.VISIBLE
            }
            R.id.mRight -> {
                mBeautyFlag = if (++mBeautyFlag >= 7) 0 else mBeautyFlag
                AiyaEffects.getInstance().set(ISdkManager.SET_BEAUTY_LEVEL, mBeautyFlag)
                refreshRightBtn()
            }
        }
    }


    fun saveBitmapAsync(bytes: ByteArray, width: Int, height: Int) {
        Thread(Runnable {
            LogUtils.e("has take pic")
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val b = ByteBuffer.wrap(bytes)
            bitmap.copyPixelsFromBuffer(b)
            saveBitmap(bitmap)
            bitmap.recycle()
        }).start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                android.util.Log.e("wuwang", "data:" + getRealFilePath(data.data)!!)
                val dataPath = getRealFilePath(data.data)
                if (dataPath != null && dataPath.endsWith(".json")) {
                    AiyaEffects.getInstance().setEffect(dataPath)
                }
            }
        }
    }

    fun getRealFilePath(uri: Uri?): String? {
        if (null == uri) return null
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null)
            data = uri.path
        else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = contentResolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }

    //图片保存
    fun saveBitmap(b: Bitmap) {
        val path = sd + "/DCIM/Camera/"
        val folder = File(path)
        if (!folder.exists() && !folder.mkdirs()) {
            runOnUiThread { Toast.makeText(this@EffectSelectActivity, "无法保存照片", Toast.LENGTH_SHORT).show() }
            return
        }
        val dataTake = System.currentTimeMillis()
        val jpegName = path + dataTake + ".jpg"
        try {
            val fout = FileOutputStream(jpegName)
            val bos = BufferedOutputStream(fout)
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            bos.close()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        runOnUiThread { Toast.makeText(this@EffectSelectActivity, "保存成功->" + jpegName, Toast.LENGTH_SHORT).show() }

    }
}
