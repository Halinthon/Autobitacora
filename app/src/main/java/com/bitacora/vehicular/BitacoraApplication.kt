package com.bitacora.vehicular

import android.app.Application
import com.bitacora.vehicular.notification.NotificationScheduler
import com.bitacora.vehicular.util.TemaPreferencias

class BitacoraApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TemaPreferencias.cargar(this)
        NotificationScheduler.programar(this)
    }
}
