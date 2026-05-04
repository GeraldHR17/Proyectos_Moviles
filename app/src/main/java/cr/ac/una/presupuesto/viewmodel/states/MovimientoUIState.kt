package cr.ac.una.presupuesto.viewmodel.states

import android.net.Uri
import cr.ac.una.presupuesto.data.model.Movimiento

data class MovimientoUIState(
    val showDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val showUpdateConfirmDialog: Boolean = false,
    val movimientoSeleccionado: Movimiento? = null,
    val monto: String = "",
    val tipo: String = "",
    val fecha: String = "",
    val montoError: Boolean = false,
    val tipoError: Boolean = false,
    val fechaError: Boolean = false,
    val imagenUri: Uri? = null
)
