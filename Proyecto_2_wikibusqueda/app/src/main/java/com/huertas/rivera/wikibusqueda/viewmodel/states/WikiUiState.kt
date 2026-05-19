package com.huertas.rivera.wikibusqueda.viewmodel.states

import com.huertas.rivera.wikibusqueda.data.model.Page

data class WikiUiState(
    val query: String = "",
    val articles: List<Page> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedArticleForPreview: Page? = null,
)
