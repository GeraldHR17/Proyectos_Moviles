package cr.ac.una.presupuesto.data.model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Movimiento(
    var id: String = "",
    var monto: Double = 0.0,
    var tipo: String = "",
    var fecha: String = "",
    var imagenUrl: String? = null,
    var longitud: Double? = null,
    var latitud: Double? = null

): Serializable
