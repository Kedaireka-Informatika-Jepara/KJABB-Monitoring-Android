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
    public val sensorData : ArrayList<SensorData> = getDataApi()
    public val sensorModel : ArrayList<SensorModel> = getSensorApi()
    public fun dateConverter(dateTanggal: String, dateWaktu: String) : Timestamp{
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return Timestamp(Date(inputFormat.parse(dateTanggal + " " + dateWaktu).time))
    }
    public fun getRecordsWithId(sensor : Sensor, sensorData: ArrayList<SensorData>): Sensor {
        var id : String = ""
        var name : String = ""
        var value : String = ""
        var unit : String = ""
        var createdAt : Timestamp = Timestamp(Date(0))
        var urlIcon : String? = ""
        if (sensor.id == "1") {
            for (data in sensorData) {
                id = data.id
                name = sensor.name
                value = data.turbidity
                unit = sensor.unit
                createdAt = dateConverter(data.tanggal, data.waktu)
                urlIcon = sensor.urlIcon
            }
        }
        else if (sensor.id == "2") {
            for (data in sensorData) {
                id = data.id
                name = sensor.name
                value = data.amonia
                unit = sensor.unit
                createdAt = dateConverter(data.tanggal, data.waktu)
                urlIcon = sensor.urlIcon
            }
        }else if (sensor.id == "3") {
            for (data in sensorData) {
                id = data.id
                name = sensor.name
                value = data.suhu
                unit = sensor.unit
                createdAt = dateConverter(data.tanggal, data.waktu)
                urlIcon = sensor.urlIcon
            }
        }else if (sensor.id == "4") {
            for (data in sensorData) {
                id = data.id
                name = sensor.name
                value = data.ph
                unit = sensor.unit
                createdAt = dateConverter(data.tanggal, data.waktu)
                urlIcon = sensor.urlIcon
            }
        }else if (sensor.id == "5") {
            for (data in sensorData) {
                id = data.id
                name = sensor.name
                value = data.dissolved_oxygen
                unit = sensor.unit
                createdAt = dateConverter(data.tanggal, data.waktu)
                urlIcon = sensor.urlIcon
            }
        }
        else{
            for (data in sensorData) {
                id = data.id
                name = sensor.name
                value = data.curah_hujan
                unit = sensor.unit
                createdAt = dateConverter(data.tanggal, data.waktu)
                urlIcon = sensor.urlIcon
            }
        }
        return Sensor(id, name, value, unit, createdAt, urlIcon)

    }
}