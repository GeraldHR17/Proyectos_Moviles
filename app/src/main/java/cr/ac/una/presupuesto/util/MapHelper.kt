package cr.ac.una.presupuesto.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object MapHelper {
    fun abrirMapa(
        context: Context,
        lat: Double,
        long: Double
    ){
        var uri = Uri.parse(
            "geo:$lat,$long?g=$lat,$long"
        )

        val intent = Intent(
            Intent.ACTION_VIEW,
            uri
        )

        intent.setPackage(
            "com.google.android.apps.map"
        )
        context.startActivity(intent)
    }
}