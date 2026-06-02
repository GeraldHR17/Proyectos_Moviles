package com.huertas.rivera.wikibusqueda.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articulo_consulta")
data class ArticuloConsulta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val key: String,
    val fechaConsulta: Long = System.currentTimeMillis()
)