@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.bitacora.vehicular.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bitacora.vehicular.ui.screens.*
import com.bitacora.vehicular.viewmodel.BitacoraViewModel

data class ItemBarra(val ruta: String, val etiqueta: String, val icono: ImageVector)

private val itemsBarraInferior = listOf(
    ItemBarra("dashboard", "Inicio", Icons.Default.Home),
    ItemBarra("vehiculos", "Vehículo", Icons.Default.DirectionsCar),
    ItemBarra("mantenimiento", "Mantenim.", Icons.Default.Build),
    ItemBarra("documentos", "Documentos", Icons.Default.Description),
    ItemBarra("mas", "Más", Icons.Default.MoreHoriz)
)

/** Rutas secundarias, accedidas desde el menú "Más", que llevan su propia barra superior con botón atrás. */
private val titulosSecundarios = mapOf(
    "tarjeta_propiedad" to "Tarjeta de propiedad",
    "licencia_conduccion" to "Licencia de conducción",
    "incidentes" to "Incidentes",
    "impuestos" to "Impuestos",
    "otros_pagos" to "Otros pagos",
    "kilometraje" to "Kilometraje",
    "combustible" to "Combustible",
    "enlaces" to "Enlaces de interés",
    "backup" to "Respaldo de datos"
)

@Composable
fun BitacoraNavHost(viewModel: BitacoraViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val rutaActual = backStackEntry?.destination

                itemsBarraInferior.forEach { item ->
                    NavigationBarItem(
                        selected = rutaActual?.hierarchy?.any { it.route == item.ruta } == true,
                        onClick = {
                            // Sin saveState/restoreState: evita que "Más" quede en blanco al
                            // regresar desde una subpantalla (Incidentes, Impuestos, etc.).
                            navController.navigate(item.ruta) {
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(item.icono, contentDescription = item.etiqueta) },
                        label = { Text(item.etiqueta) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {
            composable("dashboard") { DashboardScreen(viewModel) }
            composable("vehiculos") { VehiculosScreen(viewModel) }
            composable("mantenimiento") { MantenimientoScreen(viewModel) }
            composable("documentos") { DocumentosScreen(viewModel) }
            composable("mas") { MasScreen(alSeleccionar = { ruta -> navController.navigate(ruta) }) }

            composable("tarjeta_propiedad") { ConBarraSuperior("tarjeta_propiedad", navController) { TarjetaPropiedadScreen(viewModel) } }
            composable("licencia_conduccion") { ConBarraSuperior("licencia_conduccion", navController) { LicenciaConduccionScreen(viewModel) } }
            composable("incidentes") { ConBarraSuperior("incidentes", navController) { IncidentesScreen(viewModel) } }
            composable("impuestos") { ConBarraSuperior("impuestos", navController) { ImpuestosScreen(viewModel) } }
            composable("otros_pagos") { ConBarraSuperior("otros_pagos", navController) { OtrosPagosScreen(viewModel) } }
            composable("kilometraje") { ConBarraSuperior("kilometraje", navController) { KilometrajeScreen(viewModel) } }
            composable("combustible") { ConBarraSuperior("combustible", navController) { CombustibleScreen(viewModel) } }
            composable("enlaces") { ConBarraSuperior("enlaces", navController) { EnlacesScreen(viewModel) } }
            composable("backup") { ConBarraSuperior("backup", navController) { BackupScreen(viewModel) } }
        }
    }
}

@Composable
private fun ConBarraSuperior(ruta: String, navController: NavHostController, contenido: @Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulosSecundarios[ruta] ?: "") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        androidx.compose.foundation.layout.Box(androidx.compose.ui.Modifier.padding(padding)) {
            contenido()
        }
    }
}
