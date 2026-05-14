package cr.ac.una.presupuesto.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import cr.ac.una.presupuesto.data.model.Movimiento
import cr.ac.una.presupuesto.data.repository.MovimientoRepository
import cr.ac.una.presupuesto.util.LocationHelper
import cr.ac.una.presupuesto.viewmodel.states.MovimientoUIState

class MovimientoViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = MovimientoRepository()
    var listaMovimientos = mutableStateListOf<Movimiento>()

    var uiState by mutableStateOf(MovimientoUIState())
        private set

    val balanceTotal by derivedStateOf {
        listaMovimientos.sumOf { mov ->
            if (mov.tipo == "Crédito") mov.monto else -mov.monto
        }
    }

    init {
        cargarMovimientos()
    }

    private fun cargarMovimientos() {
        repo.obtenerMovimientos { lista ->
            listaMovimientos.apply {
                clear()
                addAll(lista)
            }
        }
    }

    fun actualizarImagen(uri: Uri?) {
        uiState = uiState.copy(imagenUri = uri)
    }

    fun onMontoChange(monto: String) {
        if (monto.all { char -> char.isDigit() || char == '.' }) {
            uiState = uiState.copy(monto = monto, montoError = false)
        }
    }

    fun onTipoChange(tipo: String) {
        uiState = uiState.copy(tipo = tipo, tipoError = false)
    }

    fun onFechaChange(fecha: String) {
        uiState = uiState.copy(fecha = fecha, fechaError = false)
    }

    fun abrirDialog(movimiento: Movimiento? = null) {
        uiState = if (movimiento != null) {
            MovimientoUIState(
                showDialog = true,
                movimientoSeleccionado = movimiento,
                monto = movimiento.monto.toString(),
                tipo = movimiento.tipo,
                fecha = movimiento.fecha
            )
        } else {
            MovimientoUIState(showDialog = true)
        }
    }

    fun cerrarDialog() {
        uiState = uiState.copy(showDialog = false)
        limpiarFormulario()
    }

    fun confirmarEliminar(movimiento: Movimiento) {
        uiState = uiState.copy(movimientoSeleccionado = movimiento, showDeleteConfirmDialog = true)
    }

    fun eliminarConfirmado() {
        uiState.movimientoSeleccionado?.let { repo.eliminarMovimiento(it.id) }
        uiState = uiState.copy(showDeleteConfirmDialog = false, movimientoSeleccionado = null)
    }

    private fun validarCampos(): Boolean {
        val montoErr = uiState.monto.isBlank() || uiState.monto.toDoubleOrNull() == null
        val tipoErr = uiState.tipo.isBlank()
        val fechaErr = uiState.fecha.isBlank()
        uiState = uiState.copy(montoError = montoErr, tipoError = tipoErr, fechaError = fechaErr)
        return !montoErr && !tipoErr && !fechaErr
    }

    fun guardarMovimiento() {
        if (validarCampos()) {
            LocationHelper.obtenerUbicacion(getApplication()) { latitud, longitud ->
                val movimiento = uiState.movimientoSeleccionado?.copy(
                    monto = uiState.monto.toDouble(),
                    tipo = uiState.tipo,
                    fecha = uiState.fecha,
                    latitud = latitud ?: uiState.movimientoSeleccionado?.latitud,
                    longitud = longitud ?: uiState.movimientoSeleccionado?.longitud
                ) ?: Movimiento(
                    monto = uiState.monto.toDouble(),
                    tipo = uiState.tipo,
                    fecha = uiState.fecha,
                    latitud = latitud,
                    longitud = longitud
                )

                repo.guardarMovimientoConImagen(movimiento, uiState.imagenUri)
                cerrarDialog()
            }
        }
    }

    fun actualizarConfirmado() {
        guardarMovimiento()
        uiState = uiState.copy(showUpdateConfirmDialog = false)
    }

    fun cancelarAccion() {
        uiState = uiState.copy(showUpdateConfirmDialog = false, showDeleteConfirmDialog = false)
    }

    private fun limpiarFormulario() {
        uiState = MovimientoUIState()
    }
}
