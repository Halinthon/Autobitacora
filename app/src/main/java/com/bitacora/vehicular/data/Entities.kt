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
    val lugarMatricula: String,
    val fotoPath: String? = null
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

@Entity(
    tableName = "abastecimientos",
    foreignKeys = [ForeignKey(entity = Vehiculo::class, parentColumns = ["id"], childColumns = ["vehiculoId"], onDelete = CASCADE)]
)
data class Abastecimiento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehiculoId: Long,
    val fecha: LocalDate,
    val valor: Double,
    val galones: Double,
    val kilometraje: Int,
    val valorGalon: Double,
    val lugar: String // Coordenadas GPS "lat, lon" capturadas automáticamente al guardar
)

@Entity(
    tableName = "tarjetas_propiedad",
    foreignKeys = [ForeignKey(entity = Vehiculo::class, parentColumns = ["id"], childColumns = ["vehiculoId"], onDelete = CASCADE)]
)
data class TarjetaPropiedad(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehiculoId: Long,
    val numeroMotor: String,
    val vin: String,
    val chasis: String,
    val propietario: String,
    val cedula: String,
    val servicio: String,
    val fechaMatricula: LocalDate,
    val fechaExpedicion: LocalDate,
    val organismoTransito: String,
    val fotoPath: String? = null
)

@Entity(
    tableName = "licencias_conduccion",
    foreignKeys = [ForeignKey(entity = Vehiculo::class, parentColumns = ["id"], childColumns = ["vehiculoId"], onDelete = CASCADE)]
)
data class LicenciaConduccion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehiculoId: Long,
    val numero: String,
    val nombre: String,
    val fechaExpedicion: LocalDate,
    val fechaVencimiento: LocalDate,
    val categoria: String,
    val restricciones: String,
    val organismoTransito: String,
    val fotoPath: String? = null
)
