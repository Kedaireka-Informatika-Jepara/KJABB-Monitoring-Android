package com.kedaireka.monitoringkjabb.ui.dashboard

import android.text.format.DateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kedaireka.monitoringkjabb.model.GraphData
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.model.SensorData
import com.kedaireka.monitoringkjabb.utils.FirebaseDatabase.Companion.DATABASE_REFERENCE
import com.kedaireka.monitoringkjabb.utils.retrofitApi.ApiSensorData
import com.kedaireka.monitoringkjabb.utils.retrofitApi.RetrofitClient
import com.kedaireka.monitoringkjabb.utils.retrofitApi.RetrofitClientInsertDummy
import com.kedaireka.monitoringkjabb.utils.retrofitApi.getSensorApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import kotlin.random.Random

class DashboardViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _data = MutableLiveData<ArrayList<Sensor>>()
    val data : LiveData<ArrayList<Sensor>> = _data

    private val _thresholdData = MutableLiveData<ArrayList<Map<String, Double>>>()
    val thresholdData = _thresholdData

    init {
        getSensorsData()
    }

    private fun getSensorsData() {
        _isLoading.postValue(true)
        val sensorData = arrayListOf<Sensor>()
        val thresholdData = arrayListOf<Map<String, Double>>()

        val refRealtimeDatabase = DATABASE_REFERENCE
        refRealtimeDatabase.keepSynced(true)
//        val graphData : GraphData
        val sensorModel = getSensorApi()

        RetrofitClient.instance.getPosts().enqueue(
            object : Callback<GraphData> {
                override fun onResponse(
                    call: Call<GraphData>,
                    response: Response<GraphData>,
                ) {
                    response.body()?.let {
                        val arrayListSensorData: SensorData = ArrayList(it.sensor)[0]
                        val sensorDataValue: Array<String> = arrayOf(
                            arrayListSensorData.turbidity,
                            arrayListSensorData.amonia,
                            arrayListSensorData.suhu,
                            arrayListSensorData.ph,
                            arrayListSensorData.tds,
                            arrayListSensorData.curah_hujan,
                        )
                        val sensorDataUnit: Array<String> =
                            arrayOf("NTU", "ppm", "°C", "", "ppm", "mm")
                        val sensorDataDate = ApiSensorData().dateConverter(
                            arrayListSensorData.tanggal,
                            arrayListSensorData.waktu,
                        )
                        for (i in 0 until 6) {
                            sensorData.add(
                                Sensor(
                                    sensorModel[i].id_sensor,
                                    sensorModel[i].nama_sensor,
                                    sensorDataValue[i],
                                    sensorDataUnit[i],
                                    sensorDataDate,
                                    sensorModel[i].url,
                                ),
                            )
                            thresholdData.add(
                                hashMapOf(
                                    "upper" to sensorModel[i].batas_atas.toDouble(),
                                    "lower" to sensorModel[i].batas_bawah.toDouble(),
                                ),
                            )
                        }
                        _thresholdData.postValue(thresholdData)
                        _data.postValue(sensorData)
                        _isLoading.postValue(false)
                    }
                }

                override fun onFailure(call: Call<GraphData>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            },
        )
    }

////    Create Dummy Data for 5 minutes time series
//private fun createDummyRecords() {
//    val dummyRecords = ArrayList<SensorData>()
//    // Water Temperature
//    for (i in 1..1000) {
//
//        val timeInMillis = Date().time - (300000 * i)
//        val tanggal = DateFormat.format("yyyy-MM-dd", timeInMillis)
//        val waktu = DateFormat.format("HH:mm:ss", timeInMillis)
//        val turbidity = Random.nextDouble(0.0, 3.4).toString()
//        val amonia = Random.nextDouble(0.0, 4.9).toString()
//        val suhu = Random.nextDouble(24.0, 26.7).toString()
//        val ph = Random.nextDouble(6.7, 7.7).toString()
//        val tds = Random.nextDouble(0.0, 500.0).toString()
//        val curah = Random.nextDouble(0.0, 4950.0).toString()
//        RetrofitClientInsertDummy.instance.insertDummy(tanggal, suhu, ).enqueue(
//            object : Callback<SensorData> {
//                override fun onResponse(call: Call<SensorData>, response: Response<SensorData>) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onFailure(call: Call<SensorData>, t: Throwable) {
//                    TODO("Not yet implemented")
//                }
//
//            }
//
//
////        val db = DATABASE_REFERENCE
////        val data = mutableMapOf<String, Any>()
////        data["created_at"] = timeInMillis / 1000
////        data["value"] = (Random.nextDouble(20.0, 32.0) * 100).roundToInt() / 100.0
////
////        db.child("sensors/water_temperature/records/${timeInMillis / 1000}").setValue(data)
//    }
    
}