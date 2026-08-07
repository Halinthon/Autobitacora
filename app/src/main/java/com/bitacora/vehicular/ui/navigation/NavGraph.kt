package com.bitacora.vehicular.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
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
                            navController.navigate(item.ruta) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
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
            composable("incidentes") { IncidentesScreen(viewModel) }
            composable("impuestos") { ImpuestosScreen(viewModel) }
            composable("otros_pagos") { OtrosPagosScreen(viewModel) }
            composable("enlaces") { EnlacesScreen(viewModel) }
            composable("backup") { BackupScreen(viewModel) }
        }
    }
}
