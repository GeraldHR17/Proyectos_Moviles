package com.huertas.rivera.wikibusqueda.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huertas.rivera.wikibusqueda.ui.components.ArticleCard
import com.huertas.rivera.wikibusqueda.ui.components.EmptyState
import com.huertas.rivera.wikibusqueda.ui.components.LoadingIndicator

import com.huertas.rivera.wikibusqueda.ui.components.SearchBar
import com.huertas.rivera.wikibusqueda.viewmodel.WikiViewModel
import com.huertas.rivera.wikibusqueda.data.model.Page
import com.huertas.rivera.wikibusqueda.ui.components.ArticleList
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SearchScreen(
    onArticleClick: (String) -> Unit,
    wikiViewModel: WikiViewModel = viewModel()
) {

    val uiState by wikiViewModel.uiState.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        SearchBar(
            query = uiState.query,

            onQueryChange = {
                wikiViewModel.onSearchQueryChanged(it)
            }
        )
        if (uiState.articles.isNotEmpty()) {

            Text(
                text = "Mantén presionado un artículo para ver la vista previa",

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),

                style = MaterialTheme.typography.bodySmall,

                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))
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

                uiState.articles.isEmpty()
                        && uiState.query.isNotBlank()
                        && !uiState.isLoading -> {

                    EmptyState()
                }

                else -> {

                    ArticleList(

                        articles = uiState.articles,

                        onArticleClick = onArticleClick,

                        onArticleLongClick = { article ->
                            wikiViewModel.showPreview(article)
                        },

                        modifier = Modifier.fillMaxSize()
                    )

                    if (
                        uiState.isLoading &&
                        uiState.articles.isNotEmpty()
                    ) {

                        Box(
                            modifier = Modifier.fillMaxSize(),

                            contentAlignment = Alignment.TopCenter
                        ) {


                        }
                    }
                }
            }
        }
    }

    if (uiState.selectedArticleForPreview != null) {

        val article = uiState.selectedArticleForPreview!!

        val cleanExcerpt =
            article.excerpt
                ?.replace(Regex("<[^>]*>"), "")
                ?: ""

        ModalBottomSheet(

            onDismissRequest = {
                wikiViewModel.dismissPreview()
            },

            sheetState = sheetState,

            containerColor = MaterialTheme.colorScheme.surface

        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 32.dp)
            ) {

                Text(
                    text = "Vista previa del artículo",

                    style = MaterialTheme.typography.labelMedium,

                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column {

                    Text(
                        text = article.title ?: "Sin título",

                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = article.description
                            ?: "Información de Wikipedia",

                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = cleanExcerpt.ifBlank {
                        "No hay un extracto disponible para este artículo."
                    },

                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 24.sp
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(

                    onClick = {

                        wikiViewModel.dismissPreview()

                        onArticleClick(article.key ?: "")
                    },

                    modifier = Modifier.fillMaxWidth(),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {

                    Text("Leer artículo completo")
                }
            }
        }
    }
}

