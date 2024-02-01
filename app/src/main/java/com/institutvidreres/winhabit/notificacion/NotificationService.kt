package com.institutvidreres.winhabit.notificacion

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.institutvidreres.winhabit.MainActivity
import com.institutvidreres.winhabit.R

class NotificationService : Service() {

    private val CHANNEL_ID = "my_channel_id"
    private val NOTIFICATION_ID = 1
    private val DELAY_MILLIS = 5000L // 5 segundos
    private var isAppInForeground = false

    private var timerHandler: Handler? = null
    private var notificationRunnable: Runnable? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startTimer()
        startForeground(
            NOTIFICATION_ID,
            createForegroundNotification()
        ) // Iniciar el servicio en primer plano
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Channel"
            val descriptionText = "Descripción del canal"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startTimer() {
        timerHandler = Handler()
        notificationRunnable = Runnable {
            if (!isAppInForeground) {
                showNotification()
            }
        }
        timerHandler?.postDelayed(notificationRunnable!!, DELAY_MILLIS)
    }

    private fun showNotification() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("WinHabits")
            .setContentText("¡Vuelve con tus hábitos y consigue recompensas!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        timerHandler?.removeCallbacks(notificationRunnable!!)
        timerHandler = null
        notificationRunnable = null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            isAppInForeground = intent.getBooleanExtra("isAppInForeground", false)
        }
        return Service.START_STICKY // Devolver este valor para reiniciar el servicio si se destruye
    }

    private fun createForegroundNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Servicio en primer plano")
            .setContentText("Este servicio muestra una notificación persistente")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }
}
