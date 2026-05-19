package com.huertas.rivera.wikibusqueda.data.remote

import com.huertas.rivera.wikibusqueda.data.model.PageDetailResponse
import com.huertas.rivera.wikibusqueda.data.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WikipediaApiService {

    @GET("w/rest.php/v1/search/page")
    suspend fun searchArticles(

        @Query("q")
        query: String,

        @Query("limit")
        limit: Int = 10
    ): SearchResponse

    @GET("api/rest_v1/page/summary/{title}")
    suspend fun getArticleSummary(

        @Path("title")
        title: String
    ): PageDetailResponse
}