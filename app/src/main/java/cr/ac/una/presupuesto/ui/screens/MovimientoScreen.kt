package cr.ac.una.presupuesto.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cr.ac.una.presupuesto.ui.components.MovimientoCard
import cr.ac.una.presupuesto.viewmodel.MovimientoViewModel

@Composable
fun MovimientoScreen(
    viewModel: MovimientoViewModel
) {
    val uiState = viewModel.uiState

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.abrirDialog() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar"
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn {
                items(viewModel.listaMovimientos) { mov ->
                    MovimientoCard(
                        movimiento = mov,
                        onEdit = { viewModel.abrirDialog(it) },
                        onDelete = { viewModel.confirmarEliminar(it) }
                    )
                }
            }

            if (uiState.showDialog) {
                MovimientoDialog(viewModel)
            }

            if (uiState.showDeleteConfirmDialog) {
                ConfirmDeleteDialog(viewModel)
            }

            if (uiState.showUpdateConfirmDialog) {
                ConfirmUpdateDialog(viewModel)
            }
        }
    }
}

@Composable
fun ConfirmDeleteDialog(viewModel: MovimientoViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.cancelarAccion() },
        title = { Text("Confirmar eliminación") },
        text = { Text("¿Está seguro de que desea eliminar este registro?") },
        confirmButton = {
            TextButton(onClick = { viewModel.eliminarConfirmado() }) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.cancelarAccion() }) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ConfirmUpdateDialog(viewModel: MovimientoViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.cancelarAccion() },
        title = { Text("Confirmar actualización") },
        text = { Text("¿Está seguro de que desea actualizar este registro?") },
        confirmButton = {
            TextButton(onClick = { viewModel.actualizarConfirmado() }) {
                Text("Actualizar")
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.cancelarAccion() }) {
                Text("Cancelar")
            }
        }
    )
}
