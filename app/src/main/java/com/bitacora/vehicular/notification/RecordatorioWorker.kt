package com.bitacora.vehicular.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bitacora.vehicular.data.Repository
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private const val CANAL_ID = "recordatorios_vehiculo"
private const val DIAS_ALERTA_DOCUMENTOS = 30L
private const val KM_ALERTA_ACEITE = 500

class RecordatorioWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val repo = Repository(applicationContext)
        crearCanalNotificaciones()

        val hoy = LocalDate.now()
        var idNotificacion = 1000

        // SOAT y tecnomecánica próximos a vencer, de todos los vehículos
        repo.soatDao.obtenerTodos().forEach { soat ->
            val dias = ChronoUnit.DAYS.between(hoy, soat.fechaVencimiento)
            if (dias in 0..DIAS_ALERTA_DOCUMENTOS) {
                notificar(idNotificacion++, "SOAT por vencer", "Vence en $dias días (${soat.fechaVencimiento})")
            }
        }
        repo.tecnomecanicaDao.obtenerTodas().forEach { tm ->
            val dias = ChronoUnit.DAYS.between(hoy, tm.fechaVencimiento)
            if (dias in 0..DIAS_ALERTA_DOCUMENTOS) {
                notificar(idNotificacion++, "Tecnomecánica por vencer", "Vence en $dias días (${tm.fechaVencimiento})")
            }
        }

        // Próximo cambio de aceite, comparando con el último kilometraje registrado
        repo.vehiculoDao.obtenerTodos()
        val vehiculos = repo.registroOdometroDao.obtenerTodos()
        // Agrupar el último km por vehiculoId
        val ultimoKmPorVehiculo = vehiculos.groupBy { it.vehiculoId }
            .mapValues { entry -> entry.value.maxByOrNull { it.fecha } }

        ultimoKmPorVehiculo.forEach { (vehiculoId, registro) ->
            val kmActual = registro?.kilometraje ?: return@forEach
            val ultimoCambio = repo.cambioAceiteDao.obtenerUltimo(vehiculoId) ?: return@forEach
            val restante = ultimoCambio.proximoCambioKm - kmActual
            if (restante in 0..KM_ALERTA_ACEITE) {
                notificar(idNotificacion++, "Cambio de aceite cerca", "Faltan $restante km para el próximo cambio")
            }
        }

        return Result.success()
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID, "Recordatorios de vehículo", NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    private fun notificar(id: Int, titulo: String, texto: String) {
        val notificacion = NotificationCompat.Builder(applicationContext, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = androidx.core.app.NotificationManagerCompat.from(applicationContext)
        runCatching { manager.notify(id, notificacion) }
    }
}
