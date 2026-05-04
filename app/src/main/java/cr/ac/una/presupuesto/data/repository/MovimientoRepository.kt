package cr.ac.una.presupuesto.data.repository

import android.net.Uri
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import cr.ac.una.presupuesto.data.model.Movimiento
import java.util.UUID

class MovimientoRepository {

    private val db = FirebaseDatabase.getInstance().getReference("movimientos")

    fun guardarMovimiento(movimiento: Movimiento) {
        val id = if (movimiento.id.isEmpty()) db.push().key else movimiento.id
        movimiento.id = id!!
        db.child(id).setValue(movimiento)
    }

    fun guardarMovimientoConImagen(movimiento: Movimiento, imagenUri: Uri?) {
        if (imagenUri != null) {
            val storage = FirebaseStorage.getInstance()
            val reference = storage.reference.child("movimientos/${UUID.randomUUID()}.jpg")

            // Guardamos temporalmente la URI local para que no viaje vacío si el upload tarda o falla
            movimiento.imagenUrl = imagenUri.toString()

            reference.putFile(imagenUri).continueWithTask { task ->
                if (!task.isSuccessful) task.exception?.let { throw it }
                reference.downloadUrl
            }.addOnSuccessListener { uri ->
                movimiento.imagenUrl = uri.toString()
                guardarMovimiento(movimiento)
            }.addOnFailureListener {
                // Si el storage falla, guardamos el movimiento con la URI local string como fallback
                guardarMovimiento(movimiento)
            }
        } else {
            guardarMovimiento(movimiento)
        }
    }

    fun actualizarMovimiento(movimiento: Movimiento) {
        db.child(movimiento.id).setValue(movimiento)
    }

    fun eliminarMovimiento(id: String) {
        db.child(id).removeValue()
    }

    fun obtenerMovimientos(
        onResult: (List<Movimiento>) -> Unit
    ) {
        db.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val lista = mutableListOf<Movimiento>()
                    for (dato in p0.children) {
                        val mov = dato.getValue(Movimiento::class.java)
                        mov?.let { lista.add(it) }
                    }
                    onResult(lista)
                }

                override fun onCancelled(p0: DatabaseError) {}
            }
        )
    }
}
