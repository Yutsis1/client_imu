package com.example.client_imu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.*
import org.json.JSONObject

//import kotlin.coroutines

class MainActivity : AppCompatActivity() {

    private val httpClient: HttpImuClient = HttpImuClient("http://192.168.50.200:8081")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun setUrlButton(view: View) {
//        get editText object
        val editUrl: EditText = findViewById(R.id.editURL)
//        get text from this object
        val editUrlString = editUrl.text.toString()
//        reset URL on http_client
        httpClient.changeUrl("http://" + editUrlString)

    }

    fun sendPost(view: View) {
        var responseJson = JSONObject()
        val textPostResponse: TextView = findViewById(R.id.text_post_response)
//        test body
        val testPostString = "{\'lol\':\'kek\'}"
        val job: Job = CoroutineScope(Dispatchers.IO).launch {
            responseJson = httpClient.doMethodPost(testPostString)
        }

//        Not a good solution, I guess should be some method to update view without stoping the app
        while (job.isActive) {

        }
        textPostResponse.text = responseJson.toString()

    }


    fun sendGet(view: View) {
        var responseJson = JSONObject()
        val textPostResponse: TextView = findViewById(R.id.text_post_response)
        val job: Job = CoroutineScope(Dispatchers.IO).launch {
            responseJson = httpClient.doMethodGet()

        }

//        Not a good solution, I guess should be some method to update view without stoping the app
        while (job.isActive) {

        }
        textPostResponse.text = responseJson.toString()
    }



    fun copyToPost(view: View) {
//        method to copy data from editGet to editPOSTDataTest
        val editGetResponse: EditText = findViewById(R.id.editGet)
        val editPostData: EditText = findViewById(R.id.editPOSTDataTest)
        val textInGetField = editGetResponse.text
        editPostData.text = textInGetField

    }

    fun startImuActivity(view: View){
        val imuIntent = Intent(this, ImuActivity::class.java)

        imuIntent.putExtra(ImuActivity.URL_SEVER_ADDRESS, httpClient.mUrl.toString())
        startActivity(imuIntent)
    }
}