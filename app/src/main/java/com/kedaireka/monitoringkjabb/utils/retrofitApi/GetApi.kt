package com.kedaireka.monitoringkjabb.utils.retrofitApi

import com.kedaireka.monitoringkjabb.model.GraphData
import com.kedaireka.monitoringkjabb.model.SensorData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface Api {
    @GET("data")
    fun getPosts(): retrofit2.Call<GraphData>
}

object RetrofitClient {
    private const val BASE_URL = "https://monitoring.cemebsa.com/"

    val instance: Api by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(Api::class.java)
    }
}

fun getDataApi() : ArrayList<SensorData> {
    var dataResponse = ArrayList<SensorData>()
    RetrofitClient.instance.getPosts().enqueue(object : Callback<GraphData>{
        override fun onResponse(
            call: Call<GraphData>,
            response: Response<GraphData>
        ) {
            response.body()?.let { dataResponse.addAll(it.data) }
        }

        override fun onFailure(call: Call<GraphData>, t: Throwable) {
        }
    })
    return dataResponse
}