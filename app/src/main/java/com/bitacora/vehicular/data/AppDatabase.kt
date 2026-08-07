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
        OtroPago::class, Enlace::class, RegistroOdometro::class, Abastecimiento::class,
        TarjetaPropiedad::class, LicenciaConduccion::class
    ],
    version = 3,
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
    abstract fun abastecimientoDao(): AbastecimientoDao
    abstract fun tarjetaPropiedadDao(): TarjetaPropiedadDao
    abstract fun licenciaConduccionDao(): LicenciaConduccionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bitacora_vehicular.db"
                )
                    // No hay usuarios en producción todavía: si la estructura cambia,
                    // se recrea la base de datos en vez de escribir migraciones manuales.
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
    }
}
