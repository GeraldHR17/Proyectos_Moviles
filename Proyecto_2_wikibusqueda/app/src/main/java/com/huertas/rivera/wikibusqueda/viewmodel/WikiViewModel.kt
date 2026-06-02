package com.huertas.rivera.wikibusqueda.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.huertas.rivera.wikibusqueda.data.WikiDatabase
import com.huertas.rivera.wikibusqueda.data.entity.ArticuloConsulta
import com.huertas.rivera.wikibusqueda.data.entity.Favorito
import com.huertas.rivera.wikibusqueda.data.model.Page
import com.huertas.rivera.wikibusqueda.data.repository.WikipediaRepository
import com.huertas.rivera.wikibusqueda.viewmodel.states.WikiUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WikiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WikipediaRepository()
    private val db = WikiDatabase.getInstance(application)
    private val dao = db.articuloConsultaDao()
    private val favoritoDao = db.favoritoDao()

    private var searchJob: Job? = null

    private val _uiState = MutableStateFlow(WikiUiState())
    val uiState: StateFlow<WikiUiState> = _uiState.asStateFlow()

    // Flow reactivo de favoritos
    val favoritos: StateFlow<List<Favorito>> = favoritoDao.getAllFavoritos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        try {
            val articlesFound = repository.searchArticles(query)
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
        _uiState.value = _uiState.value.copy(selectedArticleForPreview = article)
    }

    fun dismissPreview() {
        _uiState.value = _uiState.value.copy(selectedArticleForPreview = null)
    }

    // ─── Room: registrar consulta al abrir un artículo ───────────────────────

    fun registrarConsulta(titulo: String, key: String) {
        viewModelScope.launch {
            dao.insert(
                ArticuloConsulta(
                    titulo = titulo,
                    key = key
                )
            )
        }
    }

    // ─── Room: Favoritos ─────────────────────────────────────────────────────

    fun toggleFavorito(titulo: String, url: String, descripcion: String) {
        viewModelScope.launch {
            val currentFavorito = favoritoDao.getFavoritoByUrl(url).firstOrNull()
            if (currentFavorito != null) {
                favoritoDao.deleteFavorito(currentFavorito)
            } else {
                favoritoDao.insertFavorito(
                    Favorito(titulo = titulo, url = url, descripcion = descripcion)
                )
            }
        }
    }

    fun deleteFavorito(favorito: Favorito) {
        viewModelScope.launch {
            favoritoDao.deleteFavorito(favorito)
        }
    }

    fun isFavorito(url: String): Flow<Boolean> = favoritoDao.isFavorito(url)

    // ─── Room: Flow de cantidad de consultas para un artículo ────────────────

    fun getCantidadConsultas(key: String): Flow<Int> =
        dao.getCantidadConsultas(key)

    // ─── Room: Flow de lista de fechas de consulta ───────────────────────────

    fun getConsultasByKey(key: String): Flow<List<ArticuloConsulta>> =
        dao.getConsultasByKey(key)
}
