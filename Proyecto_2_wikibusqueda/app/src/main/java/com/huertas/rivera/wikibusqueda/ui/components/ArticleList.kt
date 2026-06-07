package com.huertas.rivera.wikibusqueda.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.huertas.rivera.wikibusqueda.data.model.Page

@Composable
fun ArticleList(
    articles: List<Page>,
    onArticleClick: (String, String, String) -> Unit, // Key, Titulo, Descripcion
    onArticleLongClick: (Page) -> Unit, // Añadido para la vista previa
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(articles) { article ->
            ArticleCard(
                page = article,
                onClick = onArticleClick,
                onLongClick = { onArticleLongClick(article) }
            )
        }
    }
}
