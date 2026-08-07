package com.bitacora.vehicular.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        Vehiculo::class, CambioAceite::class, Reparacion::class, CompraAutoparte::class,
        Incidente::class, Tecnomecanica::class, Soat::class, Impuesto::class,
        OtroPago::class, Enlace::class, RegistroOdometro::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehiculoDao(): VehiculoDao
    abstract fun cambioAceiteDao(): CambioAceiteDao
    abstract fun reparacionDao(): ReparacionDao
    abstract fun compraAutoparteDao(): CompraAutoparteDao
    abstract fun incidenteDao(): IncidenteDao
    abstract fun tecnomecanicaDao(): TecnomecanicaDao
    abstract fun soatDao(): SoatDao
    abstract fun impuestoDao(): ImpuestoDao
    abstract fun otroPagoDao(): OtroPagoDao
    abstract fun enlaceDao(): EnlaceDao
    abstract fun registroOdometroDao(): RegistroOdometroDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bitacora_vehicular.db"
                ).build().also { INSTANCE = it }
            }
    }
}
