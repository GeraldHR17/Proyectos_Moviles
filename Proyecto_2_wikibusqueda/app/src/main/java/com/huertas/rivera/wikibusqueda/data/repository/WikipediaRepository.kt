package com.huertas.rivera.wikibusqueda.data.repository

import com.huertas.rivera.wikibusqueda.data.model.Page
import com.huertas.rivera.wikibusqueda.data.remote.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class WikipediaRepository {

    suspend fun searchArticles(query: String): List<Page> = withContext(Dispatchers.IO) {
        // 1. Buscamos los artículos base
        val response = RetrofitInstance.api.searchArticles(query)
        val pages = response.pages ?: return@withContext emptyList()

        // 2. Ejecutamos todas las peticiones de resumen en paralelo
        val deferredPages = pages.map { page ->
            async {
                try {
                    val summary = RetrofitInstance.api.getArticleSummary(page.key ?: "")

                    // Priorizamos la imagen original de alta calidad
                    val highResUrl = summary.originalImage?.source

                    // Si no tiene imagen original, usamos el thumbnail del buscador como respaldo
                    val fallbackUrl = page.thumbnail?.url?.let { url ->
                        if (url.startsWith("//")) "https:$url" else url
                    }

                    page.copy(imageUrl = highResUrl ?: fallbackUrl)
                } catch (e: Exception) {
                    // Si falla el summary, intentamos salvar la tarjeta usando el thumbnail base
                    val fallbackUrl = page.thumbnail?.url?.let { url ->
                        if (url.startsWith("//")) "https:$url" else url
                    }
                    page.copy(imageUrl = fallbackUrl)
                }
            }
        }

        // 3. Esperamos a que terminen todas las peticiones y retornamos la lista armada
        return@withContext deferredPages.awaitAll()
    }
}