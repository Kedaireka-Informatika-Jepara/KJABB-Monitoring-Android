package com.kedaireka.monitoringkjabb.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.utils.FirebaseDatabase.Companion.DATABASE_REFERENCE
import com.kedaireka.monitoringkjabb.utils.retrofitApi.getDataApi
import java.text.SimpleDateFormat
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
//        createDummyRecords()
//        createDummyRecordsNewSensors()
        getSensorsData()
    }

    private fun getSensorsData() {
        _isLoading.postValue(true)
        val sensorData = arrayListOf<Sensor>()
        val thresholdData = arrayListOf<Map<String, Double>>()

        val graphData = getDataApi()



        val refRealtimeDatabase = DATABASE_REFERENCE
        refRealtimeDatabase.keepSynced(true)
        for (data in graphData){

        }
        refRealtimeDatabase.child("sensors").get().addOnSuccessListener { result ->

            val sensorDataId = graphData[0].id
            val sensorDataName = graphData[0].amonia
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//            val sensorDataDate = inputFormat.parse(graphData[0].tanggal + " " + graphData[0].waktu).time
//            val sensorDataCreatedAt = Timestamp(Date(sensorDataDate.time))
            val sensorDataIcon = "https://firebasestorage.googleapis.com/v0/b/monitoring-kjabb.appspot.com/o/icons%2FThermometer-icon.png?alt=media&token=aa04b652-2b50-422a-8c66-4c7f7f066fd1"
            sensorData.add(Sensor(sensorDataId, sensorDataName, sensorDataName, "mlg", Timestamp(Date(10)), sensorDataIcon))
            var up : Double = 1.0
            var down : Double = 2.0
            thresholdData.add(hashMapOf("upper" to up, "lower" to down))


            for (sensor in result.children) {

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