package com.ilyadev.meowmoments.presentation.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ilyadev.meowmoments.R
import com.ilyadev.meowmoments.presentation.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Можно отправить токен на сервер, если будет бэкенд
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Обработка данных уведомления
        val title = message.notification?.title ?: "Кошачий факт"
        val body = message.notification?.body ?: "Откройте приложение, чтобы узнать интересный факт о котах!"

        // Показываем уведомление
        notificationHelper.showFactNotification(title, body)
    }
}