package com.huertas.rivera.wikibusqueda.data.model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de búsqueda optimizada para evitar errores de parseo.
 */
data class SearchResponse(
    @SerializedName("pages") val pages: List<Page>? = emptyList()
)
