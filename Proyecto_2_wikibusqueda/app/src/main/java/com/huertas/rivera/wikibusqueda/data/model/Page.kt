package com.huertas.rivera.wikibusqueda.data.model

import com.google.gson.annotations.SerializedName

data class Page(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("key") val key: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("excerpt")
    val excerpt: String? = null,
    @SerializedName("thumbnail") val thumbnail: Thumbnail? = null,
    val imageUrl: String? = null
)