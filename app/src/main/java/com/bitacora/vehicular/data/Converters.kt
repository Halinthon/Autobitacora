package com.bitacora.vehicular.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromTipoVehiculo(v: TipoVehiculo?): String? = v?.name

    @TypeConverter
    fun toTipoVehiculo(v: String?): TipoVehiculo? = v?.let { TipoVehiculo.valueOf(it) }

    @TypeConverter
    fun fromTipoIncidente(v: TipoIncidente?): String? = v?.name

    @TypeConverter
    fun toTipoIncidente(v: String?): TipoIncidente? = v?.let { TipoIncidente.valueOf(it) }

    @TypeConverter
    fun fromMedioPago(v: MedioPago?): String? = v?.name

    @TypeConverter
    fun toMedioPago(v: String?): MedioPago? = v?.let { MedioPago.valueOf(it) }
}
