package com.huertas.rivera.wikibusqueda.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huertas.rivera.wikibusqueda.util.Constants
import com.huertas.rivera.wikibusqueda.viewmodel.WikiViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ArticleScreen(
    articleKey: String,
    articleTitle: String,
    articleDesc: String,
    viewModel: WikiViewModel,
    onBackClick: () -> Unit
) {
    // Un único punto de verdad — todo el estado viene del UiState
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrarHistorial by remember { mutableStateOf(false) }

    // Registra la consulta y carga el estado centralizado al abrir el artículo
    LaunchedEffect(articleKey) {
        viewModel.registrarConsulta(titulo = articleTitle, key = articleKey)
        viewModel.cargarEstadoArticulo(articleKey)
    }

    // Limpia el estado del artículo del UiState al salir de la pantalla
    DisposableEffect(articleKey) {
        onDispose { viewModel.limpiarEstadoArticulo() }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Top Bar ──────────────────────────────────────────────────────────
        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }

                    Text(
                        text = "Artículo",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )

                    // Favorito — lee desde uiState.isFavorito
                    IconButton(onClick = {
                        viewModel.toggleFavorito(
                            titulo = articleTitle,
                            url = articleKey,
                            descripcion = articleDesc
                        )
                    }) {
                        Icon(
                            imageVector = if (uiState.isFavorito)
                                Icons.Default.Favorite
                            else
                                Icons.Default.FavoriteBorder,
                            contentDescription = if (uiState.isFavorito)
                                "Quitar de favoritos"
                            else
                                "Agregar a favoritos",
                            tint = if (uiState.isFavorito)
                                Color.Red
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Contador — lee desde uiState.visitCount
                    Text(
                        text = "Consultas realizadas: ${uiState.visitCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = { mostrarHistorial = true }) {
                        Text("Ver Detalle")
                    }
                }
            }
        }

        // ── WebView ──────────────────────────────────────────────────────────
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        useWideViewPort = true
                        loadWithOverviewMode = true
                    }
                    webViewClient = WebViewClient()
                    loadUrl(Constants.ARTICLE_URL + articleKey)
                }
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
    }

    // ── Historial de consultas ────────────────────────────────────────────────
    if (mostrarHistorial) {
        Dialog(
            onDismissRequest = { mostrarHistorial = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Historial de consultas",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { mostrarHistorial = false }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Cerrar"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = articleTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Lista — lee desde uiState.consultas
                    if (uiState.consultas.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No hay consultas registradas.")
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.consultas) { consulta ->
                                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        val fecha = SimpleDateFormat(
                                            "dd/MM/yyyy hh:mm:ss a",
                                            Locale.getDefault()
                                        ).format(Date(consulta.fechaConsulta))
                                        Text(
                                            text = fecha,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}