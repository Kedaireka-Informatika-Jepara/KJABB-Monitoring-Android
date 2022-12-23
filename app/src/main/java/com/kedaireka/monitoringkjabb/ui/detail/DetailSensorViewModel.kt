package com.kedaireka.monitoringkjabb.ui.detail

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
import com.kedaireka.monitoringkjabb.utils.retrofitApi.getDataApi
import com.kedaireka.monitoringkjabb.utils.retrofitApi.getSensorApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class DetailSensorViewModel : ViewModel() {

    companion object {
        private const val TAG = "DetailSensorViewModel"
    }

    private val _sensorRecordInRange = MutableLiveData<ArrayList<Sensor>>()
    val sensorRecordInRange: LiveData<ArrayList<Sensor>> = _sensorRecordInRange

    private val _dataSensor = MutableLiveData<ArrayList<Sensor>>()
    val dataSensor: LiveData<ArrayList<Sensor>> = _dataSensor

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading

    private val _thresholds = MutableLiveData<Map<String, String>>()
    val thresholds = _thresholds

    fun getSensorRecordInRange(sensor: Sensor, start: Long, end: Long) {
//        val databaseRef = ApiSensorData().sensorData


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
                        Log.d(DetailSensorViewModel::class.java.simpleName, e.message.toString())
                    }
                }
                _sensorRecordInRange.postValue(records)
            }.addOnFailureListener {
                it.printStackTrace()
            }

    }

    fun getSensorRecords(sensor: Sensor) {
        _isLoading.value = true


//        val graphData : ArrayList<SensorData> = getDataApi()
//        val sensorModel : ArrayList<SensorModel> = getSensorApi()


        RetrofitClient.instance.getPosts().enqueue(object : Callback<GraphData> {
            override fun onResponse(
                call: Call<GraphData>,
                response: Response<GraphData>
            ) {
                response.body()?.let {
                    val records = arrayListOf<Sensor>()
                    val arrayListSensorData: ArrayList<SensorData> = ArrayList(it.graph.takeLast(7))
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

                    _isLoading.postValue(false)
                    _dataSensor.postValue(records)
                }
            }

            override fun onFailure(call: Call<GraphData>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

//        val dbRef = DATABASE_REFERENCE
//        dbRef.child("sensors/${sensor.id}/records").orderByKey().limitToLast(10).get()
//            .addOnSuccessListener { result ->
//
//                val records = arrayListOf<Sensor>()
//                for (document in result.children) {
//                    try {
//                        val id = sensor.id
//                        val name = sensor.name
//                        val value = document.child("value").value.toString()
//                        val unit = sensor.unit
//                        val createdAt =
//                            Timestamp(
//                                Date(
//                                    document.child("created_at").value.toString().toLong() * 1000
//                                )
//                            )
//                        val urlIcon = sensor.urlIcon
//
//                        records.add(Sensor(id, name, value, unit, createdAt, urlIcon))
//                    } catch (e: Exception) {
//                        Log.d(DetailSensorViewModel::class.java.simpleName, e.message.toString())
//                    }
//                }
//                records.reverse()
//
//                _isLoading.postValue(false)
//                _dataSensor.postValue(records)
//            }
//            .addOnFailureListener {
//                it.printStackTrace()
//            }
    }

    fun getThresholdsData(sensor: Sensor) {
        val dbRef = DATABASE_REFERENCE
        dbRef.child("sensors/${sensor.id}/thresholds").get().addOnSuccessListener { result ->
            val dataThreshold = mapOf(
                "upper" to result.child("upper").value.toString(),
                "lower" to result.child("lower").value.toString(),
            )

            _thresholds.postValue(dataThreshold)
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }
}