package com.kedaireka.monitoringkjabb.ui.prediction.parameter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.ui.statistics.parameter.PhLevelsFragmentViewModel
import com.kedaireka.monitoringkjabb.utils.FirebaseDatabase.Companion.DATABASE_REFERENCE
import java.util.*
import kotlin.collections.ArrayList


class PredictionViewModel : ViewModel() {
    private val _records = MutableLiveData<ArrayList<Sensor>>()
    val records: LiveData<ArrayList<Sensor>> = _records

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _growthRate = MutableLiveData<Double>()
    val growthRate: LiveData<Double> = _growthRate

    private val _thresholds = MutableLiveData<Map<String, String>>()
    val thresholds = _thresholds

    private val _sensorRecordInRange = MutableLiveData<ArrayList<Sensor>>()
    val sensorRecordImage: LiveData<ArrayList<Sensor>> = _sensorRecordInRange

    private val _avg = MutableLiveData<Double>()
    val avg: LiveData<Double> = _avg

    private val _min = MutableLiveData<Double>()
    val min: LiveData<Double> = _min

    private val _max = MutableLiveData<Double>()
    val max: LiveData<Double> = _max

    fun getPredRecord(sensor: Sensor) {
        _isLoading.postValue(true)

        val dbRef = DATABASE_REFERENCE
        dbRef.child("sensors/${sensor.id}/records").orderByKey().limitToLast(10).get()
            .addOnSuccessListener { result ->
                val records = arrayListOf<Sensor>()
                var min = Double.MAX_VALUE
                var max = Double.MIN_VALUE
                var counter = 0.0
                var lastDate = result.children.last().child("created_at").value.toString().toLong()
                var lastValue = result.children.last().child("value").value.toString().toDouble()
                var firstValue = result.children.first().child("value").value.toString().toDouble()
                val growthRate = (lastValue - firstValue) / firstValue


                for (document in result.children) {
                    try {
                        val id = sensor.id
                        val name = sensor.name
                        val value = (document.child("value").value.toString().toDouble()*growthRate).toString()
                        val unit = sensor.unit
                        val createdAt =
                            Timestamp(
                                Date(
                                    document.child("created_at").value.toString().toLong() * 1000
                                )
                            )
                        val urlIcon = sensor.urlIcon

                        val valueInDouble = value.toDouble()
                        counter += valueInDouble

                        if (valueInDouble < min) {
                            min = valueInDouble
                        }
                        if (valueInDouble > max) {
                            max = valueInDouble
                        }

                        records.add(Sensor(id, name, value, unit, createdAt, urlIcon))

                    } catch (e: Exception){
                        Log.d(
                            PhLevelsFragmentViewModel::class.java.simpleName,
                            "getPHRecord: ${e.message.toString()}"
                        )
                    }
                }

                for ( r in records) {

                }
                records.reverse()

                print(records)
                val avg: Double = counter / records.size
                _isLoading.postValue(false)
                _records.postValue(records)
                _min.postValue(0.0)
                _max.postValue(14.0)
                _avg.postValue(avg)
            }
            .addOnFailureListener { it.printStackTrace() }
    }
    }

}