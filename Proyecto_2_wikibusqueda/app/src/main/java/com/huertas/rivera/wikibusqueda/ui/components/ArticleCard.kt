package com.huertas.rivera.wikibusqueda.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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

import coil.size.Size

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArticleCard(

    page: Page,

    onClick: () -> Unit,

    onLongClick: () -> Unit = {}
) {

    val context = LocalContext.current

    val imageLoader = remember {

        ImageLoader.Builder(context)

            .components {
                add(SvgDecoder.Factory())
            }

            .okHttpClient {

                OkHttpClient.Builder()

                    .addInterceptor { chain ->

                        val request = chain.request().newBuilder()

                            .header(
                                "User-Agent",
                                "WikiBusqueda/1.0 (Android)"
                            )

                            .build()

                        chain.proceed(request)
                    }

                    .build()
            }

            .crossfade(true)

            .build()
    }

    val fallbackImageUrl = page.thumbnail?.url?.let {

        if (it.startsWith("//")) "https:$it"
        else it
    }

    val cleanExcerpt =
        page.excerpt?.replace(Regex("<[^>]*>"), "")
            ?: ""

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(2.dp)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),

        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),

        shape = RoundedCornerShape(0.dp)
    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
                .padding(12.dp)
        ) {

            Row(

                verticalAlignment = Alignment.CenterVertically,

                modifier = Modifier.fillMaxWidth()
            ) {

                Box(

                    modifier = Modifier
                        .size(90.dp)
                        .background(Color.Black.copy(alpha = 0.05f))
                        .border(
                            3.dp,
                            MaterialTheme.colorScheme.onSurface
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    SubcomposeAsyncImage(

                        model = ImageRequest.Builder(context)
                            .data(fallbackImageUrl)
                            .size(Size.ORIGINAL) // o un tamaño específico en px
                            .crossfade(true)
                            .build(),

                        imageLoader = imageLoader,

                        contentDescription = page.title,

                        modifier = Modifier.fillMaxSize(),

                        contentScale = ContentScale.Fit,

                        loading = {

                        },

                        error = {

                            Icon(
                                imageVector = Icons.Default.Search,

                                contentDescription = null,

                                modifier = Modifier.padding(16.dp),

                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }

                Column(

                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                ) {

                    Text(

                        text = page.title ?: "Sin título",

                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Text(

                        text = page.description
                            ?: "Información de Wikipedia",

                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.secondary
                        ),

                        maxLines = 1,

                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (cleanExcerpt.isNotBlank()) {

                Spacer(modifier = Modifier.height(12.dp))

                Box(

                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.background.copy(
                                alpha = 0.4f
                            )
                        )
                        .padding(8.dp)
                ) {

                    Text(

                        text = cleanExcerpt,

                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),

                        maxLines = 3,

                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}