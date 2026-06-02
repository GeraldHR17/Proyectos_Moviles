package com.huertas.rivera.wikibusqueda.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favoritos")
data class Favorito(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val url: String, // Usaremos la URL o el key como identificador único para el artículo
    val descripcion: String,
    val fechaAgregado: Long = System.currentTimeMillis()
)
