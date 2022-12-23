package com.kedaireka.monitoringkjabb.utils.retrofitApi

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kedaireka.monitoringkjabb.model.GraphData
import com.kedaireka.monitoringkjabb.model.SensorData
import com.kedaireka.monitoringkjabb.model.SensorModel
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

interface ApiSensor {
    @GET("data/threshold")
    fun getPosts(): retrofit2.Call<ArrayList<SensorModel>>
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

object RetrofitClientSensor {
    private const val BASE_URL = "https://monitoring.cemebsa.com/"

    val instance: ApiSensor by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiSensor::class.java)
    }
}

fun getDataApi() : LiveData<ArrayList<SensorData>> {
    var dataResponse = MutableLiveData<ArrayList<SensorData>>()
    var tempResponse = ArrayList<SensorData>()
    RetrofitClient.instance.getPosts().enqueue(object : Callback<GraphData>{
        override fun onResponse(
            call: Call<GraphData>,
            response: Response<GraphData>
        ) {
            response.body()?.let { tempResponse.addAll(it.data)
                dataResponse.postValue(tempResponse)
            Log.d("getApi", dataResponse.toString())}

        }

        override fun onFailure(call: Call<GraphData>, t: Throwable) {
        }
    })
    return dataResponse
}

fun getSensorApi() : ArrayList<SensorModel> {
    var dataResponse = ArrayList<SensorModel>()
    RetrofitClientSensor.instance.getPosts().enqueue(object : Callback<ArrayList<SensorModel>>{
        override fun onResponse(
            call: Call<ArrayList<SensorModel>>,
            response: Response<ArrayList<SensorModel>>
        ) {
            response.body()?.let { dataResponse.addAll(it) }
        }

        override fun onFailure(call: Call<ArrayList<SensorModel>>, t: Throwable) {
        }
    })
    return dataResponse
}