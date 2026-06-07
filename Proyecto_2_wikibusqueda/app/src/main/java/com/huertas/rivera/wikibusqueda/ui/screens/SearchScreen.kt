package com.huertas.rivera.wikibusqueda.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huertas.rivera.wikibusqueda.ui.components.EmptyState
import com.huertas.rivera.wikibusqueda.ui.components.LoadingIndicator
import com.huertas.rivera.wikibusqueda.ui.components.SearchBar
import com.huertas.rivera.wikibusqueda.viewmodel.WikiViewModel
import com.huertas.rivera.wikibusqueda.ui.components.ArticleList
import com.huertas.rivera.wikibusqueda.ui.components.WelcomeAnimation

@Composable
fun SearchScreen(
    onArticleClick: (String, String, String) -> Unit,
    onNavigateToFavoritos: () -> Unit,
    wikiViewModel: WikiViewModel = viewModel()
) {
    val uiState by wikiViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SearchBar(
                        query = uiState.query,
                        onQueryChange = {
                            wikiViewModel.onSearchQueryChanged(it)
                        }
                    )
                }
                IconButton(onClick = onNavigateToFavoritos) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Ir a Favoritos",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (uiState.articles.isNotEmpty()) {
            Text(
                text = "Manten presionado un artículo para ver una vista previa",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                uiState.isLoading && uiState.articles.isEmpty() -> {
                    LoadingIndicator()
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "Error desconocido",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.articles.isEmpty() && uiState.query.isNotBlank() && !uiState.isLoading -> {
                    EmptyState()
                }
                uiState.articles.isEmpty() && uiState.query.isBlank() && !uiState.isLoading -> {
                    WelcomeAnimation()
                }
                else -> {
                    ArticleList(
                        articles = uiState.articles,
                        onArticleClick = { key, title, desc ->
                            onArticleClick(key, title, desc)
                        },
                        onArticleLongClick = { article ->       // ← esto es lo que falta
                            wikiViewModel.showPreview(article)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    uiState.selectedArticleForPreview?.let { article ->
        Dialog(onDismissRequest = { wikiViewModel.dismissPreview() }) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = article.title ?: "Sin título",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = article.description ?: "Wikipedia",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    val cleanExcerpt = article.excerpt?.replace(Regex("<[^>]*>"), "") ?: ""
                    Text(text = cleanExcerpt, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            wikiViewModel.dismissPreview()
                            onArticleClick(article.key ?: "", article.title ?: "", article.description ?: "Información de Wikipedia")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Leer artículo completo")
                    }
                }
            }
        }
    }
}
