package com.kedaireka.monitoringkjabb.ui.statistics.parameter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kedaireka.monitoringkjabb.model.GraphData
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.model.SensorData
import com.kedaireka.monitoringkjabb.model.SensorModel
import com.kedaireka.monitoringkjabb.utils.FirebaseDatabase.Companion.DATABASE_REFERENCE
import com.kedaireka.monitoringkjabb.utils.retrofitApi.ApiSensorData
import com.kedaireka.monitoringkjabb.utils.retrofitApi.RetrofitClient
import com.kedaireka.monitoringkjabb.utils.retrofitApi.RetrofitClientSensor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class AmmoniaFragmentViewModel : ViewModel() {

    private val _records = MutableLiveData<ArrayList<Sensor>>()
    val records: LiveData<ArrayList<Sensor>> = _records

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _thresholds = MutableLiveData<Map<String, String>>()
    val thresholds = _thresholds

    private val _sensorRecordInRange = MutableLiveData<ArrayList<Sensor>>()
    val sensorRecordInRange: LiveData<ArrayList<Sensor>> = _sensorRecordInRange

    private val _avg = MutableLiveData<Double>()
    val avg: LiveData<Double> = _avg

    private val _min = MutableLiveData<Double>()
    val min: LiveData<Double> = _min

    private val _max = MutableLiveData<Double>()
    val max: LiveData<Double> = _max

    fun getDORecord(sensor: Sensor) {
        _isLoading.postValue(true)

        RetrofitClient.instance.getPosts().enqueue(object : Callback<GraphData> {
            override fun onResponse(
                call: Call<GraphData>,
                response: Response<GraphData>
            ) {
                response.body()?.let {
                    val records = arrayListOf<Sensor>()
                    var min = Double.MAX_VALUE
                    var max = Double.MIN_VALUE
                    var counter = 0.0
                    val arrayListSensorData: ArrayList<SensorData> = ArrayList(it.graph.takeLast(10))
                    val id = sensor.id
                    val name = sensor.name
                    val urlIcon = sensor.urlIcon
                    val unit = sensor.unit

                    if (id == "1"){
                        for (data in arrayListSensorData) {
                            val value = data.turbidity
                            val createdAt = ApiSensorData().dateConverter(data.tanggal, data.waktu)
                            records.add(Sensor(id, name, value, unit, createdAt, urlIcon))
                        }
                    }
                    else if (id == "2"){
                        for (data in arrayListSensorData) {
                            val value = data.amonia
                            val createdAt = ApiSensorData().dateConverter(data.tanggal, data.waktu)
                            records.add(Sensor(id, name, value, unit, createdAt, urlIcon))
                        }
                    }
                    else if (id == "3"){
                        for (data in arrayListSensorData) {
                            val value = data.suhu
                            val createdAt = ApiSensorData().dateConverter(data.tanggal, data.waktu)
                            records.add(Sensor(id, name, value, unit, createdAt, urlIcon))
                        }
                    }
                    else if (id == "4"){
                        for (data in arrayListSensorData) {
                            val value = data.ph
                            val createdAt = ApiSensorData().dateConverter(data.tanggal, data.waktu)
                            records.add(Sensor(id, name, value, unit, createdAt, urlIcon))
                        }
                    }
                    else if (id == "5"){
                        for (data in arrayListSensorData) {
                            val value = data.tds
                            val createdAt = ApiSensorData().dateConverter(data.tanggal, data.waktu)
                            records.add(Sensor(id, name, value, unit, createdAt, urlIcon))
                        }
                    }
                    else if (id == "6"){
                        for (data in arrayListSensorData) {
                            val value = data.curah_hujan
                            val createdAt = ApiSensorData().dateConverter(data.tanggal, data.waktu)
                            records.add(Sensor(id, name, value, unit, createdAt, urlIcon))
                        }
                    }
                    records.reverse()
                    val avg: Double = counter / records.size

                    _isLoading.postValue(false)
                    _records.postValue(records)
                    _min.postValue(min)
                    _max.postValue(max)
                    _avg.postValue(avg)
                }
            }

            override fun onFailure(call: Call<GraphData>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
    fun getSensorRecordInRange(sensor: Sensor, start: Long, end: Long) {
        val dbRef =
            Firebase.database("https://monitoring-kjabb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("sensors/${sensor.id}/records")

        dbRef.orderByKey().startAfter(start.toString()).endBefore(end.toString())
            .get().addOnSuccessListener { result ->
                val records = arrayListOf<Sensor>()
                Log.d("DetailSensorViewModel", result.childrenCount.toString())
                for (document in result.children) {
                    try {
                        val id = sensor.id
                        val name = sensor.name
                        val value = document.child("value").value.toString()
                        val unit = sensor.unit
                        val createdAt =
                            Timestamp(
                                Date(
                                    document.child("created_at").value.toString().toLong() * 1000
                                )
                            )
                        val urlIcon = sensor.urlIcon
                        records.add(Sensor(id, name, value, unit, createdAt, urlIcon))
                    } catch (e: Exception) {
                        Log.d(
                            AmmoniaFragmentViewModel::class.java.simpleName,
                            "getSensorRecordInRange: ${e.message.toString()}"
                        )
                    }
                }
                _sensorRecordInRange.postValue(records)
            }.addOnFailureListener {
                it.printStackTrace()
            }

    }

    fun getThresholdsData(sensor: Sensor) {
        RetrofitClientSensor.instance.getPosts().enqueue(object : Callback<ArrayList<SensorModel>> {
            override
            fun onResponse(
                call: Call<ArrayList<SensorModel>>,
                response: Response<ArrayList<SensorModel>>
            ){
                response.body().let {
                    val sensorModel : ArrayList<SensorModel>? = it
                    val dataThreshold = mapOf(
                        "upper" to sensorModel?.get(sensor.id.toInt())?.batas_atas.toString(),
                        "lower" to sensorModel?.get(sensor.id.toInt())?.batas_bawah.toString(),
                    )
                    _thresholds.postValue(dataThreshold)

                }
            }
            override fun onFailure(call: Call<ArrayList<SensorModel>>, t: Throwable) {
            }

        })
    }
}