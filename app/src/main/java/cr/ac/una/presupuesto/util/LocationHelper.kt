package cr.ac.una.presupuesto.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

object LocationHelper{
    fun obtenerUbicacion(
        context: Context,
        callback: (Double?, Double?) -> Unit)
    {
        if(ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
        )!= PackageManager.PERMISSION_GRANTED){
           callback(null, null)
            return
        }
        var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            if (location != null){
                callback(location.latitude, location.longitude)
        }
            else{
                callback(null, null)
    }
            }.addOnFailureListener {
                callback(null, null)
        }
         }
}