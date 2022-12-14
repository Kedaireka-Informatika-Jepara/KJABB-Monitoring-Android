package com.kedaireka.monitoringkjabb.ui.statistics.parameter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.kedaireka.monitoringkjabb.model.GraphData
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.model.SensorData
import com.kedaireka.monitoringkjabb.model.SensorModel
import com.kedaireka.monitoringkjabb.utils.retrofitApi.ApiSensorData
import com.kedaireka.monitoringkjabb.utils.retrofitApi.RetrofitClient
import com.kedaireka.monitoringkjabb.utils.retrofitApi.RetrofitClientSensor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TurbidityFragmentViewModel : ViewModel() {

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

                    var counter = 0.0
                    val arrayListSensorData: ArrayList<SensorData> = ArrayList(it.graph.take(10))
                    var tempVal = arrayListSensorData[0].turbidity.toDouble()
                    var min = tempVal
                    var max = tempVal
                    val id = sensor.id
                    val name = sensor.name
                    val urlIcon = sensor.urlIcon
                    val unit = sensor.unit

//                    Iterate through all data
                    for (data in arrayListSensorData) {
                        val value = data.turbidity.toDouble()
                        counter += value
                        if (min > value){
                            min = value
                        }
                        if (max < value){
                            max = value
                        }
                        val createdAt = ApiSensorData().dateConverter(data.tanggal, data.waktu)
                        records.add(Sensor(id, name, value.toString(), unit, createdAt, urlIcon))
                    }
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
                    for(data in arrayListSensorData){
                        val createdAt : Long = inputFormat.parse(data.tanggal + " " + data.waktu).time/1000
//                        Log.d(AmmoniaFragmentViewModel::class.java.simpleName,createdAt.toString())
//                        Log.d(AmmoniaFragmentViewModel::class.java.simpleName,start.toString())

                        if (createdAt>=start){
                            val value = data.turbidity
                            records.add(Sensor(id, name, value, unit, Timestamp(Date(createdAt*1000)), urlIcon))
                        }
                        if (createdAt>end){
                            break
                        }
                    }
                    _sensorRecordInRange.postValue(records)

                }
            }

            override fun onFailure(call: Call<GraphData>, t: Throwable) {
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