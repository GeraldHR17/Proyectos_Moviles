package cr.ac.una.presupuesto.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.*

@Composable
fun MapaScreen(
    latitud: Double,
    longitud: Double,
    onVolver: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        val ubicacion = LatLng(latitud, longitud)
        val cameraPositionState =
            rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    ubicacion,
                    16f
                )
            }
        GoogleMap(
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(
                    position = ubicacion
                ),
                title = "Ubicación"
            )
        }
        IconButton(
            onClick = onVolver,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
        }
    }
}