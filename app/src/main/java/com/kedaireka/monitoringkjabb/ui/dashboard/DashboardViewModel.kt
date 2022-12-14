package com.kedaireka.monitoringkjabb.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.utils.FirebaseDatabase.Companion.DATABASE_REFERENCE
import com.kedaireka.monitoringkjabb.utils.retrofitApi.PostResponse
import com.kedaireka.monitoringkjabb.utils.retrofitApi.RetrofitClient
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

//    val MYSQLDATABASE = MySQLDatabase.fetchData().start()

    init {
//        createDummyRecords()
//        createDummyRecordsNewSensors()
        getSensorsData()
    }

    private fun getSensorsData() {
        _isLoading.postValue(true)
        val sensorData = arrayListOf<Sensor>()
        val thresholdData = arrayListOf<Map<String, Double>>()

        val mySqlDatabase = ArrayList<PostResponse>()
        RetrofitClient.instance.getPosts().enqueue(object: Callback<ArrayList<PostResponse>>{
            override fun onResponse(
                call: Call<ArrayList<PostResponse>>,
                response: Response<ArrayList<PostResponse>>
            ) {
                response.body()?.let { mySqlDatabase.addAll(it) }

            }

            override fun onFailure(call: Call<ArrayList<PostResponse>>, t: Throwable) {

            }
        })

        val refRealtimeDatabase = DATABASE_REFERENCE
        refRealtimeDatabase.keepSynced(true)
        refRealtimeDatabase.child("sensors").get().addOnSuccessListener { result ->

            for (sensor in result.children) {
                val id_sql = mySqlDatabase[0].sensor[0].id
                val name_sql = mySqlDatabase[0].sensor[0].relay
                val value_sql = 5
                val unit_sql = ""
                val created_sql = Timestamp(Date(mySqlDatabase[0].sensor[0].tanggal))
                val url_icon = ""
                sensorData.add(Sensor("1","sensor","1","s", Timestamp(Date(10000)),""))
                thresholdData.add(hashMapOf("upper" to value_sql.toDouble()-1, "lower" to value_sql.toDouble()+1))

                val id = sensor.key!!
                val name = sensor.child("data/name").value.toString()
                val value =
                    sensor.child("records").children.last().child("value").value.toString()
                val unit = sensor.child("data/unit").value.toString()
                val createdAt =
                    sensor.child("records").children.last()
                        .child("created_at").value.toString()
                val urlIcon = sensor.child("data/url_icon").value.toString()

//                Konversi millisecond to Date
                val createdAtTimestamp = Timestamp(Date(createdAt.toLong() * 1000))
                sensorData.add(Sensor(id, name, value, unit, createdAtTimestamp, urlIcon))

                val upper = sensor.child("thresholds/upper").value.toString().toDouble()
                val lower = sensor.child("thresholds/lower").value.toString().toDouble()

                thresholdData.add(hashMapOf("upper" to upper, "lower" to lower))
            }

            _thresholdData.postValue(thresholdData)
            _data.postValue(sensorData)
            _isLoading.postValue(false)
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

    private fun createDummyRecords() {
        // Water Temperature
        for (i in 1..1100) {
            val timeInMillis = Date().time - (1800000 * i)
            val db = DATABASE_REFERENCE
            val data = mutableMapOf<String, Any>()
            data["created_at"] = timeInMillis / 1000
            data["value"] = (Random.nextDouble(20.0, 32.0) * 100).roundToInt() / 100.0

            db.child("sensors/water_temperature/records/${timeInMillis / 1000}").setValue(data)
        }

        // Ammonia
        for (i in 1..1100) {
            val timeInMillis = Date().time - (1_800_000 * i)
            val db = DATABASE_REFERENCE
            val data = mutableMapOf<String, Any>()
            data["created_at"] = timeInMillis / 1000
            data["value"] = (Random.nextDouble(0.02, 0.2) * 100).roundToInt() / 100.0

            db.child("sensors/ammonia/records/${timeInMillis / 1000}").setValue(data)
        }

        // Raindrops
        for (i in 1..1100) {
            val timeInMillis = Date().time - (1800000 * i)
            val db = DATABASE_REFERENCE
            val data = mutableMapOf<String, Any>()
            data["created_at"] = timeInMillis / 1000
            data["value"] = Random.nextInt(0, 4)

            db.child("sensors/raindrops/records/${timeInMillis / 1000}").setValue(data)
        }

    }
    private fun createDummyRecordsNewSensors() {
        // pH Level
        for (i in 1..1100) {
            val timeInMillis = Date().time - (1800000 * i)
            val db = DATABASE_REFERENCE
            val data = mutableMapOf<String, Any>()
            data["created_at"] = timeInMillis / 1000
            data["value"] = (Random.nextDouble(5.00, 9.00) * 100).roundToInt() / 100.0

            db.child("sensors/ph_level/records/${timeInMillis / 1000}").setValue(data)
        }

        // Dissolved Oxygen
        for (i in 1..1100) {
            val timeInMillis = Date().time - (1800000 * i)
            val db = DATABASE_REFERENCE
            val data = mutableMapOf<String, Any>()
            data["created_at"] = timeInMillis / 1000
            data["value"] = (Random.nextDouble(0.02, 0.2) * 100).roundToInt() / 100.0

            db.child("sensors/dissolved_oxygen/records/${timeInMillis / 1000}").setValue(data)
        }
    }

}