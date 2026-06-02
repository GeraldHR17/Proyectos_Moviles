package com.huertas.rivera.wikibusqueda.data.repository

import com.huertas.rivera.wikibusqueda.data.model.Page
import com.huertas.rivera.wikibusqueda.data.remote.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class WikipediaRepository {

    suspend fun searchArticles(query: String): List<Page> = withContext(Dispatchers.IO) {

        val response = RetrofitInstance.api.searchArticles(query)
        val pages = response.pages ?: return@withContext emptyList()


        val deferredPages = pages.map { page ->
            async {
                try {
                    val summary = RetrofitInstance.api.getArticleSummary(page.key ?: "")


                    val highResUrl = summary.originalImage?.source


                    val fallbackUrl = page.thumbnail?.url?.let { url ->
                        if (url.startsWith("//")) "https:$url" else url
                    }

                    page.copy(imageUrl = highResUrl ?: fallbackUrl)
                } catch (e: Exception) {

                    val fallbackUrl = page.thumbnail?.url?.let { url ->
                        if (url.startsWith("//")) "https:$url" else url
                    }
                    page.copy(imageUrl = fallbackUrl)
                }
            }
        }


        return@withContext deferredPages.awaitAll()
    }
}