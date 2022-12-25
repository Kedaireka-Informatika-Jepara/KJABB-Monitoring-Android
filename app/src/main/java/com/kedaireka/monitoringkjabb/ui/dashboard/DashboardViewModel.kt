package com.kedaireka.monitoringkjabb.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kedaireka.monitoringkjabb.model.GraphData
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.model.SensorData
import com.kedaireka.monitoringkjabb.utils.FirebaseDatabase.Companion.DATABASE_REFERENCE
import com.kedaireka.monitoringkjabb.utils.retrofitApi.ApiSensorData
import com.kedaireka.monitoringkjabb.utils.retrofitApi.RetrofitClient
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
                            arrayOf("NTU", "mg/l", "°C", "", "ppm", "mm")
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
}