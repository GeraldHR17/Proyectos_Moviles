package com.huertas.rivera.wikibusqueda.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huertas.rivera.wikibusqueda.data.repository.WikipediaRepository
import com.huertas.rivera.wikibusqueda.viewmodel.states.WikiUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.huertas.rivera.wikibusqueda.data.model.Page
class WikiViewModel : ViewModel() {

    private val repository = WikipediaRepository()
    private var searchJob: Job? = null

    private val _uiState = MutableStateFlow(WikiUiState())
    val uiState: StateFlow<WikiUiState> = _uiState.asStateFlow()

    fun onSearchQueryChanged(query: String) {

        searchJob?.cancel()

        if (query.isBlank()) {

            _uiState.value = _uiState.value.copy(
                query = query,
                articles = emptyList(),
                isLoading = false,
                error = null
            )

            return
        }

        _uiState.value = _uiState.value.copy(
            query = query,
            articles = emptyList(),
            isLoading = true,
            error = null
        )

        searchJob = viewModelScope.launch {
            delay(500)
            searchArticles(query)
        }
    }

    private suspend fun searchArticles(query: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )

        try {
            // Manejo seguro de la respuesta para evitar nulos
            val articlesFound =
                repository.searchArticles(query)

            _uiState.value = _uiState.value.copy(
                articles = articlesFound,
                isLoading = false
            )
        } catch (e: Exception) {
            Log.e("WikiViewModel", "Fallo en la petición a Wikipedia", e)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Error: ${e.localizedMessage ?: "Fallo de conexión"}"
            )
        }
    }


    fun showPreview(article: Page) {

        _uiState.value =
            _uiState.value.copy(
                selectedArticleForPreview = article
            )
    }

    fun dismissPreview() {

        _uiState.value =
            _uiState.value.copy(
                selectedArticleForPreview = null
            )
    }

}
