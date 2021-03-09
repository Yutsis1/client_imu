package com.example.client_imu

import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.util.*

class HttpImuClient(url: String) {
    private val client = OkHttpClient()
    private var mUrl: URL = URL(url)
//    private  lateinit var response_json :JSONObject

    fun changeUrl(newURL: String) {
        mUrl = URL(newURL)
        client.newBuilder().protocols(Arrays.asList(Protocol.HTTP_1_0))
    }

    fun doMethodGet(): JSONObject {
        val request = Request.Builder()
                .url("$mUrl/position")
                .addHeader("Content-type", "application/json")
                .get()
                .build()


        val response = client.newCall(request).execute()
        val respString = response.body!!.string()
        return JSONObject(respString)
    }

    fun doMethodPost(payload: String): JSONObject {
        val requestBody = payload.toRequestBody()
        val request = Request.Builder()
                .method("POST", requestBody)
                .url(mUrl.toString())
                .build()

        val response = client.newCall(request).execute()
        return JSONObject(response.body!!.string())

    }

}