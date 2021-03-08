package com.example.client_imu

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private  var http_client : HttpImuClient = HttpImuClient("http://192.168.50.200")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun setUrlButton(view: View){
//        get editText object
        val editUrl : EditText = findViewById(R.id.editURL)
//        get text from this object
        val editUrlString = editUrl.text.toString()
//        reset URL on http_client
        http_client.changeUrl("http://" + editUrlString)

    }

    fun sendPost(view: View){

    }

    fun sendGet(view: View){

            val responseJson: JSONObject = http_client.doMethodGet()


        val textPostResponse : TextView = findViewById(R.id.text_post_response)

        textPostResponse.text = responseJson.toString()


    }

    fun copyToPost(view: View){
//        method to copy data from editGet to editPOSTDataTest
        val editGetResponse: EditText = findViewById(R.id.editGet)
        val editPostData: EditText = findViewById(R.id.editPOSTDataTest)
        val textInGetField = editGetResponse.text
        editPostData.text = textInGetField

    }
}