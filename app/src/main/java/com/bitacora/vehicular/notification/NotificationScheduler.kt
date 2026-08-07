package com.bitacora.vehicular.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val NOMBRE_TRABAJO = "recordatorios_vehiculo_periodico"

    /** Programa la revisión de vencimientos una vez al día. */
    fun programar(context: Context) {
        val request = PeriodicWorkRequestBuilder<RecordatorioWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            NOMBRE_TRABAJO,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
