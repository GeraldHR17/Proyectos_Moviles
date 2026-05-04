package cr.ac.una.presupuesto.ui.screens

import android.Manifest
import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import cr.ac.una.presupuesto.util.crearUriImage
import cr.ac.una.presupuesto.viewmodel.MovimientoViewModel
import java.util.*

@Composable
fun MovimientoDialog(
    viewModel: MovimientoViewModel
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val opcionesTipo = listOf("Ingreso", "Egreso")

    // DatePicker refactorizado para usar DatePickerDialog estándar y evitar ExperimentalMaterial3Api
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year)
            viewModel.onFechaChange(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    var localUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.actualizarImagen(localUri)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = crearUriImage(context)
            localUri = uri
            cameraLauncher.launch(uri)
        }
    }

    AlertDialog(
        onDismissRequest = { viewModel.cerrarDialog() },
        title = {
            Text(
                text = if (uiState.movimientoSeleccionado == null) "Nuevo Movimiento" else "Modificar Movimiento",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.monto,
                    onValueChange = { viewModel.onMontoChange(it) },
                    label = { Text("Monto") },
                    prefix = { Text("₡") },
                    isError = uiState.montoError,
                    supportingText = {
                        if (uiState.montoError) {
                            Text("Ingrese un monto válido")
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Dropdown refactorizado para evitar ExperimentalMaterial3Api
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = uiState.tipo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Desplegar"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        isError = uiState.tipoError,
                        supportingText = {
                            if (uiState.tipoError) {
                                Text("Seleccione un tipo")
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        opcionesTipo.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    viewModel.onTipoChange(opcion)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.fecha,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha") },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Seleccionar fecha"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    isError = uiState.fechaError,
                    supportingText = {
                        if (uiState.fechaError) {
                            Text("Seleccione una fecha")
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Tomar foto")
                }

                // Vista previa de la imagen (local o remota)
                val imageSource = uiState.imagenUri ?: uiState.movimientoSeleccionado?.imagenUrl
                if (imageSource != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = imageSource,
                            contentDescription = "Vista previa",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.cerrarDialog() }) {
                Text("Cancelar")
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.guardarMovimiento() },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (uiState.movimientoSeleccionado == null) "Guardar" else "Actualizar")
            }
        }
    )
}
