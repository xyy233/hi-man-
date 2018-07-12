package com.cstore.zhiyazhang.cstoremanagement.view.checkin

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.ActivityCompat
import android.util.SparseIntArray
import android.view.MenuItem
import android.view.Surface
import android.view.SurfaceHolder
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.cstore.zhiyazhang.cstoremanagement.R
import com.cstore.zhiyazhang.cstoremanagement.presenter.personnel.CheckInPresenter
import com.cstore.zhiyazhang.cstoremanagement.utils.MyActivity
import com.cstore.zhiyazhang.cstoremanagement.utils.MyImage
import kotlinx.android.synthetic.main.activity_check_in.*
import kotlinx.android.synthetic.main.layout_camera.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import java.util.*

/**
 * Created by zhiya.zhang
 * on 2017/10/11 15:15.
 */
class CheckInActivity(override val layoutId: Int = R.layout.activity_check_in) : MyActivity() {
    companion object {
        private val ORIENTATIONS = SparseIntArray()

        ///为了使照片竖直显示
        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 0)
            ORIENTATIONS.append(Surface.ROTATION_90, 90)
            ORIENTATIONS.append(Surface.ROTATION_180, 180)
            ORIENTATIONS.append(Surface.ROTATION_270, 270)
        }
    }

    private var mSurfaceHolder: SurfaceHolder? = null
    private var mCameraManager: CameraManager? = null//摄像头管理器
    private var childHandler: Handler? = null
    private var mainHandler: Handler? = null
    private var mImageReader: ImageReader? = null
    private var mCameraCaptureSession: CameraCaptureSession? = null
    private var mCameraDevice: CameraDevice? = null
    private val presenter = CheckInPresenter(this, this)


    override fun initView() {
        my_toolbar.title = getString(R.string.check_in)
        my_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        my_toolbar.toolbar_btn.visibility = View.VISIBLE
        my_toolbar.toolbar_btn.text = "查看大夜签到照片"
        setSupportActionBar(my_toolbar)
        mSurfaceHolder = my_camera_surface.holder
        mSurfaceHolder!!.setFixedSize(320, 240)
        mSurfaceHolder!!.setKeepScreenOn(true)
        ordinary_check.isChecked = true
        night_check.isChecked = false
        mSurfaceHolder!!.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                if (mCameraDevice != null) {//释放Camera
                    mCameraDevice!!.close()
                    mCameraDevice = null
                }
            }

            override fun surfaceCreated(p0: SurfaceHolder?) {
                initCamera()
            }

        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return false
    }

    override fun onBackPressed() {
        if (getData1() == 1) {
            startActivity(Intent(this@CheckInActivity, CheckInDYActivity::class.java))
            finish()
        }
        super.onBackPressed()
    }

    override fun initClick() {
        ordinary_check.setOnClickListener {
            ordinary_check.toggle()
            night_check.toggle()
        }
        night_check.setOnClickListener {
            ordinary_check.toggle()
            night_check.toggle()
        }
        check_in_access.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(check_in_edit.windowToken, 0)
            if (check_in_edit.length() == 8) {
                takePicture()
            } else {
                showPrompt("请输入正确的工号！")
            }
        }

        loading.setOnClickListener {
            showPrompt(getString(R.string.wait_loading))
        }

        my_toolbar.toolbar_btn.setOnClickListener {
            startActivity(Intent(this@CheckInActivity, CheckInDYActivity::class.java))
        }
    }

    override fun initData() {
    }

    /**
     * 拍照后的图片监听
     */
    private val cameraListener: ImageReader.OnImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        mCameraDevice!!.close()
        my_camera_surface.visibility = View.GONE
        my_camera_img.visibility = View.VISIBLE
        //以下全是在处理处理图片
        val buffer = reader.acquireNextImage().planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        if (bitmap != null) {
            val newBitmap = MyImage.reverseBitmap(bitmap, 0)
            if (newBitmap != null) {
                my_camera_img.setImageBitmap(newBitmap)
                presenter.checkInUser(check_in_edit.text.toString(), newBitmap)
            } else showPrompt(getString(R.string.socketError))
        } else {
            showPrompt(getString(R.string.socketError))
        }
    }

    /**
     * 初始化Camera
     */
    private fun initCamera() {
        val handlerThread = HandlerThread("Camera2")
        handlerThread.start()
        childHandler = Handler(handlerThread.looper)
        mainHandler = Handler(mainLooper)
        mImageReader = ImageReader.newInstance(320, 240, ImageFormat.JPEG, 1)

        //得到临时照片,可以直接处理了发送socket
        mImageReader!!.setOnImageAvailableListener(cameraListener, mainHandler)

        mCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) return
            mCameraManager!!.openCamera("1", stateCallback, mainHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * 拍照
     */
    private fun takePicture() {
        if (mCameraDevice == null) return
        // 创建拍照需要的CaptureRequest.Builder
        val captureRequestBuilder: CaptureRequest.Builder
        val cameraCharacteristics: CameraCharacteristics
        try {
            captureRequestBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            cameraCharacteristics = mCameraManager!!.getCameraCharacteristics("1")
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            captureRequestBuilder.addTarget(mImageReader!!.surface)
            // 自动对焦
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            // 自动曝光
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
            // 获取手机方向
            val rotation = windowManager.defaultDisplay.rotation
            // 根据设备方向计算设置照片的方向
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, sensorToDeviceRotation(cameraCharacteristics, rotation))
            //拍照
            val mCaptureRequest = captureRequestBuilder.build()
            mCameraCaptureSession!!.capture(mCaptureRequest, null, childHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * 获得方向,
     * 三星有问题,设置方向后的图是旋转的,不管了反正公司用的是小米~
     */
    private fun sensorToDeviceRotation(c: CameraCharacteristics, rotation: Int): Int {
        val sensorOrientation = c.get(CameraCharacteristics.SENSOR_ORIENTATION)
        var deviceOrientation = ORIENTATIONS.get(rotation)
        if (c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
            deviceOrientation = -deviceOrientation
        }
        return (sensorOrientation + deviceOrientation + 360) % 360
    }

    /**
     * 摄像头创建监听
     */
    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {//打开摄像头
            mCameraDevice = camera
            //开启预览
            takePreview()
        }

        override fun onDisconnected(camera: CameraDevice) {//关闭摄像头
            if (null != mCameraDevice) {
                mCameraDevice!!.close()
                this@CheckInActivity.mCameraDevice = null
            }
        }

        override fun onError(camera: CameraDevice, error: Int) {//发生错误
            Toast.makeText(this@CheckInActivity, "摄像头开启失败", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 开始预览
     */
    private fun takePreview() {
        try {
            // 创建预览需要的CaptureRequest.Builder
            val previewRequestBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(mSurfaceHolder!!.surface)
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            mCameraDevice!!.createCaptureSession(Arrays.asList(mSurfaceHolder!!.surface, mImageReader!!.surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                    if (null == mCameraDevice) return
                    // 当摄像头已经准备好时，开始显示预览
                    mCameraCaptureSession = cameraCaptureSession
                    try {
                        // 自动对焦
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                        // 显示预览
                        val previewRequest = previewRequestBuilder.build()
                        mCameraCaptureSession!!.setRepeatingRequest(previewRequest, null, childHandler)
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }

                }

                override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                    Toast.makeText(this@CheckInActivity, "配置失败", Toast.LENGTH_SHORT).show()//showPrompt(getString(R.string.system_error))
                }
            }, childHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }


    override fun onDestroy() {
        childHandler?.removeCallbacksAndMessages(null)
        mainHandler?.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
        check_in_access.isEnabled = false
    }

    //这里不继承父类的方法，手动控制
    override fun hideLoading() {
        loading.visibility = View.GONE
        check_in_access.isEnabled = true
    }

    override fun errorDealWith() {
        my_camera_surface.visibility = View.VISIBLE
        my_camera_img.visibility = View.GONE
    }

    override fun <T> requestSuccess(rData: T) {
        showPrompt(getString(R.string.saveDone))
        my_camera_surface.visibility = View.GONE
        my_camera_img.visibility = View.VISIBLE
        onBackPressed()
    }

    /**
     * 得到签到类型
     * @return 0=普通签到 1=大夜签到
     */
    override fun getData1(): Any? {
        return if (ordinary_check.isChecked) {
            0
        } else {
            1
        }
    }
}