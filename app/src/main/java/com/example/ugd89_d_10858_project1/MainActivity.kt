package com.example.ugd89_d_10858_project1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.CAMERA_SERVICE
import android.hardware.*
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import java.io.IOException
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private var mCamera:Camera? = null
    private var mCameraView: CameraView? = null
    lateinit var proximitySensor : Sensor
    lateinit var sensorManager: SensorManager
    private var check:Int = 0
    lateinit var sensorStatusTV : TextView
    private var currentCameraId: Int = Camera.CameraInfo.CAMERA_FACING_BACK
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorStatusTV = findViewById(R.id.idTVSensorStatus)
        var proximitySensorEventListener : SensorEventListener? = object : SensorEventListener{
            override fun onAccuracyChanged(sensor: Sensor, Accuracy: Int) {

            }
            override fun onSensorChanged(event: SensorEvent) {
                //NB: if you don't release the current camera before switching, you app will crate
                if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                    if (event.values[0] == 0f && check == 0) {
                        try {
                            mCamera = openFrontCamera()
                        } catch (e: Exception) {
                            Log.d("error", "Failed to get Camera" + e.message)
                        }
                        if (mCamera != null) {
                            mCameraView = CameraView(this@MainActivity, mCamera!!)
                            val camera_view = findViewById<View>(R.id.FlCamera) as FrameLayout
                            camera_view.addView(mCameraView)
                        }
                        @SuppressLint("MissingInFlatedId", "LocalSuppress")
                        val imageClose = findViewById<View>(R.id.imgClose) as ImageButton
                        imageClose.setOnClickListener { view: View? -> System.exit(0) }
                        check = 1
                        sensorStatusTV.text = "Front Camera"
                    }else{
                        if(check == 0) {
                            try {
                                mCamera = openBackCamera()
                            } catch (e: Exception) {
                                Log.d("error", "Failed to get Camera" + e.message)
                            }
                            if (mCamera != null) {
                                mCameraView = CameraView(this@MainActivity, mCamera!!)
                                val camera_view = findViewById<View>(R.id.FlCamera) as FrameLayout
                                camera_view.addView(mCameraView)
                            }
                            @SuppressLint("MissingInFlatedId", "LocalSuppress")
                            val imageClose = findViewById<View>(R.id.imgClose) as ImageButton
                            imageClose.setOnClickListener { view: View? -> System.exit(0) }
                            sensorStatusTV.text = "Back Camera"
                        }
                    }
                }
            }
        }
        // on below line we are initializing our sensor manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // on below line we are initializing our proximity sensor variable
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        // on below line we are checking if the proximity sensor is null
        if (proximitySensor == null) {
            // on below line we are displaying a toast if no sensor is available
            Toast.makeText(this, "No proximity sensor found in device..", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            // on below line we are registering
            // our sensor with sensor manager
            sensorManager.registerListener(
                proximitySensorEventListener,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

    }
    private fun openFrontCamera(): Camera? {
        var cameraCount = 0
        var mCamera: Camera? = null
        val cameraInfo = Camera.CameraInfo()
        cameraCount = Camera.getNumberOfCameras()
        for (camId in 0 until cameraCount) {
            Camera.getCameraInfo(camId, cameraInfo)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    mCamera = Camera.open(camId)
                } catch (e: RuntimeException) {
                    Log.e("Your_TAG", "Camera failed to open: " + e.localizedMessage)
                }
            }
        }
        return mCamera
    }

    private fun openBackCamera(): Camera? {
        var cameraCount = 0
        var mCamera: Camera? = null
        val cameraInfo = Camera.CameraInfo()
        cameraCount = Camera.getNumberOfCameras()
        for (camId in 0 until cameraCount) {
            Camera.getCameraInfo(camId, cameraInfo)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    mCamera = Camera.open(camId)
                } catch (e: RuntimeException) {
                    Log.e("Your_TAG", "Camera failed to open: " + e.localizedMessage)
                }
            }
        }
        return mCamera
    }
}