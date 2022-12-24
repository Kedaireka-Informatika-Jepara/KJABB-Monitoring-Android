package com.kedaireka.monitoringkjabb.utils.retrofitApi

import com.google.firebase.Timestamp
import com.kedaireka.monitoringkjabb.model.GraphData
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.model.SensorData
import com.kedaireka.monitoringkjabb.model.SensorModel
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ApiSensorData {
    public fun dateConverter(dateTanggal: String, dateWaktu: String): Timestamp {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return Timestamp(Date(inputFormat.parse(dateTanggal + " " + dateWaktu).time))
    }

    public fun dateConverterPred(dateTanggal: String, dateWaktu: String): Timestamp {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return Timestamp(Date(inputFormat.parse(dateTanggal + " " + dateWaktu).time + 86400))
    }
}