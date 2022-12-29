package com.kedaireka.monitoringkjabb.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.Timestamp
import com.kedaireka.monitoringkjabb.R
import com.kedaireka.monitoringkjabb.model.GraphData
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.model.SensorData
import com.kedaireka.monitoringkjabb.model.SensorModel
import com.kedaireka.monitoringkjabb.ui.detail.DetailSensorActivity
import com.kedaireka.monitoringkjabb.utils.FirebaseDatabase.Companion.DATABASE_REFERENCE
import com.kedaireka.monitoringkjabb.utils.retrofitApi.ApiSensorData
import com.kedaireka.monitoringkjabb.utils.retrofitApi.RetrofitClient
import com.kedaireka.monitoringkjabb.utils.retrofitApi.RetrofitClientSensor
import com.kedaireka.monitoringkjabb.utils.retrofitApi.getSensorApi
import org.apache.poi.ss.util.DateFormatConverter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ThresholdWarningReceiver : BroadcastReceiver() {

    companion object {
        private const val ID_THRESHOLD_WARNING = 102
    }

    override fun onReceive(context: Context, intent: Intent) {
        getSensorsData(context, ID_THRESHOLD_WARNING)
    }

    fun setRepeatingThresholdAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ThresholdWarningReceiver::class.java)

        val calendar = Calendar.getInstance()

        val pendingIntent = PendingIntent.getBroadcast(context, ID_THRESHOLD_WARNING, intent, 0)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_HALF_HOUR,
            pendingIntent
        )

        Toast.makeText(context, R.string.threshold_warning_activated, Toast.LENGTH_SHORT).show()
    }

    private fun showAlarmNotification(
        context: Context,
        notificationId: Int,
        sensor: Sensor = Sensor("5", "TDS", "267.12", "ppm", Timestamp(Date(Date().time))),
        threshold: Map<String, Double> = hashMapOf("upper" to 6.0, "lower" to 9.0)
    ) {
        val CHANNEL_ID = "Channel_2"
        val CHANNEL_NAME = "Threshold Warning Notification"

        val intent = Intent(context, DetailSensorActivity::class.java)
        intent.putExtra("data", sensor)
        intent.putExtra("upper", threshold["upper"])
        intent.putExtra("lower", threshold["lower"])

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val mBuilder = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(context.getString(R.string.threshold_warning))
            .setContentText(context.getString(R.string.threshold_notification_message))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${sensor.name} berada diluar batas aman dengan nilai ${sensor.value}")
            )
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.description = CHANNEL_NAME

            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }

        val notification = mBuilder.build()
        mNotificationManager.notify(notificationId, notification)
    }

    fun cancelThresholdAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ThresholdWarningReceiver::class.java)
        val requestCode = ID_THRESHOLD_WARNING
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)
        pendingIntent.cancel()

        alarmManager.cancel(pendingIntent)

        Toast.makeText(context, R.string.threshold_warning_deactivated, Toast.LENGTH_SHORT).show()
    }

    private fun getSensorsData(context: Context, notificationId: Int) {
//
//        showAlarmNotification(context, notificationId)
        Log.d("ThresholdWarning", "Fetching Data")
        val sensorModel = getSensorApi()
        val thresholdData = arrayListOf<Map<String, Double>>()
        for (i in 0 until 6){
            thresholdData.add(hashMapOf("upper" to sensorModel[i].batas_atas.toDouble(), "lower" to sensorModel[i].batas_bawah.toDouble()))
        }

        RetrofitClient.instance.getPosts().enqueue(object : Callback<GraphData> {
            override fun onResponse(
                call: Call<GraphData>,
                response: Response<GraphData>
            ) {
                response.body()?.let {

                    val sensorData = ArrayList<Sensor>()
                    val lastData = it.data.last()
                    val createdAt = ApiSensorData().dateConverter(lastData.tanggal, lastData.waktu)
                    sensorData.add(Sensor("1", "Turbidity", lastData.turbidity, "NTU", createdAt, ""))
                    sensorData.add(Sensor("2", "Amonia", lastData.amonia, "mg/l", createdAt, ""))
                    sensorData.add(Sensor("3", "Suhu", lastData.suhu, "C", createdAt, ""))
                    sensorData.add(Sensor("4", "pH", lastData.ph, "", createdAt, ""))
                    sensorData.add(Sensor("5", "Tds", lastData.tds, "ppm", createdAt, ""))
                    sensorData.add(Sensor("6", "Curah Hujan", lastData.curah_hujan, "mm", createdAt, ""))
                    for (i in sensorData.indices) {
                        val upper = thresholdData[i]["upper"]!!
                        val lower = thresholdData[i]["lower"]!!
                        val value = sensorData[i].value.toDouble()

                        if (value !in lower..upper) {
                            showAlarmNotification(
                                context,
                                notificationId,
                                sensorData[i],
                                thresholdData[i]
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GraphData>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}