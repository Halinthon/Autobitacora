package com.bitacora.vehicular

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.bitacora.vehicular.ui.navigation.BitacoraNavHost
import com.bitacora.vehicular.ui.theme.BitacoraVehicularTheme
import com.bitacora.vehicular.viewmodel.BitacoraViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BitacoraViewModel by viewModels { BitacoraViewModel.factory(application) }

    private val lanzadorPermisoNotificaciones =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* resultado ignorado: la app funciona igual sin el permiso */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            lanzadorPermisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            BitacoraVehicularTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BitacoraNavHost(viewModel)
                }
            }
        }
    }
}
