package com.example.client_imu

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Toast
import kotlinx.coroutines.*
import org.json.JSONObject
import java.time.Clock
import java.time.LocalDateTime

class ImuActivity : AppCompatActivity(), SensorEventListener {
    companion object {
        const val URL_SEVER_ADDRESS = "http://192.168.50.200:8081"
    }

    //    values for Sensor

    private lateinit var senSensorManager: SensorManager
    private lateinit var senAccelerometer: Sensor
    private lateinit var senSensorGyro: Sensor
    private lateinit var senSensorMagnet: Sensor
    private lateinit var senSensorGravity: Sensor
    private lateinit var senSensorRotation: Sensor
//    private val arraySensor = arrayOf(
//        senAccelerometer, senSensorGyro, senSensorMagnet, senSensorGravity, senSensorRotation
//    )

    var handler: Handler = Handler(Looper.getMainLooper())
    var runnable: Runnable? = null
    val delay = 10

    private lateinit var job: Job

    //    Values for work sensors
    private var lastUpdate: Long = 0
    private var accel: Array<Float> = arrayOf(0f, 0f, 0f)
    private var gyro: Array<Float> = arrayOf(0f, 0f, 0f)
    private var rotation: Array<Float> = arrayOf(0f, 0f, 0f, 0f)
    private var gravity: Array<Float> = arrayOf(0f, 0f, 0f)
    private var magnet: Array<Float> = arrayOf(0f, 0f, 0f)
    private val httpClient: HttpImuClient = HttpImuClient(URL_SEVER_ADDRESS)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imu)

        //        make sensors values
        senSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        //
        senSensorGyro = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        senSensorManager.registerListener(this, senSensorGyro, SensorManager.SENSOR_DELAY_NORMAL)

        senSensorGravity = senSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        senSensorManager.registerListener(this, senSensorGravity, SensorManager.SENSOR_DELAY_NORMAL)

        senSensorRotation = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        senSensorManager.registerListener(
            this,
            senSensorRotation,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        //
        senSensorMagnet = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        senSensorManager.registerListener(this, senSensorMagnet, SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
            val imuPostString = this.generatePostString()
            job = CoroutineScope(Dispatchers.IO).launch {
                var responseJson = httpClient.doMethodPost(imuPostString)
            }

//            Toast.makeText(this@ImuActivity, "This method will run every 10 seconds", Toast.LENGTH_SHORT).show()
        }.also { runnable = it }, delay.toLong())
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!)
    }

    private fun generatePostString(): String {
//        val accelData = JSONObject()
//        val gyroData = JSONObject()
//        val gravitylData = JSONObject()
//        val rotationlData = JSONObject()
        val sensorsData = JSONObject()

        sensorsData.put("accel_rawX", accel[0])
        sensorsData.put("accel_rawY", accel[1])
        sensorsData.put("accel_rawZ", accel[2])

        sensorsData.put("gyro_rawX", gyro[0])
        sensorsData.put("gyro_rawY", gyro[1])
        sensorsData.put("gyro_rawZ", gyro[2])

        sensorsData.put("gravity_rawX", gravity[0])
        sensorsData.put("gravity_rawY", gravity[1])
        sensorsData.put("gravity_rawZ", gravity[2])

        sensorsData.put("rotation_rawX", rotation[0])
        sensorsData.put("rotation_rawY", rotation[1])
        sensorsData.put("rotation_rawZ", rotation[2])
        sensorsData.put("rotation_rawW", rotation[3])



        val currentMoment = LocalDateTime.now()
        sensorsData.put("time", currentMoment.toLocalTime().toString())
        return sensorsData.toString()
    }

//    override fun onSensorChanged(event: SensorEvent?) {
//        lateinit var imuPostString: String
//        val mySensor = event?.sensor
//
//        fun updateData(event: SensorEvent?) : String {
//            val lastX: Float = event!!.values[0]
//            val lastY: Float = event.values[1]
//            val lastZ: Float = event.values[2]
//
//            val aboutData = JSONObject()
//            val sensorData = JSONObject()
//
//
//            val currentMoment = LocalDateTime.now()
//            sensorData.put("rawX", lastX)
//            sensorData.put("rawY", lastY)
//            sensorData.put("rawZ", lastZ)
//            sensorData.put("time", currentMoment.toLocalTime().toString())
//            aboutData.put(event.sensor.name.toString(), sensorData)
//
//            return aboutData.toString()
//        }
//
//        if (mySensor != null) {
//            imuPostString = updateData(event)
//        }
//        job = CoroutineScope(Dispatchers.IO).launch {
//            var responseJson = httpClient.doMethodPost(imuPostString)
//        }
//
//    }

    override fun onSensorChanged(event: SensorEvent?) {
        val sensor = event?.sensor

        if (sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            this.accel[0] = event.values[0]
            this.accel[1] = event.values[1]
            this.accel[2] = event.values[2]
        }

        if (sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            this.rotation[0] = event.values[0]
            this.rotation[1] = event.values[1]
            this.rotation[2] = event.values[2]
            this.rotation[3] = event.values[2]
        }

        if (sensor?.type == Sensor.TYPE_GRAVITY) {
            this.gravity[0] = event.values[0]
            this.gravity[1] = event.values[1]
            this.gravity[2] = event.values[2]
        }

        if (sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            this.magnet[0] = event.values[0]
            this.magnet[1] = event.values[1]
            this.magnet[2] = event.values[2]
        }

        if (sensor?.type == Sensor.TYPE_GYROSCOPE) {
            this.gyro[0] = event.values[0]
            this.gyro[1] = event.values[1]
            this.gyro[2] = event.values[2]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    fun stopClient(view: View) {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
    }
}

