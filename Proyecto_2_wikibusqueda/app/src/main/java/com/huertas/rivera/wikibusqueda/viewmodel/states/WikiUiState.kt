package com.huertas.rivera.wikibusqueda.viewmodel.states

import com.huertas.rivera.wikibusqueda.data.entity.ArticuloConsulta
import com.huertas.rivera.wikibusqueda.data.model.Page

data class WikiUiState(
    val query: String = "",
    val articles: List<Page> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedArticleForPreview: Page? = null,

    val visitCount: Int = 0,
    val isFavorito: Boolean = false,
    val consultas: List<ArticuloConsulta> = emptyList()
)