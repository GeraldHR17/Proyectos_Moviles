package com.huertas.rivera.wikibusqueda.data.dao

import androidx.room.*
import com.huertas.rivera.wikibusqueda.data.entity.Favorito
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {
    @Query("SELECT * FROM favoritos ORDER BY fechaAgregado DESC")
    fun getAllFavoritos(): Flow<List<Favorito>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorito(favorito: Favorito)

    @Delete
    suspend fun deleteFavorito(favorito: Favorito)

    @Query("SELECT * FROM favoritos WHERE url = :url LIMIT 1")
    fun getFavoritoByUrl(url: String): Flow<Favorito?>

    @Query("SELECT EXISTS(SELECT 1 FROM favoritos WHERE url = :url)")
    fun isFavorito(url: String): Flow<Boolean>
}
