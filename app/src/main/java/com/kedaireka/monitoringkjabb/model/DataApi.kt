package com.kedaireka.monitoringkjabb.model

import com.google.gson.annotations.SerializedName

data class SensorModel(
    val id_sensor: String,
    val nama_sensor: String,
    val batas_bawah: String,
    val batas_atas: String,
    val url: String
)

data class SensorData(
    val id: String,
    val tanggal: String,
    val suhu: String,
    @SerializedName("co2")
    val amonia: String,
    val curah_hujan: String,
    val ph: String,
    val tds: String,
    val turbidity: String,
    val waktu: String
)

data class GraphData(
    val sensor: List<SensorData>,
    val graph: List<SensorData>,
    val data: List<SensorData>
)