package com.bitacora.vehicular.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VehiculoDao {
    @Insert suspend fun insertar(v: Vehiculo): Long
    @Update suspend fun actualizar(v: Vehiculo)
    @Delete suspend fun eliminar(v: Vehiculo)
    @Query("SELECT * FROM vehiculos ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<Vehiculo>>
    @Query("SELECT * FROM vehiculos WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Vehiculo?
}

@Dao
interface CambioAceiteDao {
    @Insert suspend fun insertar(c: CambioAceite): Long
    @Update suspend fun actualizar(c: CambioAceite)
    @Delete suspend fun eliminar(c: CambioAceite)
    @Query("SELECT * FROM cambios_aceite WHERE vehiculoId = :vehiculoId ORDER BY fecha DESC")
    fun obtenerPorVehiculo(vehiculoId: Long): Flow<List<CambioAceite>>
    @Query("SELECT * FROM cambios_aceite WHERE vehiculoId = :vehiculoId ORDER BY fecha DESC LIMIT 1")
    suspend fun obtenerUltimo(vehiculoId: Long): CambioAceite?
    @Query("SELECT COALESCE(SUM(costo),0) FROM cambios_aceite WHERE vehiculoId = :vehiculoId")
    suspend fun sumaCostos(vehiculoId: Long): Double
    @Query("SELECT COALESCE(SUM(costo),0) FROM cambios_aceite WHERE vehiculoId = :vehiculoId AND fecha BETWEEN :desde AND :hasta")
    suspend fun sumaCostosEnRango(vehiculoId: Long, desde: String, hasta: String): Double
    @Query("SELECT * FROM cambios_aceite")
    suspend fun obtenerTodos(): List<CambioAceite>
}

@Dao
interface ReparacionDao {
    @Insert suspend fun insertar(r: Reparacion): Long
    @Update suspend fun actualizar(r: Reparacion)
    @Delete suspend fun eliminar(r: Reparacion)
    @Query("SELECT * FROM reparaciones WHERE vehiculoId = :vehiculoId ORDER BY fecha DESC")
    fun obtenerPorVehiculo(vehiculoId: Long): Flow<List<Reparacion>>
    @Query("SELECT COALESCE(SUM(costo),0) FROM reparaciones WHERE vehiculoId = :vehiculoId")
    suspend fun sumaCostos(vehiculoId: Long): Double
    @Query("SELECT COALESCE(SUM(costo),0) FROM reparaciones WHERE vehiculoId = :vehiculoId AND fecha BETWEEN :desde AND :hasta")
    suspend fun sumaCostosEnRango(vehiculoId: Long, desde: String, hasta: String): Double
    @Query("SELECT * FROM reparaciones")
    suspend fun obtenerTodos(): List<Reparacion>
}

@Dao
interface CompraAutoparteDao {
    @Insert suspend fun insertar(c: CompraAutoparte): Long
    @Update suspend fun actualizar(c: CompraAutoparte)
    @Delete suspend fun eliminar(c: CompraAutoparte)
    @Query("SELECT * FROM compras_autopartes WHERE vehiculoId = :vehiculoId ORDER BY fecha DESC")
    fun obtenerPorVehiculo(vehiculoId: Long): Flow<List<CompraAutoparte>>
    @Query("SELECT COALESCE(SUM(costo),0) FROM compras_autopartes WHERE vehiculoId = :vehiculoId")
    suspend fun sumaCostos(vehiculoId: Long): Double
    @Query("SELECT COALESCE(SUM(costo),0) FROM compras_autopartes WHERE vehiculoId = :vehiculoId AND fecha BETWEEN :desde AND :hasta")
    suspend fun sumaCostosEnRango(vehiculoId: Long, desde: String, hasta: String): Double
    @Query("SELECT * FROM compras_autopartes")
    suspend fun obtenerTodos(): List<CompraAutoparte>
}

@Dao
interface IncidenteDao {
    @Insert suspend fun insertar(i: Incidente): Long
    @Update suspend fun actualizar(i: Incidente)
    @Delete suspend fun eliminar(i: Incidente)
    @Query("SELECT * FROM incidentes WHERE vehiculoId = :vehiculoId ORDER BY fecha DESC")
    fun obtenerPorVehiculo(vehiculoId: Long): Flow<List<Incidente>>
    @Query("SELECT * FROM incidentes")
    suspend fun obtenerTodos(): List<Incidente>
}

@Dao
interface TecnomecanicaDao {
    @Insert suspend fun insertar(t: Tecnomecanica): Long
    @Update suspend fun actualizar(t: Tecnomecanica)
    @Delete suspend fun eliminar(t: Tecnomecanica)
    @Query("SELECT * FROM tecnomecanicas WHERE vehiculoId = :vehiculoId ORDER BY fechaVencimiento DESC")
    fun obtenerPorVehiculo(vehiculoId: Long): Flow<List<Tecnomecanica>>
    @Query("SELECT * FROM tecnomecanicas ORDER BY fechaVencimiento ASC")
    suspend fun obtenerTodas(): List<Tecnomecanica>
    @Query("SELECT COALESCE(SUM(valor),0) FROM tecnomecanicas WHERE vehiculoId = :vehiculoId")
    suspend fun sumaCostos(vehiculoId: Long): Double
    @Query("SELECT COALESCE(SUM(valor),0) FROM tecnomecanicas WHERE vehiculoId = :vehiculoId AND fechaExpedicion BETWEEN :desde AND :hasta")
    suspend fun sumaCostosEnRango(vehiculoId: Long, desde: String, hasta: String): Double
}

@Dao
interface SoatDao {
    @Insert suspend fun insertar(s: Soat): Long
    @Update suspend fun actualizar(s: Soat)
    @Delete suspend fun eliminar(s: Soat)
    @Query("SELECT * FROM soats WHERE vehiculoId = :vehiculoId ORDER BY fechaVencimiento DESC")
    fun obtenerPorVehiculo(vehiculoId: Long): Flow<List<Soat>>
    @Query("SELECT * FROM soats ORDER BY fechaVencimiento ASC")
    suspend fun obtenerTodos(): List<Soat>
    @Query("SELECT COALESCE(SUM(valor),0) FROM soats WHERE vehiculoId = :vehiculoId")
    suspend fun sumaCostos(vehiculoId: Long): Double
    @Query("SELECT COALESCE(SUM(valor),0) FROM soats WHERE vehiculoId = :vehiculoId AND fechaExpedicion BETWEEN :desde AND :hasta")
    suspend fun sumaCostosEnRango(vehiculoId: Long, desde: String, hasta: String): Double
}

@Dao
interface ImpuestoDao {
    @Insert suspend fun insertar(i: Impuesto): Long
    @Update suspend fun actualizar(i: Impuesto)
    @Delete suspend fun eliminar(i: Impuesto)
    @Query("SELECT * FROM impuestos WHERE vehiculoId = :vehiculoId ORDER BY fechaPago DESC")
    fun obtenerPorVehiculo(vehiculoId: Long): Flow<List<Impuesto>>
    @Query("SELECT COALESCE(SUM(valor),0) FROM impuestos WHERE vehiculoId = :vehiculoId")
    suspend fun sumaCostos(vehiculoId: Long): Double
    @Query("SELECT COALESCE(SUM(valor),0) FROM impuestos WHERE vehiculoId = :vehiculoId AND fechaPago BETWEEN :desde AND :hasta")
    suspend fun sumaCostosEnRango(vehiculoId: Long, desde: String, hasta: String): Double
    @Query("SELECT * FROM impuestos")
    suspend fun obtenerTodos(): List<Impuesto>
}

@Dao
interface OtroPagoDao {
    @Insert suspend fun insertar(o: OtroPago): Long
    @Update suspend fun actualizar(o: OtroPago)
    @Delete suspend fun eliminar(o: OtroPago)
    @Query("SELECT * FROM otros_pagos WHERE vehiculoId = :vehiculoId ORDER BY fecha DESC")
    fun obtenerPorVehiculo(vehiculoId: Long): Flow<List<OtroPago>>
    @Query("SELECT COALESCE(SUM(valor),0) FROM otros_pagos WHERE vehiculoId = :vehiculoId")
    suspend fun sumaCostos(vehiculoId: Long): Double
    @Query("SELECT COALESCE(SUM(valor),0) FROM otros_pagos WHERE vehiculoId = :vehiculoId AND fecha BETWEEN :desde AND :hasta")
    suspend fun sumaCostosEnRango(vehiculoId: Long, desde: String, hasta: String): Double
    @Query("SELECT * FROM otros_pagos")
    suspend fun obtenerTodos(): List<OtroPago>
}

@Dao
interface EnlaceDao {
    @Insert suspend fun insertar(e: Enlace): Long
    @Update suspend fun actualizar(e: Enlace)
    @Delete suspend fun eliminar(e: Enlace)
    @Query("SELECT * FROM enlaces WHERE vehiculoId = :vehiculoId ORDER BY fechaGuardado DESC")
    fun obtenerPorVehiculo(vehiculoId: Long): Flow<List<Enlace>>
    @Query("SELECT * FROM enlaces")
    suspend fun obtenerTodos(): List<Enlace>
}

@Dao
interface RegistroOdometroDao {
    @Insert suspend fun insertar(r: RegistroOdometro): Long
    @Delete suspend fun eliminar(r: RegistroOdometro)
    @Query("SELECT * FROM registros_odometro WHERE vehiculoId = :vehiculoId ORDER BY fecha DESC, id DESC LIMIT 1")
    suspend fun obtenerUltimo(vehiculoId: Long): RegistroOdometro?
    @Query("SELECT * FROM registros_odometro WHERE vehiculoId = :vehiculoId ORDER BY fecha DESC, id DESC")
    fun obtenerPorVehiculo(vehiculoId: Long): Flow<List<RegistroOdometro>>
    @Query("SELECT * FROM registros_odometro")
    suspend fun obtenerTodos(): List<RegistroOdometro>
}

@Dao
interface AbastecimientoDao {
    @Insert suspend fun insertar(a: Abastecimiento): Long
    @Update suspend fun actualizar(a: Abastecimiento)
    @Delete suspend fun eliminar(a: Abastecimiento)
    @Query("SELECT * FROM abastecimientos WHERE vehiculoId = :vehiculoId ORDER BY fecha DESC")
    fun obtenerPorVehiculo(vehiculoId: Long): Flow<List<Abastecimiento>>
    @Query("SELECT COALESCE(SUM(valor),0) FROM abastecimientos WHERE vehiculoId = :vehiculoId")
    suspend fun sumaCostos(vehiculoId: Long): Double
    @Query("SELECT COALESCE(SUM(valor),0) FROM abastecimientos WHERE vehiculoId = :vehiculoId AND fecha BETWEEN :desde AND :hasta")
    suspend fun sumaCostosEnRango(vehiculoId: Long, desde: String, hasta: String): Double
    @Query("SELECT * FROM abastecimientos")
    suspend fun obtenerTodos(): List<Abastecimiento>
}
