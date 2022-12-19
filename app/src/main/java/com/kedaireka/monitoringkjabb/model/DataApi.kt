package com.kedaireka.monitoringkjabb.model

import com.google.gson.annotations.SerializedName

data class SensorData(
    val id: String,
    val tanggal: String,
    val suhu: String,
    val amonia: String,
    val curah_hujan: String,
    val ph: String,
    @SerializedName("do")
    val dissolved_oxygen: String,
    val turbidity: String,
    val waktu: String
)

data class GraphData(
    val sensor: List<SensorData>,
    val graph: List<SensorData>,
    val data: List<SensorData>
)