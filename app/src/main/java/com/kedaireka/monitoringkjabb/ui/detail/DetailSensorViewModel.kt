package com.kedaireka.monitoringkjabb.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
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
import java.text.SimpleDateFormat
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

        RetrofitClient.instance.getPosts().enqueue(object : Callback<GraphData> {
            override fun onResponse(
                call: Call<GraphData>,
                response: Response<GraphData>
            ) {
                response.body()?.let {
                    val arrayListSensorData: ArrayList<SensorData> = ArrayList(it.data)
                    val records = arrayListOf<Sensor>()
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

                    val id = sensor.id
                    val name = sensor.name
                    val unit = sensor.unit
                    val urlIcon = sensor.urlIcon
                    if (id.toInt() == 1){
                        for(data in arrayListSensorData){
                            val createdAt : Long = inputFormat.parse(data.tanggal + " " + data.waktu).time/1000

                            if (createdAt>=start){
                                val value = data.turbidity
                                records.add(Sensor(id, name, value, unit, Timestamp(Date(createdAt*1000)), urlIcon))
                            }
                            if (createdAt>end){
                                break
                            }
                        }
                    }
                    else if (id.toInt() == 2){
                        for(data in arrayListSensorData){
                            val createdAt : Long = inputFormat.parse(data.tanggal + " " + data.waktu).time/1000

                            if (createdAt>=start){
                                val value = data.amonia
                                records.add(Sensor(id, name, value, unit, Timestamp(Date(createdAt*1000)), urlIcon))
                            }
                            if (createdAt>end){
                                break
                            }
                        }
                    }
                    else if (id.toInt() == 3){
                        for(data in arrayListSensorData){
                            val createdAt : Long = inputFormat.parse(data.tanggal + " " + data.waktu).time/1000

                            if (createdAt>=start){
                                val value = data.suhu
                                records.add(Sensor(id, name, value, unit, Timestamp(Date(createdAt*1000)), urlIcon))
                            }
                            if (createdAt>end){
                                break
                            }
                        }
                    }
                    else if (id.toInt() == 4){
                        for(data in arrayListSensorData){
                            val createdAt : Long = inputFormat.parse(data.tanggal + " " + data.waktu).time/1000

                            if (createdAt>=start){
                                val value = data.ph
                                records.add(Sensor(id, name, value, unit, Timestamp(Date(createdAt*1000)), urlIcon))
                            }
                            if (createdAt>end){
                                break
                            }
                        }
                    }
                    else if (id.toInt() == 5){
                        for(data in arrayListSensorData){
                            val createdAt : Long = inputFormat.parse(data.tanggal + " " + data.waktu).time/1000

                            if (createdAt>=start){
                                val value = data.tds
                                records.add(Sensor(id, name, value, unit, Timestamp(Date(createdAt*1000)), urlIcon))
                            }
                            if (createdAt>end){
                                break
                            }
                        }
                    }
                    else {
                        for(data in arrayListSensorData){
                            val createdAt : Long = inputFormat.parse(data.tanggal + " " + data.waktu).time/1000

                            if (createdAt>=start){
                                val value = data.curah_hujan
                                records.add(Sensor(id, name, value, unit, Timestamp(Date(createdAt*1000)), urlIcon))
                            }
                            if (createdAt>end){
                                break
                            }
                        }
                    }


                    _sensorRecordInRange.postValue(records)

                }
            }

            override fun onFailure(call: Call<GraphData>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getSensorRecords(sensor: Sensor) {
        _isLoading.value = true

        RetrofitClient.instance.getPosts().enqueue(object : Callback<GraphData> {
            override fun onResponse(
                call: Call<GraphData>,
                response: Response<GraphData>
            ) {
                response.body()?.let {
                    val records = arrayListOf<Sensor>()
                    val arrayListSensorData: ArrayList<SensorData> = ArrayList(it.graph.take(10))
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
                    _isLoading.postValue(false)
                    _dataSensor.postValue(records)
                }
            }

            override fun onFailure(call: Call<GraphData>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getThresholdsData(sensor: Sensor) {
        RetrofitClientSensor.instance.getPosts().enqueue(object : Callback<ArrayList<SensorModel>> {
            override
            fun onResponse(
                call: Call<ArrayList<SensorModel>>,
                response: Response<ArrayList<SensorModel>>
            ){
                response.body()?.let {
                    val sensorModel : ArrayList<SensorModel> = it
                    val dataThreshold = mapOf(
                        "upper" to sensorModel?.get(sensor.id.toInt()-1).batas_atas,
                        "lower" to sensorModel?.get(sensor.id.toInt()-1).batas_bawah,
                    )
                    _thresholds.postValue(dataThreshold)
                }
            }
            override fun onFailure(call: Call<ArrayList<SensorModel>>, t: Throwable) {
            }
        })
    }
}