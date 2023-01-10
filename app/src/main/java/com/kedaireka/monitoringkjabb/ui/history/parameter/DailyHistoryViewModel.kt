package com.kedaireka.monitoringkjabb.ui.history.parameter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.kedaireka.monitoringkjabb.model.GraphData
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.model.SensorData
import com.kedaireka.monitoringkjabb.utils.retrofitApi.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DailyHistoryViewModel : ViewModel() {

    private val TAG = "DailyHistoryViewModel"

    private val _records = MutableLiveData<ArrayList<Sensor>>()
    val records: LiveData<ArrayList<Sensor>> = _records

    private val _avg = MutableLiveData<Double>()
    val avg: LiveData<Double> = _avg

    private val _min = MutableLiveData<Double>()
    val min: LiveData<Double> = _min

    private val _max = MutableLiveData<Double>()
    val max: LiveData<Double> = _max

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getSensorHistory(sensor: Sensor) {
        _isLoading.postValue(true)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        RetrofitClient.instance.getPosts().enqueue(object : Callback<GraphData> {
            override fun onResponse(
                call: Call<GraphData>,
                response: Response<GraphData>
            ) {
                response.body()?.let {
                    val arrayListSensorData: ArrayList<SensorData> = ArrayList(it.data)
                    val records = arrayListOf<Sensor>()
                    var min = Double.MAX_VALUE
                    var max = Double.MIN_VALUE
                    var counter = 0.0
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

                    val id = sensor.id
                    val name = sensor.name
                    val unit = sensor.unit
                    val urlIcon = sensor.urlIcon

                    val stopAt = calendar.timeInMillis/1000
//                    Main ALgorithm
                    if (id.toInt() == 1){
                        for(data in arrayListSensorData.reversed()){
                            val createdAt : Long = inputFormat.parse(data.tanggal + " " + data.waktu).time/1000
                            if (stopAt > createdAt){
                                break
                            }
                            val value = data.turbidity.toDouble()
                            counter += value
                            if (min > value) {
                                min = value
                            }
                            if (max < value){
                                max = value
                            }
                            records.add(Sensor(id, name, value.toString(), unit, Timestamp(Date(createdAt*1000)), urlIcon))

                        }
                    }
                    else if (id.toInt() == 2){
                        for(data in arrayListSensorData.reversed()){
                            val createdAt : Long = inputFormat.parse(data.tanggal + " " + data.waktu).time/1000
                            if (stopAt > createdAt){
                                break
                            }
                            val value = data.amonia.toDouble()
                            counter += value
                            if (min > value) {
                                min = value
                            }
                            if (max < value){
                                max = value
                            }
                            records.add(Sensor(id, name, value.toString(), unit, Timestamp(Date(createdAt*1000)), urlIcon))

                        }
                    }
                    else if (id.toInt() == 3){
                        for(data in arrayListSensorData.reversed()){
                            val createdAt : Long = inputFormat.parse(data.tanggal + " " + data.waktu).time/1000
                            if (stopAt > createdAt){
                                break
                            }
                            val value = data.suhu.toDouble()
                            counter += value
                            if (min > value) {
                                min = value
                            }
                            if (max < value){
                                max = value
                            }
                            records.add(Sensor(id, name, value.toString(), unit, Timestamp(Date(createdAt*1000)), urlIcon))

                        }
                    }
                    else if (id.toInt() == 4){
                        for(data in arrayListSensorData.reversed()){
                            val createdAt : Long = inputFormat.parse(data.tanggal + " " + data.waktu).time/1000
                            if (stopAt > createdAt){
                                break
                            }
                            val value = data.ph.toDouble()
                            counter += value
                            if (min > value) {
                                min = value
                            }
                            if (max < value){
                                max = value
                            }
                            records.add(Sensor(id, name, value.toString(), unit, Timestamp(Date(createdAt*1000)), urlIcon))

                        }
                    }
                    else if(id.toInt() == 5){
                        for(data in arrayListSensorData.reversed()){
                            val createdAt : Long = inputFormat.parse(data.tanggal + " " + data.waktu).time/1000
                            if (stopAt > createdAt){
                                break
                            }
                            val value = data.tds.toDouble()
                            counter += value
                            if (min > value) {
                                min = value
                            }
                            if (max < value){
                                max = value
                            }
                            records.add(Sensor(id, name, value.toString(), unit, Timestamp(Date(createdAt*1000)), urlIcon))

                        }
                    }
                    else {
                        for(data in arrayListSensorData.reversed()){
                            val createdAt : Long = inputFormat.parse(data.tanggal + " " + data.waktu).time/1000
                            if (stopAt > createdAt){
                                break
                            }
                            val value = data.curah_hujan.toDouble()
                            counter += value
                            if (min > value) {
                                min = value
                            }
                            if (max < value){
                                max = value
                            }
                            records.add(Sensor(id, name, value.toString(), unit, Timestamp(Date(createdAt*1000)), urlIcon))

                        }
                    }

                    val avg: Double = counter / records.size
//                    Post Data
                    records.reverse()
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
//
//        val dbRef =
//            Firebase.database("https://monitoring-kjabb-default-rtdb.asia-southeast1.firebasedatabase.app/")
//                .getReference("sensors/${sensor.id}/records")
//        dbRef.orderByKey().startAfter((calendar.timeInMillis / 1000).toString()).get()
//            .addOnSuccessListener { result ->
//                val records = arrayListOf<Sensor>()
//                var min = Double.MAX_VALUE
//                var max = Double.MIN_VALUE
//                var counter = 0.0
//
//                for (document in result.children) {
//                    try {
//                        val id = sensor.id
//                        val name = sensor.name
//                        val value = document.child("value").value.toString()
//                        val unit = sensor.unit
//                        val createdAt = Timestamp(
//                            Date(
//                                document.child("created_at").value.toString().toLong() * 1000
//                            )
//                        )
//                        val urlIcon = sensor.urlIcon
//                        val valueInDouble = value.toDouble()
//                        counter += valueInDouble
//
//                        if (valueInDouble < min) {
//                            min = valueInDouble
//                        }
//                        if (valueInDouble > max) {
//                            max = valueInDouble
//                        }
//
//                        records.add(Sensor(id, name, value, unit, createdAt, urlIcon))
//                    } catch (e: Exception) {
//                        Log.d(DailyHistoryViewModel::class.java.simpleName, e.message.toString())
//                    }
//                }
//
//                val avg: Double = counter / records.size
//
//                _records.postValue(records)
//                _min.postValue(min)
//                _max.postValue(max)
//                _avg.postValue(avg)
//
//            }

        _isLoading.value = false

    }

}