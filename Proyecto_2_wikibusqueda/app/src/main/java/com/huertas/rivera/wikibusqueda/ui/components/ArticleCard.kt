package com.huertas.rivera.wikibusqueda.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.huertas.rivera.wikibusqueda.data.model.Page
import okhttp3.OkHttpClient

@Composable
fun ArticleCard(
    page: Page,
    onClick: (String, String, String) -> Unit, // Key, Titulo, Descripcion
    onLongClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // Configuramos un ImageLoader que incluya el User-Agent para evitar errores 403
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components { add(SvgDecoder.Factory()) }
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", "WikiBusqueda/1.0")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            }
            .build()
    }

    val displayImageUrl = page.imageUrl ?: page.thumbnail?.url?.let {
        if (it.startsWith("//")) "https:$it" else it
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            // Usamos pointerInput para detectar clics y pulsaciones largas de forma estable
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onClick(
                            page.key ?: "",
                            page.title ?: "",
                            page.description ?: "Información de Wikipedia"
                        )
                    },
                    onLongPress = {
                        onLongClick()
                    }
                )
            },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clip(RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context).data(displayImageUrl).build(),
                        imageLoader = imageLoader,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = {
                            Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    )
                }

                Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                    Text(
                        text = page.title ?: "Sin título",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = page.description ?: "Información de Wikipedia",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Limpieza del extracto (elimina etiquetas HTML como <span class="searchmatch">)
            val cleanExcerpt = page.excerpt?.replace(Regex("<[^>]*>"), "") ?: ""
            if (cleanExcerpt.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = cleanExcerpt,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
