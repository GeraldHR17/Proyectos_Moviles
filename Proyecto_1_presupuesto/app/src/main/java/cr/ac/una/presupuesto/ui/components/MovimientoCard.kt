package cr.ac.una.presupuesto.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import cr.ac.una.presupuesto.data.model.Movimiento

@Composable
fun MovimientoCard(
    movimiento: Movimiento,
    onEdit: (Movimiento) -> Unit,
    onDelete: (Movimiento) -> Unit,
    onVerMapa: (Double, Double) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onEdit(movimiento) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen o Placeholder
            if (!movimiento.imagenUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = movimiento.imagenUrl,
                    contentDescription = "Imagen",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (movimiento.tipo == "Crédito") "+" else "-",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del movimiento
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movimiento.tipo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (movimiento.tipo == "Crédito") Color(0xFF4CAF50) else Color(0xFFF44336)
                )
                Text(
                    text = movimiento.fecha,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "₡${movimiento.monto}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Botones de acción
            Row {
                // Icono de ubicación (solo si tiene coordenadas)
                if (movimiento.latitud != null && movimiento.longitud != null) {
                    IconButton(onClick = {
                        onVerMapa(movimiento.latitud!!, movimiento.longitud!!)
                    }) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Ver ubicación",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(onClick = { onDelete(movimiento) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
