package cr.ac.una.presupuesto.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object MapHelper {
    fun abrirMapa(context: Context, latitud: Double, longitud: Double) {
        val uri = Uri.parse("geo:$latitud,$longitud?q=$latitud,$longitud")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        context.startActivity(intent)
    }
}
