package com.kedaireka.monitoringkjabb.utils.retrofitApi

import com.kedaireka.monitoringkjabb.model.SensorData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInsertDummy {
    @FormUrlEncoded
    @POST("api/DataSensor/ucupasu")
    fun insertDummy(
//        @Field("id_sensor") id_sensor: String,
        @Field("tanggal") tanggal: String,
        @Field("suhu") suhu: String,
        @Field("gas") amonia: String,
        @Field("curah_hujan") curah_hujan: String,
        @Field("ph") ph: String,
        @Field("tds") tds: String,
        @Field("turbidity") turbidity: String,
        @Field("waktu") waktu: String,
    ): Call<SensorData>
}

object RetrofitClientInsertDummy {
    private const val BASE_URL = "https://monitoring.cemebsa.com/"

    val instance: ApiInsertDummy by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiInsertDummy::class.java)
    }
}












