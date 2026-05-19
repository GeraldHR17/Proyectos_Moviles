package com.huertas.rivera.wikibusqueda.data.model

import com.google.gson.annotations.SerializedName

data class PageDetailResponse(

    @SerializedName("originalimage")
    val originalImage: OriginalImage? = null
)

data class OriginalImage(

    @SerializedName("source")
    val source: String? = null
)