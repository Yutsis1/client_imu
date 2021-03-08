package com.example.client_imu

import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class HttpImuClient(url: String) {
    private val client = OkHttpClient()
    private var mUrl: URL = URL(url)
    private  lateinit var response_json :JSONObject

    fun changeUrl(newURL: String) {
        mUrl = URL(newURL)
    }

    fun doMethodGet(): JSONObject {
        val request = Request.Builder()
                .url(mUrl.toString())
                .build()

        val response = client.newCall(request).execute()

        response_json = JSONObject(response.body.toString())
//        val call = client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                e.printStackTrace()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                response.use {
//                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
//                    for ((name, value) in response.headers) {
//                        println("$name: $value")
//                    }
//                    println(response.body!!.string())
//                }
//            }
//        })
//        call.equals()
        return response_json
    }

    fun do_method_POST(payload: String) {
        val requestBody = payload.toRequestBody()
        val request = Request.Builder()
                .method("POST", requestBody)
                .url(mUrl.toString())
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle this
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle this
            }
        })

    }

}