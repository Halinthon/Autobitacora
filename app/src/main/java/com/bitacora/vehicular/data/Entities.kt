package com.bitacora.vehicular.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class TipoVehiculo { MOTO, AUTO }
enum class TipoIncidente { PINCHAZO, MECANICA, ELECTRICO, GOLPE }
enum class MedioPago { DIGITAL, PRESENCIAL }

@Entity(tableName = "vehiculos")
data class Vehiculo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tipo: TipoVehiculo,
    val placa: String,
    val marca: String,
    val modeloAnio: Int,
    val lugarMatricula: String
)

private const val CASCADE = ForeignKey.CASCADE

@Entity(
    tableName = "cambios_aceite",
    foreignKeys = [ForeignKey(entity = Vehiculo::class, parentColumns = ["id"], childColumns = ["vehiculoId"], onDelete = CASCADE)]
)
data class CambioAceite(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehiculoId: Long,
    val fecha: LocalDate,
    val kilometraje: Int,
    val proximoCambioKm: Int,
    val costo: Double,
    val lugar: String,
    val fotoPath: String? = null
)

@Entity(
    tableName = "reparaciones",
    foreignKeys = [ForeignKey(entity = Vehiculo::class, parentColumns = ["id"], childColumns = ["vehiculoId"], onDelete = CASCADE)]
)
data class Reparacion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehiculoId: Long,
    val fecha: LocalDate,
    val resumen: String,
    val costo: Double,
    val lugar: String,
    val fotoPath: String? = null
)

@Entity(
    tableName = "compras_autopartes",
    foreignKeys = [ForeignKey(entity = Vehiculo::class, parentColumns = ["id"], childColumns = ["vehiculoId"], onDelete = CASCADE)]
)
data class CompraAutoparte(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehiculoId: Long,
    val fecha: LocalDate,
    val nombreParte: String,
    val costo: Double,
    val lugar: String,
    val fotoPath: String? = null
)

@Entity(
    tableName = "incidentes",
    foreignKeys = [ForeignKey(entity = Vehiculo::class, parentColumns = ["id"], childColumns = ["vehiculoId"], onDelete = CASCADE)]
)
data class Incidente(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehiculoId: Long,
    val fecha: LocalDate,
    val tipo: TipoIncidente,
    val lugar: String,
    val fotoPath: String? = null
)

@Entity(
    tableName = "tecnomecanicas",
    foreignKeys = [ForeignKey(entity = Vehiculo::class, parentColumns = ["id"], childColumns = ["vehiculoId"], onDelete = CASCADE)]
)
data class Tecnomecanica(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehiculoId: Long,
    val fechaExpedicion: LocalDate,
    val fechaVencimiento: LocalDate,
    val valor: Double,
    val lugar: String,
    val fotoPath: String? = null
)

@Entity(
    tableName = "soats",
    foreignKeys = [ForeignKey(entity = Vehiculo::class, parentColumns = ["id"], childColumns = ["vehiculoId"], onDelete = CASCADE)]
)
data class Soat(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehiculoId: Long,
    val fechaExpedicion: LocalDate,
    val fechaVencimiento: LocalDate,
    val valor: Double,
    val lugar: String,
    val fotoPath: String? = null
)

@Entity(
    tableName = "impuestos",
    foreignKeys = [ForeignKey(entity = Vehiculo::class, parentColumns = ["id"], childColumns = ["vehiculoId"], onDelete = CASCADE)]
)
data class Impuesto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehiculoId: Long,
    val fechaPago: LocalDate,
    val valor: Double,
    val nroRecibo: String,
    val medioPago: MedioPago,
    val fotoPath: String? = null
)

@Entity(
    tableName = "otros_pagos",
    foreignKeys = [ForeignKey(entity = Vehiculo::class, parentColumns = ["id"], childColumns = ["vehiculoId"], onDelete = CASCADE)]
)
data class OtroPago(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehiculoId: Long,
    val descripcion: String,
    val fecha: LocalDate,
    val valor: Double,
    val fotoPath: String? = null
)

@Entity(
    tableName = "enlaces",
    foreignKeys = [ForeignKey(entity = Vehiculo::class, parentColumns = ["id"], childColumns = ["vehiculoId"], onDelete = CASCADE)]
)
data class Enlace(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehiculoId: Long,
    val titulo: String,
    val url: String,
    val nota: String,
    val fechaGuardado: LocalDate,
    val categoria: String
)

@Entity(
    tableName = "registros_odometro",
    foreignKeys = [ForeignKey(entity = Vehiculo::class, parentColumns = ["id"], childColumns = ["vehiculoId"], onDelete = CASCADE)]
)
data class RegistroOdometro(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehiculoId: Long,
    val fecha: LocalDate,
    val kilometraje: Int
)
