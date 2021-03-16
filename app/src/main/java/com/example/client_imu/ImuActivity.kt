package com.example.client_imu

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.coroutines.*
import org.json.JSONObject

class ImuActivity : AppCompatActivity(), SensorEventListener {
    companion object {
        const val URL_SEVER_ADDRESS = "http://192.168.50.200:8081"
    }

    //    values for Sensor

    private lateinit var senSensorManager: SensorManager
    private lateinit var senAccelerometer: Sensor
    private lateinit var senSensorGyro: Sensor
    private lateinit var senSensorMagnet: Sensor

    private lateinit var job: Job

    //    Values for work sensors
    private var lastUpdate: Long = 0
    private var last_X: Float = 0f
    private var last_Y: Float = 0f
    private var last_Z: Float = 0f
    private val httpClient: HttpImuClient = HttpImuClient(URL_SEVER_ADDRESS)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imu)

        //        make sensors values
        senSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        //
        senSensorGyro = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        senSensorManager.registerListener(this, senSensorGyro, SensorManager.SENSOR_DELAY_NORMAL)

        //
        senSensorMagnet = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        senSensorManager.registerListener(this, senSensorMagnet, SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onSensorChanged(event: SensorEvent?) {
        lateinit var imuPostString: String
        val mySensor = event?.sensor

        fun updateData(event: SensorEvent?) : String {
            val lastX: Float = event!!.values[0]
            val lastY: Float = event.values[1]
            val lastZ: Float = event.values[2]

            val aboutData = JSONObject()
            val sensorData = JSONObject()



            sensorData.put("rawX", lastX)
            sensorData.put("rawY", lastY)
            sensorData.put("rawZ", lastZ)
            aboutData.put(event.sensor.name.toString(), sensorData)

            return aboutData.toString()
        }

        if (mySensor != null) {
            imuPostString = updateData(event)
        }
        job = CoroutineScope(Dispatchers.IO).launch {
            var responseJson = httpClient.doMethodPost(imuPostString)
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    fun stopClient(view: View){
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
    }
}

