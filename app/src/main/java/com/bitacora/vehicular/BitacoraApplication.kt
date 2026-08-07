package com.bitacora.vehicular

import android.app.Application
import com.bitacora.vehicular.notification.NotificationScheduler

class BitacoraApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationScheduler.programar(this)
    }
}
